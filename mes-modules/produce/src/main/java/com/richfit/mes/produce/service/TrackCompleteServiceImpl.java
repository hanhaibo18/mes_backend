package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.TrackAssignMapper;
import com.richfit.mes.produce.dao.TrackAssignPersonMapper;
import com.richfit.mes.produce.dao.TrackCompleteMapper;
import com.richfit.mes.produce.enmus.IdEnum;
import com.richfit.mes.produce.enmus.PublicCodeEnum;
import com.richfit.mes.produce.entity.CompleteDto;
import com.richfit.mes.produce.entity.QueryWorkingTimeVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author sun
 * @Description 跟单服务
 */
@Service
public class TrackCompleteServiceImpl extends ServiceImpl<TrackCompleteMapper, TrackComplete> implements TrackCompleteService {

    @Autowired
    private TrackCompleteMapper trackCompleteMapper;

    @Resource
    private TrackAssignService trackAssignService;
    @Resource
    public PublicService publicService;
    @Resource
    private TrackItemService trackItemService;
    @Resource
    private TrackAssignMapper trackAssignMapper;
    @Resource
    private TrackAssignPersonMapper trackAssignPersonMapper;
    @Resource
    private TrackCompleteCacheService trackCompleteCacheService;
    @Resource
    private TrackCompleteService trackCompleteService;
    @Resource
    private TrackHeadService trackHeadService;

