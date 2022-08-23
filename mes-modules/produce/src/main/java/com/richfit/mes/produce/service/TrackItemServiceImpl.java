package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.TrackItemMapper;
import com.richfit.mes.produce.entity.QueryDto;
import com.richfit.mes.produce.entity.QueryFlawDetectionDto;
import com.richfit.mes.produce.entity.QueryFlawDetectionListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * @author 王瑞
 * @Description 跟单工序服务
 */
@Service
@Transactional(value = "transactionManager", propagation = Propagation.REQUIRED, rollbackFor = Throwable.class)
public class TrackItemServiceImpl extends ServiceImpl<TrackItemMapper, TrackItem> implements TrackItemService {

    @Autowired
    private TrackItemMapper trackItemMapper;

    @Autowired
    private TrackHeadService trackHeadService;

    @Autowired
    private TrackAssignService trackAssignService;

    @Autowired
    private TrackCompleteService trackCompleteService;

    @Autowired
    private TrackAssignPersonService trackAssignPersonService;

    @Autowired
    private TrackAssemblyBindingService trackAssemblyBindingService;

    @Autowired
    private TrackCheckService trackCheckService;

    @Autowired
    private TrackCheckDetailService trackCheckDetailService;

    @Autowired
    private TrackCheckAttachmentService trackCheckAttachmentService;

    @Autowired
    private LineStoreService lineStoreService;

    @Override
    public List<TrackItem> selectTrackItem(QueryWrapper<TrackItem> query) {
        return trackItemMapper.selectTrackItem(query);
    }

    @Override
    public List<TrackItem> selectTrackItemAssign(QueryWrapper<TrackItem> query) {
        return trackItemMapper.selectTrackItemAssign(query);
    }

