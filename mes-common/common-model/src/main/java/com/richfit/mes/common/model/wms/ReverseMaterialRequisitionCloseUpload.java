package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * WMS领料单关闭上传MES
 * reverse_material_requisition_close_upload
 */
@Data
public class ReverseMaterialRequisitionCloseUpload implements Serializable {
    /**
     * MES领料单ID 唯一
     */
    private String id;

    /**
     * MES领料单行项目ID 
     */
    private String applyItemId;

    /**
     * 关闭数量 未清出库数量
     */
    private String closeQuantity;

    /**
     * 备注 
     */
    private String remark;

    /**
     * 关闭操作人 
     */
    private String closeOperator;

    /**
     * 关闭操作日期 
     */
    private String closeDate;

    /**
     * 产品编号明细列表
     */
    @TableField(exist = false)
    private List<ReverseMaterialRequisitionProductList> lineList;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}