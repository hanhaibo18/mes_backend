package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.core.utils.DateUtils;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.base.CalendarClass;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.base.Sequence;
import com.richfit.mes.common.model.produce.Action;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.common.model.produce.Plan;
import com.richfit.mes.produce.dao.PlanMapper;
import com.richfit.mes.produce.entity.PlanDto;
import com.richfit.mes.produce.entity.PlanTrackItemViewDto;
import com.richfit.mes.produce.provider.BaseServiceClient;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.*;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/14 9:57
 */
@Slf4j
@Service
public class PlanServiceImpl extends ServiceImpl<PlanMapper, Plan> implements PlanService {

    final int PLAN_NEW = 0;
    final int PLAN_START = 1;
    final int PLAN_CLOSE = 2;
    private double LATE_HOUR = 0.0;
    @Autowired
    PlanMapper planMapper;

    @Autowired
    private BaseServiceClient baseServiceClient;

    @Autowired
    private TrackHeadService trackHeadService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ActionService actionService;


    private double getDailyHour() {
        if (LATE_HOUR == 0.0) {

            LATE_HOUR = queryDailyWorkHour();
        }
        return LATE_HOUR;
    }

    private double queryDailyWorkHour() {
        CommonResult<List<CalendarClass>> classList = baseServiceClient.selectCalendarClass(null);

        double allTime = 8;
        if (classList != null) {
            allTime = 0;
            for (CalendarClass c : classList.getData()) {
                double diff = (c.getEndTime().getTime() - c.getStartTime().getTime()) / (1000 * 60 * 60);
                allTime += diff;
            }
        }
        return allTime;
    }

    @Override
    public IPage<Plan> queryPage(Page<Plan> planPage, PlanDto planDto) {
        IPage<Plan> planList = planMapper.queryPlan(planPage, planDto);

        //TODO 应该根据计划关联的图号工艺版本号  去计算工序数量
        //根据图号，查询工艺对应工序，填充工序数量  列表显示时才进行数据封装，在别的接口调用时过滤掉
        if (planDto.isShowList()) {
            List<Branch> branchList = baseServiceClient.selectBranchChildByCode("").getData();
            for (Plan plan : planList.getRecords()) {
                try {
                    List<Sequence> sequence = baseServiceClient.getByRouterNo(plan.getDrawNo(), null, null, null).getData();
                    plan.setTotalProgress(null == sequence ? 0 : plan.getProjNum() * sequence.size());
                    plan.setLackNum(plan.getProjNum() - plan.getProcessingNum() - plan.getStoreNum());

                    CommonResult<Router> router = baseServiceClient
                            .getRouterByNo(plan.getDrawNo(), null);

                    plan.setProcessStatus(router.getData() != null ? Integer.valueOf(router.getData().getStatus()) : 0);

                    compPlanAlarmStatus(plan, sequence);

                    findBranchName(plan, branchList);
                } catch (Exception e) {

                }
            }
        }

        return planList;
    }


    /**
     * 计算计划报警状态  0:正常 1：提前  2：延后
     *
     * @param plan
     * @return
     */
    private void compPlanAlarmStatus(Plan plan, List<Sequence> sequence) {

        //按一天8小时工作日 计算当前距离计划结束时间的 剩余工作小时数  remain_hour
        //TODO 工厂日历  工厂排班   周末节假日过滤
        float remainHour = (float) (getDailyHour() * DateUtils.workDays(new Date(), plan.getEndTime()));

        //计算剩余工序还需要的工作小时数    准结+额定工时          need_hour
        //2.1 查询已报工工序列表
        List<PlanTrackItemViewDto> trackItems = this.queryPlanTrackItem(plan.getId());
        //2.2 遍历sequence  sum（每道工序（准结+额定工时）* （plan数量-该工序已报工数量）） = need_hour
        float needHour = (float) 0.0;
        float totalHour = (float) 0.0;
        float alreadyWorkHour = (float) 0.0;
        if (null != sequence && plan.getProjNum() > 0) {
            for (Sequence s : sequence) {
                int compOptNum = 0;   //指定工序已完成数量
                for (PlanTrackItemViewDto ti : trackItems) {
                    if (s.getId().equals(ti.getOptId())) {
                        compOptNum += ti.getCompleteQty() != null ? ti.getCompleteQty() : 0.0;
                    }
                }
                log.debug("plan [{}], sequence [{}],compOptNum is [{}]", plan.getProjCode(), s.getOptName(), compOptNum);
                needHour += (s.getPrepareEndHours() + s.getSinglePieceHours()) * (plan.getProjNum() - compOptNum);
                totalHour += (s.getPrepareEndHours() + s.getSinglePieceHours()) * plan.getProjNum();
                alreadyWorkHour += (s.getPrepareEndHours() + s.getSinglePieceHours()) * compOptNum;
                log.debug("plan [{}], needHour [{}],totalHour is [{}],alreadyWorkHour is [{}]",
                        plan.getProjCode(), needHour, totalHour, alreadyWorkHour);
            }
            plan.setTotalHour(totalHour);
            plan.setAlreadyWorkHour(alreadyWorkHour);
        }
        // remain_hour-need_hour >8  超前 1  remain_hour-need_hour <0 延后 2  否则正常0
        log.debug("plan [{}] remainHour [{}], needHour [{}]", plan.getWorkNo(), remainHour, needHour);
        if (remainHour < 0) {
            plan.setAlarmStatus(2);
        } else if (remainHour - needHour > getDailyHour() && plan.getOptionProgress() > 0) {
            plan.setAlarmStatus(1);
        } else if (remainHour - needHour < 0) {
            plan.setAlarmStatus(2);
        }
    }

