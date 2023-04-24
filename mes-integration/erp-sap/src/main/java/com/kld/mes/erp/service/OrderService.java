package com.kld.mes.erp.service;

import com.kld.mes.erp.entity.order.ZC80PPIF009Response;
import com.kld.mes.erp.entity.order.ZPPS0008;
import com.kld.mes.erp.entity.order.creat.Zc80Ppif032SI;
import com.kld.mes.erp.entity.order.creat.Zc80Ppif032SO;
import com.richfit.mes.common.model.produce.Order;

import java.util.List;

/**
 * ERP订单服务
 *
 * @author: 王瑞
 * @create: 2022/8/1 15:04
 */
public interface OrderService {

    List<Order> getErpCode(String erpCode, String selectDate, String controller, String orderNo) throws Exception;

    public List<Zc80Ppif032SO> creat(List<Zc80Ppif032SI> zc80Ppif032SOList) throws Exception;

}
