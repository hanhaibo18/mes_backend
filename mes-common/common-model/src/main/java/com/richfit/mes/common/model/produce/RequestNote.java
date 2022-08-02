package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * produce_request_note
 *
 * @author
 */
@Data
public class RequestNote extends BaseEntity<RequestNote> {

    /**
     * 跟单ID
     */
    private String trackHeadId;

    /**
     * 跟单工序Id
     */
    private String trackItemId;

    /**
     * 申请单号
     */
    private String requestNoteNumber;


    /**
     * 所属机构
     */
    private String branchCode;

    /**
     * 所属租户
     */
    private String tenantId;


}
