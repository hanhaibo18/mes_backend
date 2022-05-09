package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.dao.TrackItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author 王瑞
 * @Description 跟单工序服务
 */
@Service
public class TrackItemServiceImpl extends ServiceImpl<TrackItemMapper, TrackItem> implements TrackItemService {

    @Autowired
    private TrackItemMapper trackItemMapper;

    @Override
    public List<TrackItem> selectTrackItem(QueryWrapper<TrackItem> query) {
        return trackItemMapper.selectTrackItem(query);
    }

    @Override
    public List<TrackItem> selectTrackItemAssign(QueryWrapper<TrackItem> query) {
        return trackItemMapper.selectTrackItemAssign(query);
    }

    @Override
    public List<TrackItem> queryTrackItemByTrackNo(String trackNo) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_head_id", trackNo);
        return this.list(queryWrapper);
    }

}
