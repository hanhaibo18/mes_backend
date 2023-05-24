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
     * MES领料单编号
     */
    private String applyNum;

    /**
     * 操作人
     */
    private String returnUser;

    /**
     * 操作时间
     */
    private String returnDate;

    /**
     * 备注
     */
    private String remark;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}