    @Override
    public IPage<TrackComplete> queryPage(Page page, QueryWrapper<TrackComplete> query) {

        return trackCompleteMapper.queryPage(page, query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveComplete(List<CompleteDto> completeDtoList) {
        for (CompleteDto completeDto : completeDtoList) {
            if (StringUtils.isNullOrEmpty(completeDto.getQcPersonId())) {
                return CommonResult.failed("质检人员不能为空");
            }
            if (null == completeDto.getTrackCompleteList() && completeDto.getTrackCompleteList().isEmpty()) {
                return CommonResult.failed("报工人员不能为空");
            }
            if (StringUtils.isNullOrEmpty(completeDto.getTiId())) {
                return CommonResult.failed("工序Id不能为空");
            }
            TrackItem trackItem = trackItemService.getById(completeDto.getTiId());
            //检验人
            trackItem.setQualityCheckBy(completeDto.getQcPersonId());
            //检验车间
            trackItem.setQualityCheckBranch(completeDto.getQualityCheckBranch());
            //根据工序Id删除缓存表数据
            QueryWrapper<TrackCompleteCache> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("ti_id", completeDto.getTiId());
            double numDouble = 0.00;
            for (TrackComplete trackComplete : completeDto.getTrackCompleteList()) {
                if (trackComplete.getReportHours() > trackItem.getSinglePieceHours()) {
                    return CommonResult.failed("报工工时不能大于额定工时");
                }
                trackComplete.setId(null);
                trackComplete.setAssignId(completeDto.getAssignId());
                trackComplete.setTiId(completeDto.getTiId());
                trackComplete.setTrackId(completeDto.getTrackId());
                trackComplete.setTrackNo(completeDto.getTrackNo());
                trackComplete.setProdNo(completeDto.getProdNo());
                trackComplete.setCompleteBy(SecurityUtils.getCurrentUser().getUsername());
                trackComplete.setCompleteTime(new Date());
                trackComplete.setDetectionResult("-");
                trackComplete.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                numDouble += trackComplete.getCompletedQty();
            }
            Assign assign = trackAssignService.getById(completeDto.getAssignId());
            //跟新工序完成数量
            trackItem.setCompleteQty(trackItem.getCompleteQty() + numDouble);
            double intervalNumber = assign.getQty() + 0.0;
            if (numDouble > assign.getQty()) {
                return CommonResult.failed("报工数量:" + numDouble + ",派工数量:" + assign.getQty() + "完工数量不得大于" + assign.getQty());
            }
            if (numDouble < intervalNumber - 0.1) {
                return CommonResult.failed("报工数量:" + numDouble + ",派工数量:" + assign.getQty() + "完工数量不得少于" + (intervalNumber - 0.1));
            }
            if (assign.getQty() >= numDouble && intervalNumber - 0.1 <= numDouble) {
                //调用工序激活方法
                Map<String, String> map = new HashMap<>(3);
                map.put(IdEnum.FLOW_ID.getMessage(), trackItem.getFlowId());
                map.put(IdEnum.TRACK_HEAD_ID.getMessage(), completeDto.getTrackId());
                map.put(IdEnum.TRACK_ITEM_ID.getMessage(), completeDto.getTiId());
                map.put(IdEnum.ASSIGN_ID.getMessage(), completeDto.getAssignId());
                publicService.publicUpdateState(map, PublicCodeEnum.COMPLETE.getCode());
                //更改状态 标识当前工序完成
                trackItem.setIsDoing(2);
                trackItem.setIsOperationComplete(1);
                trackItemService.updateById(trackItem);
                trackCompleteCacheService.remove(queryWrapper);
            }
            log.error(completeDto.getTrackCompleteList().toString());
            this.saveBatch(completeDto.getTrackCompleteList());
        }
        return CommonResult.success(true);
    }

    @Override
    public CommonResult<QueryWorkingTimeVo> queryDetails(String assignId, String tiId, Integer state) {
        if (StringUtils.isNullOrEmpty(tiId)) {
            return CommonResult.failed("工序Id不能为空");
        }
        if (StringUtils.isNullOrEmpty(assignId)) {
            return CommonResult.failed("派工Id不能为空");
        }
        QueryWorkingTimeVo queryWorkingTimeVo = new QueryWorkingTimeVo();
        Assign assign = trackAssignMapper.queryAssign(assignId);
        assign.setAssignPersons(trackAssignPersonMapper.selectList(new QueryWrapper<AssignPerson>().eq("assign_id", assign.getId())));

        QueryWrapper<TrackComplete> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ti_id", tiId);

        List<TrackComplete> completeList = new ArrayList<>();
        if (0 == state) {
            completeList = trackCompleteMapper.queryCompleteCache(queryWrapper);
        } else {
            completeList = this.list(queryWrapper);
        }

        TrackItem trackItem = trackItemService.getById(tiId);
        queryWorkingTimeVo.setTrackCompleteList(completeList);
        queryWorkingTimeVo.setAssign(assign);
        queryWorkingTimeVo.setQcPersonId(trackItem.getQualityCheckBy());
        return CommonResult.success(queryWorkingTimeVo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<Boolean> updateComplete(CompleteDto completeDto) {
        if (StringUtils.isNullOrEmpty(completeDto.getTiId())) {
            return CommonResult.failed("工序Id不能为空");
        }
        if (StringUtils.isNullOrEmpty(completeDto.getQcPersonId())) {
            return CommonResult.failed("质检人员不能为空");
        }
        if (null == completeDto.getTrackCompleteList() && completeDto.getTrackCompleteList().isEmpty()) {
            return CommonResult.failed("报工人员不能为空");
        }
        TrackItem trackItem = trackItemService.getById(completeDto.getTiId());
        //检验人
        trackItem.setQualityCheckBy(completeDto.getQcPersonId());
        trackItem.setQualityCheckBranch(completeDto.getQualityCheckBranch());
        //根据工序Id先删除,在重新新增数据
        QueryWrapper<TrackComplete> removeComplete = new QueryWrapper<>();
        removeComplete.eq("ti_id", completeDto.getTiId());
        this.remove(removeComplete);
        double numDouble = 0.00;
        Assign assign = trackAssignService.getById(completeDto.getAssignId());
        double intervalNumber = assign.getQty() + 0.0;
        for (TrackComplete trackComplete : completeDto.getTrackCompleteList()) {
            if (trackComplete.getReportHours() > trackItem.getSinglePieceHours()) {
                return CommonResult.failed("报工工时不能大于额定工时");
            }
            trackComplete.setAssignId(completeDto.getAssignId());
            trackComplete.setTiId(completeDto.getTiId());
            trackComplete.setTrackId(completeDto.getTrackId());
            trackComplete.setTrackNo(completeDto.getTrackNo());
            trackComplete.setProdNo(completeDto.getProdNo());
            trackComplete.setCompleteBy(SecurityUtils.getCurrentUser().getUsername());
            trackComplete.setCompleteTime(new Date());
            numDouble += trackComplete.getCompletedQty();
        }
        //报工数量判断
        if (numDouble > assign.getQty()) {
            return CommonResult.failed("报工数量:" + numDouble + ",派工数量:" + assign.getQty() + ",完工数量不得大于" + assign.getQty());
        }
        if (numDouble < intervalNumber - 0.1) {
            return CommonResult.failed("报工数量:" + numDouble + ",派工数量:" + assign.getQty() + ",完工数量不得少于" + +(intervalNumber - 0.1));
        }
        //跟新工序完成数量
        trackItem.setCompleteQty(trackItem.getCompleteQty() + numDouble);
        trackItemService.updateById(trackItem);
        return CommonResult.success(this.saveOrUpdateBatch(completeDto.getTrackCompleteList()));
    }

    /**
     * @modifiedby mafeng02 2002-07-28 ,修改没有派工的外协和探伤下的处理
     * @Description 报工回滚
     */
    @Override
    public CommonResult<Boolean> rollBack(String id) {
        String msg = "";
        TrackComplete trackComplete = this.getById(id);
        Assign assign = new Assign();
        if (null != trackComplete.getAssignId()) {
            assign = trackAssignService.getById(trackComplete.getAssignId());
        }
        TrackItem trackItem = new TrackItem();
        if (null == assign) {
            trackItem = trackItemService.getById(trackComplete.getTiId());
        } else {
            trackItem = trackItemService.getById(assign.getTiId());
        }
        if (null == trackItem) {
            removeComplete(trackComplete.getTiId());
        } else {
            QueryWrapper<TrackComplete> queryWrapper = new QueryWrapper<TrackComplete>();
            if (!StringUtils.isNullOrEmpty(id)) {
                queryWrapper.eq("track_id", id);
            } else {
                queryWrapper.eq("track_id", "-1");
            }
            queryWrapper.orderByAsc("modify_time");
            List<TrackComplete> cs = trackCompleteService.list(queryWrapper);
            //判断跟单号已质检完成，报工无法取消
            if (trackItem.getIsExistQualityCheck() == 1 && trackItem.getIsQualityComplete() == 1) {
                msg += "跟单号已质检完成，报工无法取消！";
            }
            //判断跟单号已质检完成，报工无法取消
            if (trackItem.getIsExistScheduleCheck() == 1 && trackItem.getIsScheduleComplete() == 1) {
                msg += "跟单号已调度完成，报工无法取消！";
            }
            //判断后置工序是否已派工，否则不可回滚
            QueryWrapper<Assign> queryWrapperAssign = new QueryWrapper<Assign>();
            queryWrapperAssign.eq("ti_id", trackItem.getId());
            List<Assign> assigns = trackAssignService.list(queryWrapperAssign);
            for (int j = 0; j < assigns.size(); j++) {
                TrackItem cstrackItem = trackItemService.getById(assigns.get(j).getTiId());
                if (cstrackItem.getOptSequence() > trackItem.getOptSequence()) {
                    return CommonResult.failed("无法取消报工，已有后序工序【" + cstrackItem.getOptName() + "】已派工，需要先取消后序工序");
                }
            }
            //判断后置工序是否已报工，否则不可回滚
            for (int j = 0; j < cs.size(); j++) {
                TrackItem cstrackItem = trackItemService.getById(cs.get(j).getTiId());
                if (cstrackItem.getOptSequence() > trackItem.getOptSequence()) {
                    return CommonResult.failed("无法取消报工，已有后序工序【" + cstrackItem.getOptName() + "】已报工，需要先取消后序工序");
                }
            }

            //将后置工序IS_CURRENT设置为否，状态为1
            List<TrackItem> items = trackItemService.list(new QueryWrapper<TrackItem>().eq("track_head_id", trackItem.getTrackHeadId()).orderByAsc("opt_sequence"));
            for (TrackItem trackItems : items) {
                if (trackItems.getOptSequence() > trackItem.getOptSequence() && trackItems.getIsCurrent() == 1) {
                    trackItems.setIsCurrent(0);
                    trackItems.setIsDoing(0);
                    trackItems.setIsFinalComplete("0");
                    trackItemService.updateById(trackItems);
                }
            }
            //将当前工序设置为激活
            if (msg.equals("")) {
                trackItem.setIsDoing(0);
                trackItem.setIsCurrent(1);
                trackItem.setIsFinalComplete("0");
                trackItem.setIsOperationComplete(0);
                trackItem.setAssignableQty(trackItem.getAssignableQty() + trackComplete.getCompletedQty().intValue());
                trackItemService.updateById(trackItem);
                TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
                trackHead.setStatus("1");
                trackHeadService.updateById(trackHead);
                if (null != assign) {
                    assign.setAvailQty(assign.getQty() + trackComplete.getCompletedQty().intValue());
                    assign.setState(0);
                    trackAssignService.updateById(assign);
                }
                removeComplete(trackComplete.getTiId());
            }
        }


        if (msg.equals("")) {
            return CommonResult.success(null, "删除成功！");
        } else {
            return CommonResult.failed("操作失败，请重试！" + msg);
        }
    }


    private Boolean removeComplete(String tiId) {
        QueryWrapper<TrackComplete> removeComplete = new QueryWrapper<>();
        removeComplete.eq("ti_id", tiId);
        return this.remove(removeComplete);
    }
}
