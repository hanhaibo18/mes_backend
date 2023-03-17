package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * WMS出库信息上传MES(产品编号明细列表)
 * reverse_output_database_product_list
 */
@Data
public class ReverseOutputDatabaseProductList implements Serializable {
    /**
     * 参考单行项目ID
     */
    private String lineItemId;

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