package com.richfit.mes.common.model.sys;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

/**
 * 编码规则模块
 *
 * @author 马峰
 * @since 2020-09-07
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
public class CodeRuleTemp extends BaseEntity<CodeRuleTemp> {

    /**
     * 租户ID
     */
    protected String tenantId;
    /**
     * 机构编码
     */
    protected String branchCode;
    /**
     * 分类名称
     */
    public String name;
    /**
     * 分类名称
     */
    public String code;
    /**
     * 是否内置 0-否 1-是
     */
    public  int isInner;
    /**
     * 1-是否强制 0-可编辑
     */
    public int codeType;
    /**
     * 状态 1-启用 2-停用
     */
    public int status;
    /**
     * 最新值
     */
    public String curValue;
    /**
     * 最大长度
     */
    public String maxLength;
     /**
     * 是否固定长度
     */
    public String isFixed;
    
}
