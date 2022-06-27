package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * @author 王瑞
 * @Description 跟单
 */
@Data
public class TrackHeadInfo extends BaseEntity<TrackHeadInfo> {

    private static final long serialVersionUID = 6336423092552908350L;
    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 审批人
     */
    private String approvalBy;

    /**
     * 签发人
     */
    private String issueBy;

    /**
     * 组织机构编号
     */
    private String branchCode;
}
