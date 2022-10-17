package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * produce_disqualification_user_opinion
 *
 * @author
 */
@Data
public class DisqualificationUserOpinion extends BaseEntity<DisqualificationUserOpinion> {

    /**
     * 不合格品主表Id
     */
    private String disqualificationId;

    /**
     * 审核人员
     */
    private String userId;

    /**
     * 审核人员姓名
     */
    private String userName;

    /**
     * 审核人员车间
     */
    private String userBranch;

    /**
     * 审核人员车间名称
     */
    private String userBranchName;

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
