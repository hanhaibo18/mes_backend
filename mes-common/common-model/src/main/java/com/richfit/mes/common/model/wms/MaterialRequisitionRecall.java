package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * material_requisition_recall  MES领料单撤回上传WMS
 */
@Data
public class MaterialRequisitionRecall implements Serializable {
    /**
     * MES领料单ID
     */
    private String applyId;

    /**
     * MES领料单行项目ID
     */
    private String applyLineId;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}