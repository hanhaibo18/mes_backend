package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author renzewen
 * @Description 热工工时表
 */
@Data
public class Hour extends BaseEntity<Hour> {

    @ApiModelProperty(value = "工时版本id", dataType = "String")
    private String verId;

    @ApiModelProperty(value = "设备类型", dataType = "String")
    private String deviceType;

    @ApiModelProperty(value = "工序", dataType = "String")
    private String sequence;

    @ApiModelProperty(value = "重量下限", dataType = "String")
    private String weightDown;

    @ApiModelProperty(value = "重量上限", dataType = "String")
    private String weightUp;

    @ApiModelProperty(value = "碳化上限", dataType = "String")
    private String cUp;

    @ApiModelProperty(value = "碳化下限", dataType = "String")
    private String cDown;

    @ApiModelProperty(value = "氮化上限", dataType = "String")
    private String nUp;

    @ApiModelProperty(value = "氮化下限", dataType = "String")
    private String nDown;

    @ApiModelProperty(value = "是否高温", dataType = "String")
    private String isHighTemp;

    @ApiModelProperty(value = "工时", dataType = "String")
    private String hour;

}
