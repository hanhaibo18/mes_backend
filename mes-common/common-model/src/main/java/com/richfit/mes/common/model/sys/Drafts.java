package com.richfit.mes.common.model.sys;

import java.io.Serializable;
import java.util.Date;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * sys_drafts
 * @author
 */
@Data
public class Drafts extends BaseEntity<Drafts> {

    /**
     * 标题
     */
    private String title;

    /**
     * 内容
     */
    private String content;

    /**
     * 收件人ID列表
     */
    private String userAddress;

    private String tenantId;

    private String branchCode;


    private static final long serialVersionUID = 1L;
}
