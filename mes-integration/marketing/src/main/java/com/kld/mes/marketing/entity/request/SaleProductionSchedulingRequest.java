package com.kld.mes.marketing.entity.request;

import lombok.Data;

import java.util.Date;
import java.util.List;

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
     *排产单附件
     */
    private List<String> relation_id;

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

    /**
     * 排产清单附件
     */
    private List<String> relation_note_id;

}
