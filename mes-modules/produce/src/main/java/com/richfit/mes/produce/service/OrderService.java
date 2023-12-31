package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.common.model.produce.Plan;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.produce.entity.OrderDto;

import javax.servlet.http.HttpServletRequest;
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
     * @param trackHead 跟单信息
     */
    void orderDataTrackHead(TrackHead trackHead);

    /**
     * 功能描述: 通过订单订单号码更新订单的计划数量、毛胚使用数量方法
     *
     * @param branchCode 车间编码
     * @param orderNo    订单号码
     */
    void orderDataUsed(String branchCode, String orderNo);

    Order deleteOrder(String id);

    boolean saveByPlan(List<Plan> plans);
}
