package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * produce_disqualification_user_opinion
 *
 * @author
 */
@Data
@Accessors(chain = true)
public class DisqualificationUserOpinion extends BaseEntity<DisqualificationUserOpinion> {

    /**
     * 不合格品主表Id
     */
    private String disqualificationId;

    /**
     * 排序
     */
    private int sort;

    /**
     * 意见
     */
    private String opinion;

    /**
     * 所属机构
     */
    private String branchCode;

    /**
     * 所属租户
     */
    private String tenantId;

    private static final long serialVersionUID = 1L;
}
