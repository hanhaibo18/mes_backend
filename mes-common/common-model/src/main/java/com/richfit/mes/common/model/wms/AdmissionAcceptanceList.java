package com.richfit.mes.common.model.wms;


import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * admission_acceptance_list 外协-入场验收情况列表
 */
@Data
public class AdmissionAcceptanceList implements Serializable {
    /**
     * 报检单ID
     */
    private String insId;

    /**
     * 复验项目 (材料和探伤是两类报告)
     */
    private String reinsItem;

    /**
     * 复验报告编号
     */
    private String reinsNum;

    /**
     * 复验报告  路径地址
     */
    private String reinsUrl;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}