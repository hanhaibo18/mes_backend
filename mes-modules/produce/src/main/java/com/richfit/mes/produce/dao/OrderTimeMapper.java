package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.hourSum.OrderTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderTimeMapper extends BaseMapper<OrderTime> {



    @Select("select orderNo,drawNo,endHours,pieceHours,(endHours+pieceHours) as sumHours  from v_produce_track_order_plan_head_item ${ew.customSqlSegment}")
    List<OrderTime> query(Page<OrderTime> orderTimePage, @Param(Constants.WRAPPER) QueryWrapper<List> wrapper);



}
