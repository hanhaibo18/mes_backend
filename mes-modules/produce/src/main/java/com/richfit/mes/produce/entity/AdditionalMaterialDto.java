package com.richfit.mes.produce.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: AdditionalMaterialDto.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年07月26日 17:54:00
 */
@Data
public class AdditionalMaterialDto {
    @ApiModelProperty(value = "物料号", dataType = "String")
    private String materialNo;
    @ApiModelProperty(value = "物料名称", dataType = "String")
    private String materialName;
    @ApiModelProperty(value = "图号", dataType = "String")
    private String drawingNo;
    @ApiModelProperty(value = "数量", dataType = "int")
    private int count;
    @ApiModelProperty(value = "原因", dataType = "int")
    private String cause;
    @ApiModelProperty(value = "说明", dataType = "int")
    private String explain;
    @ApiModelProperty(value = "是否关键件", dataType = "String")
    private String isKeyPart;
    @ApiModelProperty(value = "是否仓储领料", dataType = "String")
    private String isNeedPicking;
    @ApiModelProperty(value = "实物配送区分", dataType = "String")
    private String isEdgeStore;
    @ApiModelProperty(value = "单位")
    private String unit;

    private String trackHeadId;
    private String tiId;
    private String branchCode;

}
