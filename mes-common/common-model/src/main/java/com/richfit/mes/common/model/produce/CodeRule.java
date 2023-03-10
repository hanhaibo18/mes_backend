package com.richfit.mes.common.model.produce;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
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
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
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
            return Arrays.asList(roleId.split(","));
        }
        return roleIdList;
    }





}
