package com.richfit.mes.common.model.produce.hourSum;

import lombok.Data;

@Data
public class WorkingHours {

    public String productName;
    /**
     * 图号
     */
    private String drawNo;

    /**
     * 版本号
     */
    private String version;
    /**
     * 准结工时
     */

    private Double prepareEndHours;
    /**
     * 定额工时
     */

    private Double singlePieceHours;

    /**
     * 总工时
     */
    private Double totalProductiveHours;
    /**
     * 物料编码
     */
    private String materialNo;

}
