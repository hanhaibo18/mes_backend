package com.richfit.mes.produce.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: OutsourceDto.java
 * @Author: Hou XinYu
 * @Description: 外协报工 id参数
 * @CreateTime: 2023年02月07日 10:15:00
 */
@Data
public class OutsourceDto {
    @ApiModelProperty(value = "工序序号", dataType = "String")
    private String optNo;
    @ApiModelProperty(value = "工序名字", dataType = "String")
    private String optName;
}
