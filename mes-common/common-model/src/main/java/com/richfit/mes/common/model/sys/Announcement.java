package com.richfit.mes.common.model.sys;

import java.io.Serializable;
import java.util.Date;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * sys_announcement
 * @author Mr.hou
 */
@Data
@Accessors(chain = true)
@NoArgsConstructor
public class Announcement extends BaseEntity<Announcement> implements Serializable {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 通知类型 待定
     */
    private String noticeType;

    /**
     * 所属类型 编码
     */
    private String type;

    /**
     * 浏览量
     */
    private Long browse;

    private String groupUser;

    private String procInstId;

    /**
     * 通知公告用   是否置顶（1置顶，0不置顶）
     */
    private Integer ifTop;

    /**
     * 置顶级别
     */
    private Integer topNumber;

    /**
     * 发布课室
     */
    private String publishOrgId;
    /**
     * 文件链接
     */
    private String fileId;
    /**
     * 文件链接
     */
    private String filePath;

        private String tenantId;

    /**
     * 所属机构
     */
    private String branchCode;

    /**
     * 创建人组织机构ID
     */
    private String createOrgId;

    /**
     * 修改人组织机构ID
     */
    private String modifyOrgId;

    /**
     * 逻辑删除
     */
    private Integer bsflag;

    @TableField(exist = false)
    private String userName;
    @TableField(exist = false)
    private String orgName;

    private static final long serialVersionUID = 1L;
}
