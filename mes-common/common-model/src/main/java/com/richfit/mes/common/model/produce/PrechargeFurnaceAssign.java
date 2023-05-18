package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * @author HanHaiBo
 * @date 2023/5/16 9:57
 */
@Data
public class PrechargeFurnaceAssign extends BaseEntity<PrechargeFurnaceAssign> {
    /**
     * 预装炉id
     */
    private Long prechargeFurnaceId;
    /**
     * 配送人id
     */
    private String userId;

}
