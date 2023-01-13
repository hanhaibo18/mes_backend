package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author renzewen
 * @Description （热工）步骤工时版本
 */
@Data
public class StepHourVer extends BaseEntity<StepHourVer> {

    public static String YES_ACTIVATE = "1";

    @ApiModelProperty(value = "id", dataType = "String")
    private String id;

    @ApiModelProperty(value = "工时版本", dataType = "ver")
    private String ver;

    @ApiModelProperty(value = "是否激活过", dataType = "String")
    private String isActivated;

    @ApiModelProperty(value = "是否激活", dataType = "String")
    private String isActivate;

    @ApiModelProperty(value = "激活人", dataType = "String")
    private String activateBy;

    @ApiModelProperty(value = "激活时间", dataType = "String")
    private String activateTime;

    @ApiModelProperty(value = "组织机构", dataType = "String")
    private String branchCode;

    @ApiModelProperty(value = "租户id", dataType = "String")
    private String tenantId;

}
