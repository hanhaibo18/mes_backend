package com.richfit.mes.common.model.sys.dto;

import com.baomidou.mybatisplus.annotation.TableField;
import com.richfit.mes.common.core.base.BaseDto;
import com.richfit.mes.common.model.sys.TenantUser;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotBlank;
import java.util.Set;

/**
 * @author sun
 * @Description TenantUser
 */
@Data
@ApiModel
@EqualsAndHashCode(callSuper = true)
public class TenantUserDto extends BaseDto<TenantUser> {

    @ApiModelProperty(value = "用户类型")
    private Integer userType;

    @ApiModelProperty(value = "用户账号")
    @NotBlank(message = "用户名不能为空")
    @Length(min = 3, max = 32, message = "用户名长度在3到32个字符")
    private String userAccount;

    @ApiModelProperty(value = "用户密码")
    @NotBlank(message = "用户密码不能为空")
    @Length(min = 6, max = 20, message = "密码长度在5到20个字符")
    private String passwd;

    @ApiModelProperty(value = "租户ID")
    private String tenantId;

    @ApiModelProperty(value = "用户手机号")
    private String telephone;

    @ApiModelProperty(value = "用户邮箱")
    private String mail;

    @ApiModelProperty(value = "用户状态，true为可用")
    private Boolean status = true;

    @ApiModelProperty(value = "用户姓名")
    @NotBlank(message = "用户姓名不能为空")
    private String emplName;

    @ApiModelProperty(value = "二级单位")
    private String orgId;

    @ApiModelProperty(value = "所在结构ID")
    private String belongOrgId;

    @ApiModelProperty(value = "用户拥有的角色id列表")
    private Set<String> roleIds;

    @TableField(exist = false)
    @ApiModelProperty(value = "用户类型(0=普通,1=普通质检,2=质检租户)")
    private String userRoleType;
}
