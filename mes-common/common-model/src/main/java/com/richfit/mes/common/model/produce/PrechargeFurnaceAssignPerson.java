package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * @author HanHaiBo
 * @date 2023/5/16 9:57
 */
@Data
public class PrechargeFurnaceAssignPerson extends BaseEntity<PrechargeFurnaceAssignPerson> {
    /**
     * 预装炉id
     */
    private Long prechargeFurnaceId;
    /**
     * 配送人id
     */
    private String userId;
    /**
     * 预装炉派工id
     */
    private String prechargeFurnaceAssignId;

}
