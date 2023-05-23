package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 
 * product_no_list  产品编号列表
 */
@Data
public class ProductNoList implements Serializable {
    /**
     * 报检单ID
     */
    private String insId;

    /**
     * 产品编号
     */
    private String productNum;

    /**
     * 合格数量
     */
    private BigDecimal qualifiedQuantity;

    /**
     * 不合格数量
     */
    private BigDecimal unqualifiedQuantity;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}