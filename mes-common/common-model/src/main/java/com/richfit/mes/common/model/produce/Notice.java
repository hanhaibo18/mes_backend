package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Date;

/**
 * produce_notice
 *
 * @author
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Notice extends BaseEntity<Notice> {
    private static final long serialVersionUID = 1L;
    /**
     * 通知状态 0 = 未接收, 1 = 已接收, 2 = 退回
     */
    private String notificationState;
    /**
     * 排产状态 0 = 待排产, 1= 已排产, 2 = 已下发 ,3 = 已取消
     */
    private String schedulingState;
    /**
     * 接受状态 0 = 待确认, 1 = 已确认, 2 = 已取消
     */
    private String acceptingState;
    /**
     * 通知类型(J47 = 排产通知 , J48 = 排产变更通知)
     */
    private String notificationType;
    /**
     * 排产单号
     */
    private String productionOrder;
    /**
     * 排产类别
     */
    private String productionType;
    /**
     * 用户
     */
    private String userUnit;
    /**
     * 工作号
     */
    private String workNo;
    /**
     * 产品名称
     */
    private String produceName;
    /**
     * 产品型号
     */
    private String produceType;
    /**
     * 数量
     */
    private Integer quantity;
    /**
     * 技术完成时间
     */
    private Date technicalCompletionTime;
    /**
     * 毛坯完成时间
     */
    private Date blankFinishTime;
    /**
     * 上齐井场完成时间
     */
    private Date wellSiteCompletionTime;
    /**
     * 钻机升起完成时间
     */
    private Date rigCompletionTime;
    /**
     * 交货日期
     */
    private Date deliveryDate;
    /**
     * 落成单位
     */
    private String designatedUnit;
    /**
     * 销售排产日期
     */
    private Date salesSchedulingDate;
    /**
     * 生产排产日期
     */
    private Date productionScheduleDate;
    /**
     * 退回原因
     */
    private String reasonReturn;
    /**
     * 发文单位
     */
    private String issuingUnit;
    private String tenantId;
}
