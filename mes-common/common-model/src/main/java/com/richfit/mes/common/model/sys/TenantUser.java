package com.richfit.mes.common.model.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.Set;

/**
 * <p>
 * 租户用户
 * </p>
 *
 * @author gaoliang
 * @since 2020-05-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
public class TenantUser extends BaseEntity<TenantUser> {


    /**
     * 用户类型
     */
    private Integer userType;

    /**
     * 账号
     */
    private String userAccount;

    /**
     * 密码
     */
    private String passwd;

    /**
     * 租户ID
     */
    private String tenantId;

    /**
     * 手机
     */
    private String telephone;

    /**
     * 邮箱
     */
    private String mail;

    /**
     * 账号状态
     */
    private Boolean status;

    /**
     * 员工姓名
     */
    private String emplName;

    /**
     * 二级单位
     */
    private String orgId;

    /**
     * 所在结构ID
     */
    private String belongOrgId;

    /**
     * 角色
     */
    @TableField(exist = false)
    private Set<String> roleIds;


    @TableField(exist = false)
    @ApiModelProperty(value = "用户类型(0=普通,1=普通质检,2=质检租户)")
    private String userRoleType;

    @TableField(exist = false)
    private String orgName;

    @TableField(exist = false)
    private String belongOrgName;

    @TableField(exist = false)
    private String tenantErpCode;

}
