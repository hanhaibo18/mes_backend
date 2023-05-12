package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 申请单上传  产品编号明细列表
 *  applyLineProductList
 */
@Data
public class ApplyLineProductList implements Serializable {

    /**
     *  MES申请单行id
     */
    private String applyLineId;

    /**
     *  产品编号
     */
    private String productNum;

    /**
     *  数量
     */
    private Integer quantity;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
