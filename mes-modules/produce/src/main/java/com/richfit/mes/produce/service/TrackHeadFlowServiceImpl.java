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
    @Autowired
    public TrackFlowMapper trackFlowMapper;

    @Override
    public List<TrackHead> selectTrackFlowList(Map<String, String> map) throws Exception {
        return trackFlowMapper.selectTrackFlowList(map);
    }
}
