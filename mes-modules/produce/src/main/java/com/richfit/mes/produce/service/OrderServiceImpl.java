package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.produce.dao.OrderMapper;
import com.richfit.mes.produce.dao.TrackFlowMapper;
import com.richfit.mes.produce.entity.OrderDto;
import com.richfit.mes.produce.provider.BaseServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Autowired
    TrackFlowMapper trackFlowMapper;

    @Resource
    private BaseServiceClient baseServiceClient;

    @Override
    public IPage<Order> queryPage(Page<Order> orderPage, OrderDto orderDto) {

        IPage<Order> planList = orderMapper.queryOrderList(orderPage, orderDto);

        List<Branch> branchList = baseServiceClient.selectBranchChildByCode("").getData();

        for (Order order : planList.getRecords()) {
            findBranchName(order, branchList);
            if (order.getProjNum() == null) {
                order.setProjNum(0);
            }
            if (order.getStoreNum() == null) {
                order.setStoreNum(0);
            }
        }

        return planList;
    }

    @Override
    public List<Order> queryList(OrderDto orderDto) {
        List<Order> planList = orderMapper.queryOrderListNoPage(orderDto);

        List<Branch> branchList = baseServiceClient.selectBranchChildByCode("").getData();

        for (Order order : planList) {
            findBranchName(order, branchList);
            if (order.getProjNum() == null) {
                order.setProjNum(0);
            }
            if (order.getStoreNum() == null) {
                order.setStoreNum(0);
            }
        }

        return planList;
    }

    @Override
    //精准匹配MaterialCode
    public List<Order> queryPageEqMaterialCode(OrderDto orderDto) {

        List<Order> planList = orderMapper.queryOrderListEqMaterialCode(orderDto);

        List<Branch> branchList = baseServiceClient.selectBranchChildByCode("").getData();

        for (Order order : planList) {
            findBranchName(order, branchList);
            if (order.getProjNum() == null) {
                order.setProjNum(0);
            }
            if (order.getStoreNum() == null) {
                order.setStoreNum(0);
            }
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

    @Override
    public void orderDataTrackHead(TrackHead trackHead) {
        String orderId = trackHead.getProductionOrderId();
        String orderNo = trackHead.getProductionOrder();
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(orderNo)) {
            Map map = new HashMap();
            map.put("productionOrder", orderNo);
            map.put("branchCode", trackHead.getBranchCode());
            map.put("tenantId", trackHead.getTenantId());
            List<TrackHead> trackFlowList = trackFlowMapper.selectTrackFlowList(map);
            int numberComplete = 0;
            int numberDoing = 0;
            for (TrackHead trackFlow : trackFlowList) {
                if ("1".equals(trackFlow.getStatus())) {
                    //完成
                    numberDoing += trackFlow.getNumber();
                } else if ("2".equals(trackFlow.getStatus())) {
                    //完成
                    numberComplete += trackFlow.getNumber();
                } else if ("8".equals(trackFlow.getStatus())) {
                    //完工质量资料
                    numberComplete += trackFlow.getNumber();
                } else if ("9".equals(trackFlow.getStatus())) {
                    //交付
                    numberComplete += trackFlow.getNumber();
                }
            }
            //由于之前订单同步删除订单流程bug会导致订单编码的id变更，故加入订单号码查询的流程
            QueryWrapper<Order> queryWrapperOrder = new QueryWrapper<>();
            queryWrapperOrder.eq("order_sn", orderNo);
            queryWrapperOrder.eq("branch_code", trackHead.getBranchCode());
            queryWrapperOrder.eq("tenant_id", trackHead.getTenantId());
            List<Order> orderList = this.list(queryWrapperOrder);
            if (orderList != null && orderList.size() > 0) {
                Order order = orderList.get(0);
                order.setStoreNum(numberComplete);
                int i = order.getStoreNum().compareTo(order.getOrderNum());
                if (order.getStoreNum() == 0) {
                    order.setProduction(0);
                }
                if (numberDoing > 0) {
                    order.setProduction(1);
                }
                if (i >= 0) {
                    order.setProduction(2);
                }
                orderMapper.updateById(order);
            }
        }
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
