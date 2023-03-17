package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * apply_line_info_list 申请单上传 行数据
 */
@Data
public class ApplyLineInfoList implements Serializable {
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

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}