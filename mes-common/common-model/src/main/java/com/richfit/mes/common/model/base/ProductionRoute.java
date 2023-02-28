package com.richfit.mes.common.model.base;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author HanHaiBo
 * @date 2023/2/20 14:53
 */
@Data
public class ProductionRoute extends BaseEntity<ProductionRoute> {
    /**
     * 生产路线名称
     */
    @ApiModelProperty(value = "生产路线名称", dataType = "String")
    private String productionRouteName;

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID", dataType = "String")
    private String tenantId;

    /**
     * 机构编号
     */
    @ApiModelProperty(value = "机构编号", dataType = "String")
    private String branchCode;
}
