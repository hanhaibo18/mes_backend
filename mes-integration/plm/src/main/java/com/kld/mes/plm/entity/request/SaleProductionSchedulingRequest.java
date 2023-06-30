package com.kld.mes.plm.entity.request;

import lombok.Data;

import java.util.Date;

/**
 * pdm 销售排产单
 */
@Data
public class SaleProductionSchedulingRequest {
    /**
     *排产单号
     */
    private String code;

    /**
     *用户
     */
    private String customer;

    /**
     *工作号
     */
    private String track_no;

    /**
     *产品名称
     */
    private String name;

    /**
     *数量
     */
    private Integer number;

    /**
     *交货期
     */
    private Date delivery_time;

    /**
     *附件
     */
    private String relation_id;

    /**
     *排产日期
     */
    private Date create_time;

    /**
     *物料编码
     */
    private String material_code;

    /**
     *物料名称
     */
    private String erp_name;

    /**
     *图号
     */
    private String draw_no;

}
