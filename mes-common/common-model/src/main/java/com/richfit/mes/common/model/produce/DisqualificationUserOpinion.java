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
     * 意见类型(0 = 不合格品情况,1 = 质控工程师评审意见,2 = 责任单位1 ,3 = 责任单位2)
     */
    private int type;


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
