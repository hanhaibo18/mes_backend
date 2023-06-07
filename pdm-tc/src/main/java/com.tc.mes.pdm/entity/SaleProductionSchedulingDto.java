package com.tc.mes.pdm.entity;

import lombok.Data;

import java.util.Date;

/**
 * 销售排产单
 */
@Data
public class SaleProductionSchedulingDto {
    /**
     *排产单号
     */
    private String code;

    /**
     *用户
     */
    private String name;

    /**
     *工作号
     */
    private String trackNo;

    /**
     *产品名称
     */
    private String productName;

    /**
     *数量
     */
    private Integer number;

    /**
     *交货期
     */
    private Date deliveryTime;

    /**
     *附件
     */
    private String relationId;

    /**
     *排产日期
     */
    private Date createTime;

    /**
     *物料编码
     */
    private String materialCode;

    /**
     *物料名称
     */
    private String erpName;

    /**
     *图号
     */
    private String drawNo;

}
