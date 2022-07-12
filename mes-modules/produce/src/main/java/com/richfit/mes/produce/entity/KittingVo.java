package com.richfit.mes.produce.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: kittingVo.java
 * @Author: Hou XinYu
 * @Description: 齐套性检查
 * @CreateTime: 2022年07月07日 14:43:00
 */
@Data
public class KittingVo {
    @ApiModelProperty(value = "物料图号", dataType = "String")
    private String drawingNo;
    @ApiModelProperty(value = "物料名称", dataType = "String")
    private String materialName;
    @ApiModelProperty(value = "单套数", dataType = "Integer")
    private Integer unitNumber;
    @ApiModelProperty(value = "需要套数量", dataType = "Integer")
    private Integer needUnitNumber;
    @ApiModelProperty(value = "库存数量")
    private Integer inventory;
    @ApiModelProperty(value = "剩余数量")
    private Integer surplusNumber;
    @ApiModelProperty(value = "是否齐套")
    private Integer isKitting;
}
