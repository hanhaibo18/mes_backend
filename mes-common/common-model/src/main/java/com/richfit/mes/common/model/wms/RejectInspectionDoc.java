package com.richfit.mes.common.model.wms;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

import java.io.Serializable;

/**
 * 
 * reject_inspection_doc MES报检单驳回WMS
 */
@Data
public class RejectInspectionDoc implements Serializable {
    /**
     * WMS报检单ID 唯一
     */
    private String id;

    /**
     * 驳回原因
     */
    private String returnReason;

    /**
     * 驳回操作人
     */
    private String returnUser;

    /**
     * 驳回操作日期
     */
    private String returnDate;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;

}