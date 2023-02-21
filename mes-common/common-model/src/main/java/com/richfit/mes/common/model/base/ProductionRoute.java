package com.richfit.mes.common.model.base;

import com.richfit.mes.common.core.base.BaseEntity;
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
    private String productionRouteName;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 机构编号
     */
    private String branchCode;
}
