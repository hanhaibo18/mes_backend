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
 * @author sun
 * @Description 跟单服务
 */
@Service
@Transactional
public class TrackHeadFlowServiceImpl extends ServiceImpl<TrackFlowMapper, TrackFlow> implements TrackHeadFlowService {
    /**
     * 质量检测卡已审核
     */
    public static final String EXAMINE_CARD_DATA_YES = "Y";

    /**
     * 质量检测卡审核不通过
     */
    public static final String EXAMINE_CARD_DATA_NO = "N";

    /**
     * 质量检测卡资料生成
     */
    public static final String CARD_DATA_YES = "Y";

    /**
     * 质量检测卡资料未生成
     */
    public static final String CARD_DATA_NO = "N";

    @Autowired
    public TrackFlowMapper trackFlowMapper;

    @Override
    public List<TrackHead> selectTrackFlowList(Map<String, String> map) {
        return trackFlowMapper.selectTrackFlowList(map);
    }

    @Override
    public void examineCard(String flowId, String approved) {
        TrackFlow trackFlow = this.getById(flowId);
        if (EXAMINE_CARD_DATA_YES.equals(approved)) {
            trackFlow.setIsExamineCardData(approved);
            trackFlow.setIsCardData(CARD_DATA_YES);
        } else {
            trackFlow.setIsExamineCardData(EXAMINE_CARD_DATA_NO);
            trackFlow.setIsCardData(CARD_DATA_NO);
        }
        this.updateById(trackFlow);
    }
}
