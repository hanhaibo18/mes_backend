package com.richfit.mes.common.model.sys.vo;

import com.baomidou.mybatisplus.annotation.FieldFill;
import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseVo;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.model.sys.TenantUser;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * @author sun
 * @Description 租户用户
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class TenantUserVo extends BaseVo<TenantUser> {
    private static final long serialVersionUID = 207908208583575574L;

    public TenantUserVo(TenantUser user) {
        BeanUtils.copyProperties(user, this);
    }

    /**
     * 用户类型
     */
    @ApiModelProperty(value = "用户类型", dataType = "Integer")
    private Integer userType;

    /**
     * 账号
     */
    @ApiModelProperty(value = "账号", dataType = "String")
    private String userAccount;

    /**
     * 租户ID
     */
    @ApiModelProperty(value = "租户ID", dataType = "String")
    private String tenantId;

    /**
     * 手机
     */
    @ApiModelProperty(value = "手机", dataType = "String")
    private String telephone;

    /**
     * 邮箱
     */
    @ApiModelProperty(value = "邮箱", dataType = "String")
    private String mail;

    /**
     * 账号状态
     */
    @ApiModelProperty(value = "账号状态", dataType = "String")
    private Boolean status;

    /**
     * 员工姓名
     */
    @ApiModelProperty(value = "员工姓名", dataType = "String")
    private String emplName;

    /**
     * 二级单位
     */
    @ApiModelProperty(value = "二级单位", dataType = "String")
    private String orgId;

    /**
     * 所在结构ID
     */
    @ApiModelProperty(value = "所在结构ID", dataType = "String")
    private String belongOrgId;

    /**
     * 角色
     */
    @ApiModelProperty(value = "角色", dataType = "String")
    private List<Role> roleList;

    @ApiModelProperty(value = "二级单位名称", dataType = "String")
    private String orgName;
    @ApiModelProperty(value = "所在结构单位名称", dataType = "String")
    private String belongOrgName;
    @ApiModelProperty(value = "用户角色类型", dataType = "String")
    private String userRoleType;


    /**
     * 创建者
     */
    @TableField(fill = FieldFill.INSERT)
    protected String createBy;

    /**
     * 创建日期
     */
    @TableField(fill = FieldFill.INSERT)
    protected Date createTime;

    /**
     * 更新者
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected String modifyBy;

    /**
     * 更新日期
     */
    @TableField(fill = FieldFill.INSERT_UPDATE)
    protected Date modifyTime;


    @TableField(exist = false)
    @ApiModelProperty(value = "租户ERPCODE", dataType = "String")
    private String tenantErpCode;

    @TableField(exist = false)
    @ApiModelProperty(value = "所在租户公司 code", dataType = "String")
    private String companyCode;

    @ApiModelProperty(value = "工时比例")
    private Double ratioHours;

    @TableField(exist = false)
    @ApiModelProperty(value = "menuType为2的menuCode", dataType = "String")
    private Set<String> permissions;


}
