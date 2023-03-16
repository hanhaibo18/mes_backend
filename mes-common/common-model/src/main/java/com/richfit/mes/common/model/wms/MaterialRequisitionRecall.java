package com.richfit.mes.common.model.wms;

import lombok.Data;

/**
 * 
 * material_requisition_recall  MES领料单撤回上传WMS
 */
@Data
public class MaterialRequisitionRecall {
    /**
     * MES领料单ID
     */
    private String applyId;

    /**
     * MES领料单行项目ID
     */
    private String applyLineId;

}