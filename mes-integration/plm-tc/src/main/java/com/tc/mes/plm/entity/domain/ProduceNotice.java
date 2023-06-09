package com.tc.mes.plm.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 排产单
 * @TableName produce_notice
 */
@TableName(value ="produce_notice")
@Data
public class ProduceNotice implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

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
     * 图号
     */
    private String drawingNo;

    /**
     * 物料号
     */
    private String materialNo;

    /**
     * 物料名称
     */
    private String materialName;

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

    /**
     *
     */
    private String tenantId;

    /**
     * 所属机构
     */
    private String branchCode;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String modifyBy;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 附件
     */
    private String previewUrl;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
