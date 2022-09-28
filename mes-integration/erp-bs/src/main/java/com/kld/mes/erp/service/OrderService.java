package com.kld.mes.erp.service;

import com.richfit.mes.common.model.produce.Order;

import java.util.List;

/**
 * ERP生产订单
 *
 * @author: fengxy
 * @create: 2022年9月13日11:13:04
 */
public interface OrderService {

    List<Order> getErpCode(String erpCode, String selectDate, String controller, String orderNo) throws Exception;

}
