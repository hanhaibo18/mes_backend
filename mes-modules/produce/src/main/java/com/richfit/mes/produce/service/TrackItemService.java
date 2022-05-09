package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.entity.QueryDto;
import com.richfit.mes.produce.entity.QueryFlawDetectionDto;

import java.util.List;

/**
 * @author 王瑞
 * @Description 跟单工序
 */
public interface TrackItemService extends IService<TrackItem> {
    List<TrackItem> selectTrackItem(QueryWrapper<TrackItem> query);

    List<TrackItem> selectTrackItemAssign(QueryWrapper<TrackItem> query);

    /**
     * 功能描述: 根据跟单ID查询跟单工序
     *
     * @param trackNo
     * @Author: xinYu.hou
     * @Date: 2022/5/9 8:02
     * @return: List<TrackItem>
     **/
    List<TrackItem> queryTrackItemByTrackNo(String trackNo);

    IPage<TrackItem> queryFlawDetectionList(QueryDto<QueryFlawDetectionDto> queryDto);
}
