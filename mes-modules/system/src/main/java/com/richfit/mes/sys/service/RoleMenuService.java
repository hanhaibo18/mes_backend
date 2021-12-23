package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.sys.RoleMenu;

import java.util.List;

/**
 * @author sun
 * @Description  角色菜单表 服务类
 */
public interface RoleMenuService extends IService<RoleMenu> {
    /**
     * 更新角色菜单
     * @param menus 角色菜单列表
     * @return
     */
    Boolean saveRoleMenus(List<RoleMenu> menus);

}
