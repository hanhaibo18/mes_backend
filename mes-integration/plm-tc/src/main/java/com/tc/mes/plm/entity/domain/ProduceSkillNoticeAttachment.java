package com.tc.mes.plm.entity.domain;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
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
    @TableId(type = IdType.ASSIGN_UUID)
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