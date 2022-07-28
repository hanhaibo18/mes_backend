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
    private String materialNo;
    private String materialName;
    private String drawingNo;
    private int count;
    private String cause;
    private String explain;
    @ApiModelProperty(value = "是否关键件", dataType = "String")
    private String isKeyPart;
    @ApiModelProperty(value = "是否仓储领料", dataType = "String")
    private String isNeedPicking;
    @ApiModelProperty(value = "实物配送区分", dataType = "String")
    private String isEdgeStore;

    private String trackHeadId;
    private String tiId;
    private String branchCode;

}