    @Override
    public Map computePlanNeedHour(Plan plan) {

        Map map = new HashMap();
        List<Sequence> sequence = new ArrayList<>();
        sequence = baseServiceClient.getByRouterNo(plan.getDrawNo(), null, null, null).getData();

        //2.1 查询已报工工序列表
        List<PlanTrackItemViewDto> trackItems = this.queryPlanTrackItem(plan.getId());

        float needHourMin = (float) 0.0;
        float needHourMax = (float) 0.0;

        if (null != sequence && plan.getProjNum() > 0) {
            for (Sequence s : sequence) {
                int compOptNum = 0;   //指定工序已完成数量
                for (PlanTrackItemViewDto ti : trackItems) {
                    if (s.getId().equals(ti.getOptId())) {
                        compOptNum += ti.getCompleteQty() != null ? ti.getCompleteQty() : 0.0;
                    }
                }
                log.debug("plan [{}], sequence [{}],compOptNum is [{}]", plan.getProjCode(), s.getOptName(), compOptNum);
                needHourMax += (s.getPrepareEndHours() + s.getSinglePieceHours()) * (plan.getProjNum() - compOptNum);
                needHourMin += s.getSinglePieceHours() * (plan.getProjNum() - compOptNum);
                if (compOptNum == 0) {
                    needHourMin += s.getPrepareEndHours();    //如果本工序一个都没加工，则最少时间应+一次准备工时
                }
            }
        }

        map.put("needHourMin", needHourMin);
        map.put("needHourMax", needHourMax);
        map.put("dailyWorkHour", getDailyHour());
        return map;
    }

