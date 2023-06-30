package com.tc.mes.plm.entity.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * PDM附件
 * @TableName produce_mes_pdm_attachment
 */
@TableName(value ="produce_mes_pdm_attachment")
@Data
public class ProduceMesPdmAttachment implements Serializable {
    /**
     * id
     */
    private String id;

    /**
     * 表名id
     */
    private String tableId;

    /**
     * 文件地址
     */
    private String fileUrl;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}