package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.TrackFlow;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.produce.dao.TrackFlowMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
}
