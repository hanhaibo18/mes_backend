package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.ProducePurchaseOrder;
import com.richfit.mes.produce.entity.PurchaseOrderSynchronizationDto;

import java.util.List;

/**
 * @ClassName: ProducePurchaseOrderService.java
 * @Author: Hou XinYu
 * @Description: 采购订单
 * @CreateTime: 2022年01月10日 10:47:00
 */
public interface ProducePurchaseOrderService extends IService<ProducePurchaseOrder> {
    /**
     * 功能描述: 查询同步订单
     * @Author: xinYu.hou
     * @Date: 2022/1/13 13:55
     * @param purchaseOrderSynchronizationDto
     * @return: List<ProducePurchaseSynchronization>
     **/
    List<ProducePurchaseOrder> queryPurchaseSynchronization(PurchaseOrderSynchronizationDto purchaseOrderSynchronizationDto);
}
