package com.richfit.mes.common.model.produce;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.List;

/**
 * 编码规则模块
 *
 * @author 马峰
 * @since 2020-09-07
 */
@Data
public class CodeRule extends BaseEntity<CodeRule> {

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

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public void setBranchCode(String branchCode) {
        this.branchCode = branchCode;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public int getIsInner() {
        return isInner;
    }

    public void setIsInner(int isInner) {
        this.isInner = isInner;
    }

    public int getCodeType() {
        return codeType;
    }

    public void setCodeType(int codeType) {
        this.codeType = codeType;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getCurValue() {
        return curValue;
    }

    public void setCurValue(String curValue) {
        this.curValue = curValue;
    }

    public String getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(String maxLength) {
        this.maxLength = maxLength;
    }

    public String getIsFixed() {
        return isFixed;
    }

    public void setIsFixed(String isFixed) {
        this.isFixed = isFixed;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
    }

    public void setRoleIdList(List<String> roleIdList) {
        this.roleIdList = roleIdList;
    }

    public String getRuleNo() {
        return ruleNo;
    }

    public void setRuleNo(String ruleNo) {
        this.ruleNo = ruleNo;
    }

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
     /**
     * 规则级别（0公司级、1车间级、2权限级）
     */
    public String level;
    /**
     * 权限级 角色id集合
     */
    public String roleId;

    /**
     * 前端用的角色ids
     */
    @TableField(exist = false)
    public List<String> roleIdList;

    /**
     * 规则号，Excel导入使用
     */
    @TableField(exist = false)
    public String ruleNo;

    public String getRoleId() {
        StringBuilder stringBuilder = new StringBuilder();
        if(!ObjectUtil.isEmpty(this.roleIdList)){
            for (String siteId : this.roleIdList) {
                if(!StringUtils.isEmpty(String.valueOf(stringBuilder))){
                    stringBuilder.append(",");
                }
                stringBuilder.append(siteId);
            }
            roleId = String.valueOf(stringBuilder);
        }
        return roleId;
    }

    public List<String> getRoleIdList() {
        if(!StringUtils.isEmpty(roleId) && (ObjectUtil.isEmpty(roleIdList) || roleIdList.size()==0)){
            roleIdList = Arrays.asList(roleId.split(","));
        }
        return roleIdList;
    }





}
