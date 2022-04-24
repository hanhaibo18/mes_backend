package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.OrderTime;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderTimeMapper extends BaseMapper<OrderTime> {

    @Select("select ordere.order_sn as ordersn,plan.draw_no as drawNo,ordere.start_time startTime,head.product_no as productNo,head.production_order as productionOrder from produce_plan plan\n" +
            "\t left join produce_order ordere on plan.order_no=ordere.order_sn\n" +
            "\t LEFT JOIN produce_track_head head on head.work_plan_id=plan.id\n" +
            "\t LEFT JOIN produce_track_item item  on item.track_head_id=head.id\n" +
            "\t LEFT JOIN produce_track_complete complete on complete.track_id=head.id ${ew.customSqlSegment}")
    List<OrderTime> query(Page<OrderTime> orderTimePage, @Param(Constants.WRAPPER) QueryWrapper<List<OrderTime>> wrapper );
}
