package com.richfit.mes.common.model.sys;

import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * @author sun
 * @Description 角色
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
public class RoleTemp extends BaseEntity<RoleTemp> {
    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    @ApiModelProperty(value = "角色名称")
    private String roleName;
    /**
     * 角色code
     */
    @NotBlank(message = "角色标识不能为空")
    @ApiModelProperty(value = "角色标识")
    private String roleCode;

    /**
     * 角色type
     */
    @NotBlank(message = "角色类型")
    @ApiModelProperty(value = "角色type")
    private String roleType;

    /**
     * 角色描述
     */
    @ApiModelProperty(value = "描述")
    private String roleDesc;
    /**
     * 是否默认 1-启用，0-禁用
     */
    @ApiModelProperty(value = "1-启用，0-禁用")
    private Boolean enabled;

}
