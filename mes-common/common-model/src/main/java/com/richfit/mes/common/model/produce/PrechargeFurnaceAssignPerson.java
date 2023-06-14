package com.richfit.mes.common.model.produce;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

import java.util.List;

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
     * 配送人name
     */
    private String userName;
    /**
     * 预装炉派工id
     */
    private String prechargeFurnaceAssignId;

}
