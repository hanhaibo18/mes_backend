package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.OperationAssign;
import com.richfit.mes.common.model.base.Sequence;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.AssignPerson;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.controller.TrackAssignController;
import com.richfit.mes.produce.enmus.PublicCodeEnum;
import com.richfit.mes.produce.provider.BaseServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * @ClassName: PublicServiceImpl.java
 * @Author: Hou XinYu
 * @Description: 状态流转业务
 * @CreateTime: 2022年07月12日 16:34:00
 */
@Slf4j
@Service
public class PublicServiceImpl implements PublicService {

    @Resource
    private TrackHeadService trackHeadService;
    @Resource
    private TrackItemService trackItemService;
    @Resource
    private TrackAssignService trackAssignService;
    @Resource
    private PlanService planService;
    @Resource
    private LineStoreService lineStoreService;
    @Resource
    private BaseServiceClient baseServiceClient;
    @Resource
    private TrackAssignController trackAssignController;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean publicUpdateState(Map<String, String> map, int code) {
        //派工 暂时关闭派工激活下工序
        if (PublicCodeEnum.DISPATCHING.getCode() == code) {
//            return updateDispatching(map);
            return true;
        }
        //报工
        if (PublicCodeEnum.COMPLETE.getCode() == code) {
            return updateComplete(map);
        }

        //质检
        if (PublicCodeEnum.QUALITY_TESTING.getCode() == code) {
            return updateQualityTesting(map);
        }
        //调度
        if (PublicCodeEnum.DISPATCH.getCode() == code) {
            return updateDispatch(map);
        }
        return false;
    }

