package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 模型库
 *
 * @author 张盘石
 * @since 2022-11-08
 */
@Data
public class HotModelStore extends BaseEntity<HotModelStore> {


    @ApiModelProperty(value = "租户id ", dataType = "String")
    private String tenantId;

    @ApiModelProperty(value = "模型名称 ", dataType = "String")
    private String modelName;

    @ApiModelProperty(value = "模型类型 ", dataType = "Integer")
    private Integer modelType;

    @ApiModelProperty(value = "模型数量(正常) ", dataType = "Integer")
    private Integer normalNum;

    @ApiModelProperty(value = "模型图号 ", dataType = "String")
    private String modelDrawingNo;

    @ApiModelProperty(value = "货位号 ", dataType = "String")
    private String locationNo;

    @ApiModelProperty(value = "模型数量(报废) ", dataType = "String")
    private Integer scrapNum;

    @ApiModelProperty(value = "模型备注 ", dataType = "String")
    private String modelRemark;

    @ApiModelProperty(value = "版本号", dataType = "String")
    private String version;


}

