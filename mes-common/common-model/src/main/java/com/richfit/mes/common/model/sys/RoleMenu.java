package com.richfit.mes.common.model.sys;

import com.richfit.mes.common.core.base.BaseEntity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * @author sun
 * @Description 角色菜单表
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
public class RoleMenu extends BaseEntity<RoleMenu> {
    /**
     * 用户ID
     */
    private String roleId;
    /**
     * 菜单ID
     */
    private String menuId;
    /**
     * 按钮权限
     */
    private String permission;
}