    /**
     * 功能描述: 派工
     *
     * @Author: xinYu.hou
     * @Date: 2022/8/23 15:08
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateDispatching(Map<String, String> map) {
        String tiId = map.get("trackItemId");
        TrackItem trackItem = trackItemService.getById(tiId);

        boolean activation = false;
        if (null == trackItem) {
            return false;
        } else {
            if (0 == trackItem.getAssignableQty()) {
                if (trackItem.getIsCurrent() == 1 && trackItem.getIsExistScheduleCheck() == 0 && trackItem.getIsExistQualityCheck() == 0) {
                    //激活下工序
                    TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
                    if (!StringUtils.isNullOrEmpty(trackHead.getWorkPlanId())) {
                        //更新计划的工序以完成数量
                        planService.planData(trackHead.getWorkPlanId());
                    }
                    activation = activation(trackItem);
                }
            }
        }
        return activation;
    }

    /**
     * 功能描述: 报工
     *
     * @Author: xinYu.hou
     * @Date: 2022/8/23 15:08
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateComplete(Map<String, String> map) {

        String tiId = map.get("trackItemId");
        TrackItem trackItem = trackItemService.getById(tiId);

        String assignId = map.get("assignId");
        Assign assignById = trackAssignService.getById(assignId);

        int isComplete = 1;

        //派工状态设置为完成
        assignById.setState(2);
        trackAssignService.updateById(assignById);

        Boolean activationProcess = false;
        boolean isNext = false;

        if (trackItem.getIsExistQualityCheck().equals(0) && trackItem.getIsExistScheduleCheck().equals(0)) {
            trackItem.setIsFinalComplete(String.valueOf(isComplete));
            isNext = true;
        }
        //判断整个工序是否完成，如果完成，则将完成数量和完成状态写入
        double doubleQty = assignById.getQty();
        //trackItem.getCompleteQty() == assignById.getQty()
        if (assignById.getQty() > trackItem.getCompleteQty() && trackItem.getCompleteQty() > doubleQty - 0.1) {
            QueryWrapper<Assign> queryWrapper = new QueryWrapper<Assign>();
            queryWrapper.eq("ti_id", trackItem.getId());
            List<Assign> assigns = trackAssignService.list(queryWrapper);
            double sum = 0;

            for (Assign assign : assigns) {
                //判断当前工序其他派工是否完成
                if (!assign.getId().equals(assignById.getId()) && assign.getState() != 2) {
                    isComplete = 0;
                }
                //计算派工数量
                sum += assign.getQty();
            }

            trackItem.setOperationCompleteTime(new Date());
            trackItem.setCompleteQty(sum);
            //当前工序是否完成
            trackItem.setIsOperationComplete(isComplete);
            trackItem.setOperationCompleteTime(new Date());
            trackItemService.updateById(trackItem);
        }
        if (isNext) {
            TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
            if (null != trackHead.getWorkPlanId()) {
                planService.planData(trackHead.getWorkPlanId());
            }
            activationProcess = this.activationProcess(map);
        }
        return activationProcess;
    }

    /**
     * 功能描述: 质检
     *
     * @Author: xinYu.hou
     * @Date: 2022/8/23 15:08
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateQualityTesting(Map<String, String> map) {
        String tiId = map.get("trackItemId");
        TrackItem trackItem = trackItemService.getById(tiId);
        //质检完成
        trackItem.setIsQualityComplete(1);
        trackItem.setQualityResult(1);
        trackItem.setQualityCompleteTime(new Date());
        //如果不需要调度审核，则将工序设置为完成，并激活下个工序
        if (trackItem.getIsExistScheduleCheck() == 0 && trackItem.getIsQualityComplete() == 1) {
            trackItem.setIsOperationComplete(1);
            trackItem.setOperationCompleteTime(new Date());
            trackItem.setIsFinalComplete("1");
            trackItem.setCompleteQty(trackItem.getBatchQty().doubleValue());
            trackItemService.updateById(trackItem);
            TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
            if (!StringUtils.isNullOrEmpty(trackHead.getWorkPlanId())) {
                planService.planData(trackHead.getWorkPlanId());
            }
            return activationProcess(map);
        }
        trackItemService.updateById(trackItem);
        return true;
    }

    /**
     * 功能描述: 调度
     *
     * @Author: xinYu.hou
     * @Date: 2022/8/23 15:08
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean updateDispatch(Map<String, String> map) {
        String tiId = map.get("trackItemId");
        TrackItem trackItem = trackItemService.getById(tiId);
        TrackHead trackHead = trackHeadService.getById(map.get("trackHeadId"));
        if (0 == trackItem.getNextOptSequence()) {
            trackHeadService.trackHeadFinish(trackItem.getFlowId());
            //设置产品完工
            lineStoreService.changeStatus(trackHead);
            //设置计划状态
            planService.updatePlanStatus(trackHead.getWorkPlanNo(), trackHead.getTenantId());
        } else {
            trackItem.setIsFinalComplete("1");
            trackItem.setCompleteQty(trackItem.getBatchQty().doubleValue());
            if (null != SecurityUtils.getCurrentUser()) {
                trackItem.setScheduleCompleteBy(SecurityUtils.getCurrentUser().getUsername());
            }
            trackItem.setScheduleCompleteTime(new Date());
            trackItem.setIsOperationComplete(1);
            trackItem.setOperationCompleteTime(new Date());
            trackItem.setIsFinalComplete("1");
            trackItemService.updateById(trackItem);
        }
        if (!StringUtils.isNullOrEmpty(trackHead.getWorkPlanId())) {
            planService.planData(trackHead.getWorkPlanId());
        }
        return this.activationProcess(map);
    }

    /**
     * 功能描述: 激活工序
     *
     * @Author: xinYu.hou
     * @Date: 2022/8/23 15:08
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean activationProcess(Map<String, String> map) {
        //倒序获取工序列表
        QueryWrapper<TrackItem> currentTrackItem = new QueryWrapper<>();
        currentTrackItem.eq("flow_id", map.get("flowId"));
        currentTrackItem.eq("is_current", 1);
        currentTrackItem.orderByDesc("sequence_order_by");
        List<TrackItem> currentTrackItemList = trackItemService.list(currentTrackItem);
        for (TrackItem trackItem : currentTrackItemList) {
            trackItem.setIsCurrent(0);
            trackItemService.updateById(trackItem);
        }

        //判断还有没有下工序
        if (currentTrackItemList.get(0).getNextOptSequence() == 0) {
            trackHeadService.trackHeadFinish(map.get("flowId"));
            return true;
        }
        boolean activation = false;
        if (!currentTrackItemList.isEmpty() && currentTrackItemList.get(0).getNextOptSequence() != 0) {
            //激活下工序
            activation = activation(currentTrackItemList.get(0));
        }
        return activation;
    }

    /**
     * 功能描述: 第三方激活
     *
     * @Author: xinYu.hou
     * @Date: 2022/8/23 15:08
     **/
    @Override
    public Boolean thirdPartyAction(String trackHeadId, String certificateId, List<String> optSequenceList) {
        if (StringUtils.isNullOrEmpty(certificateId)) {
            return false;
        }
        if (optSequenceList.isEmpty()) {
            return false;
        }
        //通过跟单ID和工序顺序倒序查询工序
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_head_id", trackHeadId);
        queryWrapper.in("opt_sequence", optSequenceList);
        queryWrapper.orderByDesc("opt_sequence");
        List<TrackItem> trackItemList = trackItemService.list(queryWrapper);
        //向最后面的工序插入合格证
        trackItemList.get(0).setCertificateNo(certificateId);
        for (TrackItem trackItem : trackItemList) {
            //是否是当前工序
            trackItem.setIsCurrent(0);
            //3 == 完工
            trackItem.setIsDoing(3);
            //当前工序是否完成
            trackItem.setIsOperationComplete(1);
        }
        //设置当前工序在传入的最后一道工序上
        trackItemList.get(0).setIsCurrent(1);
        trackItemService.updateBatchById(trackItemList);
        //判断是否质检,只判断最后一道工序
        boolean isNext = false;
        TrackItem lastTrackItem = trackItemList.get(0);
        //不质检,不调度
        if (0 == lastTrackItem.getIsExistQualityCheck() && 0 == lastTrackItem.getIsExistScheduleCheck()) {
            isNext = true;
        }
        //已质检完成,不调度
        if (1 == lastTrackItem.getIsExistQualityCheck() && 1 == lastTrackItem.getIsQualityComplete() && 0 == lastTrackItem.getIsExistScheduleCheck()) {
            isNext = true;
        }
        //调度完成
        if (1 == lastTrackItem.getIsExistScheduleCheck() && 1 == lastTrackItem.getIsScheduleComplete()) {
            isNext = true;
        }
        //判断还有没有下工序
        TrackHead trackHead = trackHeadService.getById(trackHeadId);
        //没有下工序设置完工
        if (lastTrackItem.getNextOptSequence() == 0) {
            //设置产品完工
            lineStoreService.changeStatus(trackHead);
            //跟单完成
            trackHeadService.trackHeadFinish(trackHeadId);
            //设置计划状态
            if (!StringUtils.isNullOrEmpty(trackHead.getWorkPlanId())) {
                planService.updatePlanStatus(trackHead.getWorkPlanNo(), trackHead.getTenantId());
            }
            return true;
        }
        boolean activation = false;
        if (isNext) {
            //有下工序的时候有计划,触发计划计算工序
            if (!StringUtils.isNullOrEmpty(trackHead.getWorkPlanId())) {
                planService.planData(trackHead.getWorkPlanId());
            }
            //下工序激活
            activation = activation(trackItemList.get(0));
        }
        return activation;
    }

