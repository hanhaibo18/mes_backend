package com.richfit.mes.common.model.base;

import com.richfit.mes.common.core.base.BaseEntity;
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
    private String processName;
    /**
     * 工序顺序
     */
    private Integer processSequence;
    /**
     * 生产路线id
     */
    private String productionRouteId;
}
