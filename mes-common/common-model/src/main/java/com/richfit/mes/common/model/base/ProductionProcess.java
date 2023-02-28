package com.richfit.mes.common.model.base;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author HanHaiBo
 * @date 2023/2/20 15:22
 */
@Data
public class ProductionProcess extends BaseEntity<ProductionProcess> {
    /**
     * 工序名称
     */
    @ApiModelProperty(value = "工序名称", dataType = "String")
    private String processName;
    /**
     * 工序顺序
     */
    @ApiModelProperty(value = "工序顺序", dataType = "Integer")
    private Integer processSequence;
    /**
     * 生产路线id
     */
    @ApiModelProperty(value = "生产路线id", dataType = "String")
    private String productionRouteId;
}
