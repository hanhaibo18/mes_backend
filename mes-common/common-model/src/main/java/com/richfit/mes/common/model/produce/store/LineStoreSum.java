package com.richfit.mes.common.model.produce.store;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @Author: GaoLiang
 * @Date: 2022/5/31 16:23
 */
@Data
public class LineStoreSum {

    @ApiModelProperty(value = "图号")
    private String drawingNo;

    @ApiModelProperty(value = "物料编号")
    private String materialNo;

    @ApiModelProperty(value = "物料名称")
    private String materialName;

    @ApiModelProperty(value = "库存数量")
    private Integer number;

    @ApiModelProperty(value = "在制数量")
    private Integer makingNumber;

    @ApiModelProperty(value = "完工数量")
    private Integer compNumber;

}
