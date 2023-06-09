package com.tc.mes.plm.entity.dto;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 生产排产单同步到 plm
 */
@Data
public class ProductionSchedulingDto {
    /**
     *  排产单号（必填）
     */
    private String schedulingNo;

    /**
     *  通知来源（必填）
     */
    private String noticSouce;

    /**
     *  技术准备完成时间（必填）
     */
    private String techPlanTime;

    /**
     *  交货期（必填）
     */
    private Date deliveryDate;

    /**
     *  落成单位（必填）
     */
    private String execuOrganization;

    /**
     *  工作号 （必填） 多个
     */
    private String workNo;

    /**
     *  排产单位（必填）
     */
    private String schedulingGroup;

    /**
     *  生产排产日期（必填）
     */
    private String schedulingDate;

    /**
     *  用户（必填）
     */
    private String customerName;

    /**
     *  排产类型（必填）
     */
    private String schedulingType;

    /**
     *  产品名称（必填）
     */
    private String productName;

    /**
     *  排产单名称 指定格式pdf
     */
    private List<String> previewUrl;
}