package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * WMS报检单上传MES(产品编号明细列表)
 * reverse_inspection_line_list
 */
@Data
public class ReverseInspectionLineList implements Serializable {
    /**
     * 报检单ID
     */
    private String id;

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