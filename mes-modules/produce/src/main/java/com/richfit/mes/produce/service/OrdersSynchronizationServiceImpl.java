package com.richfit.mes.produce.service;

import com.richfit.mes.produce.entity.OrdersSynchronizationDto;
import org.springframework.beans.factory.annotation.Value;

/**
 * @ClassName: OrdersSynchronizationServiceImpl.java
 * @Author: Hou XinYu
 * @Description: 订单同步
 * @CreateTime: 2022年01月06日 15:57:00
 */
public class OrdersSynchronizationServiceImpl implements OrdersSynchronizationService{

    @Value("${synchronization.orders-synchronization}")
    private String notSynchronizationOrder;
    @Override
    public String queryNotSynchronizationOrder(OrdersSynchronizationDto ordersSynchronizedO) {
//        HttpClients client = new HttpClients();
        return null;
    }
}
