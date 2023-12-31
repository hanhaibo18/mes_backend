package com.richfit.mes.produce.service;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.TrackFlow;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.produce.dao.TrackFlowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhiqiang.lu
 * @date 2022.8.25
 */
@Service
@Transactional
public class TrackHeadFlowServiceImpl extends ServiceImpl<TrackFlowMapper, TrackFlow> implements TrackHeadFlowService {


    @Autowired
    public TrackFlowMapper trackFlowMapper;

    @Override
    public List<TrackHead> selectTrackFlowList(Map<String, String> map) {
        return trackFlowMapper.selectTrackFlowList(map);
    }

    @Override
    public void examineCard(String flowId, String approved) {
        TrackFlow trackFlow = this.getById(flowId);
        if (TrackFlow.EXAMINE_CARD_DATA_YES.equals(approved)) {
            trackFlow.setIsExamineCardData(approved);
            trackFlow.setIsCardData(TrackFlow.CARD_DATA_YES);
        } else {
            trackFlow.setIsExamineCardData(TrackFlow.EXAMINE_CARD_DATA_NO);
            trackFlow.setIsCardData(TrackFlow.CARD_DATA_NO);
        }
        this.updateById(trackFlow);
    }

    @Override
    public List<TrackFlow> queryTrackFlowListByTrackHeadId(String trackHeadId) {
        QueryWrapper<TrackFlow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_head_id", trackHeadId);
        return this.list(queryWrapper);
    }

    @Override
    public List<TrackFlow> selectFlowList(Map<String, String> map) {
        List<TrackFlow> trackFlows = new ArrayList<>();
        List<TrackHead> trackHeads = trackFlowMapper.selectTrackFlowList(map);
        for (TrackHead trackHead : trackHeads) {
            TrackFlow trackFlow = JSON.parseObject(JSON.toJSONString(trackHead), TrackFlow.class);
            trackFlows.add(trackFlow);
        }
        return trackFlows;
    }
}
