package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author renzewen
 * @Description （热工）步骤工时
 */
@Data
public class StepHour extends BaseEntity<StepHour> {

    public static String YES_ACTIVATE = "1";

    @ApiModelProperty(value = "id", dataType = "String")
    private String id;

    @ApiModelProperty(value = "版本id", dataType = "ver")
    private String verId;

    @ApiModelProperty(value = "步骤名称", dataType = "ver")
    private String stepName;

    @ApiModelProperty(value = "工时比例", dataType = "String")
    private String hourRatio;

    @ApiModelProperty(value = "步骤类型", dataType = "String")
    private String stepType;

    @ApiModelProperty(value = "是否首次", dataType = "String")
    private String isAdd;

}
