package com.kld.mes.attachment.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 附件表
 * @TableName attachment
 */
@TableName(value ="sys_attachment")
@Data
public class Attachment implements Serializable {
    /**
     * 
     */
    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    /**
     * 附件名称
     */
    private String attachName;

    /**
     * 附件类型
     */
    private String attachType;

    /**
     * 附件大小
     */
    private String attachSize;

    /**
     * 文件ID
     */
    private String fastFileId;

    /**
     * 预览地址
     */
    private String previewUrl;

    /**
     * 创建人
     */
    private String createBy;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 修改人
     */
    private String modifyBy;

    /**
     * 修改时间
     */
    private Date modifyTime;

    /**
     * 备注
     */
    private String remark;

    /**
     * 状态
     */
    private String status;

    /**
     * 租户id
     */
    private String tenantId;

    /**
     * 组名称(跟单id)
     */
    private String groupName;

    /**
     * 业务模块
     */
    private String module;

    /**
     * 类型
     */
    private String classify;

    /**
     * 关系
     */
    private String relationId;

    /**
     * 关系名称
     */
    private String relationName;

    /**
     * 关系类型
     */
    private String relationType;

    /**
     * 文件类型
     */
    private String type;

    /**
     * 图号
     */
    private String drawingNo;

    /**
     * 产品编号
     */
    private String productNo;

    @TableField(exist = false)
    private static final long serialVersionUID = 1L;
}