package com.pdm.mes.schedule.entity.vo;


import lombok.Data;

import java.util.Date;

/**
 * 排产单Vo
 */
@Data
public class SaleProduceNoticeVo {
    /**
     * id
     */
    private String id;

    /**
     *排产单号
     */
    private String productionOrder;

    /**
     *用户
     */
    private String userUnit;

    /**
     *工作号
     */
    private String workNo;

    /**
     *产品名称
     */
    private String produceName;

    /**
     *数量
     */
    private Integer quantity;

    /**
     *交货期
     */
    private Date deliveryDate;

    /**
     *附件
     */
    private String previewUrl;

    /**
     *排产日期
     */
    private Date salesSchedulingDate;

    /**
     *物料编码
     */
    private String materialNo;

    /**
     *物料名称
     */
    private String materialName;

    /**
     *图号
     */
    private String drawingNo;
}
