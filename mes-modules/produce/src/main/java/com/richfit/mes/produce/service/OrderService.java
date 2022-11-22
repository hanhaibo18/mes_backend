package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.produce.entity.OrderDto;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2020/8/11 9:08
 */
public interface OrderService extends IService<Order> {
    IPage<Order> queryPage(Page<Order> planPage, OrderDto orderDto);

    List<Order> queryList(OrderDto orderDto);

    //精准匹配MaterialCode
    List<Order> queryPageEqMaterialCode(OrderDto orderDto);

    Order queryOrder(String id);

    void findBranchName(Order order);

    void setOrderStatusStart(String id);

    void setOrderStatusNew(String id);

    void setOrderStatusClose(String id);

    Order findByOrderCode(String orderCode, String tenantId);

    /**
     * 功能描述: 通过订单id更新订单的交货数量方法
     *
     * @param orderId 订单id
     */
    void orderDataTrackHead(TrackHead trackHead);
}
