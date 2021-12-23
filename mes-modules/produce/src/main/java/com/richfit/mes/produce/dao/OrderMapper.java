package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.produce.entity.OrderDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: GaoLiang
 * @Date: 2020/8/11 9:16
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {
    IPage<Order> queryOrder(Page<Order> orderPage, @Param("param")OrderDto orderDto);
}
