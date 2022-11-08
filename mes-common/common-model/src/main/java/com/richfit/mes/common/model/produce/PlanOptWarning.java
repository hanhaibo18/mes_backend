package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @Author: zhiqiang.lu
 * @Date: 2020/8/8 9:59
 */
@Data
@TableName("produce_plan_opt_warning")
public class PlanOptWarning extends BaseEntity<PlanOptWarning> {

    private static final long serialVersionUID = -1472432735506772177L;

    @ApiModelProperty(value = "计划id", dataType = "String")
    private String planId;

    @ApiModelProperty(value = "跟单号码", dataType = "String")
    private String trackNo;

    @ApiModelProperty(value = "工序序号", dataType = "String")
    private String optNo;

    @ApiModelProperty(value = "工序名称", dataType = "String")
    private String optName;

    @ApiModelProperty(value = "工序名称", dataType = "Integer")
    private Integer sequenceOrderBy;

    @ApiModelProperty(value = "预警时间", dataType = "String")
    private String dateWarning;

    @ApiModelProperty(value = "产品编号", dataType = "String")
    private String productNo;

    @ApiModelProperty(value = "track_item表id", dataType = "String")
    private String trackItemId;
    @TableField(exist = false)
    @ApiModelProperty(value = "是否为关键工序", dataType = "int")
    private int isKey;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序是否完工", dataType = "Integer")
    private Integer isOperationComplete;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序完成时间", dataType = "Date")
    private Date operationCompleteTime;

    @TableField(exist = false)
    @ApiModelProperty(value = "生产数量", dataType = "Integer")
    private Integer assignableQty;

    @TableField(exist = false)
    @ApiModelProperty(value = "完成数量", dataType = "Double")
    private Double completeQty;

    @TableField(exist = false)
    @ApiModelProperty(value = "剩余天数", dataType = "Long")
    private Long days;

}
