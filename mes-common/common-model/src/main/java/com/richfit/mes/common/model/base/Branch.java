package com.richfit.mes.common.model.base;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * @author 王瑞
 * @Description 组织机构
 */
@Data
public class Branch extends BaseEntity<Branch> {

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 机构编码
     */
    private String branchCode;

    /**
     * 机构名称
     */
    private String branchName;

    /**
     * 上级机构编码
     */
    private String mainBranchCode;

    /**
     * 机构层级
     */
    private String branchLevel;

    /**
     * 机构类型
     */
    private String branchType;

    /**
     * 排序号
     */
    private Integer orderNo;

    /**
     * 是否使用
     */
    private String isUse;

}
