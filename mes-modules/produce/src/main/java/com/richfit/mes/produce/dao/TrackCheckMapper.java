package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.richfit.mes.common.model.produce.TrackCheck;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author 马峰
 * @Description 质检Mapper
 */
@Mapper
public interface TrackCheckMapper extends BaseMapper<TrackCheck> {

    /**
     * 功能描述: 查询过滤已质检信息
     *
     * @param page
     * @param query
     * @Author: xinYu.hou
     * @Date: 2022/11/23 20:19
     * @return: IPage<TrackCheck>
     **/
    @Select("SELECT track.* FROM produce_track_check track  LEFT JOIN produce_track_item item ON track.ti_id = item.id ${ew.customSqlSegment}")
    IPage<TrackCheck> queryTrackCheckPage(IPage<TrackCheck> page, @Param(Constants.WRAPPER) Wrapper<TrackCheck> query);


}
