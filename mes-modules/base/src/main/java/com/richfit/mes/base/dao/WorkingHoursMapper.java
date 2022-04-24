package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.base.WorkingHours;
import com.richfit.mes.common.model.produce.OrderTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WorkingHoursMapper extends BaseMapper<WorkingHours> {

    @Select("select router.draw_no as drawNo,SUM(sequence.prepare_end_hours) prepareEndHours,SUM(sequence.single_piece_hours) singlePieceHours,sum(sequence.prepare_end_hours+sequence.single_piece_hours) totalProductiveHours,opt_name from base_router router left join base_sequence sequence on router.id=sequence.router_id  ${ew.customSqlSegment}")
    List<WorkingHours> selectOrderTime( @Param(Constants.WRAPPER) QueryWrapper<List> wrapper);

}
