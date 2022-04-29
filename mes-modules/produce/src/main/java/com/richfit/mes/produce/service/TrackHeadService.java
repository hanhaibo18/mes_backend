package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.LineStore;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import org.apache.poi.ss.formula.functions.T;

import java.util.List;

/**
 * @author 王瑞
 * @Description 跟单服务
 */
public interface TrackHeadService extends IService<TrackHead> {

    boolean saveTrackHead(TrackHead trackHead, List<LineStore> lineStores, List<TrackItem> trackItems);

    boolean deleteTrackHead(List<TrackHead> trackHeads);

    IPage<TrackHead> selectTrackHeadRouter(Page<TrackHead> page, QueryWrapper<TrackHead> query);

    IPage<TrackHead> selectTrackHeadCurrentRouter(Page<TrackHead> page, QueryWrapper<TrackHead> query);

    /**
     * 功能描述: 对当前跟单增加计划
     * @Author: xinYu.hou
     * @Date: 2022/4/19 18:07
     * @param documentaryId 跟单Id
     * @param workPlanId 计划Id
     * @return: boolean
     **/
    boolean updateTrackHeadPlan(String documentaryId,String workPlanId);

    /**
     * 功能描述: 根据计划Id 查询跟单
     * @Author: xinYu.hou
     * @Date: 2022/4/20 11:42
     * @param workPlanId 计划Id
     * @return: 数量
     **/
    Integer queryTrackHeadList(String workPlanId);
}
