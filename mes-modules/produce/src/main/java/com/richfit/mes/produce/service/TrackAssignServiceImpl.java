package com.richfit.mes.produce.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.produce.forg.ForgHour;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.*;
import com.richfit.mes.produce.entity.ForDispatchingDto;
import com.richfit.mes.produce.entity.KittingVo;
import com.richfit.mes.produce.entity.QueryProcessVo;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.forg.ForgHourService;
import com.richfit.mes.produce.service.heat.PrechargeFurnaceService;
import com.richfit.mes.produce.service.quality.InspectionPowerService;
import com.richfit.mes.produce.utils.DateUtils;
import com.richfit.mes.produce.utils.ProcessFiltrationUtil;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author 马峰
 * @Description 跟单派工服务
 */
@Service
public class
TrackAssignServiceImpl extends ServiceImpl<TrackAssignMapper, Assign> implements TrackAssignService {

    @Autowired
    public TrackAssignMapper trackAssignMapper;
    @Resource
    public TrackItemService trackItemService;
    @Resource
    public TrackHeadService trackHeadService;
    @Resource
    public PlanService planService;
    @Resource
    private BaseServiceClient baseServiceClient;
    @Resource
    private LineStoreMapper lineStoreMapper;
    @Resource
    private TrackAssemblyService trackAssembleService;
    @Resource
    private ProduceRoleOperationService roleOperationService;
    @Resource
    private SystemServiceClient systemServiceClient;
    @Resource
    private TrackAssignPersonService trackAssignPersonService;
    @Autowired
    private TrackCompleteService trackCompleteService;
    @Autowired
    private TrackCompleteMapper trackCompleteMapper;
    @Autowired
    private PrechargeFurnaceService prechargeFurnaceService;
    @Autowired
    private InspectionPowerService inspectionPowerService;
    @Autowired
    private TrackAssignService trackAssignService;

    @Resource
    private TrackHeadFlowService trackFlowService;

    @Autowired
    private RecordsOfPourOperationsMapper recordsOfPourOperationsMapper;
    @Autowired
    private PrechargeFurnaceAssignService prechargeFurnaceAssignService;


    @Override
    public IPage<TrackItem> getPageAssignsByStatus(Page page, QueryWrapper<TrackItem> qw, String orderCol, String order, List<String> excludeOrderCols) {
        IPage<TrackItem> pageAssignsByStatus = trackAssignMapper.getPageAssignsByStatus(page, qw);
        if (null != pageAssignsByStatus.getRecords()) {
            for (TrackItem trackItem : pageAssignsByStatus.getRecords()) {
                TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
                trackItem.setRouterId(trackHead.getRouterId());
                trackItem.setWeight(trackHead.getWeight());
                //工作号
                trackItem.setWorkNo(trackHead.getWorkNo());
                trackItem.setProductName(trackHead.getProductName());
                trackItem.setPartsName(trackHead.getMaterialName());
                //工艺版本
                trackItem.setRouterVer(trackHead.getRouterVer());
                trackItem.setTotalQuantity(trackItem.getNumber());
                trackItem.setWorkPlanNo(trackHead.getWorkPlanNo());
                trackItem.setDispatchingNumber(trackItem.getAssignableQty());
//                String version = baseServiceClient.queryCraft(trackItem.getOptName(), trackItem.getBranchCode());
//                if (!StringUtils.isNullOrEmpty(version)) {
//                    trackItem.setVersions(version);
//                }
            }
            //排序
            orderByCol(orderCol, order, excludeOrderCols, pageAssignsByStatus);
        }
        return pageAssignsByStatus;
    }

    @Override
    public IPage<TrackItem> getPageAssignsHot(Page page, QueryWrapper<TrackItem> qw) {
        return trackAssignMapper.getPageAssignsHot(page, qw);
    }

    @Override
    public List<TrackItem> getAssignsHot(QueryWrapper<TrackItem> qw) {
        return trackAssignMapper.getPageAssignsHot(qw);
    }


    @Override
    public IPage<TrackItem> getPageAssignsByStatusAndTrack(Page page, @Param("name") String name, QueryWrapper<TrackItem> qw, String orderCol, String order, List<String> excludeOrderCols) {
        //进行sql拼接
        name = name.replaceAll(" ", "");
        IPage<TrackItem> trackItemList = trackAssignMapper.getPageAssignsByStatusAndTrack(page, name, qw);
        if (null != trackItemList.getRecords()) {
            for (TrackItem trackItem : trackItemList.getRecords()) {
                TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
                trackItem.setRouterId(trackHead.getRouterId());
                trackItem.setWeight(trackHead.getWeight());
                trackItem.setWorkNo(trackHead.getWorkNo());
                trackItem.setProductName(trackHead.getProductName());
                trackItem.setPartsName(trackHead.getMaterialName());
                //工艺版本
                trackItem.setRouterVer(trackHead.getRouterVer());
                trackItem.setTotalQuantity(trackItem.getNumber());
                trackItem.setDispatchingNumber(trackItem.getAssignableQty());
                trackItem.setWorkPlanNo(trackHead.getWorkPlanNo());
                String version = baseServiceClient.queryCraft(trackItem.getOptName(), trackItem.getBranchCode());
                if (!StringUtils.isNullOrEmpty(version)) {
                    trackItem.setVersions(version);
                }
            }
            //排序
            orderByCol(orderCol, order, excludeOrderCols, trackItemList);
        }
        return trackItemList;
    }

    @Override
    public IPage<TrackItem> getPageAssignsByStatusAndRouter(Page page, @Param("name") String name, QueryWrapper<TrackItem> qw, String orderCol, String order, List<String> excludeOrderCols) {
        IPage<TrackItem> trackItemList = trackAssignMapper.getPageAssignsByStatusAndRouter(page, name, qw);
        if (null != trackItemList.getRecords()) {
            for (TrackItem trackItem : trackItemList.getRecords()) {
                TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
                trackItem.setRouterId(trackHead.getRouterId());
                trackItem.setWeight(trackHead.getWeight());
                trackItem.setWorkNo(trackHead.getWorkNo());
                trackItem.setProductName(trackHead.getProductName());
                trackItem.setPartsName(trackHead.getMaterialName());
                trackItem.setTotalQuantity(trackItem.getNumber());
                trackItem.setDispatchingNumber(trackItem.getAssignableQty());
                trackItem.setWorkPlanNo(trackHead.getWorkPlanNo());
                String version = baseServiceClient.queryCraft(trackItem.getOptName(), trackItem.getBranchCode());
                if (!StringUtils.isNullOrEmpty(version)) {
                    trackItem.setVersions(version);
                }
            }
            //排序
            orderByCol(orderCol, order, excludeOrderCols, trackItemList);
        }
        return trackItemList;
    }

    /**
     * 跟单派工排序
     *
     * @param orderCol            排序字段
     * @param order               排序类型
     * @param excludeOrderCols    排序的字段
     * @param pageAssignsByStatus 排序的集合
     */
    private void orderByCol(String orderCol, String order, List<String> excludeOrderCols, IPage<TrackItem> pageAssignsByStatus) {
        if (!StringUtils.isNullOrEmpty(orderCol) && excludeOrderCols.contains(orderCol)) {
            if ("workNo".equals(orderCol)) {
                if ("desc".equals(order)) {
                    pageAssignsByStatus.getRecords().sort(Comparator.nullsFirst(Comparator.comparing(TrackItem::getWorkNo, Comparator.nullsLast(String::compareTo))).reversed());
                } else {
                    pageAssignsByStatus.getRecords().sort(Comparator.nullsFirst(Comparator.comparing(TrackItem::getWorkNo, Comparator.nullsLast(String::compareTo))));
                }
            }
            if ("versions".equals(orderCol)) {
                if ("desc".equals(order)) {
                    pageAssignsByStatus.getRecords().sort(Comparator.nullsFirst(Comparator.comparing(TrackItem::getVersions, Comparator.nullsLast(String::compareTo))).reversed());
                } else {
                    pageAssignsByStatus.getRecords().sort(Comparator.nullsFirst(Comparator.comparing(TrackItem::getVersions, Comparator.nullsLast(String::compareTo))));
                }
            }
            if ("totalQuantity".equals(orderCol)) {
                if ("desc".equals(order)) {
                    pageAssignsByStatus.getRecords().sort(Comparator.nullsFirst(Comparator.comparing(TrackItem::getTotalQuantity, Comparator.nullsLast(Integer::compareTo))).reversed());
                } else {
                    pageAssignsByStatus.getRecords().sort(Comparator.nullsFirst(Comparator.comparing(TrackItem::getTotalQuantity, Comparator.nullsLast(Integer::compareTo))));
                }
            }
            if ("dispatchingNumber".equals(orderCol)) {
                if ("desc".equals(order)) {
                    pageAssignsByStatus.getRecords().sort(Comparator.nullsFirst(Comparator.comparing(TrackItem::getDispatchingNumber, Comparator.nullsLast(Integer::compareTo))).reversed());
                } else {
                    pageAssignsByStatus.getRecords().sort(Comparator.nullsFirst(Comparator.comparing(TrackItem::getDispatchingNumber, Comparator.nullsLast(Integer::compareTo))));
                }
            }

        }
    }


    @Resource
    private TrackAssignPersonMapper trackAssignPersonMapper;


    @Override
    public IPage<Assign> queryPage(Page page, String siteId, String trackNo, String routerNo, String startTime, String endTime, String state, String userId, String branchCode, String productNo, String classes, String order, String orderCol, String holdStatus) throws ParseException {
        QueryWrapper<Assign> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(trackNo)) {
            trackNo = trackNo.replaceAll(" ", "");
            queryWrapper.apply("replace(replace(replace(u.track_no2, char(13), ''), char(10), ''),' ', '') like '%" + trackNo + "%'");
        }
        if (!StringUtils.isNullOrEmpty(routerNo)) {
            DrawingNoUtil.queryLike(queryWrapper, "u.drawing_no", routerNo);
        }
        if (!StringUtils.isNullOrEmpty(siteId)) {
            queryWrapper.like("u.assign_by", siteId);
        }
        if (!StringUtils.isNullOrEmpty(productNo)) {
            queryWrapper.like("u.product_no", productNo);
        }
        if (!StringUtils.isNullOrEmpty(holdStatus)) {
            if (holdStatus.equals("1")) {
                queryWrapper.le("u.hold_finished_time", new Date());
            } else if (holdStatus.equals("2")) {
                queryWrapper.ge("u.hold_finished_time", new Date());
            }
        }
        if (StrUtil.isNotBlank(userId)) {
            queryWrapper.and(wrapper -> wrapper.like("u.user_id", userId).or().or(wrapper1 -> wrapper1.like("u.user_id", "/")));
        }

        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryWrapper.apply("UNIX_TIMESTAMP(u.assign_time) >= UNIX_TIMESTAMP('" + startTime + " ')");
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            Calendar calendar = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            calendar.setTime(sdf.parse(endTime));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            queryWrapper.apply("UNIX_TIMESTAMP(u.assign_time) <= UNIX_TIMESTAMP('" + sdf.format(calendar.getTime()) + " 00:00:00')");
        }
        if ("0,1".equals(state)) {
            queryWrapper.in("u.state", 0, 1);
        }
        if ("2".equals(state)) {
            queryWrapper.in("u.state", 2);
        }
        //增加工序过滤（新要求，屏蔽）
