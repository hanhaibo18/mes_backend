package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.TrackFlow;
import com.richfit.mes.common.model.produce.TrackHead;

import java.util.List;
import java.util.Map;

/**
 * @author zhiqiang.lu
 * @date 2022.8.25
 */
public interface TrackHeadFlowService extends IService<TrackFlow> {
    /**
     * 功能描述: 查询跟单与生产线视图
     *
     * @param map 查询条件
     * @return 返回视图列表
     * @author zhiqiang.lu
     * @date 2022.8.25
     */
    List<TrackHead> selectTrackFlowList(Map<String, String> map);

    /**
     * 功能描述: 质量检测卡审核流程
     *
     * @param flowId   生产线id
     * @param approved 是否通过：Y通过，N不通过
     * @author zhiqiang.lu
     * @date 2022.8.25
     */
    void examineCard(String flowId, String approved);


    /**
     * 功能描述: 根据跟单Id 查询生产线列表
     *
     * @param trackHeadId 计划Id
     * @Author: zhiqiang.lu
     * @Date: 2023/4/26 11:42
     * @return: 数量
     **/
    List<TrackFlow> queryTrackFlowListByTrackHeadId(String trackHeadId);
}
