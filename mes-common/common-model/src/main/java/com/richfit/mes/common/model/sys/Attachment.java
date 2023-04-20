package com.richfit.mes.common.model.sys;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * @author sun
 * @Description 附件信息
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
public class Attachment extends BaseEntity<Attachment> {

    /**
     * 租户ID
     */
    private String tenantId;
    /**
     * 附件名称
     */
    private String attachName;

    /**
     * 附件大小
     */
    private String attachSize;

    /**
     * 附件类型
     */
    private String attachType;

    /**
     * 组名称
     */
    private String groupName;

    /**
     * 文件ID
     */
    @JsonIgnore
    private String fastFileId;

    /**
     * 业务模块
     */
    private String module;

    /**
     * 预览地址
     */
    private String previewUrl;

    /**
     * 关联实体ID
     */
    private String relationId;

    /**
     * 关联实体ID
     */
    private String relationName;

    /**
     * 关联实体ID
     */
    private String relationType;

    /**
     * 分类
     */
    private String classify;

    /**
     * 状态
     */
    private String status;


    /**
     * 文件类型
     */
    private String type;

    /**
     * 图号
     */
    private String drawingNo;

    /**
     * 产品编码
     */
    private String productNo;

}
