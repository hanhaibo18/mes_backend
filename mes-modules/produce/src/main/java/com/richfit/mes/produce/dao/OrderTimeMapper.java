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

    //    @Select("select * from produce_plan plan\n" +
//            "\t left join produce_order ordere on plan.order_no=ordere.order_sn\n" +
//            "\t LEFT JOIN produce_track_head head on head.work_plan_id=plan.id\n" +
//            "\t LEFT JOIN produce_track_item item  on item.track_head_id=head.id\n" +
//            "\t LEFT JOIN produce_track_complete complete on complete.track_id=head.id ${ew.customSqlSegment}")
//    List<OrderTime> query(Page<OrderTime> orderTimePage, @Param(Constants.WRAPPER) QueryWrapper<List> wrapper );

    //根据订单号查询准结工时，额定工时，总工时
//    @Select("  SELECT  plan.order_no as orderno,plan.draw_no as drawno,item.prepare_end_hours as endHours,item.single_piece_hours as piecehours,(item.prepare_end_hours+item.single_piece_hours) as totalProductiveHours\n" +
//            "  FROM produce_plan plan\n" +
//            "  LEFT JOIN produce_order ordere on plan.order_no=ordere.order_sn\n" +
//            "  LEFT JOIN produce_track_head head on head.work_plan_id=plan.id\n" +
//            "  LEFT JOIN produce_track_item item  on item.track_head_id=head.id\n" +
//            "  LEFT JOIN produce_track_complete complete on complete.track_id=head.id ${ew.customSqlSegment}")
//    List<OrderTime> query(Page<OrderTime> orderTimePage, @Param(Constants.WRAPPER) QueryWrapper<List> wrapper);
    @Select("  select plan.order_no as orderNo,plan.draw_no as drawNo,ordere.order_date orderDate,\n" +
            "item.prepare_end_hours as endHours,item.single_piece_hours as pieceHours,(item.prepare_end_hours+item.single_piece_hours) as sumhours\n" +
            "FROM produce_order as ordere \n" +
            "LEFT JOIN produce_plan as plan  on  ordere.order_sn= plan.order_no\n" +
            "LEFT JOIN produce_track_head head on plan.id=head.work_plan_id\n" +
            "LEFT JOIN produce_track_item item on item.track_head_id=head.id ${ew.customSqlSegment}")
    List<OrderTime> query(Page<OrderTime> orderTimePage, @Param(Constants.WRAPPER) QueryWrapper<List> wrapper);



}
