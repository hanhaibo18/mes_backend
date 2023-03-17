package com.richfit.mes.common.model.wms;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 产品编号明细列表
 * reverse_input_database_cover_product_list
 */
@Data
public class ReverseInputDatabaseCoverProductList implements Serializable {
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