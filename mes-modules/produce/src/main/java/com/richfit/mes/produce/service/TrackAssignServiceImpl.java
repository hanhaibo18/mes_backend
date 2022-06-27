package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.Plan;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.dao.TrackAssignMapper;
import com.richfit.mes.produce.entity.QueryProcessVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Collections;
import java.util.List;

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

    public IPage<TrackItem> getPageAssignsByStatus(Page page, QueryWrapper<TrackItem> qw) {
        IPage<TrackItem> pageAssignsByStatus = trackAssignMapper.getPageAssignsByStatus(page, qw);
        if (null != pageAssignsByStatus.getRecords()) {
            for (TrackItem trackItem : pageAssignsByStatus.getRecords()) {
                TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
                trackItem.setRouterId(trackHead.getRouterId());
                trackItem.setWeight(trackHead.getWeight());
                trackItem.setWorkNo(trackHead.getWorkNo());
                trackItem.setProductNo(trackHead.getProductNo());
                trackItem.setProductName(trackHead.getProductName());
                trackItem.setPartsName(trackHead.getMaterialName());
                if (!StringUtils.isNullOrEmpty(trackHead.getWorkPlanId())) {
                    trackItem.setWorkPlanNo(trackHead.getWorkPlanId());
                    Plan plan = planService.getById(trackHead.getWorkPlanId());
                    trackItem.setTotalQuantity(plan.getProjNum());
                    trackItem.setDispatchingNumber(plan.getTrackNum());
                }
            }
        }
        return pageAssignsByStatus;
    }

    public IPage<TrackItem> getPageAssignsByStatusAndTrack(Page page, @Param("name") String name, QueryWrapper<TrackItem> qw) {
        IPage<TrackItem> trackItemList = trackAssignMapper.getPageAssignsByStatusAndTrack(page, name, qw);
        if (null != trackItemList.getRecords()) {
            for (TrackItem trackItem : trackItemList.getRecords()) {
                TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
                trackItem.setRouterId(trackHead.getRouterId());
                trackItem.setWeight(trackHead.getWeight());
                trackItem.setWorkNo(trackHead.getWorkNo());
                trackItem.setProductNo(trackHead.getProductNo());
                trackItem.setProductName(trackHead.getProductName());
                trackItem.setPartsName(trackHead.getMaterialName());
                if (!StringUtils.isNullOrEmpty(trackHead.getWorkPlanId())) {
                    trackItem.setWorkPlanNo(trackHead.getWorkPlanId());
                    Plan plan = planService.getById(trackHead.getWorkPlanId());
                    trackItem.setTotalQuantity(plan.getProjNum());
                    trackItem.setDispatchingNumber(plan.getTrackNum());
                }
            }
        }
        return trackItemList;
    }

    public IPage<TrackItem> getPageAssignsByStatusAndRouter(Page page, @Param("name") String name, QueryWrapper<TrackItem> qw) {
        IPage<TrackItem> trackItemList = trackAssignMapper.getPageAssignsByStatusAndRouter(page, name, qw);
        if (null != trackItemList.getRecords()) {
            for (TrackItem trackItem : trackItemList.getRecords()) {
                TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
                trackItem.setRouterId(trackHead.getRouterId());
                trackItem.setWeight(trackHead.getWeight());
                trackItem.setWorkNo(trackHead.getWorkNo());
                trackItem.setProductNo(trackHead.getProductNo());
                trackItem.setProductName(trackHead.getProductName());
                trackItem.setPartsName(trackHead.getMaterialName());
                if (!StringUtils.isNullOrEmpty(trackHead.getWorkPlanId())) {
                    trackItem.setWorkPlanNo(trackHead.getWorkPlanId());
                    Plan plan = planService.getById(trackHead.getWorkPlanId());
                    trackItem.setTotalQuantity(plan.getProjNum());
                    trackItem.setDispatchingNumber(plan.getTrackNum());
                }
            }
        }
        return trackItemList;
    }

    @Override
    public IPage<Assign> queryPage(Page page, String siteId, String trackNo, String routerNo, String startTime, String endTime, String state, String userId, String branchCode) {
        IPage<Assign> queryPage = trackAssignMapper.queryPage(page, siteId, trackNo, routerNo, startTime, endTime, state, userId, branchCode);
        if (null != queryPage.getRecords()) {
            for (Assign assign : queryPage.getRecords()) {
                TrackHead trackHead = trackHeadService.getById(assign.getTrackId());
                assign.setRouterId(trackHead.getRouterId());
                assign.setWeight(trackHead.getWeight());
                assign.setWorkNo(trackHead.getWorkNo());
                assign.setProductName(trackHead.getProductName());
                if (!StringUtils.isNullOrEmpty(trackHead.getWorkPlanId())) {
                    assign.setWorkPlanNo(trackHead.getWorkPlanId());
                    Plan plan = planService.getById(trackHead.getWorkPlanId());
                    assign.setTotalQuantity(plan.getProjNum());
                    assign.setDispatchingNumber(plan.getTrackNum());
                }
            }
        }
        return queryPage;
    }

    @Override
    public List<QueryProcessVo> queryProcessList(String trackHeadId) {
        List<QueryProcessVo> processList = trackAssignMapper.queryProcessList(trackHeadId);
        if (processList == null || processList.isEmpty()) {
            return Collections.emptyList();
        }
        for (QueryProcessVo queryProcess : processList) {
            Integer state = trackAssignMapper.isDispatching(queryProcess.getId());
            if (null != state) {
                queryProcess.setIsDispatching("是");
                StringBuffer stringBuffer = new StringBuffer();
                if (0 == state) {
                    stringBuffer.append("未开工");
                } else {
                    stringBuffer.append("以开工");
                }
                //判断是否是本工序
                if (1 == queryProcess.getIsCurrent()) {
                    stringBuffer.insert(0, "本工序-");
                }
                queryProcess.setOptState(stringBuffer.toString());
            } else {
                queryProcess.setIsDispatching("否");
                queryProcess.setOptState("未开工");
            }
        }
        return processList;
    }

    @Override
    public boolean updateProcess(Assign assign) {
        return this.updateById(assign);
    }


}
