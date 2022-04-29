package com.richfit.mes.common.model.produce.hourSum;

import lombok.Data;


@Data
public class OrderTime {

    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 工作号
     */
    private String workNo;
    /**
     * 图号
     */
    private String drawNo;
    /**
     * 物料编码
     */
    public String materialNo;

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
    private Double sumHours;






}
