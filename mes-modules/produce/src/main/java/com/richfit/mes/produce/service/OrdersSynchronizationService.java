package com.richfit.mes.produce.service;


import com.richfit.mes.produce.entity.OrdersSynchronizationDto;

/**
 * @Author: xinYu hou
 * @Date: 2022年1月6日14:21:35
 */
public interface OrdersSynchronizationService{

    /**
     * 功能描述:查询未同步订单
     * @Author: xinYu.hou
     * @Date: 2022/1/6 16:01
     * @param ordersSynchronizedO
     * @return: java.lang.String
     **/
    String queryNotSynchronizationOrder(OrdersSynchronizationDto ordersSynchronizedO);
}
