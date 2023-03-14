package com.richfit.mes.common.model.wms;

import lombok.Data;

/**
 * 
 * reject_inspection_doc MES报检单驳回WMS
 */
@Data
public class RejectInspectionDoc {
    /**
     * WMS报检单ID 唯一
     */
    private String id;

    /**
     * 驳回原因
     */
    private String returnReason;

    /**
     * 驳回操作人
     */
    private String returnUser;

    /**
     * 驳回操作日期
     */
    private String returnDate;

}