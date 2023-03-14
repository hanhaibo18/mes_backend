package com.richfit.mes.common.model.wms;

import lombok.Data;

/**
 * 
 * requisition_line_info_list 产品编号明细列表
 */
@Data
public class RequisitionLineInfoList {
    /**
     * MES领料单行id
     */
    private String applyLineId;

    /**
     * 产品编号
     */
    private String productNum;

    /**
     * 数量
     */
    private String quantity;

}