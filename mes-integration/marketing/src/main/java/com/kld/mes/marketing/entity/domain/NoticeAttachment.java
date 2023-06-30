package com.kld.mes.marketing.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 销售排产单文件关联信息
 * @TableName notice_attachment
 */
@TableName(value ="produce_notice_attachment")
@Data
public class NoticeAttachment implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.AUTO)
    private Integer id;

    /**
     * 销售排产通知ID
     */
    private String noticeId;

    /**
     * 文件ID
     */
    private String attachmentId;

    /**
     * 文件类型 1 = 排产单附件 2 = 排产清单附件
     */
    private String type;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}