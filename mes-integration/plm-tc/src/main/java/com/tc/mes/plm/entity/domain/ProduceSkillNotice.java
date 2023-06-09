package com.tc.mes.plm.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 技术通知单
 * @TableName produce_skill_notice
 */
@TableName(value ="produce_skill_notice")
@Data
public class ProduceSkillNotice implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 通知状态 0 = 未接收, 1 = 已接收, 2 = 退回
     */
    private String notificationState;

    /**
     * 调度状态 0 = 未转调度, 1 = 未下发 ,2 = 已下发
     */
    private String dispatchState;

    /**
     * 接受状态 0 = 待确认, 1 = 已确认, 2 = 已取消
     */
    private String acceptingState;

    /**
     * 技术通知编号
     */
    private String skillNoticeNumber;

    /**
     * 技术通知名称
     */
    private String skillNoticeName;

    /**
     * 调度通知编号
     */
    private String dispatchNoticeNumber;

    /**
     * 工作号
     */
    private String workNo;

    /**
     * 图号
     */
    private String drawingNo;

    /**
     * 发文单位
     */
    private String issuingUnit;

    /**
     * 钻机名称
     */
    private String drillingRigName;

    /**
     * 部件名称
     */
    private String partsName;

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
     * 技术通知版本
     */
    private String skillNoticeRevId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}