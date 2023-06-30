package com.tc.mes.plm.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * PDM附件
 * @TableName mes_pdm_attachment
 */
@TableName(value ="mes_pdm_attachment")
@Data
public class MesPdmAttachment implements Serializable {
    /**
     * id
     */
    @TableId(type = IdType.ASSIGN_UUID)
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