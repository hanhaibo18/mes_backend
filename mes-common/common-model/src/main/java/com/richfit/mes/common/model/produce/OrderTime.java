package com.richfit.mes.common.model.produce;

import lombok.Data;

import java.util.Date;

@Data
public class OrderTime {

    /**
     * 订单号
     */
    private String orderSn;
    /**
     * 图号
     */
    private String drawNo;

    /**
     * 生产订单编号
     */
    private String productionOrder;

    /**
     * 产品名称
     */
    private String productNo;
    /**
     * 准结工时
     */
    private String prepareEndHours;
    /**
     * 定额工时
     */
    private String singlePieceHours;
    /**
     * 总工时
     */
    private String totalProductiveHours;

    private Date startTime;

    private Date endTime;

    private String branchCode;


}