    /**
     * 功能描述: 根据时间区间 和 图号批量获取计划
     *
     * @param startTime 开始时间
     * @param endTime   结束时间
     * @param drawingNo 图号
     * @Author: xinYu.hou
     * @Date: 2022/4/20 10:59
     * @return: List<Map < String, String>>
     **/
    @Override
    public List<Map<String, String>> getPlanList(Date startTime, Date endTime, String drawingNo, String tenantId, String branchCode) {
        QueryWrapper<Plan> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("draw_no", drawingNo);
        queryWrapper.ge("create_time", startTime);
        //处理结束时间
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(endTime);
        calendar.add(Calendar.DAY_OF_MONTH, 1);
        queryWrapper.le("create_time", calendar.getTime());
        if (!StringUtil.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        if (!StringUtil.isNullOrEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }
        List<Plan> planList = this.list(queryWrapper);
        return disposePlan(planList);
    }

    public List<Map<String, String>> disposePlan(List<Plan> planList) {
        List<Map<String, String>> planListMap = new ArrayList<>();
        for (Plan plan : planList) {
            Map<String, String> planMap = new HashMap<>();
            Integer integer = trackHeadService.queryTrackHeadList(plan.getId());
            if (plan.getProjNum() <= integer) {
                continue;
            }
            planMap.put("id", plan.getId());
            int num = plan.getProjNum() - integer;
            planMap.put("name", plan.getId() + ",可跟单数量:" + num);
            planListMap.add(planMap);
        }
        return planListMap;
    }

    /*
    status 0:无关联跟单  1：有关联跟单，跟单中数量之和<计划数量  2：跟单中数量之和 = 计划数量
     */
    @Override
    public boolean updatePlanStatus(String projCode, String tenantId) {

        Plan plan = planMapper.findPlan(projCode, tenantId);

        if (plan.getProjNum() == plan.getStoreNum() && plan.getStatus() != 2) {
            plan.setStatus(2);
            this.updateById(plan);
        }
        if (plan.getProjNum() > plan.getStoreNum() && plan.getStatus() == 2) {
            plan.setStatus(1);
            this.updateById(plan);
        }
        return true;
    }

    @Override
    public List<PlanTrackItemViewDto> queryPlanTrackItem(String planId) {
        return planMapper.queryPlanTrackItem(planId);
    }

    @Override
    public void findBranchName(Plan plan) {
        List<Branch> branchList = baseServiceClient.selectBranchChildByCode("").getData();
        findBranchName(plan, branchList);
    }

    @Override
    public boolean setPlanStatusStart(String projCode, String tenantId) {
        return setPlanStatus(PLAN_START, projCode, tenantId);
    }

    @Override
    public boolean setPlanStatusNew(String projCode, String tenantId) {
        return setPlanStatus(PLAN_NEW, projCode, tenantId);
    }

    @Override
    public boolean setPlanStatusClose(String projCode, String tenantId) {
        return setPlanStatus(PLAN_CLOSE, projCode, tenantId);
    }

    @Override
    public CommonResult<Object> savePlan(Plan plan) {

        checkPlan(plan);

        //根据计划图号判断计划类型  机加or装配or？
//
        CommonResult<Router> router = baseServiceClient.getRouterByNo(plan.getDrawNo(), null);
//        plan.setDrawNoType(router.getData() != null ? router.getData().getType() : null);
        //更新对应订单状态
        if (StringUtils.hasText(plan.getOrderNo())) {
            Order order = orderService.findByOrderCode(plan.getOrderNo(), plan.getTenantId());
            if (order != null) {
                if (order.getProjNum() + plan.getProjNum() > order.getOrderNum()) {
                    return CommonResult.failed("计划数量超出订单未计划数量");
                } else if (order.getProjNum() + plan.getProjNum() == order.getOrderNum()) {
                    orderService.setOrderStatusClose(order.getId());   //订单全部安排计划
                } else {
                    orderService.setOrderStatusStart(order.getId());   //订单部分安排计划
                }
            }
        }

        return CommonResult.success(this.save(plan));
    }

    @Override
    public CommonResult<Object> updatePlan(Plan plan) {
        checkPlan(plan);
        delPlan(plan);

        Action action = new Action();
        action.setActionType("1");
        action.setActionItem("1");
        action.setRemark("计划号：" + plan.getProjNum() + "，图号:" + plan.getDrawNo());
        actionService.saveAction(action);

        return savePlan(plan);
    }

    @Override
    public boolean delPlan(Plan plan) {
        this.removeById(plan.getId());

        //更新对应订单状态
        if (StringUtils.hasText(plan.getOrderNo())) {
            Order order = orderService.findByOrderCode(plan.getOrderNo(), plan.getTenantId());
            if (order != null) {
                if (order.getProjNum() == 0) {
                    orderService.setOrderStatusNew(order.getId());   //订单全部未安排计划
                } else {
                    orderService.setOrderStatusStart(order.getId());   //订单部分安排计划
                }
            }
        }

        return true;
    }

    @Override
    public CommonResult<Object> addPlan(Plan plan) {

        CommonResult<Object> result = savePlan(plan);

        Action action = new Action();
        action.setActionType("0");
        action.setActionItem("1");
        action.setRemark("计划号：" + plan.getProjNum() + "，图号:" + plan.getDrawNo());
        actionService.saveAction(action);

        return result;
    }

    private boolean setPlanStatus(int status, String projCode, String tenantId) {

        UpdateWrapper<Plan> planUpdateWrapper = new UpdateWrapper<>();
        planUpdateWrapper.eq("tenant_id", tenantId);
        planUpdateWrapper.eq("proj_code", projCode);

        Plan plan = this.getOne(planUpdateWrapper);

        planUpdateWrapper.lambda()
                .eq(Plan::getId, plan.getId())
                .setSql("status=" + status);
        this.update(planUpdateWrapper);

        return true;
    }

    /**
     * 填充计划中的 组织机构  加工单位名称
     *
     * @param plan
     * @param branchList
     */
    private void findBranchName(Plan plan, List<Branch> branchList) {
        for (Branch b : branchList) {
            if (b.getBranchCode().equals(plan.getBranchCode())) {
                plan.setBranchName(b.getBranchName());
            }
            if (b.getBranchCode().equals(plan.getInchargeOrg())) {
                plan.setInchargeOrgName(b.getBranchName());
            }
        }
    }

    protected void checkPlan(Plan plan) {

        if (plan.getProjNum() <= 0) {
            throw new GlobalException("计划数量须>0", ResultCode.INVALID_ARGUMENTS);
        }

    }
}
