package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.TrackCheck;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.dao.TrackCheckCountMapper;
import com.richfit.mes.produce.dao.TrackCheckMapper;
import com.richfit.mes.produce.entity.CountDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author mafeng
 * @Description 质检结果
 */
@Service
public class TrackCheckServiceImpl extends ServiceImpl<TrackCheckMapper, TrackCheck> implements TrackCheckService {

    @Autowired
    private TrackCheckMapper trackCheckMapper;

    @Autowired
    private TrackCheckCountMapper trackCheckCountMapper;

    @Resource
    private TrackItemService trackItemService;

    public List<CountDto> count(String dateType, String startTime, @Param("endTime") String endTime) {
        return trackCheckCountMapper.count(dateType, startTime, endTime);
    }

    @Override
    public List<TrackItem> getItemList(String tiId) {
        TrackItem trackItem = trackItemService.getById(tiId);
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_head_id", trackItem.getTrackHeadId());
        queryWrapper.gt("opt_sequence", trackItem.getOptSequence());
        queryWrapper.orderByAsc("opt_sequence");
        return trackItemService.list(queryWrapper);
    }

}
