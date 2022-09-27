package com.richfit.mes.produce.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.LineStoreMapper;
import com.richfit.mes.produce.dao.TrackAssignMapper;
import com.richfit.mes.produce.entity.KittingVo;
import com.richfit.mes.produce.entity.QueryProcessVo;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 马峰
 * @Description 跟单派工服务
 */
@Service
public class TrackAssignServiceImpl extends ServiceImpl<TrackAssignMapper, Assign> implements TrackAssignService {

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
                String version = baseServiceClient.queryCraft(trackItem.getOptId());
                if (!StringUtils.isNullOrEmpty(version)) {
                    trackItem.setVersions(version);
                }
            }
            //排序
            orderByCol(orderCol, order, excludeOrderCols, pageAssignsByStatus);
        }
        return pageAssignsByStatus;
    }


    @Override
    public IPage<TrackItem> getPageAssignsByStatusAndTrack(Page page, @Param("name") String name, QueryWrapper<TrackItem> qw, String orderCol, String order, List<String> excludeOrderCols) {
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
                String version = baseServiceClient.queryCraft(trackItem.getOptId());
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
                String version = baseServiceClient.queryCraft(trackItem.getOptId());
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


    @Override
    public IPage<Assign> queryPage(Page page, String siteId, String trackNo, String routerNo, String startTime, String endTime, String state, String userId, String branchCode, String productNo, String classes) throws ParseException {
        QueryWrapper<Assign> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(trackNo)) {
            trackNo = trackNo.replaceAll(" ", "");
            queryWrapper.apply("replace(replace(replace(u.track_no2, char(13), ''), char(10), ''),' ', '') like '%" + trackNo + "%'");
        }
        if (!StringUtils.isNullOrEmpty(routerNo)) {
            queryWrapper.like("u.drawing_no", routerNo);
        }
        if (!StringUtils.isNullOrEmpty(siteId)) {
            queryWrapper.like("u.assign_by", siteId);
        }
        if (!StringUtils.isNullOrEmpty(productNo)) {
            queryWrapper.like("u.product_no", productNo);
        }
        if (!StrUtil.isBlank(userId)) {
            queryWrapper.likeRight("u.user_id", userId + ",");
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
        queryWrapper.eq("u.classes", classes);
        queryWrapper.eq("u.branch_code", branchCode);
        queryWrapper.eq("u.tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.orderByDesc("u.assign_time");
        IPage<Assign> queryPage = trackAssignMapper.queryPageNew(page, queryWrapper);
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
            Integer state = trackAssignMapper.isDispatching(queryProcess.getId());
            if (null != state) {
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
                List<TrackAssembly> list = trackAssembleService.list(queryWrapper);
                for (TrackAssembly trackAssembly : list) {
                    if ("0".equals(trackAssembly.getIsKeyPart())) {
                        continue;
                    }
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

    @Override
    public CommonResult<Boolean> startWorking(List<String> assignIdList) {
        try {
            for (String assignId : assignIdList) {
                Assign assign = this.getById(assignId);
                TrackItem trackItem = trackItemService.getById(assign.getTiId());
                trackItem.setIsDoing(1);
                trackItem.setStartDoingTime(new Date());
                trackItem.setStartDoingUser(SecurityUtils.getCurrentUser().getUsername());
            }
        } catch (Exception e) {
            throw new GlobalException("开工失败,请重试", ResultCode.FAILED);
        }
        return CommonResult.success(true);
    }

    @Override
    public Integer queryDispatchingNumber() {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        //增加工序过滤
        CommonResult<TenantUserVo> result = systemServiceClient.queryByUserId(SecurityUtils.getCurrentUser().getUserId());
        QueryWrapper<ProduceRoleOperation> queryWrapperRole = new QueryWrapper<>();
        List<String> roleId = result.getData().getRoleList().stream().map(BaseEntity::getId).collect(Collectors.toList());
        queryWrapperRole.in("role_id", roleId);
        List<ProduceRoleOperation> operationList = roleOperationService.list(queryWrapperRole);
        Set<String> set = operationList.stream().map(ProduceRoleOperation::getOperationId).collect(Collectors.toSet());
        queryWrapper.in("operatipon_id", set);
        queryWrapper.eq("is_current", 1);
        queryWrapper.eq("branch_code", SecurityUtils.getCurrentUser().getOrgId());
        return trackAssignMapper.queryDispatchingNumber(queryWrapper);
    }

    @Override
    public Integer workNumber() {
        QueryWrapper<Assign> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("state", 0, 1);
        queryWrapper.likeRight("user_id", SecurityUtils.getCurrentUser().getUsername() + ",");
        queryWrapper.eq("branch_code", SecurityUtils.getCurrentUser().getOrgId());
        return this.count(queryWrapper);
    }


}
