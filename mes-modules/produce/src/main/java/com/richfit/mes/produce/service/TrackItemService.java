package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.entity.QueryDto;
import com.richfit.mes.produce.entity.QueryFlawDetectionDto;
import com.richfit.mes.produce.entity.QueryFlawDetectionListDto;

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

    /**
     * 功能描述: 分页查询未探伤
     *
     * @param queryDto
     * @Author: xinYu.hou
     * @Date: 2022/5/11 7:11
     * @return: IPage<TrackItem>
     **/
    IPage<TrackItem> queryFlawDetectionList(QueryDto<QueryFlawDetectionDto> queryDto);

    /**
     * 功能描述: 增加探伤报告
     *
     * @param trackItem
     * @Author: xinYu.hou
     * @Date: 2022/5/11 7:10
     * @return: Boolean
     **/
    Boolean updateFlawDetection(TrackItem trackItem);

    /**
     * 功能描述: 分页查询探伤
     *
     * @param queryDto
     * @Author: xinYu.hou
     * @Date: 2022/5/11 7:11
     * @return: IPage<TrackItem>
     **/
    IPage<TrackItem> queryFlawDetectionPage(QueryDto<QueryFlawDetectionListDto> queryDto);
}
