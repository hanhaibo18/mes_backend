package com.richfit.mes.produce.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.base.ProjectBom;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.produce.bom.ProduceProjectBom;
import com.richfit.mes.common.model.produce.store.PlanExtend;
import com.richfit.mes.common.model.util.ActionUtil;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.aop.OperationLog;
import com.richfit.mes.produce.aop.OperationLogAspect;
import com.richfit.mes.produce.dao.*;
import com.richfit.mes.produce.entity.PlanSplitDto;
import com.richfit.mes.produce.entity.PlanTrackItemViewDto;
import com.richfit.mes.produce.entity.TrackHeadPublicDto;
import com.richfit.mes.produce.entity.extend.ProjectBomComplete;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.WmsServiceClient;
import com.richfit.mes.produce.service.bom.ProjectBomService;
import com.richfit.mes.produce.utils.DateUtils;
import com.richfit.mes.produce.utils.Utils;
import io.netty.util.internal.StringUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

import static com.richfit.mes.produce.aop.LogConstant.PLAN_ID;

/**
 * @Author: zhiqiang.lu
 * @Date: 2020.9.2 9:54
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
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

    @Autowired
    private PlanExtendMapper planExtendMapper;

    @Autowired
    private HotDemandService hotDemandService;

    @Autowired
    private ProjectBomService projectBomService;

    @Autowired
    private TrackHeadFlowService trackHeadFlowService;

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
     * 功能描述: 齐套物料BOM数据封装（计划未匹配跟单）
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
                    if (!StringUtil.isNullOrEmpty(pb.getBomGrouping())) {
                        if (pb.getId().equals(group.get(pb.getBomGrouping()))) {
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
     * 功能描述: 齐套物料数据合并封装（计划已匹配跟单）
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
                            projectBomComplete.setInstallIds(trackAssembly.getId() + "," + projectBomComplete.getInstallIds());
                            projectBomComplete.setInstallNumber(trackAssembly.getNumberInstall() + projectBomComplete.getInstallNumber());
                        }
                    }
                    if (flag) {
                        ProjectBomComplete projectBomComplete = new ProjectBomComplete();
                        projectBomComplete.setTenantId(plan.getTenantId());
                        projectBomComplete.setBranchCode(plan.getBranchCode());
                        projectBomComplete.setPlanNumber(plan.getProjNum());
                        projectBomComplete.setPlanNeedNumber(plan.getProjNum() * trackAssembly.getNumber());
                        projectBomComplete.setNumber(trackAssembly.getNumber());
                        projectBomComplete.setInstallIds(trackAssembly.getId());
                        projectBomComplete.setInstallNumber(trackAssembly.getNumberInstall());
                        projectBomComplete.setProdDesc(trackAssembly.getName());
                        projectBomComplete.setMaterialNo(trackAssembly.getMaterialNo());
                        projectBomComplete.setDrawingNo(trackAssembly.getDrawingNo());
                        projectBomComplete.setSourceType(trackAssembly.getSourceType());
                        projectBomComplete.setUnit(trackAssembly.getUnit());
                        projectBomComplete.setIsKeyPart(trackAssembly.getIsKeyPart());
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
        for (ProjectBomComplete pbc : projectBomCompleteList) {
            int num = wmsServiceClient.queryMaterialCount(pbc.getMaterialNo()).getData();

//            int totalErp = 0;
            double totalWms = Double.valueOf(num).intValue();
            double totalStore = 0;
            double totalMiss = 0;
            int unit = Utils.unit(pbc.getUnit());
//            pbc.setErpNumber(totalErp / unit);
            pbc.setWmsNumber(totalWms / unit);
            Integer totalMaterial = lineStoreMapper.selectTotalNum(pbc.getMaterialNo(), pbc.getBranchCode(), pbc.getTenantId());
            if (totalMaterial != null) {
                totalStore += lineStoreMapper.selectTotalNum(pbc.getMaterialNo(), pbc.getBranchCode(), pbc.getTenantId());
                log.info("线边库物料数量： [{}]", totalStore);
                pbc.setStoreNumber(totalStore / unit);
            }
            totalMiss = pbc.getPlanNeedNumber() * unit - totalWms - totalStore;
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
    @OperationLog(actionType = "1", actionItem = "1", argType = PLAN_ID)
    public void planData(String planId) {
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(planId)) {
            QueryWrapper planWrapper = new QueryWrapper();
            planWrapper.notIn("status", 4);
            planWrapper.eq("id", planId);
            Plan plan = planMapper.selectOne(planWrapper);
            if (plan != null) {
                QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<TrackHead>();
                queryWrapper.eq("work_plan_id", planId);
                queryWrapper.eq("branch_code", plan.getBranchCode());
                queryWrapper.eq(StrUtil.isNotBlank(plan.getWorkNo()), "work_no", plan.getWorkNo());
                List<TrackHead> trackHeadList = trackHeadMapper.selectList(queryWrapper);
                //库存数量
                int storeNum = 0;
                //在制数量
                int processNum = 0;
                //已交数量
                int deliveryNum = 0;
                //工序数量
                int optNumber = 0;
                //工序在制数量
                int optProcessNumber = 0;
                //跟单完成数量
                int trackHeadFinish = 0;
                for (TrackHead trackHead : trackHeadList) {
                    if (TrackHead.IS_TEST_BAR_1.equals(trackHead.getIsTestBar())) {
                        //试棒实验跟单不进行计划数量的消耗
                        continue;
                    }
                    QueryWrapper<TrackItem> queryWrapperTrackItem = new QueryWrapper<TrackItem>();
                    queryWrapperTrackItem.eq("track_head_id", trackHead.getId());
                    List<TrackItem> trackItemList = trackItemMapper.selectList(queryWrapperTrackItem);
                    optNumber += trackItemList.size();
                    System.out.println("---");
                    System.out.println(trackHead.getStatus());
                    switch (trackHead.getStatus()) {
                        case "0":
                        case "1":
                            //0在制
                            //1未派工算在制
                            processNum += trackHead.getNumber();
                            for (TrackItem trackItem : trackItemList) {
                                int isOperationComplete = trackItem.getIsOperationComplete() == null ? 0 : trackItem.getIsOperationComplete();
                                //工序未完工
                                if (isOperationComplete == 0) {
                                    optProcessNumber++;
                                }
                            }
                            break;
                        case "2":
                            //完工
                            storeNum += trackHead.getNumber();
                            trackHeadFinish++;
                            break;
                        case "4":
                            //打印跟单
                            trackHeadFinish++;
                            break;
                        case "5":
                            //作废跟单
                            trackHeadFinish++;
                            break;
                        case "8":
                        case "9":
                            System.out.println("------------------");
                            System.out.println("已交");
                            //已交
                            //生成完工资料
                            trackHeadFinish++;
                            deliveryNum += trackHead.getNumber();
                            break;
                        default:
                    }
                }
                //库存数量
                plan.setStoreNumber(storeNum);
                //在制数量
                plan.setProcessNum(processNum);
                //交付数量
                plan.setDeliveryNum(deliveryNum);
                //缺件数量
                plan.setMissingNum(plan.getProjNum() - storeNum - processNum - deliveryNum);
                //跟单数量
                plan.setTrackHeadNumber(trackHeadList.size());
                //跟单完成数量
                plan.setTrackHeadFinishNumber(trackHeadFinish);
                //工序数量
                plan.setOptNumber(optNumber);
                //工序完成数量
                plan.setOptFinishNumber(optNumber - optProcessNumber);
                //计划数量、已交数量判断用来处理计划状态
                if (plan.getProjNum().intValue() == storeNum + deliveryNum) {
                    plan.setStatus(3);
                } else {
                    if (trackHeadList.size() > 0) {
                        plan.setStatus(1);
                    } else {
                        plan.setStatus(0);
                    }
                }
                if (plan.getMissingNum() < 0) {
                    throw new GlobalException("请重新核对计划，当前计划已出现缺件数量小于0", ResultCode.FAILED);
                }
                planMapper.updateById(plan);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void autoProjectBom(Plan plan) throws Exception {
        if (StrUtil.isBlank(plan.getProjectBom())) {
            String workNo = "";
            if (StrUtil.isNotBlank(plan.getProjectBomWork())) {
                workNo = plan.getProjectBomWork();
            } else {
                workNo = plan.getWorkNo();
            }
            if (StrUtil.isBlank(workNo)) {
                throw new Exception("工作号不能为空");
            }
            if (StrUtil.isBlank(plan.getDrawNo())) {
                throw new Exception("图号号不能为空");
            }
            List<ProduceProjectBom> projectBoms = projectBomService.getProjectBomList(workNo, plan.getDrawNo(), plan.getTenantId(), plan.getBranchCode());
            if (!CollectionUtils.isEmpty(projectBoms)) {
                ProduceProjectBom projectBom = projectBoms.get(0);
                plan.setProjectBom(projectBom.getId());
                plan.setProjectBomWork(projectBom.getWorkPlanNo());
                plan.setProjectBomGroup("{}");
                plan.setProjectBomName(projectBom.getProjectName());
                List<TrackHead> trackHeads = trackHeadService.queryTrackHeadListByPlanId(plan.getId());
                for (TrackHead trackHead : trackHeads) {
                    QueryWrapper<TrackAssembly> queryWrapper = new QueryWrapper<>();
                    queryWrapper.eq("track_head_id", trackHead.getId());
                    List<TrackAssembly> trackAssemblies = trackAssemblyService.list(queryWrapper);
                    if (StrUtil.isBlank(trackHead.getProjectBomId()) && CollectionUtils.isEmpty(trackAssemblies)) {
                        if (TrackHead.STATUS_0.equals(trackHead.getStatus()) || TrackHead.STATUS_1.equals(trackHead.getStatus())) {
                            trackHead.setProjectBomId(projectBom.getId());
                            trackHead.setProjectBomWork(projectBom.getWorkPlanNo());
                            trackHead.setProjectBomGroup("{}");
                            trackHead.setProjectBomName(projectBom.getProjectName());
                            List<TrackFlow> trackFlows = trackHeadFlowService.queryTrackFlowListByTrackHeadId(trackHead.getId());
                            for (TrackFlow trackFlow : trackFlows) {
                                if (TrackHead.STATUS_0.equals(trackFlow.getStatus()) || TrackHead.STATUS_1.equals(trackFlow.getStatus())) {
                                    TrackHeadPublicDto trackHeadPublicDto = new TrackHeadPublicDto();
                                    BeanUtils.copyProperties(trackHead, trackHeadPublicDto); //a，b为对象
                                    trackHeadPublicDto.setFlowId(trackFlow.getId());
                                    List<TrackFlow> trackFlowList = new ArrayList<>();
                                    trackFlowList.add(trackFlow);
                                    trackAssemblyService.addTrackAssemblyByTrackHead(trackHeadPublicDto, trackFlowList);
                                }
                            }
                            trackHeadService.updateById(trackHead);
                        }
                    }
                }
                this.updateById(plan);
            }
        }
    }

    /**
     * 更新需求提报表中交付数量
     *
     * @param planId
     */
    @Override
    public void updateDeliveryNum(String planId) {
        Plan plan = planMapper.selectById(planId);
        //热工分公司的数据需要更新需求提报表中的交付数量字段
        if (!ObjectUtil.isEmpty(plan)) {
            if (plan.getTenantId().equals("12345678901234567890123456789001")) {
                QueryWrapper<HotDemand> demandQueryWrapper = new QueryWrapper<>();
                demandQueryWrapper.eq("tenant_id", plan.getTenantId());
                demandQueryWrapper.apply("(plan_id='" + plan.getId() + "' or plan_id_model ='" + plan.getId() + "')");
                HotDemand hotDemand = hotDemandService.getOne(demandQueryWrapper);
                if (!ObjectUtil.isEmpty(hotDemand)) {
                    UpdateWrapper<HotDemand> updateWrapper = new UpdateWrapper<>();
                    int DeliveryNum = hotDemand.getDeliveryNum();
                    DeliveryNum += plan.getDeliveryNum();
                    updateWrapper.set("delivery_num", DeliveryNum);
                    updateWrapper.eq("tenant_id", plan.getTenantId());
                    updateWrapper.apply("(plan_id='" + plan.getId() + "' or plan_id_model ='" + plan.getId() + "')");
                    hotDemandService.update(updateWrapper);
                }
            }
        }
    }

    /*
        status 0:无关联跟单  1：有关联跟单，跟单中数量之和<计划数量  2：跟单中数量之和 = 计划数量
         */
    @Override
    public boolean updatePlanStatus(String projCode, String tenantId) {
        Plan plan = planMapper.findPlan(projCode, tenantId);
        int status = plan.getStatus() == null ? 0 : plan.getStatus();
        if (plan.getProjNum().equals(plan.getStoreNumber()) && status != 2) {
            //已完成
            plan.setStatus(3);
            this.updateById(plan);
        }
        if (plan.getProjNum().compareTo(plan.getStoreNumber()) > 0 && status == 2) {
            plan.setStatus(1);
            this.updateById(plan);
        }
        return true;
    }

    @Override
    public List<PlanTrackItemViewDto> queryPlanTrackItem(String planId) {
        return planMapper.queryPlanTrackItem(planId);
    }

    /**
     * 保存计划
     *
     * @param plan
     * @return
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Object> savePlan(Plan plan) {
        checkPlan(plan);
        //根据计划图号判断计划类型  机加or装配or？
        CommonResult<Router> router = baseServiceClient.getByRouterNo(plan.getDrawNo(), null);
//        plan.setDrawNoType(router.getData() != null ? router.getData().getType() : null);
        //更新对应订单状态
        if (StringUtils.hasText(plan.getOrderNo())) {
            Order order = orderService.findByOrderCode(plan.getOrderNo(), plan.getTenantId());
            if (order != null) {
                int orderProjNum = order.getProjNum() == null ? 0 : order.getProjNum();
                int planProjNum = plan.getProjNum() == null ? 0 : plan.getProjNum();
                int orderNum = order.getOrderNum() == null ? 0 : order.getOrderNum();
                if (orderProjNum + planProjNum > orderNum) {
                    return CommonResult.failed("计划数量超出订单未计划数量");
                } else if (orderNum == planProjNum + orderProjNum) {
                    orderService.setOrderStatusClose(order.getId());   //订单全部安排计划
                } else {
                    orderService.setOrderStatusStart(order.getId());   //订单部分安排计划
                }
            }
        }
        //获取计划的扩展信息
        QueryWrapper<PlanExtend> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("plan_id", plan.getId());
        PlanExtend planExtend = planExtendMapper.selectOne(queryWrapper);
        boolean f = this.save(plan);
        this.planData(plan.getId());
        if (ObjectUtil.isEmpty(planExtend)) {
            PlanExtend newplanExtend = new PlanExtend();
            BeanUtils.copyProperties(plan, newplanExtend);
            newplanExtend.setPlanId(plan.getId());
            planExtendMapper.insert(newplanExtend);
        } else {
            //修改扩信息
            BeanUtils.copyProperties(plan, planExtend);
            planExtend.setPlanId(plan.getId());
            planExtendMapper.updateById(planExtend);
        }
        return CommonResult.success(f);
    }

    @Override
    public CommonResult<Object> updatePlan(Plan plan) {
        checkPlan(plan);
        delPlan(plan);

        return savePlan(plan);
    }

    @Override
    public boolean delPlan(Plan plan) {
        this.removeById(plan.getId());

        //更新对应订单状态
        if (StringUtils.hasText(plan.getOrderNo())) {
            Order order = orderService.findByOrderCode(plan.getOrderNo(), plan.getTenantId());
            if (order != null) {
                int orderProjNum = order.getProjNum() == null ? 0 : order.getProjNum();
                if (orderProjNum == 0) {
                    orderService.setOrderStatusNew(order.getId());   //订单全部未安排计划
                } else {
                    orderService.setOrderStatusStart(order.getId());   //订单部分安排计划
                }
            }
        }

        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Object> addPlan(Plan plan) {

        CommonResult<Object> result = savePlan(plan);

        return result;
    }

    protected void checkPlan(Plan plan) {
        int planProjNum = plan.getProjNum() == null ? 0 : plan.getProjNum();
        if (planProjNum <= 0) {
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
                int parentPlanProjNum = parentPlan.getProjNum() == null ? 0 : parentPlan.getProjNum();
                int newPlanProjNum = newPlan.getProjNum() == null ? 0 : newPlan.getProjNum();
                if (newPlanProjNum > parentPlanProjNum) {
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
                        .set("work_plan_id", plan.getId())
                        .set("work_plan_no", plan.getProjCode())
                        .set("work_plan_end_time", plan.getEndTime());
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
                        .set("work_plan_id", plan.getId())
                        .set("work_plan_no", plan.getProjCode())
                        .set("work_plan_end_time", plan.getEndTime());
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
    public void exportPlan(MultipartFile file, HttpServletRequest request) throws IOException {
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
                //车间代码判断，并取中文名称
                CommonResult<Branch> result = baseServiceClient.selectBranchByCodeAndTenantId(plan.getInchargeOrg(), plan.getTenantId());
                if (result.getData() == null) {
                    throw new GlobalException("加工车间代码错误:" + plan.getInchargeOrg(), ResultCode.FAILED);
                }
                plan.setInchargeOrgName(result.getData().getBranchName());
                //设置优先级 默认为1
                plan.setPriority("1");
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
                //数量的为空赋值0
                //计划
                plan.setProjNum(StringUtils.isEmpty(plan.getProjNum()) ? 0 : plan.getProjNum());
                //单机
                plan.setSingleNumber(StringUtils.isEmpty(plan.getSingleNumber()) ? 0 : plan.getSingleNumber());
                //总台数
                plan.setTotalNumber(StringUtils.isEmpty(plan.getTotalNumber()) ? 0 : plan.getTotalNumber());
                //生产数量
                plan.setProcessNum(StringUtils.isEmpty(plan.getProcessNum()) ? 0 : plan.getProcessNum());
                plan.setOptNumber(0);
                plan.setOptFinishNumber(0);
                plan.setDeliveryNum(0);
                plan.setStatus(4);//导入默认为未发布状态4
                plan.setSource(2);//导入默认为车间计划
                plan.setMissingNum(StringUtils.isEmpty(plan.getMissingNum()) ? plan.getProjNum() : plan.getMissingNum());
                plan.setStoreNumber(StringUtils.isEmpty(plan.getStoreNumber()) ? 0 : plan.getStoreNumber());
                actionService.saveAction(ActionUtil.buildAction
                        (result.getData().getBranchCode(), "0", "1", "Excel导入计划单号：" + plan.getProjNum(), OperationLogAspect.getIpAddress(request)));
            }
            //保存计划列表
            this.saveBatch(sheetList);
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
    }

    /**
     * 模型车间导入计划
     *
     * @param file
     * @param request
     * @throws IOException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importPlanMX(MultipartFile file, HttpServletRequest request) throws IOException {
        //sheet计划列表
        String[] fieldNames3 = {"productName", "drawNo", "drawNoName", "texture", "priority", "projType", "workNo", "sampleNum", "projNum", "branchCode", "inchargeOrg", "startTime", "endTime", "projectNo"};
        this.importPlan(file, request, fieldNames3);
    }

    /**
     * 锻造车间导入计划
     *
     * @param file
     * @param request
     * @throws IOException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importPlanDZ(MultipartFile file, HttpServletRequest request,String branchCode) throws IOException {
        //sheet计划列表
        String[] fieldNames3 = {"productName","materialName","drawNo", "texture", "priority", "workNo", "pieceWeight", "projectName", "orderNo",
                "projNum",  "inchargeOrgName", "inchargeWorkshopName", "endTime", "planMonth"};
        this.importPlanDZ(file, request, fieldNames3, branchCode);
    }

    /**
     * 铸钢车间导入计划
     *
     * @param file
     * @param request
     * @throws IOException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importPlanZG(MultipartFile file, HttpServletRequest request) throws IOException {
        //sheet计划列表
        String[] fieldNames3 = {"productName", "drawNo", "drawNoName", "texture", "priority", "workNo", "pieceWeight", "steelWaterWeight", "projectName", "orderNo",
                "projNum", "demandTime", "branchCode", "inchargeOrg", "startTime", "endTime", "projectNo"};
        this.importPlan(file, request, fieldNames3);
    }

    /**
     * 冶炼车间导入计划
     *
     * @param file
     * @param request
     * @throws IOException
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importPlanYL(MultipartFile file, HttpServletRequest request) throws IOException {
        //sheet计划列表
        String[] fieldNames3 = {"productName", "drawNo", "drawNoName", "texture", "priority", "workNo", "ingotCase", "projectName", "orderNo",
                "projNum", "demandTime", "branchCode", "inchargeOrg", "startTime", "endTime", "projectNo", "source"};
        this.importPlan(file, request, fieldNames3);
    }

    /**
     * 导入计划(热工个性化)
     *
     * @param file
     * @param request
     * @param fieldNames3
     */
    private void importPlanDZ(MultipartFile file, HttpServletRequest request, String[] fieldNames3,String branchCode) {
        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        try {
            excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());
            file.transferTo(excelFile);
            List<Plan> list3 = ExcelUtils.importExcel(excelFile, Plan.class, fieldNames3, 1, 0, 0, tempName.toString());
            FileUtils.delete(excelFile);
            //sheet1过滤要导入的数据
            List<Plan> sheetList = list3.stream().filter(t -> {
                return !StringUtils.isEmpty(t.getInchargeWorkshopName()) ; //加工车间必填
            }).collect(Collectors.toList());
            TenantUserDetails user = SecurityUtils.getCurrentUser();

            for (Plan plan : sheetList) {
                plan.setTenantId(user.getTenantId());

                if (!ObjectUtil.isEmpty(plan.getDrawNoName()) && plan.getDrawNoName().equals("0")) {
                    plan.setDrawNoName(null);
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
                //数量的为空赋值0
                plan.setProjCode(DateUtils.formatDate(new Date(), "yyyy-MM"));
                plan.setCreateTime(new Date());
                //单机
                plan.setSingleNumber(StringUtils.isEmpty(plan.getSingleNumber()) ? 0 : plan.getSingleNumber());
                //总台数
                plan.setTotalNumber(StringUtils.isEmpty(plan.getTotalNumber()) ? 0 : plan.getTotalNumber());
                //生产数量
                plan.setProcessNum(StringUtils.isEmpty(plan.getProcessNum()) ? 0 : plan.getProcessNum());
                plan.setTrackHeadNumber(0);
                plan.setTrackHeadFinishNumber(0);
                plan.setOptNumber(0);
                plan.setOptFinishNumber(0);
                plan.setDeliveryNum(0);
                plan.setMissingNum(StringUtils.isEmpty(plan.getMissingNum()) ? plan.getProjNum() : plan.getMissingNum());
                plan.setStoreNumber(StringUtils.isEmpty(plan.getStoreNumber()) ? 0 : plan.getStoreNumber());
                plan.setSubmitOrderOrg(branchCode);//提单单位
                plan.setSubmitOrderTime(new Date());//提单时间
                plan.setSource(2);//导入默认为车间计划
                plan.setBranchCode(branchCode);
                plan.setMissingNum(plan.getProjNum());
                plan.setPriority(this.disposePriority(plan.getPriority()));

                this.disposeBranchCode(plan);
                //保存计划
                this.savePlanHot(plan);
                actionService.saveAction(ActionUtil.buildAction
                        (branchCode, "0", "1", "Excel导入计划单号：" + plan.getProjNum(), OperationLogAspect.getIpAddress(request)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
    }

    /**
     * 处理优先级编码
     * @param priority
     * @return
     */
    @Override
    public String disposePriority(String priority) {
        switch (priority) {
            case "低":
                return "0";
            case "一般":
                return "1";
            case "中":
                return "2";
            case "高":
                return "3";

            default:return "3";
        }
    }

    /**
     * 处理车间码
     * @param plan
     */
    private void disposeBranchCode(Plan plan) {
        List<Branch> org = baseServiceClient.selectOrgInner().getData();
        List<Branch> banch = baseServiceClient.selectBranchesInner(null, plan.getInchargeWorkshopName()).getData();
        Map<String, Branch> orgMap = org.stream().collect(Collectors.toMap(x -> x.getBranchName(), x -> x));
        Map<String, Branch> banchMap = banch.stream().collect(Collectors.toMap(x -> x.getBranchName(), x -> x));
        if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(orgMap)){
            Branch branch = orgMap.get(plan.getInchargeOrgName());
            if(ObjectUtils.isNotEmpty(branch)){
                plan.setInchargeOrg(branch.getBranchCode());
            }
        }
        if (com.baomidou.mybatisplus.core.toolkit.CollectionUtils.isNotEmpty(banchMap)){
            Branch branch = banchMap.get(plan.getInchargeWorkshopName());
            if(ObjectUtils.isNotEmpty(branch)){
                plan.setInchargeWorkshop(branch.getBranchCode());
            }
        }
    }
    /**
     * 导入计划(热工个性化)
     *
     * @param file
     * @param request
     * @param fieldNames3
     */
    private void importPlan(MultipartFile file, HttpServletRequest request, String[] fieldNames3) {
        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        try {
            excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());
            file.transferTo(excelFile);
            List<Plan> list3 = ExcelUtils.importExcel(excelFile, Plan.class, fieldNames3, 1, 0, 0, tempName.toString());
            FileUtils.delete(excelFile);
            //sheet1过滤要导入的数据
            List<Plan> sheetList = list3.stream().filter(t -> {
                return !StringUtils.isEmpty(t.getBranchCode())   //部门必填
                        && !StringUtils.isEmpty(t.getInchargeOrg())  //加工车间必填
                        && !StringUtils.isEmpty(t.getEndTime())      //交货期必填
                        && !StringUtils.isEmpty(t.getProjectNo());   //项目号必填
            }).collect(Collectors.toList());
            TenantUserDetails user = SecurityUtils.getCurrentUser();
            for (Plan plan : sheetList) {
                plan.setTenantId(user.getTenantId());
                //车间代码判断，并取中文名称
                CommonResult<Branch> result = baseServiceClient.selectBranchByCodeAndTenantId(plan.getInchargeOrg(), plan.getTenantId());
                if (result.getData() == null) {
                    throw new GlobalException("加工车间代码错误:" + plan.getInchargeOrg(), ResultCode.FAILED);
                }
                if (plan.getSource() != 1 || plan.getSource() != 2) {
                    throw new GlobalException("计划类型错误:" + plan.getSource(), ResultCode.FAILED);
                }
                plan.setInchargeOrgName(result.getData().getBranchName());

                if (!ObjectUtil.isEmpty(plan.getDrawNoName()) && plan.getDrawNoName().equals("0")) {
                    plan.setDrawNoName(null);
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
                //数量的为空赋值0
                plan.setProjCode(DateUtils.formatDate(new Date(), "yyyy-MM"));
                plan.setCreateTime(new Date());
                //单机
                plan.setSingleNumber(StringUtils.isEmpty(plan.getSingleNumber()) ? 0 : plan.getSingleNumber());
                //总台数
                plan.setTotalNumber(StringUtils.isEmpty(plan.getTotalNumber()) ? 0 : plan.getTotalNumber());
                //生产数量
                plan.setProcessNum(StringUtils.isEmpty(plan.getProcessNum()) ? 0 : plan.getProcessNum());
                plan.setTrackHeadNumber(0);
                plan.setTrackHeadFinishNumber(0);
                plan.setOptNumber(0);
                plan.setOptFinishNumber(0);
                plan.setDeliveryNum(0);
                plan.setMissingNum(StringUtils.isEmpty(plan.getMissingNum()) ? plan.getProjNum() : plan.getMissingNum());
                plan.setStoreNumber(StringUtils.isEmpty(plan.getStoreNumber()) ? 0 : plan.getStoreNumber());
                plan.setSubmitOrderOrg(plan.getBranchCode());//提单单位
                plan.setSubmitOrderTime(new Date());//提单时间
                plan.setSource(2);//导入默认为车间计划
                plan.setMissingNum(plan.getProjNum());
                //保存计划
                this.savePlanHot(plan);
                actionService.saveAction(ActionUtil.buildAction
                        (result.getData().getBranchCode(), "0", "1", "Excel导入计划单号：" + plan.getProjNum(), OperationLogAspect.getIpAddress(request)));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException(e.getMessage(), ResultCode.FAILED);
        }
    }

    /**
     * 热工各个车间导入计划入库操作以及扩展字段的入库
     *
     * @param plan
     * @return
     */
    public CommonResult<Object> savePlanHot(Plan plan) {
        checkPlan(plan);
        boolean f = this.save(plan);
        //this.planData(plan.getId());
        PlanExtend newplanExtend = new PlanExtend();
        BeanUtils.copyProperties(plan, newplanExtend);
        newplanExtend.setPlanId(plan.getId());
        planExtendMapper.insert(newplanExtend);
        return CommonResult.success(f);
    }


    @Override
    public void planPackageRouter(List<Plan> planList) {
        String branchCode = null;
        String drawNos = "";
        for (Plan plan : planList) {
            if (!drawNos.contains(plan.getDrawNo())) {
                drawNos += "," + plan.getDrawNo();
            }
            branchCode = plan.getBranchCode();
            plan.setProcessStatus(0);
        }
        if (!StrUtil.isBlank(drawNos)) {
            CommonResult<List<Router>> result = baseServiceClient.getByRouterNos(drawNos.substring(1), branchCode);
            if (result != null && result.getData() != null) {
                for (Plan plan : planList) {
                    for (Router router : result.getData()) {
                        if (DrawingNoUtil.drawingNo(plan.getDrawNo()).equals(DrawingNoUtil.drawingNo(router.getRouterNo()))) {
                            plan.setProcessStatus(1);
                        }
                    }
                }
            }
        }
    }

    @Override
    public void planPackageExtend(List<Plan> planList) {
        //判空
        if (CollectionUtils.isEmpty(planList)) {
            return;
        }
        List<String> planIdList = planList.stream().map(x -> x.getId()).collect(Collectors.toList());

        QueryWrapper<PlanExtend> queryWrapper = new QueryWrapper();
        queryWrapper.in("plan_id", planIdList);
        //根据id查扩展表信息
        List<PlanExtend> planExtends = planExtendMapper.selectList(queryWrapper);
        //判空
        if (CollectionUtils.isEmpty(planExtends)) {
            return;
        }
        Map<String, PlanExtend> extendMap = planExtends.stream().collect(Collectors.toMap(x -> x.getPlanId(), x -> x));
        for (Plan plan : planList) {
            PlanExtend planExtend = extendMap.get(plan.getId());
            if (!ObjectUtil.isEmpty(planExtend)) {
                BeanUtils.copyProperties(planExtend, plan, "id");
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

    /**
     * 发布计划
     *
     * @param planIdList
     * @return
     */
    @Override
    public CommonResult publish(List<String> planIdList) {
        UpdateWrapper<Plan> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("status", 0);//设置为未开始状态
        updateWrapper.in("status", 4);
        updateWrapper.in("id", planIdList);
        this.update(updateWrapper);
        return new CommonResult(ResultCode.SUCCESS);
    }
}
