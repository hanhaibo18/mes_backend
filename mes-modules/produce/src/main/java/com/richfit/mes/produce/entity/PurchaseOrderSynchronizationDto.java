package com.richfit.mes.produce.entity;

import lombok.Data;

/**
 * @ClassName: PurchaseOrderDto.java
 * @Author: Hou XinYu
 * @Description: 同步采购订单
 * @CreateTime: 2022年01月07日 16:47:00
 */
@Data
public class PurchaseOrderSynchronizationDto {
    private String startTime;
    private String endTime;
    private String code;
    private String branchCode;
    private String tenantId;
}
