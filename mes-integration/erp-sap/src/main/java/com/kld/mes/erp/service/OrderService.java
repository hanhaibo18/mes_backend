package com.kld.mes.erp.service;

import com.kld.mes.erp.entity.order.ZC80PPIF009Response;
import com.kld.mes.erp.entity.order.ZPPS0008;

import java.util.List;

/**
 * ERP订单服务
 * @author: 王瑞
 * @create: 2022/8/1 15:04
 */
public interface OrderService {

    ZC80PPIF009Response getErpCode(String erpCode, String selectDate, String controller, String orderNo);

}
