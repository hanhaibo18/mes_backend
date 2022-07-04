package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.util.Date;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/14 9:21
 */
@Data
public class Plan extends BaseEntity<Plan> {

    private static final long serialVersionUID = -1472432735506772177L;
    /**
     * 计划编号
     */
    private String projCode;
    /**
     * 工作号
     */
    private String workNo;
    /**
     * 订单id
     */
    private String orderId;
    /**
     * 订单号
     */
    private String orderNo;
    /**
     * 订单交货日期
     */
    private Date orderDeliveryDate;
    /**
     * 关联项目BOM的id
     */
    private String projectBom;
    /**
     * 关联项目BOM的workno
     */
    private String projectBomWork;
    /**
     * 关联项目BOM的名称
     */
    private String projectBomName;
    /**
     * bom分组选择
     */
    private String projectBomGroup;
    /**
     * 图号
     */
    private String drawNo;
    /**
     * 计划数量
     */
    private int projNum;
    /**
     * 已跟单数量
     */
    @TableField(exist = false)
    private int trackNum;
    /**
     * 在制数量
     */
    @TableField(exist = false)
    private int processingNum;
    /**
     * 已交数量
     */
    @TableField(exist = false)
    private int storeNum;
    /**
     * 缺件数量
     */
    @TableField(exist = false)
    private int lackNum;
    /**
     * 排序号
     */
    private int sortNo;
    /**
     * 开始时间
     */
    private Date startTime;
    /**
     * 结束时间
     */
    private Date endTime;
    /**
     * 优先级
     */
    private String priority;
    /**
     * 计划类型
     */
    private int projType;
    /**
     * 所属机构
     */
    private String branchCode;
    /**
     * 加工车间
     */
    private String inchargeOrg;
    /**
     * 状态
     */
    private int status;
    /**
     * 预警状态
     */
    private int alarmStatus;
    /**
     * 工艺状态
     */
    @TableField(exist = false)
    private int processStatus;
    /**
     * 总工序
     */
    @TableField(exist = false)
    private double totalProgress;
    /**
     * 工序进度
     */
    @TableField(exist = false)
    private double optionProgress;
    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 图号名称
     */
    private String drawNoName;

    /**
     * 加工类型
     */
    private String drawNoType;
    /**
     * 所属机构名称
     */
    @TableField(exist = false)
    private String branchName;
    /**
     * 加工车间名称
     */
    @TableField(exist = false)
    private String inchargeOrgName;

    /**
     * 关联订单编号
     */
    @TableField(exist = false)
    private String orderCode;

    /**
     * 总工时
     */
    @TableField(exist = false)
    private float totalHour;

    /**
     * 已用工时
     */
    @TableField(exist = false)
    private float alreadyWorkHour;
}
