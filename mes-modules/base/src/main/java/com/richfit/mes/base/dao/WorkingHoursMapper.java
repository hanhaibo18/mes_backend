package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.base.WorkingHours;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WorkingHoursMapper extends BaseMapper<WorkingHours> {

    @Select("select SUM(sequence.prepare_end_hours) prepareEndHours,SUM(sequence.single_piece_hours) singlePieceHours,sum(sequence.prepare_end_hours+sequence.single_piece_hours) totalProductiveHours,opt_name from base_router router left join base_sequence sequence on router.id=sequence.router_id  GROUP BY opt_name")
    List<WorkingHours> selectOrderTime();

}
