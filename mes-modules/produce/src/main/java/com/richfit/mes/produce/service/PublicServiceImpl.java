package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.enmus.PublicCodeEnum;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @ClassName: PublicServiceImpl.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年07月12日 16:34:00
 */
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

    @Override
    public Boolean publicUpdateState(Map<String, String> map, int code) {
        //派工
        if (PublicCodeEnum.DISPATCHING.getCode() == code) {
            return null;
        }
        //报工
        if (PublicCodeEnum.COMPLETE.getCode() == code) {
            return updateComplete(map);
        }
        //质检
        if (PublicCodeEnum.QUALITY_TESTING.getCode() == code) {
            return null;
        }
        //调度
        if (PublicCodeEnum.DISPATCH.getCode() == code) {
            return null;
        }
        return null;
    }

    @Override
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
            trackItemService.updateById(trackItem);
            TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
            planService.planData(trackHead.getWorkPlanId());
            if (isNext) {
                //TODO:下工序激活
                String trackHeadId = map.get("trackHeadId");
                activationProcess = this.activationProcess(trackHeadId);
            }
        }
        return activationProcess;
    }

    @Override
    public Boolean activationProcess(String trackHeadId) {
        //倒序获取工序列表
        QueryWrapper<TrackItem> currentTrackItem = new QueryWrapper<>();
        currentTrackItem.eq("track_head_id", trackHeadId);
        currentTrackItem.eq("is_current", 1);
        currentTrackItem.orderByDesc("sequence_order_by");
        List<TrackItem> currentTrackItemList = trackItemService.list(currentTrackItem);
        for (TrackItem trackItem : currentTrackItemList) {
            trackItem.setIsCurrent(0);
        }

        boolean activation = false;
        if (!currentTrackItemList.isEmpty() && currentTrackItemList.get(0).getNextOptSequence() != 0) {
            //激活下工序
            activation = activation(currentTrackItemList.get(0));
        }
        return activation;
    }

    private boolean activation(TrackItem trackItem) {
        //激活下工序
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_head_id", trackItem);
        queryWrapper.eq("opt_sequence", trackItem.getNextOptSequence());
        TrackItem trackItemEntity = trackItemService.getOne(queryWrapper);
        trackItemEntity.setIsCurrent(1);
        trackItemService.updateById(trackItemEntity);
        if (trackItemEntity.getOptParallelType() == 1 && trackItemEntity.getNextOptSequence() != 0) {
            queryTrackItemList(trackItemEntity);
        }
        return true;
    }

    private void queryTrackItemList(TrackItem trackItems) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_head_id", trackItems.getTrackHeadId());
        queryWrapper.eq("opt_sequence", trackItems.getNextOptSequence());
        TrackItem trackItem = trackItemService.getOne(queryWrapper);
        trackItem.setIsCurrent(1);
        trackItemService.updateById(trackItem);
        if (trackItem.getOptParallelType() == 1 && trackItem.getNextOptSequence() != 0) {
            queryTrackItemList(trackItems);
        }
    }
}