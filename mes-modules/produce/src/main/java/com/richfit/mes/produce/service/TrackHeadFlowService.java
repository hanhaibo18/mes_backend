package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.TrackFlow;
import com.richfit.mes.common.model.produce.TrackHead;

import java.util.List;
import java.util.Map;

/**
 * @author 王瑞
 * @Description 跟单服务
 */
public interface TrackHeadFlowService extends IService<TrackFlow> {
    public List<TrackHead> selectTrackFlowList(Map<String, String> map) throws Exception;
}
