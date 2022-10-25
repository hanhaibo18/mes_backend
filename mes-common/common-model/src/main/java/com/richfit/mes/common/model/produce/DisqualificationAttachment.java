package com.richfit.mes.common.model.produce;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;

/**
 * produce_disqualification_attachment
 *
 * @author hou xinyu
 */
@Data
public class DisqualificationAttachment extends BaseEntity<DisqualificationAttachment> {
    /**
     * 不合格品表
     */
    private String disqualificationId;

    /**
     * 文件Id
     */
    private String fileId;

    /**
     * 文件名称
     */
    private String fileName;

    /**
     * 工厂代码
     */
    private String branchCode;

    /**
     * 租户Id
     */
    private String tenantId;

    private static final long serialVersionUID = 1L;
}
