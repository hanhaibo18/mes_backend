package com.kld.mes.plm.entity.request;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 生产排产单 plm
 */
@Data
public class ProductionSchedulingRequest {
    /**
     *  排产单号（必填）
     */
    private String scheduling_no;

    /**
     *  通知来源（必填）
     */
    private String notic_souce;

    /**
     *  技术准备完成时间（必填）
     */
    private String tech_plan_time;

    /**
     *  交货期（必填）
     */
    private Date delivery_date;

    /**
     *  落成单位（必填）
     */
    private String execu_organization;

    /**
     *  工作号 （必填） 多个
     */
    private String work_no;

    /**
     *  排产单位（必填）
     */
    private String scheduling_group;

    /**
     *  生产排产日期（必填）
     */
    private String scheduling_date;

    /**
     *  用户（必填）
     */
    private String customer_name;

    /**
     *  排产类型（必填）
     */
    private String scheduling_type;

    /**
     *  产品名称（必填）
     */
    private String product_name;

    /**
     *  排产单名称 指定格式pdf
     */
    private List<String> preview_url;
}