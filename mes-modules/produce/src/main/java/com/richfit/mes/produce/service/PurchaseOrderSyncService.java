package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.ProducePurchaseOrder;
import com.richfit.mes.produce.entity.PurchaseOrderSynchronizationDto;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @ClassName: ProducePurchaseOrderSysnService.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年01月19日 15:52:00
 */
public interface PurchaseOrderSyncService extends IService<ProducePurchaseOrder> {
    /**
     * 功能描述: 查询采购订单(第三方)
     * @Author: xinYu.hou
     * @Date: 2022/1/13 13:55
     * @param purchaseOrderSynchronizationDto
     * @return: List<ProducePurchaseSynchronization>
     **/
    List<ProducePurchaseOrder> queryPurchaseSynchronization(PurchaseOrderSynchronizationDto purchaseOrderSynchronizationDto);

    /**
     * 功能描述: 定时同步采购订单
     * @Author: xinYu.hou
     * @Date: 2022/1/19 15:57
     * @return: CommonResult<Boolean>
     **/
    CommonResult<Boolean> saveTimingProducePurchaseSynchronization();

    /**
     * 功能描述: 保存同步信息
     * @Author: xinYu.hou
     * @Date: 2022/1/13 14:27
     * @param producePurchase
     * @return: CommonResult<Boolean>
     **/
    CommonResult<Boolean> saveProducePurchaseSynchronization( List<ProducePurchaseOrder> producePurchase);
}
