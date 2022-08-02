package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * produce_request_note_detail
 *
 * @author
 */
@Data
public class RequestNoteDetail extends BaseEntity<RequestNoteDetail> {
    /**
     * 申请单Id
     */
    private String noteId;

    /**
     * 物料号
     */
    private String materialNo;

    /**
     * 物料名称
     */
    private String materialName;

    /**
     * 图号
     */
    private String drawingNo;

    /**
     * 数量
     */
    private Integer number;

    /**
     * 原因
     */
    private String reason;

    /**
     * 说明
     */
    private String explain;
    
    /**
     * 是否需要领料
     */
    private String isNeedPicking;

    /**
     * 是否是关键件
     */
    private String isKeyPart;

    /**
     * 是否线边库
     */
    private String isEdgeStore;

    /**
     * 所属机构
     */
    private String branchCode;

    /**
     * 所属租户
     */
    private String tenantId;

}
