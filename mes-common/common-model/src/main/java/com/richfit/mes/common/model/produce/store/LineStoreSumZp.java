package com.richfit.mes.common.model.produce.store;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * 装配库存统计实体类
 *
 * @Author: GaoLiang
 * @Date: 2022/6/28 10:24
 */
@Data
public class LineStoreSumZp {

    @ApiModelProperty(value = "图号")
    private String drawingNo;

    @ApiModelProperty(value = "物料编号")
    private String materialNo;

    @ApiModelProperty(value = "产品名称")
    private String productName;

    @ApiModelProperty(value = "库存数量")
    private Integer storeNumber;

    @ApiModelProperty(value = "配送数量")
    private Integer deliveryNumber;

    @ApiModelProperty(value = "需求数量")
    private Integer requireNumber;

    @ApiModelProperty(value = "已装量")
    private Integer assemblyNumber;

    @ApiModelProperty(value = "待装数量")
    private Integer waitAssemblyNumber;
}
