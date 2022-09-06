package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * produce_unqualified
 *
 * @author
 */
@Data
public class ProduceUnqualified extends BaseEntity<ProduceUnqualified> {

    @ApiModelProperty(value = "跟单工序Id")
    private String trackItem;

    @ApiModelProperty(value = "处理单编号")
    private String processingBillNumber;

    @ApiModelProperty(value = "工作号")
    private String workNo;

    @ApiModelProperty(value = "产品编号")
    private String productName;

    @ApiModelProperty(value = "零部件名称")
    private String partName;

    @ApiModelProperty(value = "零部件材料")
    private String componentMaterial;

    @ApiModelProperty(value = "零部件图号")
    private String partDrawingNo;

    @ApiModelProperty(value = "不合格数量")
    private Integer unqualifyNum;

    @ApiModelProperty(value = "零部件编号")
    private String partNo;

    @ApiModelProperty(value = "不合格描述")
    private String unqualifyDescription;
}
