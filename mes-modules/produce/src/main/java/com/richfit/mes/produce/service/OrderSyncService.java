package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.produce.entity.OrdersSynchronizationDto;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @ClassName: orderSyncService.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年01月19日 16:17:00
 */
public interface OrderSyncService extends IService<Order> {

    /**
     * 功能描述: 订单同步查询
     * @Author: xinYu.hou
     * @Date: 2022/1/17 11:10
     * @param orderSynchronizationDto
     * @return: List<Order>
     **/
    List<Order> queryOrderSynchronization(OrdersSynchronizationDto orderSynchronizationDto);

    /**
     * 功能描述: 保存同步信息
     * @Author: xinYu.hou
     * @Date: 2022年1月18日14:19:44
     * @param orderList
     * @return: CommonResult<Boolean>
     **/
    CommonResult<Boolean> saveOrderSync(List<Order> orderList);

    /**
     * 功能描述: 定时保存同步信息
     * @Author: xinYu.hou
     * @Date: 2022/1/13 14:27
     * @return: CommonResult<Boolean>
     **/
    CommonResult<Boolean> saveTimingOrderSync();
}
