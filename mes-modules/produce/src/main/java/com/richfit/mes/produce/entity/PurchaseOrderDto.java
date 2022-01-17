package com.richfit.mes.produce.entity;

import lombok.Data;

import java.util.Date;

/**
 * @ClassName: PurchaseOrderDto.java
 * @Author: Hou XinYu
 * @Description: 采购订单
 * @CreateTime: 2022年01月07日 16:47:00
 */
@Data
public class PurchaseOrderDto {
    private String startTime;
    private String endTime;
    private String orderNo;
    private String branchCode;
    private String tenantId;
}
