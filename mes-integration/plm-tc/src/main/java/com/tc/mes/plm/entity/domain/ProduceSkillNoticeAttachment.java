package com.tc.mes.plm.entity.domain;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

/**
 * 技术通知单与附件关系表
 * @TableName produce_skill_notice_attachment
 */
@TableName(value ="produce_skill_notice_attachment")
@Data
public class ProduceSkillNoticeAttachment implements Serializable {
    /**
     * id
     */
    private String id;

    /**
     * 技术通知单id
     */
    private String skillNoticeId;

    /**
     * 附件url
     */
    private String url;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}