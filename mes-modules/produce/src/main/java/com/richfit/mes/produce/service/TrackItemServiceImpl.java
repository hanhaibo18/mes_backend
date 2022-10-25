package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.PdmDraw;
import com.richfit.mes.common.model.base.PdmMesOption;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.TrackAssignMapper;
import com.richfit.mes.produce.dao.TrackItemMapper;
import com.richfit.mes.produce.dao.quality.DisqualificationMapper;
import com.richfit.mes.produce.entity.ItemMessageDto;
import com.richfit.mes.produce.entity.QueryDto;
import com.richfit.mes.produce.entity.QueryFlawDetectionDto;
import com.richfit.mes.produce.entity.QueryFlawDetectionListDto;
import com.richfit.mes.produce.entity.quality.DisqualificationItemVo;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.utils.Code;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author 王瑞
 * @Description 跟单工序服务
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TrackItemServiceImpl extends ServiceImpl<TrackItemMapper, TrackItem> implements TrackItemService {

    @Autowired
    private TrackItemMapper trackItemMapper;

    @Autowired
    private TrackHeadService trackHeadService;

    @Autowired
    private TrackHeadFlowService trackHeadFlowService;

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

    @Resource
    private BaseServiceClient baseServiceClient;

    @Resource
    private TrackAssignMapper trackAssignMapper;

    @Resource
    private CodeRuleService codeRuleService;

    @Resource
    private DisqualificationMapper disqualificationMapper;

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
                    return "调度审核已完成,质检记录不可重置！";
                }

                item.setIsQualityComplete(0);
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
                // 第一行:是否质检确认 不确认为true 第二行:是否调度确认 确认为true 第三行 是否调度完成,完成为true
                else if ((item.getIsExistQualityCheck() != null && item.getIsExistQualityCheck() == 0)
                        && (item.getIsScheduleComplete() != null && item.getIsScheduleComplete() != 0)) {
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
                if (item.getIsDoing() != null && item.getIsDoing() > 0) {
                    return "操作工已开工或报工已完成，记录不可重置！";
                }
                /*if (item.getSequenceOrderBy() == 1) {
                    UpdateWrapper<TrackHead> updateWrapper = new UpdateWrapper<>();
                    updateWrapper.set("product_no", "");
                    updateWrapper.eq("id", item.getTrackHeadId());
                    trackHeadService.update(updateWrapper);
                }*/
                item.setIsSchedule(0);
                item.setAssignableQty(item.getNumber());

                //TODO:一条工序会存在多条派工记录,重构一下代码
                trackAssignMapper.deleteAssignAndPerson(tiId);
//
//                QueryWrapper<Assign> assignQueryWrapper = new QueryWrapper<>();
//                assignQueryWrapper.eq("ti_id", tiId);
//                Assign assign = trackAssignService.getOne(assignQueryWrapper);
//                if (assign != null && !StringUtils.isNullOrEmpty(assign.getId())) {
//                    QueryWrapper<AssignPerson> personQueryWrapper = new QueryWrapper<>();
//                    personQueryWrapper.eq("assign_id", assign.getId());
//                    trackAssignPersonService.remove(personQueryWrapper);
//                    trackAssignService.removeById(assign.getId());
//                } else {
//                    item.setAssignableQty(item.getBatchQty());
//                }
                //补充当工序顺序为1时进行跟单状态处理接口调用
                if (item.getOptSequence() == 1) {
                    UpdateWrapper<TrackFlow> updateWrapperTrackFlow = new UpdateWrapper<>();
                    updateWrapperTrackFlow.eq("id", item.getFlowId());
                    updateWrapperTrackFlow.set("status", "0");
                    trackHeadFlowService.update(updateWrapperTrackFlow);
                    trackHeadService.trackHeadData(item.getTrackHeadId());
                }
            }

        }
        return this.updateById(item) ? "success" : "修改跟单工序失败！";
    }

    @Override
    public String nextSequence(String flowId) {
        return trackHeadNext(flowId);
    }

    private String trackHeadNext(String flowId) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_current", 1);
        queryWrapper.eq("flow_id", flowId);
        List<TrackItem> items = this.list(queryWrapper);
        boolean isComplete = true;
        for (TrackItem item : items) {
            if (item != null && item.getIsOperationComplete() == 0) {
                isComplete = false;
                break;
            }
        }
        if (!isComplete) {
            return "选择的跟单中有未全部完成的产品，不能更新至下一步!";
        } else if (items.size() > 0) {
            TrackItem item = items.get(0);
            if (item.getNextOptSequence() == 0) {
                trackHeadService.trackHeadFinish(flowId);
            } else {
                // 若同一个跟单下的同一个步序中的所有产品都已完成,更新跟单工艺步序至下一步
                QueryWrapper<TrackItem> allWrapper = new QueryWrapper<>();
                allWrapper.eq("flow_id", flowId);
                List<TrackItem> allItems = this.list(allWrapper);
                List<TrackItem> updateItems = new ArrayList<>();
                for (TrackItem trackItem : allItems) {
                    if (trackItem.getIsCurrent() == 1) {
                        trackItem.setIsCurrent(0);
                        trackItem.setIsTrackSequenceComplete(1);
                        updateItems.add(trackItem);
                    } else if (item.getNextOptSequence() > 0) {
                        //下道激活工序
                        if (trackItem.getOriginalOptSequence().equals(item.getNextOptSequence())) {
                            trackItem.setIsCurrent(1);
                            updateItems.add(trackItem);
                        }
                    }
                }
                this.updateBatchById(updateItems);
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
    public String backSequence(String flowId) {

        QueryWrapper<TrackItem> wrapper = new QueryWrapper<>();
        wrapper.eq("flow_id", flowId);
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

        if (item.getOptSequence() == 1) {
            return "当前工序已是跟单第一步有效工序,不可回退！";
        }

        // 将当前工序is_current设为0
        item.setIsCurrent(0);
        this.updateById(item);

        // 将上道工序is_current设为1
        UpdateWrapper<TrackItem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("is_current", 1);
        updateWrapper.set("is_track_sequence_complete", 0);
        updateWrapper.eq("flow_id", flowId);
        updateWrapper.eq("next_opt_sequence", item.getOriginalOptSequence());
        this.update(updateWrapper);
        //生产线状态改为在制
        UpdateWrapper<TrackFlow> updateWrapperTrackFlow = new UpdateWrapper<>();
        updateWrapperTrackFlow.set("status", "1");
        updateWrapperTrackFlow.eq("id", item.getFlowId());
        trackHeadFlowService.update(updateWrapperTrackFlow);
        //重置跟单状态
        trackHeadService.trackHeadData(item.getTrackHeadId());
        return "success";
    }

    @Override
    public void addItemByTrackHead(TrackHead trackHead, List<TrackItem> trackItems, String productsNo, Integer number, String flowId) {
        if (trackItems != null && trackItems.size() > 0) {
            for (TrackItem item : trackItems) {
                item.setId(UUID.randomUUID().toString().replace("-", ""));
                item.setTrackHeadId(trackHead.getId());
                item.setDrawingNo(trackHead.getDrawingNo());
                item.setFlowId(flowId);
                item.setProductNo(trackHead.getDrawingNo() + " " + productsNo);
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
                if (trackHead.getStatus().equals("4")) {
                    item.setIsCurrent(0);
                }
                this.save(item);
            }
        }
    }

    @Override
    public ItemMessageDto queryItemMessageDto(String itemId) {
        TrackItem trackItem = this.getById(itemId);
        ItemMessageDto itMessage = new ItemMessageDto();
        TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
        itMessage.setDrawingNo(trackHead.getDrawingNo());
        itMessage.setSerialNumber(trackItem.getOptNo());
        itMessage.setOptName(trackItem.getOptName());
        itMessage.setItemType(trackItem.getOptType());
        itMessage.setOptParallelType(trackItem.getOptParallelType());
        itMessage.setPrepareEndHours(trackItem.getPrepareEndHours());
        itMessage.setSinglePieceHours(trackItem.getSinglePieceHours());
        itMessage.setIsExistQualityCheck(trackItem.getIsExistQualityCheck());
        itMessage.setIsExistScheduleCheck(trackItem.getIsExistScheduleCheck());
        itMessage.setNotice(trackItem.getNotice());
        //根据图号查询有没有 有图纸?
        CommonResult<List<PdmDraw>> drawList = baseServiceClient.queryDrawList(trackHead.getDrawingNo());
        if (!drawList.getData().isEmpty()) {
            itMessage.setIsDrawingNo("1");
        } else {
            itMessage.setIsDrawingNo("0");
        }
        //pdm 工序类型
        CommonResult<PdmMesOption> option = baseServiceClient.queryOptionDraw(trackItem.getOptId());
        if (null != option.getData()) {
            itMessage.setPdmItemType(option.getData().getType());
            itMessage.setNotice(option.getData().getContent());
            itMessage.setVersion(option.getData().getRev());
            itMessage.setIsDrawingNo(option.getData().getDrawing());
        }
        return itMessage;
    }

    @Override
    public DisqualificationItemVo queryItem(String tiId, String branchCode) {
        DisqualificationItemVo disqualification = disqualificationMapper.queryDisqualificationByItemId(tiId);
        if (null != disqualification) {
            return disqualification;
        }
        DisqualificationItemVo item = new DisqualificationItemVo();
        TrackItem trackItem = this.getById(tiId);
        TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
        item.trackHead(trackHead);
        //产品名称
        item.setProductName(trackItem.getProductName());
        //不合格品数量
        item.setDisqualificationNum(trackItem.getQualityUnqty());
        //获取申请单编号
        try {
            String disqualificationNo = Code.value("disqualification_no", SecurityUtils.getCurrentUser().getTenantId(), branchCode, codeRuleService);
            item.setProcessSheetNo(disqualificationNo);
            Code.update("disqualification_no", disqualificationNo, SecurityUtils.getCurrentUser().getTenantId(), branchCode, codeRuleService);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
            throw new GlobalException("获取申请单编号错误", ResultCode.FAILED);
        }
        return item;
    }

}
