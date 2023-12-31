package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author renzewen
 * @Description 热工工时表
 */
@Data
public class Hour extends BaseEntity<Hour> {

    private static final long serialVersionUID = -5801273490970600632L;

    @ApiModelProperty(value = "工时版本id", dataType = "String")
    private String verId;

    @ApiModelProperty(value = "设备类型code", dataType = "String")
    private String typeCode;

    @ApiModelProperty(value = "设备类型名称", dataType = "String")
    private String typeName;

    @ApiModelProperty(value = "工序id", dataType = "String")
    private String optId;

    @ApiModelProperty(value = "工序名称", dataType = "String")
    private String optName;

    @ApiModelProperty(value = "重量下限", dataType = "String")
    private String weightDown;

    @ApiModelProperty(value = "重量上限", dataType = "String")
    private String weightUp;

    @ApiModelProperty(value = "碳化上限", dataType = "String")
    private String layerDepthCarbonCeiling;

    @ApiModelProperty(value = "碳化下限", dataType = "String")
    private String layerDepthCarbonFloor;

    @ApiModelProperty(value = "氮化上限", dataType = "String")
    private String layerDepthNitrogenCeiling;

    @ApiModelProperty(value = "氮化下限", dataType = "String")
    private String layerDepthNitrogenFloor;

    @ApiModelProperty(value = "是否高温", dataType = "String")
    private String isHighTemp;

    @ApiModelProperty(value = "工时", dataType = "String")
    private String hour;

    @TableField(exist = false)
    @ApiModelProperty(value = "是否导入", dataType = "String")
    private  String isExport;
    @TableField(exist = false)
    @ApiModelProperty(value = "工时版本", dataType = "ver")
    private String ver;

}