    @Override
    public List<TrackItem> queryTrackItemByTrackNo(String trackNo) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_head_id", trackNo);
        queryWrapper.orderByAsc("sequence_order_by");
        return this.list(queryWrapper);
    }

    @Override
    public IPage<TrackItem> queryFlawDetectionList(QueryDto<QueryFlawDetectionDto> queryDto) {
        QueryFlawDetectionDto queryFlawDetectionDto = queryDto.getParam();
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        if (null != queryFlawDetectionDto.getEndTime() && null != queryFlawDetectionDto.getStartTime()) {
            queryWrapper.ge("create_time", queryFlawDetectionDto.getStartTime());
            //处理结束时间
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(queryFlawDetectionDto.getEndTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            queryWrapper.le("create_time", calendar.getTime());
        }
        //TODO:复检=再次检查不合格产品
        if (queryFlawDetectionDto.getIsRecheck()) {
            queryWrapper.eq("", "不合格状态码");
        }
        if (!StringUtils.isNullOrEmpty(queryFlawDetectionDto.getProductNo())) {
            queryWrapper.eq("product_no", queryFlawDetectionDto.getProductNo());
        }
        queryWrapper.isNull("flaw_detection");
        queryWrapper.eq("branch_code", queryDto.getBranchCode());
        queryWrapper.eq("tenant_id", queryDto.getTenantId());
        queryWrapper.orderByDesc("create_time");
        return this.page(new Page<>(queryDto.getPage(), queryDto.getSize()), queryWrapper);
    }

    @Override
    public Boolean updateFlawDetection(TrackItem trackItem) {
        return this.updateById(trackItem);
    }

    @Override
    public IPage<TrackItem> queryFlawDetectionPage(QueryDto<QueryFlawDetectionListDto> queryDto) {
        QueryFlawDetectionListDto queryFlawDetectionDto = queryDto.getParam();
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        if (null != queryFlawDetectionDto.getEndTime() && null != queryFlawDetectionDto.getStartTime()) {
            queryWrapper.ge("create_time", queryFlawDetectionDto.getStartTime());
            //处理结束时间
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(queryFlawDetectionDto.getEndTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            queryWrapper.le("create_time", calendar.getTime());
        }
        if (!StringUtils.isNullOrEmpty(queryFlawDetectionDto.getTrackNo())) {
            queryWrapper.eq("track_o", queryFlawDetectionDto.getTrackNo());
        }
        if (!StringUtils.isNullOrEmpty(queryFlawDetectionDto.getProductNo())) {
            queryWrapper.eq("product_no", queryFlawDetectionDto.getProductNo());
        }
        queryWrapper.isNotNull("flaw_detection");
        queryWrapper.eq("branch_code", queryDto.getBranchCode());
        queryWrapper.eq("tenant_id", queryDto.getTenantId());
        queryWrapper.orderByDesc("create_time");
        return this.page(new Page<>(queryDto.getPage(), queryDto.getSize()), queryWrapper);
    }

    @Override
    public Boolean linkToCert(String tiId, String certNo) {
        TrackItem trackItem = new TrackItem();
        trackItem.setId(tiId);
        trackItem.setCertificateNo(certNo);
        return this.updateById(trackItem);
    }

    @Override
    public Boolean unLinkFromCert(String tiId) {
        TrackItem trackItem = new TrackItem();
        trackItem.setId(tiId);
        trackItem.setCertificateNo(null);
        return this.updateById(trackItem);
    }

    @Override
    public String resetStatus(String tiId, Integer resetType) {
        TrackItem item = this.getById(tiId);
        // resetType 1:重置派工,2:重置报工,3:重置质检,4:重置调度审核,5:重置当前工序的所有记录
        if (resetType != null && item != null) {
            QueryWrapper<TrackHead> headQueryWrapper = new QueryWrapper<>();
            headQueryWrapper.eq("id", item.getTrackHeadId());
            TrackHead trackHead = trackHeadService.getOne(headQueryWrapper);

            if (resetType == 5) {
                item.setIsFinalComplete("0");
                item.setIsTrackSequenceComplete(0);
            }

            if (resetType == 4 || resetType == 5) {
                item.setIsScheduleComplete(0);
                item.setQualityCertificateDestination("");
                item.setScheduleCompleteBy("");
                item.setScheduleCompleteTime(null);
            }

            if (resetType == 3 || resetType == 5) {
                if ((item.getIsExistScheduleCheck() != null && item.getIsExistScheduleCheck() != 0)
                        && (item.getIsScheduleComplete() != null && item.getIsScheduleComplete() != 0)) {
                    return "调度审核已完成,报工记录不可重置！";
                }

                item.setIsQualityComplete(0);
                item.setQualityCheckBy("");
                item.setQualityCompleteTime(null);
                item.setQualityResult(0);

                // 删除质检相关数据
                QueryWrapper<TrackCheck> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("ti_id", item.getId());
                trackCheckService.remove(queryWrapper);

                QueryWrapper<TrackCheckDetail> detailQueryWrapper = new QueryWrapper<>();
                detailQueryWrapper.eq("ti_id", item.getId());
                trackCheckDetailService.remove(detailQueryWrapper);

                QueryWrapper<CheckAttachment> attachmentQueryWrapper = new QueryWrapper<>();
                attachmentQueryWrapper.eq("ti_id", item.getId());
                trackCheckAttachmentService.remove(attachmentQueryWrapper);
            }

            if (resetType == 2 || resetType == 5) {
                // 有质检并质检完成
                if ((item.getIsExistQualityCheck() != null && item.getIsExistQualityCheck() != 0)
                        && (item.getIsQualityComplete() != null && item.getIsQualityComplete() != 0)) {
                    return "质检已完成，报工记录不可重置！";
                }
                // 无质检，有调度审核并审核完成
                else if ((item.getIsExistQualityCheck() != null && item.getIsExistQualityCheck() == 0)
                        && (item.getIsExistScheduleCheck() != null && item.getIsExistScheduleCheck() != 0)) {
                    return "调度审核已完成,报工记录不可重置！";
                } else {
                    item.setIsDoing(0);
                    item.setIsOperationComplete(0);
                    item.setOperationCompleteTime(null);
                    item.setQualityCheckBy("");
                    item.setStartDoingTime(null);
                    item.setStartDoingUser(null);

                    QueryWrapper<TrackComplete> completeQueryWrapper = new QueryWrapper<>();
                    completeQueryWrapper.eq("ti_id", item.getId());
                    List<TrackComplete> completes = trackCompleteService.list(completeQueryWrapper);
                    double numDouble = 0.00;
                    for (TrackComplete complete : completes) {
                        numDouble += complete.getCompletedQty();
                    }
                    item.setCompleteQty(item.getCompleteQty() - numDouble);
                    trackCompleteService.remove(completeQueryWrapper);

                    UpdateWrapper<Assign> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.set("state", 0);
                    updateWrapper.eq("ti_id", item.getId());
                    trackAssignService.update(updateWrapper);

                    // 装配跟单删除相关数据
                    if (StringUtils.isNullOrEmpty(trackHead.getClasses()) && trackHead.getClasses().equals("2")) {
                        QueryWrapper<TrackAssemblyBinding> bindingQueryWrapper = new QueryWrapper<>();
                        bindingQueryWrapper.in("item_id", item.getId());
                        trackAssemblyBindingService.remove(bindingQueryWrapper);
                    }
                }
            }

            if (resetType == 1 || resetType == 5) {
                if ((item.getIsDoing() != null && item.getIsDoing() != 0)
                        && (item.getIsOperationComplete() != null && item.getIsOperationComplete() != 0)) {
                    return "操作工已开工或报工已完成，记录不可重置！";
                }

                /*if (item.getSequenceOrderBy() == 1) {
                    UpdateWrapper<TrackHead> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.set("product_no", "");
                    updateWrapper.eq("id", item.getTrackHeadId());
                    trackHeadService.update(updateWrapper);
                }*/
                item.setIsSchedule(0);

                QueryWrapper<Assign> assignQueryWrapper = new QueryWrapper<>();
                assignQueryWrapper.eq("ti_id", tiId);
                Assign assign = trackAssignService.getOne(assignQueryWrapper);
                if (assign != null && !StringUtils.isNullOrEmpty(assign.getId())) {
                    item.setAssignableQty(item.getAssignableQty() + assign.getQty());
                    QueryWrapper<AssignPerson> personQueryWrapper = new QueryWrapper<>();
                    personQueryWrapper.eq("assign_id", assign.getId());
                    trackAssignPersonService.remove(personQueryWrapper);
                    trackAssignService.removeById(assign.getId());
                } else {
                    item.setAssignableQty(item.getBatchQty());
                }
            }

        }
        return this.updateById(item) ? "success" : "修改跟单工序失败！";
    }

    @Override
    public String nextSequence(String thId) {
        return trackHeadNext(thId);
    }

    private String trackHeadNext(String thId) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_current", 1);
        queryWrapper.eq("track_head_id", thId);
        List<TrackItem> items = this.list(queryWrapper);
        boolean isComplete = true;
        for (TrackItem item : items) {
            if (item != null && "0".equals(item.getIsFinalComplete())) {
                isComplete = false;
                break;
            }
        }

        if (!isComplete) {
            return "选择的跟单中有未全部完成的产品，不能更新至下一步!";
        } else if (items.size() > 0) {
            TrackItem item = items.get(0);
            // 若同一个跟单下的同一个步序中的所有产品都已完成,更新跟单工艺步序至下一步

            QueryWrapper<TrackItem> allWrapper = new QueryWrapper<>();
            allWrapper.eq("track_head_id", item.getTrackHeadId());
            List<TrackItem> allItems = this.list(allWrapper);

            List<TrackItem> updateItems = new ArrayList<>();

            for (TrackItem trackItem : allItems) {
                if (trackItem.getIsCurrent() == 1) {
                    trackItem.setIsCurrent(0);
                    trackItem.setIsTrackSequenceComplete(1);
                    updateItems.add(trackItem);
                } else if (item.getNextOptSequence() > 0) {
                    //下道激活工序
                    if (trackItem.getOptSequence().equals(item.getNextOptSequence())) {
                        trackItem.setIsCurrent(1);
                        updateItems.add(trackItem);
                    }
                }
            }

            this.updateBatchById(updateItems);
            if (item.getNextOptSequence() == 0) {
                TrackHead trackHead = trackHeadService.getById(item.getTrackHeadId());
                trackHead.setStatus("2");
                trackHead.setCompleteTime(new Date());
                trackHeadService.updateById(trackHead);
                //设置产品完工
                lineStoreService.changeStatus(trackHead);
            }

        } else {
            return "该跟单没有当前工序！";
        }
        return "success";
    }

    private String productNext(String thId) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_current", 1);
        queryWrapper.eq("track_head_id", thId);
        List<TrackItem> items = this.list(queryWrapper);
        boolean isComplete = true;
        for (TrackItem item : items) {
            if (item != null && "0".equals(item.getIsFinalComplete())) {
                isComplete = false;
                break;
            }
        }

        if (!isComplete) {
            return "选择的跟单中有未全部完成的产品，不能更新至下一步!";
        } else {
            TrackItem item = items.get(0);

            // 若是并行工序且不合格,将该产品的并行工序中的未完成工序当前标识设为0
            if (item.getOptParallelType() == 1
                    && (item.getIsExistQualityCheck() != null && item.getIsExistQualityCheck() != 0)
                    && item.getQualityResult() == 0) {
                trackItemMapper.updateTrackItemIsCurrent(item.getId());
            }

            QueryWrapper<TrackItem> itemQueryWrapper = new QueryWrapper<>();
            itemQueryWrapper.eq("track_head_id", thId);
            itemQueryWrapper.eq("router_id", item.getRouterId());
            itemQueryWrapper.eq("is_final_complete", 0);
            itemQueryWrapper.eq("is_current", 1);
            List<TrackItem> list = this.list(itemQueryWrapper);
            if (list == null || list.size() == 0) {
                UpdateWrapper<TrackItem> updateWrapper = new UpdateWrapper<>();
                // 更新本工序,并行工序暂不更新is_current
                if (item.getOptParallelType() == 0) {
                    updateWrapper.set("is_current", 0);
                }
                updateWrapper.set("is_track_sequence_complete", 1);
                updateWrapper.eq("track_head_id", item.getTrackHeadId());
                updateWrapper.eq("router_id", item.getRouterId());
                updateWrapper.eq("is_final_complete", 1);
                this.update(updateWrapper);


                // 非并行工序,或者并行工序且所有并行工序都完成
                if (item.getOptParallelType() == 1) {
                    QueryWrapper<TrackItem> wrapper = new QueryWrapper<>();
                    wrapper.eq("track_head_id", item.getTrackHeadId());
                    wrapper.eq("is_trackSequence_complete", 0);
                    wrapper.eq("opt_sequence", item.getOptSequence());
                    wrapper.eq("is_current", 1);
                    List<TrackItem> optItems = this.list(wrapper);
                    if (optItems == null || optItems.size() == 0) {
                        UpdateWrapper<TrackItem> updateWrapper2 = new UpdateWrapper<>();
                        updateWrapper2.set("is_current", 0);
                        updateWrapper2.eq("th_id", item.getTrackHeadId());
                        updateWrapper2.eq("opt_sequence", item.getOptSequence());
                        this.update(updateWrapper2);
                    }
                }
                QueryWrapper<TrackItem> wrapper2 = new QueryWrapper<>();
                wrapper2.eq("track_head_id", item.getTrackHeadId());
                wrapper2.eq("opt_sequence", item.getOptSequence());
                wrapper2.eq("is_final_complete", 1);
                List<TrackItem> tempItem = this.list(wrapper2);

            }
        }
        return "数据更新成功！";
    }

    @Override
    public String backSequence(String thId) {

        QueryWrapper<TrackItem> wrapper = new QueryWrapper<>();
        wrapper.eq("track_head_id", thId);
        List<TrackItem> items = this.list(wrapper);
        TrackItem item = new TrackItem();
        if (items.size() > 0) {
            for (TrackItem ti : items) {
                if (ti.getIsCurrent() != null && ti.getIsCurrent() == 1) {
                    item = ti;
                    break;
                }
            }
        }

        if (item.getId() == null) {
            return "该跟单没有当前工序！";
        }

        List<TrackItem> nextItems = trackItemMapper.selectNextItem(item.getId());
        if (nextItems == null || nextItems.size() == 0) {
            return "当前工序已是跟单第一步有效工序,不可回退！";
        }

        // 将当前工序is_current设为0
        UpdateWrapper<TrackItem> itemUpdateWrapper = new UpdateWrapper<>();
        itemUpdateWrapper.set("is_current", 0);
        itemUpdateWrapper.eq("track_head_id", item.getTrackHeadId());
        itemUpdateWrapper.eq("opt_sequence", item.getOptSequence());
        this.update(itemUpdateWrapper);

        // 将上道工序is_current设为1
        UpdateWrapper<TrackItem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("is_current", 1);
        updateWrapper.set("is_track_sequence_complete", 0);
        updateWrapper.eq("track_head_id", item.getTrackHeadId());
        updateWrapper.eq("next_opt_sequence", item.getOptSequence());
        this.update(updateWrapper);

        return "success";
    }

    @Override
    public void addItemByTrackHead(TrackHead trackHead, List<TrackItem> trackItems, String productsNo, Integer number, String flowId) {
        if (trackItems != null && trackItems.size() > 0) {
            for (TrackItem item : trackItems) {
                item.setId(UUID.randomUUID().toString().replace("-", ""));
                item.setTrackHeadId(trackHead.getId());
                item.setFlowId(flowId);
                item.setProductNo(trackHead.getDrawingNo() + " " + productsNo);
                item.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                item.setCreateTime(new Date());
                item.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                item.setModifyTime(new Date());
                item.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                //可分配数量
                item.setAssignableQty(number);
                item.setNumber(number);
                item.setIsSchedule(0);
                item.setIsPrepare(0);
                item.setIsNotarize(0);
                //需要调度审核时展示
                if (1 == item.getIsExistScheduleCheck()) {
                    item.setIsScheduleCompleteShow(1);
                } else {
                    item.setIsScheduleCompleteShow(0);
                }
                this.save(item);
            }
        }
    }

}
