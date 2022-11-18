package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.produce.entity.OrderDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2020/8/11 9:16
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    IPage<Order> queryOrderList(Page<Order> orderPage, @Param("param") OrderDto orderDto);

    List<Order> queryOrderListNoPage(@Param("param") OrderDto orderDto);

    Order queryOrder(@Param("id") String id);


    //精准匹配MaterialCode
    List<Order> queryOrderListEqMaterialCode(@Param("param") OrderDto orderDto);
}
