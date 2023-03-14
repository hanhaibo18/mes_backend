package com.richfit.mes.common.model.wms;

import lombok.Data;

/**
 * 
 * apply_line_info_list 申请单上传 行数据
 */
@Data
public class ApplyLineInfoList {
    /**
     * MES申请单行id
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