package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 模型库
 *
 * @author 张盘石
 * @since 2022-11-09
 */
@Data
public class HotLongProductQueryVo extends BaseEntity<HotLongProductQueryVo> {
    @ApiModelProperty(value = "租户id ", dataType = "String")
    private String tenantId;

    @ApiModelProperty(value = "产品名称 ", dataType = "String")
    private String productName;

    @ApiModelProperty(value = "产品图号 ", dataType = "Integer")
    private String ProductDrawingNo;

    @ApiModelProperty(value = "版本号", dataType = "Integer")
    private String version;

    @ApiModelProperty(value = "页码 ", dataType = "String")
    private int page;
    @ApiModelProperty(value = "条数 ", dataType = "String")
    private int limit;

}

