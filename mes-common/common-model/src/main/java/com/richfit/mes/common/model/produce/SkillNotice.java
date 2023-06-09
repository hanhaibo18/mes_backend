package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * produce_skill_notice
 *
 * @author
 */
@Data
public class SkillNotice extends BaseEntity<SkillNotice> {

    private static final long serialVersionUID = 1L;
    /**
     * 通知状态 0 = 未接收, 1 = 已接收, 2 = 退回
     */
    private String notificationState;
    /**
     * 调度状态 0 = 待排产, 1 = 已下发 ,3 = 已取消
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
     * 下发时间
     */
    private Date issueTime;

    private String tenantId;
    /**
     * 所属机构
     */
    private String branchCode;
    @TableField(exist = false)
    private String executableUnit;
}
