package com.richfit.mes.common.model.produce;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * produce_hot_plan_node
 * @author 
 */
@Data
public class HotPlanNode   implements Serializable {
    private String id;

    /**
     * 毛坯需求id
     */
    @ApiModelProperty(value = "毛坯需求id", dataType = "String")
    private String demandId;

    /**
     * 工序名称
     */
    @ApiModelProperty(value = "工序名称", dataType = "String")
    private String optName;

    /**
     * 需求数量
     */
    @ApiModelProperty(value = "需求数量", dataType = "int")
    private Integer demandNum;

    /**
     * 工序计划完成日期
     */
    @ApiModelProperty(value = "工序计划完成日期", dataType = "Date")
    private Date finishTime;

    /**
     * 工序状态
     */
    @ApiModelProperty(value = "工序状态", dataType = "String")
    private String optStatus;

    /**
     * 租户id
     */
    @ApiModelProperty(value = "租户id", dataType = "String")
    private String tenantId;

    /**
     * 车间编码
     */
    @ApiModelProperty(value = "车间编码", dataType = "String")
    private String branchCode;

    /**
     *工序序号
     */
    @ApiModelProperty(value = "工序序号", dataType = "String")
    private String opNo;
    /**
     *工序字典id
     */
    @ApiModelProperty(value = "工序字典id", dataType = "String")
    private String optId;
    /**
     *工序id
     */
    @ApiModelProperty(value = "工序id", dataType = "String")
    private String sequenceId;
    /**
     *track_item表id
     */
    @ApiModelProperty(value = "track_item表id", dataType = "String")
    private String trackItemId;


    @ApiModelProperty(value = "预警状态 0正常  1提前 2警告 3延期 ")
    @TableField(exist = false)
    private Integer alarmStatus;

    @TableField(exist = false)
    @ApiModelProperty(value = "剩余天数", dataType = "Long")
    private Long days;

    private static final long serialVersionUID = 1L;
}