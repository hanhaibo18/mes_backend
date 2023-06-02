package com.tc.mes.pdm.entity;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 生产排产单同步到 plm
 */
@Data
public class ProductionSchedulingDto {
    /**
     *  排产单号
     */
    private String schedulingNo;

    /**
     *  排产单名称
     */
    private String objectName;

    /**
     *  通知来源
     */
    private String noticSouce;

    /**
     *  技术准备完成时间
     */
    private String techPlanTime;

    /**
     *  交货期
     */
    private Date deliveryDate;

    /**
     *  落成单位
     */
    private String execuOrganization;

    /**
     *  工作号
     */
    private String workNo;

    /**
     *  排产单位
     */
    private String schedulingGroup;

    /**
     *  生产排产日期
     */
    private String schedulingDate;

    /**
     *  用户
     */
    private String customerName;

    /**
     *  排产类型
     */
    private String schedulingType;

    /**
     *  产品名称
     */
    private String productName;

    /**
     *  审签意见
     */
    private List<String> opinion;

    /**
     *  排产单名称
     */
    private List<String> previewUrl;
}