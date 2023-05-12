package com.richfit.mes.produce.entity;

import com.richfit.mes.common.core.base.BasePageDto;
import lombok.Data;

/**
 * @Author: GaoLiang
 * @Date: 2020/8/10 18:14
 */
@Data
public class OrderDto extends BasePageDto<OrderDto> {

    private String orderSn;

    private String tenantId;

    private String branchCode;

    private String controller;

    private String status;

    private String materialCode;

    private String startTime;

    private String endTime;

    private String notEnd;  //查询未全计划的订单时  status ！=2

    private String orderCol;

    private String order;

    private String materialType;

    private String drawingNo;
}
