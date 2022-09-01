package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.model.produce.TrackItemInspection;
import com.richfit.mes.produce.dao.TrackItemInspectionMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author renzewen
 * @Description 跟单探伤工序
 */
@Service
public class TrackItemInspectionServiceImpl extends ServiceImpl<TrackItemInspectionMapper, TrackItemInspection> implements TrackItemInspectionService {

    @Resource
    private TrackItemService trackItemService;

    @Override
    public boolean saveItem(String trackItemId) {
        TrackItem trackItem = trackItemService.getById(trackItemId);
        TrackItemInspection inspection = new TrackItemInspection();
        BeanUtils.copyProperties(trackItem, inspection);
        return this.save(inspection);
    }
}
