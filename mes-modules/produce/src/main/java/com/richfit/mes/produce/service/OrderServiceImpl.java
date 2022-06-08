package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.produce.dao.OrderMapper;
import com.richfit.mes.produce.entity.OrderDto;
import com.richfit.mes.produce.provider.BaseServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2020/8/11 9:09
 */
@Slf4j
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements OrderService {

    final int ORDER_NEW = 0;
    final int ORDER_START = 1;
    final int ORDER_CLOSE = 2;

    @Autowired
    OrderMapper orderMapper;

    @Resource
    private BaseServiceClient baseServiceClient;

    @Override
    public IPage<Order> queryPage(Page<Order> orderPage, OrderDto orderDto) {

        IPage<Order> planList = orderMapper.queryOrderList(orderPage, orderDto);

        List<Branch> branchList = baseServiceClient.selectBranchChildByCode("").getData();

        for (Order order : planList.getRecords()) {
            findBranchName(order, branchList);
        }

        return planList;
    }

    @Override
    public Order queryOrder(String id) {
        return orderMapper.queryOrder(id);
    }


    @Override
    public void findBranchName(Order order) {
        List<Branch> branchList = baseServiceClient.selectBranchChildByCode("").getData();
        findBranchName(order, branchList);
    }

    @Override
    public void setOrderStatusStart(String id) {
        setOrderStatus(id, ORDER_START);
    }

    @Override
    public void setOrderStatusNew(String id) {
        setOrderStatus(id, ORDER_NEW);
    }

    @Override
    public void setOrderStatusClose(String id) {
        setOrderStatus(id, ORDER_CLOSE);
    }

    @Override
    public Order findByOrderCode(String orderCode, String tenantId) {

        Page<Order> orderPage = new Page<>(1, 10);

        OrderDto orderDto = new OrderDto();

        orderDto.setOrderSn(orderCode);
        orderDto.setTenantId(tenantId);

        IPage<Order> planList = orderMapper.queryOrderList(orderPage, orderDto);

        return planList.getRecords().size() > 0 ? planList.getRecords().get(0) : null;
    }


    private void setOrderStatus(String id, int status) {
        Order order = this.getById(id);
        order.setStatus(status);
        this.updateById(order);
    }

    private void findBranchName(Order order, List<Branch> branchList) {

        for (Branch b : branchList) {
            if (b.getBranchCode().equals(order.getBranchCode())) {
                order.setBranchName(b.getBranchName());
            }
            if (b.getBranchCode().equals(order.getInChargeOrg())) {
                order.setInchargeOrgName(b.getBranchName());
            }
        }
    }

}