//        ProcessFiltrationUtil.filtration(queryWrapper, systemServiceClient, roleOperationService);
//        queryWrapper.eq("u.classes", classes);
//        queryWrapper.eq("u.branch_code", branchCode);
        queryWrapper.eq("u.tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        //queryWrapper.eq("site_id",SecurityUtils.getCurrentUser().getBelongOrgId());
        //queryWrapper.apply("FIND_IN_SET('" + SecurityUtils.getCurrentUser().getBelongOrgId() + "',u.site_id)");
        //根据classes判断  对下料、锻造、去氢、正火进行查询控制
        if ("4".equals(classes) || "6".equals(classes) || "7".equals(classes)) {
            queryWrapper.ne("opt_type", "14")
                    .ne("opt_type", "13");
        }
        if (!StringUtils.isNullOrEmpty(orderCol)) {
            if (!StringUtils.isNullOrEmpty(order)) {
                if ("desc".equals(order)) {
                    queryWrapper.orderByDesc("u." + StrUtil.toUnderlineCase(orderCol));
                } else if ("asc".equals(order)) {
                    queryWrapper.orderByAsc("u." + StrUtil.toUnderlineCase(orderCol));
                }
            } else {
                queryWrapper.orderByDesc("u." + StrUtil.toUnderlineCase(orderCol));
            }
        } else {
            queryWrapper.orderByDesc("u.modify_time");
        }
        IPage<Assign> queryPage = trackAssignMapper.queryPageNew(page, queryWrapper);
        //工艺ids
        List<String> routerIdAndBranchCodeList = new ArrayList<>(queryPage.getRecords().stream().map(item -> item.getRouterId()+"_"+item.getBranchCode()).collect(Collectors.toSet()));
        List<Router> getRouter = baseServiceClient.getRouterByIdAndBranchCode(routerIdAndBranchCodeList).getData();

        if (null != queryPage.getRecords()) {

            for (Assign assign : queryPage.getRecords()) {
                if(!CollectionUtil.isEmpty(getRouter)){
                    Map<String, Router> routerMap = getRouter.stream().collect(Collectors.toMap(item -> item.getId()+"_"+item.getBranchCode(), Function.identity()));
                    Router router = routerMap.get(assign.getRouterId()+"_"+assign.getBranchCode());
                    //下料规格
                    assign.setBlankSpecifi(router.getBlankSpecifi());
                    //锻造下料重量
                    assign.setBlankWeight(router.getBlankWeight());
                }

                TrackHead trackHead = trackHeadService.getById(assign.getTrackId());
                TrackItem trackItem = trackItemService.getById(assign.getTiId());
                if (!StringUtils.isNullOrEmpty(trackHead.getRouterId())) {
                    assign.setRouterId(trackHead.getRouterId());
                }
                assign.setOptId(trackItem.getOptId());
                assign.setWeight(trackHead.getWeight());
                assign.setWorkNo(trackHead.getWorkNo());
                assign.setProductName(trackHead.getProductName());
                assign.setTotalQuantity(trackItem.getNumber());
                assign.setDispatchingNumber(trackItem.getAssignableQty());
                assign.setWorkPlanNo(trackHead.getWorkPlanNo());
                assign.setPartsName(trackHead.getMaterialName());
                this.disposeHoldFinishedTime(assign, trackItem);
                assign.setAssignPersons(trackAssignPersonMapper.selectList(new QueryWrapper<AssignPerson>().eq("assign_id", assign.getId())));

            }
        }
        return queryPage;
    }

    /**
     * 处理保温状态
     *
     * @param assign
     * @param trackItem
     */
    private void disposeHoldFinishedTime(Assign assign, TrackItem trackItem) {
        Long prechargeFurnaceId = trackItem.getPrechargeFurnaceId();
        if (ObjectUtil.isNotEmpty(prechargeFurnaceId)) {
            QueryWrapper<RecordsOfPourOperations> wrapper = new QueryWrapper<>();
            wrapper.eq("precharge_furnace_id", prechargeFurnaceId);
            List<RecordsOfPourOperations> recordsOfPourOperations = recordsOfPourOperationsMapper.selectList(wrapper);
            if (CollectionUtils.isNotEmpty(recordsOfPourOperations)) {
                //浇注时间赋值
                assign.setPourTime(recordsOfPourOperations.get(0).getPourTime());
            }
        }
        if (!StringUtils.isNullOrEmpty(assign.getHoldFinishedTime())) {
            int i = DateUtils.compareDateTime(DateUtils.parseDate(assign.getHoldFinishedTime(), "yyyy-MM-dd HH:mm:ss"), new Date());
            if (i < 0) {
                assign.setHoldFinishedTime("保温结束");
            } else if (i > 0) {
                assign.setHoldFinishedTime("保温中");
            }
        }
    }

    @Override
    public List<QueryProcessVo> queryProcessList(String flowId) {
        List<QueryProcessVo> processList = trackAssignMapper.queryProcessList(flowId);
        if (processList == null || processList.isEmpty()) {
            return Collections.emptyList();
        }
        for (QueryProcessVo queryProcess : processList) {
            StringBuffer stringBuffer = new StringBuffer();
            switch (queryProcess.getIsDoing()) {
//                case 0:
//                    stringBuffer.append("未开工");
//                    break;
                case 1:
                    stringBuffer.append("已开工");
                    break;
                case 2:
                    stringBuffer.append("已完工");
                    break;
                default:
                    stringBuffer.append("未开工");
                    break;
            }
            queryProcess.setOptState(stringBuffer.toString());
            List<Assign> assignList = trackAssignMapper.isDispatching(queryProcess.getId());
            boolean state = false;
            for (Assign assign : assignList) {
                state = assign.getState() == 2;
            }
            if (state) {
                queryProcess.setIsDispatching("是");
            } else {
                queryProcess.setIsDispatching("否");
            }
        }
        return processList;
    }

    @Override
    public boolean updateProcess(Assign assign) {
        return this.updateById(assign);
    }

    @Override
    public List<KittingVo> kittingExamine(String trackHeadId) {
        List<KittingVo> kittingList = new ArrayList<>();
        if (!StringUtils.isNullOrEmpty(trackHeadId)) {
            TrackHead trackHead = trackHeadService.getById(trackHeadId);
            if (null != trackHead && !StringUtils.isNullOrEmpty(trackHead.getProjectBomId())) {
                QueryWrapper<TrackAssembly> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("track_head_id", trackHeadId);
                queryWrapper.eq("grade", "L");
                List<TrackAssembly> list = trackAssembleService.list(queryWrapper);
                for (TrackAssembly trackAssembly : list) {
                    Integer number = lineStoreMapper.selectTotalNum(
                            trackAssembly.getMaterialNo(), trackHead.getBranchCode(), trackHead.getTenantId());
                    if (null == number) {
                        number = 0;
                    }
                    KittingVo kitting = new KittingVo();
                    kitting.setMaterialNo(trackAssembly.getMaterialNo());
                    kitting.setDrawingNo(trackAssembly.getDrawingNo());
                    kitting.setMaterialName(trackAssembly.getName());
                    kitting.setUnitNumber(trackAssembly.getNumber());
                    kitting.setNeedUnitNumber(1);
                    kitting.setInventory(number);
                    kitting.setIsEdgeStore(trackAssembly.getIsEdgeStore());
                    kitting.setIsKeyPart(trackAssembly.getIsKeyPart());
                    kitting.setIsNeedPicking(trackAssembly.getIsNeedPicking());
                    kitting.setSurplusNumber(number - trackAssembly.getNumber());
                    if (number - trackAssembly.getNumber() >= 0) {
                        kitting.setIsKitting(1);
                    } else {
                        kitting.setIsKitting(0);
                    }
                    kittingList.add(kitting);
                }
            }
        }
        return kittingList;
    }


    /**
     * 功能描述: 按类型获取待派工跟单
     *
     * @Author: mafeng
     * @Date: 2022/7/26 09:00
     * @return: IPage<TrackHead>
     **/
    @Override
    public IPage<TrackHead> getPageTrackHeadByType(Page page, QueryWrapper<TrackHead> qw) {
        IPage<TrackHead> list = trackAssignMapper.getPageTrackHeadByType(page, qw);
        return list;
    }

    /**
     * 功能描述: 开工修改相应状态
     * 派工表状态变更开工
     * 工序表状态变更开工
     * 跟单表状态变更开工
     * 跟单flow表状态变更开工
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> startWorking(List<String> assignIdList) {
        try {
            for (String assignId : assignIdList) {
                Assign assign = this.getById(assignId);
                assign.setState(1);
                this.updateById(assign);
                TrackItem trackItem = trackItemService.getById(assign.getTiId());
                trackItem.setIsDoing(1);
                trackItem.setStartDoingTime(new Date());
                trackItem.setStartDoingUser(SecurityUtils.getCurrentUser().getUsername());
                trackItemService.updateById(trackItem);
                UpdateWrapper<TrackHead> updateHeda = new UpdateWrapper<>();
                updateHeda.eq("id", trackItem.getTrackHeadId());
                updateHeda.set("status", 1);
                trackHeadService.update(updateHeda);
                UpdateWrapper<TrackFlow> updateFlow = new UpdateWrapper<>();
                updateFlow.eq("id", trackItem.getFlowId());
                updateFlow.set("status", 1);
                trackFlowService.update(updateFlow);
            }
        } catch (Exception e) {
            throw new GlobalException("开工失败,请重试", ResultCode.FAILED);
        }
        return CommonResult.success(true);
    }

    @Override
    public Integer queryDispatchingNumber(String branchCode) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        //增加工序过滤
        ProcessFiltrationUtil.filtration(queryWrapper, systemServiceClient, roleOperationService);
        queryWrapper.eq("is_current", 1);
        queryWrapper.eq("branch_code", branchCode);
        return trackAssignMapper.queryDispatchingNumber(queryWrapper);
    }

    @Override
    public Integer workNumber(String branchCode) {
        QueryWrapper<Assign> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("state", 0, 1);
        queryWrapper.likeRight("user_id", SecurityUtils.getCurrentUser().getUsername() + ",");
        queryWrapper.eq("branch_code", branchCode);
        return this.count(queryWrapper);
    }

    @Override
    public IPage<Assign> queryForDispatching(ForDispatchingDto dispatchingDto) throws ParseException {
        QueryWrapper<Assign> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getTrackNo())) {
            dispatchingDto.setTrackNo(dispatchingDto.getTrackNo().replaceAll(" ", ""));
            queryWrapper.apply("replace(replace(replace(u.track_no2, char(13), ''), char(10), ''),' ', '') like '%" + dispatchingDto.getTrackNo() + "%'");
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getRouterNo())) {
            DrawingNoUtil.queryLike(queryWrapper, "u.drawing_no", dispatchingDto.getRouterNo());
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getSiteId())) {
            queryWrapper.like("u.assign_by", dispatchingDto.getSiteId());
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getProductNo())) {
            queryWrapper.like("u.product_no", dispatchingDto.getProductNo());
        }
        if (!StrUtil.isBlank(dispatchingDto.getUserId())) {
            queryWrapper.likeRight("u.user_id", dispatchingDto.getUserId() + ",");
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getStartTime())) {
            queryWrapper.apply("UNIX_TIMESTAMP(u.assign_time) >= UNIX_TIMESTAMP('" + dispatchingDto.getStartTime() + " ')");
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getEndTime())) {
            Calendar calendar = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            calendar.setTime(sdf.parse(dispatchingDto.getEndTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            queryWrapper.apply("UNIX_TIMESTAMP(u.assign_time) <= UNIX_TIMESTAMP('" + sdf.format(calendar.getTime()) + " 00:00:00')");
        }
        if (StrUtil.isNotBlank(dispatchingDto.getState())) {
            queryWrapper.in("u.state", dispatchingDto.getState());
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getWorkNo())) {
            queryWrapper.inSql("u.id", "select id from produce_assign where track_id in (select id from produce_track_head where tenant_id = '" + SecurityUtils.getCurrentUser().getTenantId() + "' and work_no = '" + dispatchingDto.getWorkNo() + "')");
        }
        //增加工序过滤
        ProcessFiltrationUtil.filtration(queryWrapper, systemServiceClient, roleOperationService);
        queryWrapper.eq("u.classes", dispatchingDto.getClasses());
        queryWrapper.eq("u.branch_code", dispatchingDto.getBranchCode());
        queryWrapper.eq("u.tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        OrderUtil.query(queryWrapper, dispatchingDto.getOrderCol(), dispatchingDto.getOrder());
        IPage<Assign> queryPage = trackAssignMapper.queryPageNew(new Page(dispatchingDto.getPage(), dispatchingDto.getLimit()), queryWrapper);
        if (null != queryPage.getRecords()) {
            for (Assign assign : queryPage.getRecords()) {
                TrackHead trackHead = trackHeadService.getById(assign.getTrackId());
                TrackItem trackItem = trackItemService.getById(assign.getTiId());
                if (!StringUtils.isNullOrEmpty(trackHead.getRouterId())) {
                    assign.setRouterId(trackHead.getRouterId());
                }
                assign.setOptId(trackItem.getOptId());
                assign.setWeight(trackHead.getWeight());
                assign.setWorkNo(trackHead.getWorkNo());
                assign.setProductName(trackHead.getProductName());
                assign.setTotalQuantity(trackItem.getNumber());
                assign.setDispatchingNumber(trackItem.getAssignableQty());
                assign.setWorkPlanNo(trackHead.getWorkPlanNo());
                //添加 派工人员返回
                QueryWrapper<AssignPerson> assignPersonQueryWrapper = new QueryWrapper<>();
                assignPersonQueryWrapper.eq("assign_id", assign.getId());
                assign.setAssignPersons(trackAssignPersonService.list(assignPersonQueryWrapper));
                assign.setTypeName(trackItem.getTypeName());
                assign.setTypeCode(trackItem.getTypeCode());
            }
        }
        return queryPage;
    }

    @Override
    public IPage<Assign> queryNotAtWork(ForDispatchingDto dispatchingDto) throws ParseException {
        QueryWrapper<Assign> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getTrackNo())) {
            dispatchingDto.setTrackNo(dispatchingDto.getTrackNo().replaceAll(" ", ""));
            queryWrapper.apply("replace(replace(replace(u.track_no2, char(13), ''), char(10), ''),' ', '') like '%" + dispatchingDto.getTrackNo() + "%'");
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getRouterNo())) {
            DrawingNoUtil.queryLike(queryWrapper, "u.drawing_no", dispatchingDto.getRouterNo());
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getSiteId())) {
            queryWrapper.like("u.assign_by", dispatchingDto.getSiteId());
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getProductNo())) {
            queryWrapper.like("u.product_no", dispatchingDto.getProductNo());
        }
        if (!StrUtil.isBlank(dispatchingDto.getUserId())) {
            queryWrapper.likeRight("u.user_id", dispatchingDto.getUserId() + ",");
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getStartTime())) {
            queryWrapper.apply("UNIX_TIMESTAMP(u.assign_time) >= UNIX_TIMESTAMP('" + dispatchingDto.getStartTime() + " ')");
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getEndTime())) {
            Calendar calendar = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            calendar.setTime(sdf.parse(dispatchingDto.getEndTime()));
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            queryWrapper.apply("UNIX_TIMESTAMP(u.assign_time) <= UNIX_TIMESTAMP('" + sdf.format(calendar.getTime()) + " 00:00:00')");
        }
        if ("0,1".equals(dispatchingDto.getState())) {
            queryWrapper.in("u.state", 0, 1);
        }
        if ("2".equals(dispatchingDto.getState())) {
            queryWrapper.in("u.state", 2);
        }
        //增加工序过滤
        ProcessFiltrationUtil.filtration(queryWrapper, systemServiceClient, roleOperationService);
        queryWrapper.eq("u.classes", dispatchingDto.getClasses());
        queryWrapper.eq("u.branch_code", dispatchingDto.getBranchCode());
        queryWrapper.eq("u.tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        OrderUtil.query(queryWrapper, dispatchingDto.getOrderCol(), dispatchingDto.getOrder());
        IPage<Assign> queryPage = trackAssignMapper.queryPageNew(new Page(dispatchingDto.getPage(), dispatchingDto.getLimit()), queryWrapper);
        if (null != queryPage.getRecords()) {
            for (Assign assign : queryPage.getRecords()) {
                TrackHead trackHead = trackHeadService.getById(assign.getTrackId());
                TrackItem trackItem = trackItemService.getById(assign.getTiId());
                if (!StringUtils.isNullOrEmpty(trackHead.getRouterId())) {
                    assign.setRouterId(trackHead.getRouterId());
                }
                assign.setOptId(trackItem.getOptId());
                assign.setWeight(trackHead.getWeight());
                assign.setWorkNo(trackHead.getWorkNo());
                assign.setProductName(trackHead.getProductName());
                assign.setTotalQuantity(trackItem.getNumber());
                assign.setDispatchingNumber(trackItem.getAssignableQty());
                assign.setWorkPlanNo(trackHead.getWorkPlanNo());
            }
        }
        return queryPage;
    }


    /**
     * 铸钢已派工列表查询
     *
     * @param dispatchingDto
     * @return
     * @throws ParseException
     */
    @Override
    public IPage<AssignHot> queryDispatched(ForDispatchingDto dispatchingDto) {
        QueryWrapper<AssignHot> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getTrackNo())) {
            dispatchingDto.setTrackNo(dispatchingDto.getTrackNo().replaceAll(" ", ""));
            queryWrapper.apply("replace(replace(replace(track_no, char(13), ''), char(10), ''),' ', '') like '%" + dispatchingDto.getTrackNo() + "%'");
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getRouterNo())) {
            DrawingNoUtil.queryLike(queryWrapper, "drawing_no", dispatchingDto.getRouterNo());
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getSiteId())) {
            queryWrapper.like("assign_by", dispatchingDto.getSiteId());
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getProductNo())) {
            queryWrapper.like("product_no", dispatchingDto.getProductNo());
        }
        if (!StrUtil.isBlank(dispatchingDto.getUserId())) {
            queryWrapper.likeRight("user_id", dispatchingDto.getUserId() + ",");
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getStartTime())) {
            queryWrapper.ge(!StringUtils.isNullOrEmpty(dispatchingDto.getStartTime()), "date_format(assign_time, '%Y-%m-%d')", dispatchingDto.getStartTime());
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getEndTime())) {
            queryWrapper.le(!StringUtils.isNullOrEmpty(dispatchingDto.getEndTime()), "date_format(assign_time, '%Y-%m-%d')", dispatchingDto.getEndTime());
        }
        if (StrUtil.isNotBlank(dispatchingDto.getState())) {
            queryWrapper.in("state", dispatchingDto.getState());
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getWorkNo())) {
            queryWrapper.eq("work_no", dispatchingDto.getWorkNo());
        }
        if (!StringUtils.isNullOrEmpty(dispatchingDto.getMaterialName())) {
            queryWrapper.eq("material_name", dispatchingDto.getMaterialName());
        }
        //增加工序过滤
        ProcessFiltrationUtil.filtration(queryWrapper, systemServiceClient, roleOperationService);
        queryWrapper.eq("classes", dispatchingDto.getClasses());
        queryWrapper.eq("branch_code", dispatchingDto.getBranchCode());
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        OrderUtil.query(queryWrapper, dispatchingDto.getOrderCol(), dispatchingDto.getOrder());
        IPage<AssignHot> queryPage = trackAssignMapper.queryPageAssignTrackStoreHot(new Page(dispatchingDto.getPage(), dispatchingDto.getLimit()), queryWrapper);
        if (null != queryPage.getRecords()) {
            for (AssignHot assign : queryPage.getRecords()) {
                //添加 派工人员返回
                QueryWrapper<AssignPerson> assignPersonQueryWrapper = new QueryWrapper<>();
                assignPersonQueryWrapper.eq("assign_id", assign.getId());
                assign.setAssignPersons(trackAssignPersonService.list(assignPersonQueryWrapper));
            }
        }
        return queryPage;
    }

    @Override
    public List<Assign> find(String id, String tiId, String state, String trackId, String trackNo, String flowId) {
        QueryWrapper<Assign> queryWrapper = new QueryWrapper<Assign>();
        if (!StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (!StringUtils.isNullOrEmpty(tiId)) {
            queryWrapper.eq("ti_id", tiId);
        }
        if (!StringUtils.isNullOrEmpty(state)) {
            queryWrapper.eq("state", Integer.parseInt(state));
        }
        if (!StringUtils.isNullOrEmpty(trackId)) {
            queryWrapper.eq("track_id", trackId);
        }
        if (!StringUtils.isNullOrEmpty(trackNo)) {
            queryWrapper.eq("track_no", trackNo);
        }
        if (!StringUtils.isNullOrEmpty(flowId)) {
            queryWrapper.eq("flow_id", flowId);
        }
        queryWrapper.orderByAsc("modify_time");
        List<Assign> result = trackAssignService.list(queryWrapper);
        return result;
    }

    @Override
    public boolean deleteAssign(String[] ids) {
        for (int i = 0; i < ids.length; i++) {
            Assign assign = this.getById(ids[i]);
            TrackItem trackItem = trackItemService.getById(assign.getTiId());
            if (null == trackItem) {
                this.removeById(ids[i]);
            } else {
                if (trackItem.getIsExistQualityCheck() == 1 && trackItem.getIsQualityComplete() == 1) {
                    throw new GlobalException("跟单工序【" + trackItem.getOptName() + "】已质检完成，报工无法取消！", ResultCode.FAILED);
                }
                if (trackItem.getIsExistScheduleCheck() == 1 && trackItem.getIsScheduleComplete() == 1) {
                    throw new GlobalException("跟单工序【" + trackItem.getOptName() + "】已调度完成，报工无法取消！", ResultCode.FAILED);
                }
                List<Assign> ca = this.find(null, null, null, null, null, trackItem.getFlowId());
                for (int j = 0; j < ca.size(); j++) {
                    TrackItem cstrackItem = trackItemService.getById(ca.get(j).getTiId());
                    if (cstrackItem.getOptSequence() > trackItem.getOptSequence()) {
                        throw new GlobalException("无法回滚，需要先取消后序工序【" + cstrackItem.getOptName() + "】的派工", ResultCode.FAILED);
                    }
                }
                QueryWrapper<TrackComplete> queryWrapper = new QueryWrapper<TrackComplete>();
                queryWrapper.eq("ti_id", assign.getTiId());
                List<TrackComplete> cs = trackCompleteService.list(queryWrapper);
                if (cs.size() > 0) {
                    throw new GlobalException("无法回滚，已有报工提交，需要先取消工序【" + trackItem.getOptName() + "】的报工！", ResultCode.FAILED);
                }
                //将前置工序状态改为待派工
                List<TrackItem> items = trackItemService.list(new QueryWrapper<TrackItem>().eq("flow_id", trackItem.getFlowId()).orderByAsc("opt_sequence"));
                for (int j = 0; j < items.size(); j++) {
                    TrackItem cstrackItem = items.get(j);
                    if (cstrackItem.getOptSequence() > trackItem.getOptSequence()) {
                        cstrackItem.setIsCurrent(0);
                        cstrackItem.setIsDoing(0);
                        trackItemService.updateById(cstrackItem);
                    }
                }
                trackItem.setIsCurrent(1);
                trackItem.setIsDoing(0);
                trackItem.setIsSchedule(0);
                trackItem.setAssignableQty(trackItem.getNumber());
                trackItemService.updateById(trackItem);
                this.removeById(ids[i]);
                //如果是探伤工序，删除探伤委托任务
                if ("6".equals(trackItem.getOptType())) {
                    QueryWrapper<InspectionPower> inspectionPowerQueryWrapper = new QueryWrapper<>();
                    inspectionPowerQueryWrapper.eq("item_id", assign.getTiId());
                    inspectionPowerService.remove(inspectionPowerQueryWrapper);
                }
                //热工预装炉处理
                if (!ObjectUtil.isEmpty(trackItem.getPrechargeFurnaceId())) {
                    if (("1").equals(trackItem.getIsDoing())) {
                        throw new GlobalException("工序【" + trackItem.getOptName() + "】已开工，无法回滚！", ResultCode.FAILED);
                    }
                    QueryWrapper<TrackItem> wrapper = new QueryWrapper<>();
                    wrapper.eq("precharge_furnace_id", trackItem.getPrechargeFurnaceId());
                    List<TrackItem> list = trackItemService.list(wrapper);
                    if (list.size() == 1) {
                        //预装炉只有当前派工工序  回滚把预装炉删除
                        prechargeFurnaceService.removeById(trackItem.getPrechargeFurnaceId());
                    } else {
                        //预装炉有其他的工序时  仅把此工序移除预装炉
                        UpdateWrapper<TrackItem> trackItemUpdateWrapper = new UpdateWrapper<>();
                        trackItemUpdateWrapper.eq("id", trackItem.getId())
                                .set("precharge_furnace_id", null);
                        trackItemService.update(trackItemUpdateWrapper);
                    }
                }
            }
        }
        return true;
    }

    @Autowired
    private ForgHourService forgHourService;

    /**
     * 锻造根据工时标准计算额定工时
     */
    @Override
    public void calculationSinglePieceHours(TrackHead trackHead, TrackItem trackItem) {
        //查询跟单
        QueryWrapper<ForgHour> forgHourQueryWrapper = new QueryWrapper<>();
        forgHourQueryWrapper.eq("opt_id", trackItem.getOperatiponId())
                .eq("branch_code", trackItem.getBranchCode())
                .eq("texture", trackHead.getTexture())
                .orderByDesc("modify_time");
        List<ForgHour> list = forgHourService.list(forgHourQueryWrapper);
        //跟单重量
        trackHead.setWeight(ObjectUtil.isEmpty(trackHead.getWeight()) ? 0 : trackHead.getWeight());
        List<ForgHour> hours = list.stream().filter(item ->
                (item.getWeightUp() > trackHead.getWeight() || item.getWeightUp() == trackHead.getWeight())
                        && (item.getWeightDown() < trackHead.getWeight() || item.getWeightDown() == trackHead.getWeight())
        ).collect(Collectors.toList());

        if (hours.size() > 0) {
            trackItem.setSinglePieceHours(hours.get(0).getHour());
        }
    }

    @Override
    public boolean deleteAssignYl(String[] ids) {
        for (int i = 0; i < ids.length; i++) {
            Assign assign = this.getById(ids[i]);
            TrackItem trackItem = trackItemService.getById(assign.getTiId());
            if (null == trackItem) {
                this.removeById(ids[i]);
            } else {
                if (trackItem.getIsExistQualityCheck() == 1 && trackItem.getIsQualityComplete() == 1) {
                    throw new GlobalException("跟单工序【" + trackItem.getOptName() + "】已质检完成，报工无法取消！", ResultCode.FAILED);
                }
                if (trackItem.getIsExistScheduleCheck() == 1 && trackItem.getIsScheduleComplete() == 1) {
                    throw new GlobalException("跟单工序【" + trackItem.getOptName() + "】已调度完成，报工无法取消！", ResultCode.FAILED);
                }
                List<Assign> ca = this.find(null, null, null, null, null, trackItem.getFlowId());
                for (int j = 0; j < ca.size(); j++) {
                    TrackItem cstrackItem = trackItemService.getById(ca.get(j).getTiId());
                    if (cstrackItem.getOptSequence() > trackItem.getOptSequence()) {
                        throw new GlobalException("无法回滚，需要先取消后序工序【" + cstrackItem.getOptName() + "】的派工", ResultCode.FAILED);
                    }
                }
                QueryWrapper<TrackComplete> queryWrapper = new QueryWrapper<TrackComplete>();
                queryWrapper.eq("ti_id", assign.getTiId());
                List<TrackComplete> cs = trackCompleteService.list(queryWrapper);
                if (cs.size() > 0) {
                    throw new GlobalException("无法回滚，已有报工提交，需要先取消工序【" + trackItem.getOptName() + "】的报工！", ResultCode.FAILED);
                }
                //将前置工序状态改为待派工
                List<TrackItem> items = trackItemService.list(new QueryWrapper<TrackItem>().eq("flow_id", trackItem.getFlowId()).orderByAsc("opt_sequence"));
                for (int j = 0; j < items.size(); j++) {
                    TrackItem cstrackItem = items.get(j);
                    if (cstrackItem.getOptSequence() > trackItem.getOptSequence()) {
                        cstrackItem.setIsCurrent(0);
                        cstrackItem.setIsDoing(0);
                        trackItemService.updateById(cstrackItem);
                    }
                }
                trackItem.setIsCurrent(1);
                trackItem.setIsDoing(0);
                trackItem.setIsSchedule(0);
                trackItem.setAssignableQty(trackItem.getNumber());
                trackItemService.updateById(trackItem);
                this.removeById(ids[i]);
                //如果是探伤工序，删除探伤委托任务
                if ("6".equals(trackItem.getOptType())) {
                    QueryWrapper<InspectionPower> inspectionPowerQueryWrapper = new QueryWrapper<>();
                    inspectionPowerQueryWrapper.eq("item_id", assign.getTiId());
                    inspectionPowerService.remove(inspectionPowerQueryWrapper);
                }
            }
            //热工预装炉处理
            if (!ObjectUtil.isEmpty(trackItem.getPrechargeFurnaceId())) {
                QueryWrapper<TrackItem> wrapper = new QueryWrapper<>();
                wrapper.eq("precharge_furnace_id", trackItem.getPrechargeFurnaceId());
                List<TrackItem> list = trackItemService.list(wrapper);
                if (list.size() == 1) {
                    //预装炉只有当前派工工序  提示不可移除
                    throw new GlobalException("不能移除预装炉中所有工序！",ResultCode.FAILED);
                }
                UpdateWrapper<TrackItem> trackItemUpdateWrapper = new UpdateWrapper<>();
                trackItemUpdateWrapper.eq("id", trackItem.getId())
                        .set("precharge_furnace_id", null).set("precharge_furnace_assign_id",null);
                trackItemService.update(trackItemUpdateWrapper);
            }
        }
        return true;
    }
}