    /**
     * 功能描述: 自动派工
     *
     * @Author: xinYu.hou
     * @Date: 2022/8/23 15:08
     **/
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean automaticProcess(Map<String, String> map) {
        TrackItem trackItem = trackItemService.getById(map.get("trackItemId"));
        TrackHead trackHead = trackHeadService.getById(map.get("trackHeadId"));
        CommonResult<Sequence> sequence = baseServiceClient.querySequenceById(trackItem.getOptId());
        CommonResult<OperationAssign> assignGet = baseServiceClient.assignGet(sequence.getData().getOptId());
        if (null == assignGet.getData()) {
            return false;
        }
        Assign assign = new Assign();
        assign.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        assign.setBranchCode(trackItem.getBranchCode());
        assign.setTiId(trackItem.getId());
        assign.setTrackId(trackItem.getTrackHeadId());
        assign.setUserId(assignGet.getData().getUserId());
        assign.setEmplName(assignGet.getData().getUserName());
        assign.setSiteId(assignGet.getData().getSiteId());
        assign.setSiteName(assignGet.getData().getSiteName());
        assign.setDeviceId(assignGet.getData().getDeviceId());
        assign.setDeviceName(assignGet.getData().getDeviceName());
        assign.setPriority(assignGet.getData().getPriority());
        assign.setQty(assignGet.getData().getQty());
        assign.setAvailQty(assignGet.getData().getQty());
        assign.setState(0);
        assign.setAssignBy(assignGet.getData().getCreateBy());
        assign.setAssignTime(new Date());
        assign.setTrackNo(trackHead.getTrackNo());
        assign.setClasses(trackHead.getClasses());
        List<AssignPerson> list = new ArrayList<>();
        AssignPerson assignPerson = new AssignPerson();
        assignPerson.setUserId(assignGet.getData().getUserId());
        assignPerson.setUserName(assignGet.getData().getUserName());
        list.add(assignPerson);
        assign.setAssignPersons(list);
        CommonResult<Assign[]> commonResult = trackAssignController.batchAssign(new Assign[]{assign});
        trackItem.setIsSchedule(1);
        trackItemService.updateById(trackItem);
        //查询下工序,是否为并行工序依据并行工序来判断下工序是否激活
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("flow_id", trackItem.getFlowId());
        queryWrapper.eq("original_opt_sequence", trackItem.getNextOptSequence());
        TrackItem trackItemEntity = trackItemService.getOne(queryWrapper);
        if (null != trackItemEntity && 1 == trackItemEntity.getOptParallelType()) {
            activation(trackItem);
        }
        return null != commonResult.getData();
    }

