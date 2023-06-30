package com.kld.mes.plm.entity.vo;

import lombok.Data;

import java.util.Date;
import java.util.List;

/**
 * 生产排产单Vo
 */
@Data
public class ProduceNoticeVo {

    /**
     *排产单号
     */
    private String productionOrder;

    /**
     *通知类型
     */
    private String notificationType;

    /**
     *技术完成时间
     */
    private Date technicalCompletionTime;

    /**
     *交货日期
     */
    private Date deliveryDate;

    /**
     *落成单位
     */
    private String unit;

    /**
     *工作号
     */
    private String workNo;

    /**
     *发文单位
     */
    private String issuingUnit;

    /**
     *用户
     */
    private String userUnit;

    /**
     * 生产排产日期
     */
    private Date productionScheduleDate;

    /**
     *排产类别
     */
    private String productionType;

    /**
     *产品名称
     */
    private String produceName;

    /**
     *  排产单名称 指定格式pdf
     */
    private List<String> previewUrl;

}
