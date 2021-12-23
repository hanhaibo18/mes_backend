package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.TrackItem;

import java.util.List;

/**
 * @author 王瑞
 * @Description 跟单工序
 */
public interface TrackItemService  extends IService<TrackItem> {
    List<TrackItem> selectTrackItem(QueryWrapper<TrackItem> query);

    List<TrackItem> selectTrackItemAssign(QueryWrapper<TrackItem> query);
}
