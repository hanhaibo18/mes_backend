package com.richfit.mes.common.model.produce.hourSum;

import lombok.Data;


@Data
public class OrderTime {

    public String materialNo;

    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 图号
     */
    private String drawNo;
    /**
     * 下单时间
     */
    private String orderDate;

    /**
     * 产品名称
     */
    private String productName;

    /**
     * 准结工时
     */

    private Double endHours;
    /**
     * 额定工时
     */

    private Double pieceHours;

    /**
     * 总工时
     */
    private Double sumhours;



}
