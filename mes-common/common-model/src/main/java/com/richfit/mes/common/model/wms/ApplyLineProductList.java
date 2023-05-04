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

    private String applyLineId;

    private String productNum;

    private String quantity;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}
