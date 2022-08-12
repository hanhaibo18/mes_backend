package com.richfit.mes.produce.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.core.utils.DateUtils;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.base.*;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.LineStoreMapper;
import com.richfit.mes.produce.dao.PlanMapper;
import com.richfit.mes.produce.dao.TrackHeadMapper;
import com.richfit.mes.produce.dao.TrackItemMapper;
import com.richfit.mes.produce.entity.PlanDto;
import com.richfit.mes.produce.entity.PlanSplitDto;
import com.richfit.mes.produce.entity.PlanTrackItemViewDto;
import com.richfit.mes.produce.entity.extend.ProjectBomComplete;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.ErpServiceClient;
import com.richfit.mes.produce.provider.WmsServiceClient;
import com.richfit.mes.produce.utils.Utils;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

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

    @Resource
    private BaseServiceClient baseServiceClient;

    @Resource
    private ErpServiceClient erpServiceClient;

    @Resource
    private WmsServiceClient wmsServiceClient;

    @Autowired
    private TrackHeadService trackHeadService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private TrackHeadMapper trackHeadMapper;

    @Autowired
    private TrackItemMapper trackItemMapper;

    @Autowired
    private LineStoreMapper lineStoreMapper;

    @Autowired
    private TrackAssemblyService trackAssemblyService;

    @Value("${interface.wms.material-remaining-number}")
    private String urlStoreRemainingNumber;


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

    /**
     * 功能描述: 物料齐套性检查
     *
     * @param planId 计划id
     * @Author: zhiqiang.lu
     * @Date: 2022/6/7 11:37
     **/
    @Override
    public List<ProjectBomComplete> completeness(String planId) {
        List<ProjectBomComplete> projectBomCompleteList = new ArrayList<>();
        Plan plan = planMapper.selectById(planId);
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<TrackHead>();
        queryWrapper.eq("work_plan_id", plan.getId());
        List<TrackHead> trackHeadList = trackHeadMapper.selectList(queryWrapper);
        if (trackHeadList == null || trackHeadList.size() == 0) {
            //计划未匹配跟单
            projectBomCompleteList = projectBomCompleteList(plan);
        } else {
            //计划已匹配跟单
            projectBomCompleteList = projectBomCompleteListByTrackHead(plan, trackHeadList);
        }
        return pojectBomCompleteStoreList(projectBomCompleteList);
    }

    /**
     * 功能描述: 齐套物料查询（跟单下的装配bom装配信息合并后的列表）
     *
     * @param planList 计划信息列表
     * @Author: zhiqiang.lu
     * @Date: 2022/8/11 11:37
     **/
    @Override
    public List<ProjectBomComplete> completenessList(List<Plan> planList) {
        List<ProjectBomComplete> projectBomCompleteList = new ArrayList<>();
        for (Plan plan : planList) {
            QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<TrackHead>();
            queryWrapper.eq("work_plan_id", plan.getId());
            List<TrackHead> trackHeadList = trackHeadMapper.selectList(queryWrapper);
            List<ProjectBomComplete> projectBomCompleteListNew = new ArrayList<>();
            System.out.println(trackHeadList);
            if (trackHeadList == null || trackHeadList.size() == 0) {
                //计划未匹配跟单
                projectBomCompleteListNew = projectBomCompleteList(plan);
            } else {
                //计划已匹配跟单
                projectBomCompleteListNew = projectBomCompleteListByTrackHead(plan, trackHeadList);
            }
            for (ProjectBomComplete pbcn : projectBomCompleteListNew) {
                boolean flag = true;
                for (ProjectBomComplete pbc : projectBomCompleteList) {
                    if (pbcn.getMaterialNo().equals(pbc.getMaterialNo())) {
                        flag = false;
                        pbc.setPlanNumber(pbc.getPlanNumber() + pbcn.getPlanNumber());
                        pbc.setPlanNeedNumber(pbc.getPlanNeedNumber() + pbcn.getPlanNeedNumber());
                    }
                }
                if (flag) {
                    projectBomCompleteList.add(pbcn);
                }
            }
        }
        return pojectBomCompleteStoreList(projectBomCompleteList);
    }

    /**
     * 功能描述: 齐套物料BOM数据封装（跟单下的装配bom装配信息合并后的列表）
     *
     * @param plan 计划信息
     * @Author: zhiqiang.lu
     * @Date: 2022/8/11 11:37
     **/
    List<ProjectBomComplete> projectBomCompleteList(Plan plan) {
        List<ProjectBomComplete> projectBomCompleteList = new ArrayList<>();
        if (!StringUtil.isNullOrEmpty(plan.getProjectBom())) {
            List<ProjectBom> projectBomList = baseServiceClient.getProjectBomPartByIdList(plan.getProjectBom());
            Map<String, String> group = new HashMap<>();
            if (!StringUtil.isNullOrEmpty(plan.getProjectBomGroup())) {
                group = JSON.parseObject(plan.getProjectBomGroup(), Map.class);
            }
            for (ProjectBom pb : projectBomList) {
                //过滤H零件、齐套检查
                if ("L".equals(pb.getGrade()) && "1".equals(pb.getIsCheck())) {
                    //处理分组信息
                    if (!StringUtil.isNullOrEmpty(pb.getGroupBy())) {
                        if (pb.getId().equals(group.get(pb.getGroupBy()))) {
                            ProjectBomComplete pbc = JSON.parseObject(JSON.toJSONString(pb), ProjectBomComplete.class);
                            pbc.setPlanNumber(plan.getProjNum());
                            pbc.setPlanNeedNumber(plan.getProjNum() * pb.getNumber());
                            projectBomCompleteList.add(pbc);
                        }
                    } else {
                        ProjectBomComplete pbc = JSON.parseObject(JSON.toJSONString(pb), ProjectBomComplete.class);
                        pbc.setPlanNumber(plan.getProjNum());
                        pbc.setPlanNeedNumber(plan.getProjNum() * pb.getNumber());
                        projectBomCompleteList.add(pbc);
                    }
                }
            }
        }
        return projectBomCompleteList;
    }


    /**
     * 功能描述: 齐套物料数据合并封装（跟单下的装配bom装配信息合并后的列表）
     *
     * @param plan          计划信息
     * @param trackHeadList 计划匹配的跟单列表
     * @Author: zhiqiang.lu
     * @Date: 2022/8/11 11:37
     **/
    List<ProjectBomComplete> projectBomCompleteListByTrackHead(Plan plan, List<TrackHead> trackHeadList) {
        List<ProjectBomComplete> projectBomCompleteList = new ArrayList<>();
        for (TrackHead trackHead : trackHeadList) {
            QueryWrapper<TrackAssembly> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("track_head_id", trackHead.getId());
            List<TrackAssembly> trackAssemblies = trackAssemblyService.list(queryWrapper);
            for (TrackAssembly trackAssembly : trackAssemblies) {
                //过滤H零件、齐套检查
                if ("L".equals(trackAssembly.getGrade()) && "1".equals(trackAssembly.getIsCheck())) {
                    boolean flag = true;
                    for (ProjectBomComplete projectBomComplete : projectBomCompleteList) {
                        if (trackAssembly.getMaterialNo().equals(projectBomComplete.getMaterialNo())) {
                            flag = false;
                            projectBomComplete.setNumber(trackAssembly.getNumber() + projectBomComplete.getNumber());
                            projectBomComplete.setInstallNumber(trackAssembly.getNumber() + projectBomComplete.getInstallNumber());
                        }
                    }
                    if (flag) {
                        ProjectBomComplete projectBomComplete = new ProjectBomComplete();
                        projectBomComplete.setPlanNumber(plan.getProjNum());
                        projectBomComplete.setPlanNeedNumber(plan.getProjNum() * trackAssembly.getNumber());
                        projectBomComplete.setNumber(trackAssembly.getNumber());
                        projectBomComplete.setInstallNumber(trackAssembly.getNumber());
                        projectBomComplete.setProdDesc(trackAssembly.getName());
                        projectBomComplete.setMaterialNo(trackAssembly.getMaterialNo());
                        projectBomComplete.setDrawingNo(trackAssembly.getDrawingNo());
                        projectBomComplete.setSourceType(trackAssembly.getSourceType());
                        projectBomComplete.setUnit(trackAssembly.getUnit());
                        projectBomCompleteList.add(projectBomComplete);
                    }
                }
            }
        }
        return projectBomCompleteList;
    }

    /**
     * 功能描述: wms接口库存数量获取
     *
     * @param projectBomCompleteList 齐套数据列表
     * @Author: zhiqiang.lu
     * @Date: 2022/8/11 11:37
     **/
    List<ProjectBomComplete> pojectBomCompleteStoreList(List<ProjectBomComplete> projectBomCompleteList) {
        String materialNos = "";
        for (ProjectBomComplete pbc : projectBomCompleteList) {
            materialNos += "," + pbc.getMaterialNo();
        }
        CommonResult<List<Product>> commonResult = erpServiceClient.getStorage(materialNos.substring(1), SecurityUtils.getCurrentUser().getTenantErpCode());
        List<Product> productList = commonResult.getData();
        for (ProjectBomComplete pbc : projectBomCompleteList) {
            int num = wmsServiceClient.queryMaterialCount(pbc.getMaterialNo()).getData();
            int totalErp = 0;
            if (productList != null) {
                for (Product product : productList) {
                    if (product.getMaterialNo().equals(pbc.getMaterialNo())) {
                        totalErp = product.getQty().multiply(new BigDecimal(Utils.unit(product.getUnit()))).intValue();
                    }
                }
            }
            double totalWms = Double.valueOf(num).intValue();
            double totalStore = 0;
            double totalMiss = 0;
            int unit = Utils.unit(pbc.getUnit());
            pbc.setErpNumber(totalErp / unit);
            pbc.setWmsNumber(totalWms / unit);
            Integer totalMaterial = lineStoreMapper.selectTotalNum(pbc.getMaterialNo(), pbc.getBranchCode(), pbc.getTenantId());
            if (totalMaterial != null) {
                totalStore += lineStoreMapper.selectTotalNum(pbc.getMaterialNo(), pbc.getBranchCode(), pbc.getTenantId());
                pbc.setStoreNumber(totalStore / unit);
            }
            totalMiss = pbc.getPlanNeedNumber() * unit + pbc.getInstallNumber() * unit - totalErp - totalWms - totalStore;
            if (totalMiss > 0) {
                pbc.setMissingNumber(totalMiss / unit);
            } else {
                pbc.setMissingNumber(0);
            }
        }
        return projectBomCompleteList;
    }


    /**
     * 功能描述: 计划数据自动计算
     *
     * @param planId 计划id
     * @Author: zhiqiang.lu
     * @Date: 2022/7/8 11:37
     **/
    @Override
    public void planData(String planId) {
        Plan plan = planMapper.selectById(planId);
        if (plan != null) {
            QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<TrackHead>();
            queryWrapper.eq("work_plan_id", planId);
            List<TrackHead> trackHeadList = trackHeadMapper.selectList(queryWrapper);
//            Map<String, String> map = new HashMap();
//            map.put("workPlanId", planId);
//            List<TrackHead> trackHeadList = trackHeadService.selectTrackFlowList(map);
            int storeNum = 0;
            int processNum = 0;
            int deliveryNum = 0;
            int optNumber = 0;
            int optProcessNumber = 0;
            int trackHeadFinish = 0;
            for (TrackHead trackHead : trackHeadList) {
                QueryWrapper<TrackItem> queryWrapperTrackItem = new QueryWrapper<TrackItem>();
                queryWrapperTrackItem.eq("track_head_id", trackHead.getId());
                List<TrackItem> trackItemList = trackItemMapper.selectList(queryWrapperTrackItem);
                optNumber += trackItemList.size();
                if ("0".equals(trackHead.getStatus())) {
                    //未派工算在制
                    processNum += trackHead.getNumber();
                    for (TrackItem trackItem : trackItemList) {
                        if (trackItem.getIsOperationComplete() == 0) {
                            optProcessNumber++;
                        }
                    }
                } else if ("1".equals(trackHead.getStatus())) {
                    //在制
                    processNum += trackHead.getNumber();
                    for (TrackItem trackItem : trackItemList) {
                        if (trackItem.getIsOperationComplete() == 0) {
                            optProcessNumber++;
                        }
                    }
                } else if ("2".equals(trackHead.getStatus())) {
                    //完工
                    storeNum += trackHead.getNumber();
                } else if ("4".equals(trackHead.getStatus())) {
                    //打印跟单
                } else if ("5".equals(trackHead.getStatus())) {
                    //作废跟单
                } else if ("8".equals(trackHead.getStatus())) {
                    //生成完工资料
                    storeNum += trackHead.getNumber();
                } else if ("9".equals(trackHead.getStatus())) {
                    //已交
                } else {
                    //其余都算完工
                    trackHeadFinish++;
                    deliveryNum += trackHead.getNumber();
                }
            }
            plan.setStoreNum(storeNum);//库存数量
            plan.setProcessNum(processNum);//在制数量
            plan.setDeliveryNum(deliveryNum);//交付数量
            plan.setMissingNum(plan.getProjNum() - storeNum - processNum - deliveryNum);//缺件数量
            plan.setTrackHeadNumber(trackHeadList.size());//跟单数量
            plan.setTrackHeadFinishNumber(trackHeadFinish);//跟单完成数量
            plan.setOptNumber(optNumber);//工序数量
            plan.setOptFinishNumber(optNumber - optProcessNumber);//工序完成数量
            if (plan.getProjNum() <= plan.getDeliveryNum()) {
                plan.setStatus(3);
            } else {
                if (plan.getTrackHeadNumber() > 0) {
                    plan.setStatus(1);
                } else {
                    plan.setStatus(0);
                }
            }
            planMapper.updateById(plan);
        }
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
            //已完成
            plan.setStatus(3);
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


    /**
     * 拆分计划
     *
     * @param planSplitDto
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Object> splitPlan(PlanSplitDto planSplitDto) {
        //原计划
        Plan oldPlan = planSplitDto.getOldPlan();
        //拆分计划
        Plan newPlan = planSplitDto.getNewPlan();

        if (StringUtils.hasText(oldPlan.getId())) {
            //根据id查询父计划
            Plan parentPlan = planMapper.selectById(oldPlan.getId());

            if (!ObjectUtil.isEmpty(parentPlan)) {
                if (newPlan.getProjNum() > parentPlan.getProjNum()) {
                    return CommonResult.failed("拆分计划数量超出原计划数量");
                }
            }
            //构造新拆分计划
            Plan plan = new Plan();
            BeanUtil.copyProperties(parentPlan, plan, new String[]{"id"});
            //构造拆分计划
            plan.setOriginalPlanId(parentPlan.getId());
            plan.setOriginalProjCode(parentPlan.getOriginalProjCode());
            plan.setProjNum(newPlan.getProjNum());
            plan.setStartTime(newPlan.getStartTime());
            plan.setEndTime(newPlan.getEndTime());
            plan.setProjCode(newPlan.getProjCode());

            //修改父计划的计划数量
            UpdateWrapper<Plan> planUpdateWrapper = new UpdateWrapper<>();
            planUpdateWrapper
                    .eq("id", parentPlan.getId())
                    .set("proj_num", oldPlan.getProjNum());
            this.update(planUpdateWrapper);
            //保存拆分计划
            this.save(plan);

            //保存跟单计划
            if (!ObjectUtil.isEmpty(newPlan.getTrackHeadIds())) {
                //替换计划id
                UpdateWrapper<TrackHead> trackHeadUpdateWrapper = new UpdateWrapper<>();
                trackHeadUpdateWrapper.in("id", newPlan.getTrackHeadIds())
                        .set("work_plan_id", plan.getId());
                trackHeadService.update(trackHeadUpdateWrapper);
            }

            //拆分完旧计划数据自动计算
            planData(parentPlan.getId());
            //拆分完新计划数据自动计算
            planData(plan.getId());
        }
        return CommonResult.success(null);
    }

    /**
     * 撤销拆分
     *
     * @param id
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Object> backoutPlan(String id) {
        //校验
        Plan currPlan = planMapper.selectById(id);
        if (!ObjectUtil.isEmpty(currPlan) && !ObjectUtil.isEmpty(currPlan.getOriginalPlanId())) {
            //合并到源计划
            Plan plan = planMapper.selectById(currPlan.getOriginalPlanId());
            UpdateWrapper<Plan> planUpdateWrapper = new UpdateWrapper<>();
            planUpdateWrapper.eq("id", currPlan.getOriginalPlanId())
                    .set("proj_num", currPlan.getProjNum() + plan.getProjNum());
            this.update(planUpdateWrapper);
            //查询要合并的跟单
            QueryWrapper<TrackHead> trackHeadQueryWrapper = new QueryWrapper<>();
            List<TrackHead> trackHeads = trackHeadService.list(trackHeadQueryWrapper.eq("work_plan_id", currPlan.getId()));
            List<String> workPlanIds = new ArrayList<>(trackHeads.stream().collect(Collectors.toMap(TrackHead::getId, TrackHead::getId)).values());

            //跟单合并
            if (workPlanIds.size() > 0) {
                UpdateWrapper<TrackHead> trackHeadUpdateWrapper = new UpdateWrapper<>();
                trackHeadUpdateWrapper.in("id", workPlanIds)
                        .set("work_plan_id", plan.getId());
                trackHeadService.update(trackHeadUpdateWrapper);
            }

            //删除该计划
            this.removeById(id);

            //合并后计划数据自动计算
            planData(plan.getId());
        }
        return CommonResult.success(null);
    }

    @Override
    public void exportPlan(MultipartFile file) throws IOException {
        //sheet计划列表
        String[] fieldNames3 = {"isExport", "sortNo", "workNo", "drawNo", "drawNoName", "texture", "singleNumber", "projNum", "totalNumber"
                , "blank", "remark", "prepareBy", "approvalBy", "auditBy", "branchCode", "inchargeOrg", "storeNumber", "processNum", "materialProductionUnit"
                , "rivetingWeldingUnit", "assemblyContractorUnit", "finalAssemblyContractorUnit", "missingNum", "startTime", "projType", "projCode", "endTime", "projectNo"};

        File excelFile = null;

        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        try {
            excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());
            file.transferTo(excelFile);

            List<Plan> list3 = ExcelUtils.importExcel(excelFile, Plan.class, fieldNames3, 1, 0, 1, tempName.toString());


            FileUtils.delete(excelFile);
            //sheet1过滤要导入的数据
            List<Plan> sheetList = list3.stream().filter(t -> {
                return !StringUtils.isEmpty(t.getIsExport())
                        && !StringUtils.isEmpty(t.getBranchCode())   //部门必填
                        && !StringUtils.isEmpty(t.getInchargeOrg())  //加工车间必填
                        && !StringUtils.isEmpty(t.getEndTime())      //交货期必填
                        && !StringUtils.isEmpty(t.getProjectNo());   //项目号必填
            }).collect(Collectors.toList());

            for (Plan plan : sheetList) {
                plan.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                if (!ObjectUtil.isEmpty(plan.getDrawNo()) && plan.getDrawNo().equals("0")) {
                    plan.setDrawNo(null);
                }
                if (!ObjectUtil.isEmpty(plan.getDrawNoName()) && plan.getDrawNoName().equals("0")) {
                    plan.setDrawNoName(null);
                }
                if (!ObjectUtil.isEmpty(plan.getTexture()) && plan.getTexture().equals("0")) {
                    plan.setTexture(null);
                }
                if (!ObjectUtil.isEmpty(plan.getRemark()) && plan.getRemark().equals("0")) {
                    plan.setRemark(null);
                }
                if (!ObjectUtil.isEmpty(plan.getMaterialProductionUnit()) && plan.getMaterialProductionUnit().equals("0")) {
                    plan.setMaterialProductionUnit(null);
                }
                if (!ObjectUtil.isEmpty(plan.getRivetingWeldingUnit()) && plan.getRivetingWeldingUnit().equals("0")) {
                    plan.setRivetingWeldingUnit(null);
                }
                if (!ObjectUtil.isEmpty(plan.getAssemblyContractorUnit()) && plan.getAssemblyContractorUnit().equals("0")) {
                    plan.setAssemblyContractorUnit(null);
                }
                if (!ObjectUtil.isEmpty(plan.getFinalAssemblyContractorUnit()) && plan.getFinalAssemblyContractorUnit().equals("0")) {
                    plan.setFinalAssemblyContractorUnit(null);
                }
            }


            //保存计划列表
            this.saveBatch(sheetList);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void planPackageRouter(List<Plan> planList) {
        for (Plan plan : planList) {
            CommonResult<Router> result = baseServiceClient.getRouterByNo(plan.getDrawNo(), plan.getBranchCode());
            if (result.getData() != null && "1".equals(result.getData().getStatus())) {
                plan.setProcessStatus(1);
            } else {
                plan.setProcessStatus(0);
            }
        }
    }

    @Override
    public void planPackageStore(List<Plan> planList) {
        String drawingNos = "";
        for (Plan plan : planList) {
            drawingNos += ",'" + plan.getDrawNo() + "'";
        }
        drawingNos = drawingNos.substring(1);
        List<Map> mapList = trackHeadService.selectTrackStoreCount(drawingNos);
        for (Plan plan : planList) {
            for (Map map : mapList) {
                if (map.get("drawing_no").toString().equals(plan.getDrawNo())) {
                    plan.setStoreNumber(Integer.parseInt(map.get("number").toString()));
                }
            }
        }
    }
}
