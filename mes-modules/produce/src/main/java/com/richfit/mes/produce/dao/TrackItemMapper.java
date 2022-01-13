package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.richfit.mes.common.model.produce.TrackItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 王瑞
 * @Description 跟单工序Mapper
 */
@Mapper
public interface TrackItemMapper  extends BaseMapper<TrackItem> {

    List<TrackItem> selectTrackItem(@Param(Constants.WRAPPER) Wrapper<TrackItem> query);

    List<TrackItem> selectTrackItemAssign(@Param(Constants.WRAPPER) Wrapper<TrackItem> query);

}
