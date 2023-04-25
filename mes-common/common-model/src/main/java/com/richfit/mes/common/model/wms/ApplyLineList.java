package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * apply_line_list 申请单上传 行数据
 */
@Data
public class ApplyLineList implements Serializable {
    /**
     * MES申请单ID
     */
    private String applyId;

    /**
     * MES申请单行id
     */
    private String id;

    /**
     * MES申请单行项目
     */
    private Integer lineNum;

    /**
     * 物料编码
     */
    private String materialNum;

    /**
     * 物料名称
     */
    private String materialDesc;

    /**
     * 计量单位
     */
    private String unit;

    /**
     * 申请单数量
     */
    private Double quantity;

    /**
     * 关键件
     */
    private String crucialFlag;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}