    /**
     * 功能描述: 激活
     *
     * @Author: xinYu.hou
     * @Date: 2022/8/23 15:08
     **/
    private boolean activation(TrackItem trackItem) {
        //激活下工序
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("flow_id", trackItem.getFlowId());
        queryWrapper.eq("original_opt_sequence", trackItem.getNextOptSequence());
        TrackItem trackItemEntity = trackItemService.getOne(queryWrapper);
        trackItemEntity.setIsCurrent(1);
        trackItemEntity.setModifyTime(new Date());
        if (1 == trackItemEntity.getIsAutoSchedule()) {
            Map<String, String> map = new HashMap<>(2);
            map.put("trackItemId", trackItemEntity.getId());
            map.put("trackHeadId", trackItemEntity.getTrackHeadId());
            automaticProcess(map);
            trackItemEntity.setIsSchedule(1);
        }
        boolean update = trackItemService.updateById(trackItemEntity);
        if (trackItemEntity.getOptParallelType() == 1 && trackItemEntity.getNextOptSequence() != 0) {
            queryTrackItemList(trackItemEntity);
        }
        return update;
    }

    /**
     * 功能描述: 激活
     *
     * @Author: xinYu.hou
     * @Date: 2022/8/23 15:08
     **/
    private void queryTrackItemList(TrackItem trackItems) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("flow_id", trackItems.getFlowId());
        queryWrapper.eq("original_opt_sequence", trackItems.getNextOptSequence());
        TrackItem trackItem = trackItemService.getOne(queryWrapper);
        trackItem.setIsCurrent(1);
        trackItem.setModifyTime(new Date());
        if (1 == trackItem.getIsAutoSchedule()) {
            Map<String, String> map = new HashMap<>(2);
            map.put("trackItemId", trackItem.getId());
            map.put("trackHeadId", trackItem.getTrackHeadId());
            automaticProcess(map);
            trackItem.setIsSchedule(1);
        }
        trackItemService.updateById(trackItem);
        if (trackItem.getOptParallelType() == 1 && trackItem.getNextOptSequence() != 0) {
            queryTrackItemList(trackItems);
        }
    }


}
