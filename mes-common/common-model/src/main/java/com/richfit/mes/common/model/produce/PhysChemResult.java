package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;


/**
 * 理化检验试验结果
 *
 * @author renzewen
 * @since 2022-9-20
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class PhysChemResult extends BaseEntity<PhysChemResult> {
    @ApiModelProperty(value = "跟单工序id")
    private String itemId;
    @ApiModelProperty(value = "试验结果参数")
    private String paramInfo;
    @ApiModelProperty(value = "组织机构")
    private String branchCode;
    @ApiModelProperty(value = "租户id")
    private String tenantId;

}
