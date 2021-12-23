package com.richfit.mes.common.model.sys;

import com.richfit.mes.common.core.base.BaseEntity;
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
public class Role extends BaseEntity<Role> {
    /**
     * 角色名称
     */
    @NotBlank(message = "角色名称不能为空")
    private String roleName;
    /**
     * 角色code
     */
    @NotBlank(message = "角色标识不能为空")
    private String roleCode;
    /**
     * 角色描述
     */
    private String roleDesc;
    /**
     * 是否默认 1-启用，0-禁用
     */
    private Boolean enabled;
    /**
     * 租户ID
     */
    private String tenantId;
}
