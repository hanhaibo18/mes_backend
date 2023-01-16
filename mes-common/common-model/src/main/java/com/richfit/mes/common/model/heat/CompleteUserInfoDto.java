package com.richfit.mes.common.model.heat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: CompleteUserInfoDto.java
 * @Author: renzewen
 * @Description: 报工人员信息
 * @CreateTime: 2023年1月16日 10:54:00
 */
@Data
public class CompleteUserInfoDto {
    @ApiModelProperty(value = "员工号", dataType = "员工号")
    private String userId;
    @ApiModelProperty(value = "员工名", dataType = "员工名")
    private String userName;
}
