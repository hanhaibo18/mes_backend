package com.richfit.mes.produce.entity;

import com.richfit.mes.common.model.produce.Order;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

/**
 * @ClassName: OrderSyncDto.java
 * @Author: Hou XinYu
 * @Description: 订单同步保存
 * @CreateTime: 2022年12月02日 10:42:00
 */
@Data
public class OrderSyncDto {
    @ApiModelProperty(value = "订单信息")
    private List<Order> orderList;
    @ApiModelProperty(value = "订单查询时间")
    private String time;
    @ApiModelProperty(value = "控制者")
    private String controller;
    @ApiModelProperty(value = "erp工厂代码")
    private String erpCode;
    @ApiModelProperty(value = "工厂")
    private String branchCode;
}
