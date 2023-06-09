package com.richfit.mes.produce.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.base.PdmDraw;
import com.richfit.mes.common.model.base.PdmMesOption;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.sys.Tenant;
import com.richfit.mes.common.model.util.ActionUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.aop.OperationLogAspect;
import com.richfit.mes.produce.dao.PrechargeFurnaceMapper;
import com.richfit.mes.produce.dao.TrackAssignMapper;
import com.richfit.mes.produce.dao.TrackItemMapper;
import com.richfit.mes.produce.dao.quality.DisqualificationMapper;
import com.richfit.mes.produce.entity.ItemMessageDto;
import com.richfit.mes.produce.entity.QueryDto;
import com.richfit.mes.produce.entity.QueryFlawDetectionDto;
import com.richfit.mes.produce.entity.QueryFlawDetectionListDto;
import com.richfit.mes.produce.entity.quality.DisqualificationItemVo;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.quality.DisqualificationService;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Collectors;

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

    @Autowired
    private PrechargeFurnaceMapper prechargeFurnaceMapper;

    @Autowired
    private ActionService actionService;

    @Autowired
    private SystemServiceClient systemServiceClient;

    @Autowired
    private TrackCertificateService trackCertificateService;

    @Autowired
    private CertificateService certificateService;

    @Resource
    private BaseServiceClient baseServiceClient;

    @Resource
    private TrackAssignMapper trackAssignMapper;

    @Resource
    private DisqualificationMapper disqualificationMapper;

    @Resource
    private DisqualificationService disqualificationService;

    @Override
    public List<TrackItem> selectFinalTrackItems(String trackHeadId) {
        return trackItemMapper.getFinalTrackItems(trackHeadId);
    }

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
    public Boolean linkToCertNew(String thId, Certificate certificate) {
        UpdateWrapper<TrackItem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("track_head_id", thId);
        updateWrapper.eq("opt_no", certificate.getOptNo());
        updateWrapper.eq("branch_code", certificate.getBranchCode());
        updateWrapper.set("certificate_no", certificate.getCertificateNo());
        return this.update(updateWrapper);
    }

    @Override
    public Boolean checkIsCertRepeat(Certificate certificate) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("branch_code", certificate.getBranchCode());
        queryWrapper.eq("certificate_no", certificate.getCertificateNo());
        if (!CollectionUtils.isEmpty(this.list(queryWrapper))) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean unLinkFromCert(String tiId) {
        UpdateWrapper<TrackItem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("id", tiId);
        updateWrapper.set("certificate_no", null);
        return this.update(updateWrapper);
    }

    @Override
    public String resetStatus(String tiId, Integer resetType, HttpServletRequest request) {
        TrackItem item = this.getById(tiId);
        if (item == null) {
            return "error tiId";
        }
        QueryWrapper<TrackHead> headQueryWrapper = new QueryWrapper<>();
        headQueryWrapper.eq("id", item.getTrackHeadId());
        TrackHead trackHead = trackHeadService.getOne(headQueryWrapper);
        String actionMessage = "跟单号：" + trackHead.getTrackNo() + "，工序号：" + item.getOptNo() + "，工序名：" + item.getOptName();
        if (item.getIsCurrent() != 1) {
            return "只能操作当前工序！";
        }
        // resetType 1:重置派工,2:重置报工,3:重置质检,4:重置调度审核,5:重置当前工序的所有记录
        if (resetType != null && item != null) {
            //不管重置什么状态都需要去除当前工序最终完成状态
            item.setIsFinalComplete("0");
            if (resetType == 5) {
                item.setIsTrackSequenceComplete(0);
            }

            if (resetType == 4 || resetType == 5) {
                item.setIsScheduleComplete(0);
                item.setQualityCertificateDestination("");
                item.setScheduleCompleteBy("");
                item.setScheduleCompleteTime(null);
                //更新跟单主表以及跟单分流表中的数据信息；
                this.updateTrackHeadStatus(trackHead, item, resetType);
                actionMessage = actionMessage + ",重置调度审核";
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

                //更新跟单主表以及跟单分流表中的数据信息；
                this.updateTrackHeadStatus(trackHead, item, resetType);

                actionMessage = actionMessage + ",重置质检";
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
                        numDouble += complete.getCompletedQty() == null ? 0.0 : complete.getCompletedQty();
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

                    //更新跟单主表以及跟单分流表中的数据信息；
                    this.updateTrackHeadStatus(trackHead, item, resetType);
                }
                actionMessage = actionMessage + ",重置报工";
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
                actionMessage = actionMessage + ",重置派工";
            }
        }
        actionService.saveAction(ActionUtil.buildAction(item.getBranchCode(), "4", "2", actionMessage, OperationLogAspect.getIpAddress(request)));
        return this.updateById(item) ? "success" : "修改跟单工序失败！";
    }

    @Override
    public String nextSequence(String flowId) {
        return trackHeadNext(flowId);
    }

    private void updateTrackHeadStatus(TrackHead trackHead, TrackItem item, Integer resetType) {
        //清楚所有，状态时初始，其余情况状态为在制；
        String updateStatus = /*resetType == 5 ? "0" : */ "1";
        //如果跟单已完工，重置跟单表主表状态信息；
        if (Objects.equals(trackHead.getStatus(), "2") || Objects.equals(trackHead.getStatus(), "9")) {
            //跟单表主表信息修改；
            LambdaUpdateWrapper<TrackHead> trackHeadLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            trackHeadLambdaUpdateWrapper.set(TrackHead::getStatus, updateStatus);
            trackHeadLambdaUpdateWrapper.set(TrackHead::getCompleteTime, null);
            trackHeadLambdaUpdateWrapper.eq(TrackHead::getId, item.getTrackHeadId());
            trackHeadService.update(trackHeadLambdaUpdateWrapper);
            //跟单分流表信息修改；
            LambdaUpdateWrapper<TrackFlow> trackFlowLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            trackFlowLambdaUpdateWrapper.set(TrackFlow::getStatus, updateStatus);
            trackFlowLambdaUpdateWrapper.set(TrackFlow::getCompleteTime, null);
            trackFlowLambdaUpdateWrapper.eq(TrackFlow::getTrackHeadId, item.getTrackHeadId());
            trackHeadFlowService.update(trackFlowLambdaUpdateWrapper);
        }
    }

    private String trackHeadNext(String flowId) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        //获取该flowId的所有工序
        queryWrapper.eq("flow_id", flowId);
        queryWrapper.orderByAsc("sequence_order_by");
        List<TrackItem> allItems = this.list(queryWrapper);
        if (allItems == null || allItems.isEmpty()) {
            return "该跟单没有工序！";
        }
        List<TrackItem> updateItems = new ArrayList<>();
        queryWrapper.eq("is_current", 1);
        List<TrackItem> items = this.list(queryWrapper);
        //可能由于某些原因导致当前跟单工序没有当前工序，设置第一个工序为当前工序
        if (items.isEmpty()) {
            for (int i = 0; i < allItems.size(); i++) {
                if (allItems.get(i).getOptParallelType() == null) {
                    return "并行条件为null，请确认工序！flowId：" + flowId;
                }//不能并行的情况
                else if (allItems.get(i).getOptParallelType() == 0) {
                    allItems.get(i).setIsCurrent(1);
                    updateItems.add(allItems.get(i));
                    break;
                }//当前工序可并行的情况
                else if (allItems.get(i).getOptParallelType() == 1) {
                    allItems.get(i).setIsCurrent(1);
                    updateItems.add(allItems.get(i));
                    //判断下一步是否并行 不并行则跳出循环，并行进入下一次循环
                    if (!(allItems.get(i).getNextOptSequence() > 0 && allItems.get(i + 1).getOptParallelType() == 1)) {
                        break;
                    }
                }
            }
            this.updateBatchById(updateItems);
            return "该跟单没有当前工序！已重置当前工序为第一道工序";
        }
        boolean isComplete = true;
        for (TrackItem item : items) {
            if ((item != null && "0".equals(item.getIsFinalComplete())) || (item != null && item.getIsFinalComplete() == null)) {
                isComplete = false;
                break;
            }
        }
        if (!isComplete) {
            return "选择的跟单中有未全部完成的产品，不能更新至下一步!";
        } else if (items.get(0).getNextOptSequence() == 0) {
            //当前工序的下工序为0说明当前工序是最后一个工序 更改跟单状态为完成
            trackHeadService.trackHeadFinish(flowId);
            return "当前已经是最后一道工序！";
        } else {
            // 若同一个跟单下的同一个步序中的所有产品都已完成,更新跟单工艺步序至下一步
            for (TrackItem trackItem : allItems) {
                if (trackItem.getIsCurrent() == 1) {
                    trackItem.setIsCurrent(0);
                    trackItem.setIsTrackSequenceComplete(1);
                    updateItems.add(trackItem);
                } else if (items.get(0).getNextOptSequence() > 0) {
                    //下道激活工序
                    if (trackItem.getOriginalOptSequence().equals(items.get(0).getNextOptSequence())) {
                        trackItem.setIsCurrent(1);
                        updateItems.add(trackItem);
                    }
                }
            }
            this.updateBatchById(updateItems);
        }
        return "success";
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public String backSequence(String flowId) {
        //查询所有最终完成的工序
        QueryWrapper<TrackItem> finalWrapper = new QueryWrapper<>();
        finalWrapper.eq("flow_id", flowId);
        finalWrapper.eq("is_final_complete", 1);
        finalWrapper.orderByDesc("opt_sequence");
        List<TrackItem> finalItems = this.list(finalWrapper);
        //查询当前的工序
        QueryWrapper<TrackItem> currQueryWrapper = new QueryWrapper<>();
        currQueryWrapper.eq("flow_id", flowId);
        currQueryWrapper.eq("is_current", 1);
        currQueryWrapper.orderByAsc("opt_sequence");
        List<TrackItem> currItems = this.list(currQueryWrapper);
        if (CollectionUtils.isEmpty(currItems)) {
            return "此跟单没有当前工序！";
        }

        if (CollectionUtils.isEmpty(finalItems)) {
            return "此跟单没有工序完成";
        }

        TrackItem item = currItems.get(0);

        if (item.getSequenceOrderBy() == 1) {
            return "当前工序已是跟单第一步有效工序,不可回退！";
        }
        //回退前检查当前工序状态
        for (TrackItem currItem : currItems) {
            //开工状态
            boolean isDoing = 0 != currItem.getIsDoing();
            //是否报工
            boolean isOperationComplete = 0 != currItem.getIsOperationComplete();
            //是否质检
            boolean isQualityComplete = 0 != currItem.getIsQualityComplete();
            //是否调度
            boolean isScheduleComplete = 0 != currItem.getIsScheduleComplete();
            //是否最终完成
            boolean isFinalComplete = !"0".equals(currItem.getIsFinalComplete());
            //是否派工
            boolean isSchedule = 0 != currItem.getIsSchedule();
            if (isDoing || isOperationComplete || isQualityComplete || isScheduleComplete || isFinalComplete || isSchedule) {
                return "回退前清清除当前工序状态！";
            }
            if (certificateBegin(currItem, flowId)) {
                return "该工序已在其他车间开工，不可回退！";
            }
        }
        // 将当前工序is_current设为0
        UpdateWrapper<TrackItem> updateWrapperOld = new UpdateWrapper<>();
        updateWrapperOld.eq("flow_id", flowId);
        updateWrapperOld.eq("is_current", 1);
        updateWrapperOld.orderByDesc("opt_sequence");
        updateWrapperOld.set("is_current", 0);
        this.update(updateWrapperOld);
        // 将上道工序is_current设为1
        UpdateWrapper<TrackItem> updateWrapper = new UpdateWrapper<>();
        updateWrapper.set("is_current", 1);
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

    private boolean certificateBegin(TrackItem currItem, String flowId) {
        //当有合格证且合格证已经开了新跟单且新跟单开工时返回true，若新跟单未开工返回false同时删除跟单信息，其他情况均返回false
        QueryWrapper<TrackItem> itemQueryWrapper = new QueryWrapper<>();
        itemQueryWrapper.eq("flow_id", flowId).eq("next_opt_sequence", currItem.getOriginalOptSequence());
        List<TrackItem> trackItemList = this.list(itemQueryWrapper);
        if (CollectionUtils.isNotEmpty(trackItemList)) {
            for (TrackItem trackItem : trackItemList) {
                QueryWrapper<TrackCertificate> certificateQueryWrapper = new QueryWrapper<>();
                certificateQueryWrapper.eq("ti_id", trackItem.getId());
                TrackCertificate certificate = trackCertificateService.getOne(certificateQueryWrapper);
                if (certificate != null && certificate.getNextThId() != null) {
                    TrackHead trackHead = trackHeadService.getById(certificate.getNextThId());
                    //若跟单为初始状态则删除head，item，flow，合格证信息
                    if ("0".equals(trackHead.getStatus())) {
                        QueryWrapper<TrackFlow> flowQueryWrapper = new QueryWrapper<>();
                        flowQueryWrapper.eq("track_head_id", trackHead.getId());
                        trackHeadFlowService.remove(flowQueryWrapper);

                        QueryWrapper<TrackItem> itemQueryWrapper1 = new QueryWrapper<>();
                        itemQueryWrapper1.eq("track_head_id", trackHead.getId());
                        this.remove(itemQueryWrapper1);

                        QueryWrapper<TrackCertificate> certificateQueryWrapper1 = new QueryWrapper<>();
                        certificateQueryWrapper1.eq("item_id", trackItem.getId());
                        List<TrackCertificate> certificateList = trackCertificateService.list(certificateQueryWrapper1);
                        trackCertificateService.remove(certificateQueryWrapper1);
                        if (CollectionUtils.isNotEmpty(certificateList)) {
                            Set<String> set = certificateList.stream().map(TrackCertificate::getCertificateId).collect(Collectors.toSet());
                            certificateService.removeByIds(set);
                        }

                        trackHeadService.removeById(trackHead.getId());
                        return false;
                    } else {
                        return true;
                    }
                } else {
                    return false;
                }
            }
        }
        return false;
    }


//    @Override
//    public String backSequence(String flowId) {
//
//        QueryWrapper<TrackItem> wrapper = new QueryWrapper<>();
//        wrapper.eq("flow_id", flowId);
//        wrapper.eq("is_current", 1);
//        wrapper.orderByDesc("opt_sequence");
//        List<TrackItem> items = this.list(wrapper);
//
//
//        if (CollectionUtils.isEmpty(items)) {
//            return "该跟单没有当前工序！";
//        }
//        TrackItem item = items.get(0);
//
//        if (item.getOptSequence() == 1) {
//            return "当前工序已是跟单第一步有效工序,不可回退！";
//        }
//
//        //判断自己是否是第一道工序,第一道工序不修改状态
//        if (item.getOriginalOptSequence() != 10) {
//            // 将当前工序is_current设为0
//            UpdateWrapper<TrackItem> updateWrapperOld = new UpdateWrapper<>();
//            updateWrapperOld.eq("flow_id", flowId);
//            updateWrapperOld.eq("is_current", 1);
//            updateWrapperOld.orderByDesc("opt_sequence");
//            updateWrapperOld.set("is_current", 0);
//            this.update(updateWrapperOld);
//            // 将上道工序is_current设为1
//            UpdateWrapper<TrackItem> updateWrapper = new UpdateWrapper<>();
//            updateWrapper.set("is_current", 1);
//            updateWrapper.set("is_track_sequence_complete", 0);
//            updateWrapper.eq("flow_id", flowId);
//            updateWrapper.eq("next_opt_sequence", item.getOriginalOptSequence());
//            this.update(updateWrapper);
//        }
//        //生产线状态改为在制
//        UpdateWrapper<TrackFlow> updateWrapperTrackFlow = new UpdateWrapper<>();
//        updateWrapperTrackFlow.set("status", "1");
//        updateWrapperTrackFlow.eq("id", item.getFlowId());
//        trackHeadFlowService.update(updateWrapperTrackFlow);
//        //重置跟单状态
//        trackHeadService.trackHeadData(item.getTrackHeadId());
//        return "success";
//    }


    @Override
    public void addItemByTrackHead(TrackHead trackHead, List<TrackItem> trackItems, String productsNo, Integer number, String flowId, String priority) {
        if (trackItems != null && trackItems.size() > 0) {
            int i = 1;
            for (TrackItem item : trackItems) {
                item.setId(UUID.randomUUID().toString().replace("-", ""));
                item.setTrackHeadId(trackHead.getId());
                item.setDrawingNo(trackHead.getDrawingNo());
                item.setFlowId(flowId);
                item.setProductNo(productsNo);
                item.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                //可分配数量
                item.setAssignableQty(number);
                item.setNumber(number);
                item.setIsSchedule(0);
                item.setIsPrepare(0);
                item.setIsNotarize(0);
                item.setIsDoing(0);
                item.setIsScheduleCompleteShow(1);
                item.setOptSequence(i++);
                item.setPriority(priority);
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
            if (StrUtil.isNotBlank(disqualification.getDisqualificationType())) {
                disqualification.setTypeList(Arrays.asList(disqualification.getDisqualificationType().split(",")));
            }
            return disqualification;
        }
        DisqualificationItemVo item = new DisqualificationItemVo();
        TrackItem trackItem = this.getById(tiId);
        if (null == trackItem) {
            return null;
        }
        TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
        item.trackHead(trackHead);
        //产品编号处理
        String produceNo = trackHead.getProductNoDesc().replaceAll(trackHead.getDrawingNo() + " ", "");
        item.setProductNo(produceNo);
        //不合格品数量
        item.setDisqualificationNum(trackItem.getQualityUnqty());
        //工序名称
        item.setItemName(trackItem.getOptName());
        //工序类型
        item.setItemType(trackItem.getOptType());
        //不合格产品送出车间
        item.setMissiveBranch(trackItem.getBranchCode());
        item.setBranchCode(trackItem.getBranchCode());
        item.setTenantId(trackItem.getTenantId());
        item.setTrackHeadType(trackHead.getTrackType());
        return item;
    }

    @Override
    public List<TrackItem> queryItemByTrackHeadId(String trackHeadId) {
        TrackItem item = trackItemMapper.getTrackItemByHeadId(trackHeadId);
        if (item != null) {
            QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("flow_id", item.getFlowId());
            queryWrapper.orderByAsc("sequence_order_by");
            return this.list(queryWrapper);
        } else {
            List<TrackItem> trackItemScheduleList = new ArrayList<>();
            QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
            if (!StringUtils.isNullOrEmpty(trackHeadId)) {
                queryWrapper.eq("track_head_id", trackHeadId);
            }
            queryWrapper.orderByAsc("sequence_order_by");
            List<TrackItem> trackItems = this.list(queryWrapper);
            List<TrackItem> trackItemList = new ArrayList<>();
            //将相同的工序进行合并
            for (TrackItem trackItem : trackItems) {
                boolean flag = true;
                for (TrackItem ti : trackItemList) {
                    if (ti.getOptSequence().equals(trackItem.getOptSequence())) {
                        flag = false;
                    }
                }
                //trackItemList集合中没有的工序添加进来
                if (flag) {
                    trackItemList.add(trackItem);
                }
                if (trackItem.getIsSchedule() != null && trackItem.getIsSchedule() == 1) {
                    trackItemScheduleList.add(trackItem);
                }
            }
            return trackItemList;
        }
    }

    @Override
    public List<TrackItem> queryItemByThId(String trackHeadId) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_head_id", trackHeadId);
        queryWrapper.orderByAsc("opt_sequence");
        return this.list(queryWrapper);
    }

    @Override
    public void exportHeatTrackLabel(HttpServletResponse response, String id, String classes) {
        //获取当前租户信息
        Tenant tenant = systemServiceClient.getTenantById(SecurityUtils.getCurrentUser().getTenantId()).getData();
        //热处理车间导出
        if ("5".equals(classes)) {
            //热处理车间传入id为预装炉id 根据预装炉id获取跟单工序表
            QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<TrackItem>();
            queryWrapper.eq("precharge_furnace_id", id);
            List<TrackItem> trackItemList = trackItemMapper.selectList(queryWrapper);
            if (!trackItemList.isEmpty()) {
                //通过模板读入文件流
                ClassPathResource classPathResource = new ClassPathResource("excel/" + "heatTreatLabel.xlsx");
                int sheetNum = 0;
                try {
                    ExcelWriter writer = ExcelUtil.getReader(classPathResource.getInputStream()).getWriter();
                    XSSFWorkbook wk = (XSSFWorkbook) writer.getWorkbook();
                    for (TrackItem trackItem : trackItemList) {
                        if (sheetNum > 0) {
                            writer.setSheet(wk.cloneSheet(0));
                        }

                        //获取跟单信息
                        TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
                        //获取预装炉信息
                        PrechargeFurnace prechargeFurnace = prechargeFurnaceMapper.selectById(id);
                        //获取车间信息
                        Branch branch = baseServiceClient.selectBranchByCodeAndTenantId(trackItem.getBranchCode(), tenant.getId()).getData();
                        buildSheetInfo(tenant, trackItem, trackHead, prechargeFurnace, branch, writer);
                        writer.renameSheet(sheetNum, "sheet" + (++sheetNum));
                    }
                    buildResponseHead(response, writer);

                } catch (Exception e) {
                    log.error(e.getMessage());
                }
            }
        }
        //锻造车间导出
        if ("4".equals(classes)) {
            //装配车间传入id为 item_id
            TrackItem trackItem = trackItemMapper.selectById(id);
            if (ObjectUtil.isEmpty(trackItem)) {
                throw new GlobalException("item_id不存在！", ResultCode.FAILED);
            }
            //根据item中的track_head id 查询跟单信息
            TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
            PrechargeFurnace prechargeFurnace = null;
            if (trackItem.getPrechargeFurnaceId() != null) {
                prechargeFurnace = prechargeFurnaceMapper.selectById(trackItem.getPrechargeFurnaceId());
            }
            //获取车间信息
            Branch branch = baseServiceClient.selectBranchByCodeAndTenantId(trackItem.getBranchCode(), tenant.getId()).getData();
            ClassPathResource classPathResource = new ClassPathResource("excel/" + "heatTreatLabel.xlsx");
            try {
                ExcelWriter writer = ExcelUtil.getReader(classPathResource.getInputStream()).getWriter();
                buildSheetInfo(tenant, trackItem, trackHead, prechargeFurnace, branch, writer);
                buildResponseHead(response, writer);

            } catch (Exception e) {
                log.error(e.getMessage());
            }


        }

    }

    private void buildResponseHead(HttpServletResponse response, ExcelWriter writer) throws IOException {
        ServletOutputStream outputStream = response.getOutputStream();
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet;charset=utf-8");
        String time = "heatTreatLabel" + LocalDateTime.now();
        response.setHeader("Content-disposition", "attachment; filename=" + new String(time.getBytes("utf-8"),
                "ISO-8859-1") + ".xlsx");
        writer.flush(outputStream, true);
        IoUtil.close(outputStream);
    }

    private void buildSheetInfo(Tenant tenant, TrackItem trackItem, TrackHead trackHead, PrechargeFurnace prechargeFurnace, Branch branch, ExcelWriter writer) {
        writer.writeCellValue("B2", trackHead == null ? "" : trackHead.getWorkNo());
        writer.writeCellValue("E2", trackItem == null ? "" : trackItem.getOptName());
        writer.writeCellValue("B3", trackHead == null ? "" : trackHead.getDrawingNo());
        writer.writeCellValue("E3", trackItem == null ? "" : trackItem.getNumber());
        writer.writeCellValue("B4", trackItem == null ? "" : trackItem.getBranchCode());
        writer.writeCellValue("E4", tenant.getTenantName() + branch.getBranchName());
        writer.writeCellValue("B5", trackHead == null ? "" : trackHead.getBatchNo());
        writer.writeCellValue("E5", prechargeFurnace == null ? "" : prechargeFurnace.getFurnaceNo());
        writer.writeCellValue("B6", trackItem == null ? "" : trackItem.getRemark());
    }

    @Override
    public DisqualificationItemVo queryDisqualificationByItem(String tiId, String branchCode) {
        TrackItem trackItem = this.getById(tiId);
        if (StrUtil.isNotBlank(trackItem.getDisqualificationId())) {
            return disqualificationService.inquiryRequestFormNew(trackItem.getDisqualificationId(), branchCode);
        }
        DisqualificationItemVo disqualification = new DisqualificationItemVo();
        TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
        disqualification.trackHead(trackHead);
        //产品编号处理
        String produceNo = trackHead.getProductNoDesc().replaceAll(trackHead.getDrawingNo() + " ", "");
        disqualification.setProductNo(produceNo);
        //不合格品数量
        disqualification.setDisqualificationNum(trackItem.getQualityUnqty());
        //工序名称
        disqualification.setItemName(trackItem.getOptName());
        //工序类型
        disqualification.setItemType(trackItem.getOptType());
        //不合格产品送出车间
        disqualification.setMissiveBranch(trackItem.getBranchCode());
        disqualification.setBranchCode(trackItem.getBranchCode());
        disqualification.setTenantId(trackItem.getTenantId());
        disqualification.setTrackHeadType(trackHead.getTrackType());
        disqualification.setType(1);
        //查询上次提交记录
        DisqualificationItemVo data = disqualificationService.queryLastTimeDataByCreateBy(branchCode);
        if (data != null) {
            disqualification.setQualityCheckBy(data.getQualityCheckBy());
            disqualification.setTypeList(Arrays.asList(data.getDisqualificationType().split(",")));
            disqualification.setDisqualificationType(data.getDisqualificationType());
            disqualification.setUnitResponsibilityWithin(data.getUnitResponsibilityWithin());
        }
        return disqualification;
    }

    @Override
    public List<TrackItem> getTrackItemList(Wrapper<TrackItem> wrapper) {
        List<TrackItem> trackItemList = trackItemMapper.getTrackItemList(wrapper);
        //工艺ids
        List<String> routerIdAndBranchCodeList = new ArrayList<>(trackItemList.stream().map(item -> item.getRouterId()+"_"+item.getBranchCode()).collect(Collectors.toSet()));
        List<Router> getRouter = baseServiceClient.getRouterByIdAndBranchCode(routerIdAndBranchCodeList).getData();
        if(!CollectionUtil.isEmpty(getRouter)){
            Map<String, Router> routerMap = getRouter.stream().collect(Collectors.toMap(item -> item.getId()+"_"+item.getBranchCode(), Function.identity()));
            for (TrackItem trackItem : trackItemList) {
                Router router = routerMap.get(trackItem.getRouterId()+"_"+trackItem.getBranchCode());
                if(ObjectUtil.isEmpty(router)){
                    trackItem.setWeightMolten(router.getWeightMolten());
                    trackItem.setTexture(router.getTexture());
                }
            }
        }

        return null;
    }
}
