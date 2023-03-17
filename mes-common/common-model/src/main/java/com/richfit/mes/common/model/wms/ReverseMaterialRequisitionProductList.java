package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 产品编号明细列表
 * reverse_material_requisition_product_list
 */
@Data
public class ReverseMaterialRequisitionProductList implements Serializable {
    /**
     * MES领料单行项目ID
     */
    private String applyItemId;

    /**
     * 产品编号
     */
    private String productNo;

    /**
     * 数量
     */
    private String number;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}