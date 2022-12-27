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
public class RgDevice extends BaseEntity<RgDevice> {

    private static final long serialVersionUID = -5801273490970600632L;

    @ApiModelProperty(value = "类别id", dataType = "String")
    private String typeId;

    @ApiModelProperty(value = "类别名称", dataType = "String")
    private String typeName;

    @ApiModelProperty(value = "类别编码", dataType = "String")
    private String typeCode;

    @ApiModelProperty(value = "类别顺序", dataType = "String")
    private String typeNo;

    @ApiModelProperty(value = "设备名称", dataType = "String")
    private String deviceName;

    @ApiModelProperty(value = "设备类别", dataType = "String")
    private String deviceType;

}
