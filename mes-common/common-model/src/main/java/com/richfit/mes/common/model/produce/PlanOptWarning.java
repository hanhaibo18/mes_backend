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

    @ApiModelProperty(value = "工序序号", dataType = "String")
    private String optNo;

    @ApiModelProperty(value = "工序名称", dataType = "String")
    private String optName;

    @ApiModelProperty(value = "工序名称", dataType = "Integer")
    private Integer sequenceOrderBy;

    @ApiModelProperty(value = "预警时间", dataType = "String")
    private String dateWarning;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序是否完工", dataType = "String")
    private Integer isOperationComplete;

    @TableField(exist = false)
    @ApiModelProperty(value = "工序完成时间", dataType = "Date")
    private Date operationCompleteTime;

}
