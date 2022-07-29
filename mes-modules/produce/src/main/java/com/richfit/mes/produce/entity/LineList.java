/**
 * Copyright 2022 bejson.com
 */
package com.richfit.mes.produce.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * Auto-generated: 2022-07-26 15:58:47
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class LineList {
    @ApiModelProperty(value = "物料编码", dataType = "String")
    private String materialNum;
    @ApiModelProperty(value = "物料名称", dataType = "String")
    private String materialDesc;
    @ApiModelProperty(value = "单位", dataType = "String")
    private String unit;
    @ApiModelProperty(value = "数量", dataType = "double")
    private double quantity;
    @ApiModelProperty(value = "实物配送标识", dataType = "String")
    private String swFlag;

}
