package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.LineStore;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;

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

}
