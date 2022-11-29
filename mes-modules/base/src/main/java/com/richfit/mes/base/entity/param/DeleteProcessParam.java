package com.richfit.mes.base.entity.param;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@Data
public class DeleteProcessParam {
    @ApiModelProperty(value = "工序id", dataType = "String")
    private List<String> drawIdGroup;
    @ApiModelProperty(value = "工厂代码", dataType = "String")
    private String dataGroup;
}
