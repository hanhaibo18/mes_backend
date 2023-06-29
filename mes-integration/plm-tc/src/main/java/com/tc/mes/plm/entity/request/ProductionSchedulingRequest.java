package com.tc.mes.plm.entity.request;

import com.richfit.mes.common.model.produce.Notice;
import com.tc.mes.plm.entity.domain.MesPdmAttachment;
import com.tc.mes.plm.entity.domain.NoticeTenant;
import lombok.Data;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    private Date tech_plan_time;

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


    public ProductionSchedulingRequest(Notice notice, NoticeTenant noticeTenant, List<MesPdmAttachment> attachments) {
        // 排产单号
        this.scheduling_no = notice.getProductionOrder();
        // 通知来源
        this.notic_souce = notice.getNotificationType();
        // 技术准备完成时间
        this.tech_plan_time = notice.getTechnicalCompletionTime();
        // 交货期
        this.delivery_date = notice.getDeliveryDate();
        // 落成单位
        if (noticeTenant.getUnitType().equals(2)) {
            this.execu_organization = noticeTenant.getUnit();
        }
        // 工作号
        this.work_no = notice.getWorkNo();
        // 排产单位
        this.scheduling_group = notice.getIssuingUnit();
        // 生产排产日期
        this.scheduling_date = String.valueOf(notice.getProductionScheduleDate());
        // 用户
        this.customer_name = notice.getUserUnit();
        // 排产类别
        this.scheduling_type = notice.getProductionType();
        // 产品名称
        this.product_name = notice.getProduceName();
        // 附件
        this.preview_url = attachments.stream().map(MesPdmAttachment::getFileUrl).collect(Collectors.toList());
    }
}