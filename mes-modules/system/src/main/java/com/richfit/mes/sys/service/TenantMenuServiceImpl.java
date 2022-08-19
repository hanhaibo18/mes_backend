package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.sys.Menu;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.model.sys.RoleMenu;
import com.richfit.mes.common.model.sys.TenantMenu;
import com.richfit.mes.sys.dao.TenantMenuMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 租户菜单 服务实现类
 * </p>
 *
 * @author gaoliang
 * @since 2020-05-25
 */
@Service
public class TenantMenuServiceImpl extends ServiceImpl<TenantMenuMapper, TenantMenu> implements TenantMenuService {

    @Autowired
    private TenantMenuMapper tenantMenuMapper;

    @Autowired
    private RoleMenuService roleMenuService;

    @Autowired
    private RoleService roleService;

    @Override
    public List<Menu> queryTenantMenuByPId(String tenantId, String pId) {
        return tenantMenuMapper.queryTenantMenuByPId(tenantId, pId);
    }

    @Override
    @Transactional
    public Boolean saveTenantMenu(List<TenantMenu> menus, String tenantId) {

        //删除该租户已分配菜单
        Map parMap = new HashMap();
        parMap.put("tenant_id", tenantId);
        this.removeByMap(parMap);
        //批量保存菜单
        this.saveBatch(menus);

        //给该租户的租户管理员角色，绑定这些菜单
        saveTenantAdminRoleMenus(menus, tenantId);

        return true;
    }

    private void saveTenantAdminRoleMenus(List<TenantMenu> menus, String tenantId) {
        Role tenantAdminRol = roleService.getAdminRole(tenantId);
        List<RoleMenu> list = new ArrayList<>();
        for (TenantMenu tenantMenu : menus) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setMenuId(tenantMenu.getMenuId());
            roleMenu.setRoleId(tenantAdminRol.getId());
            list.add(roleMenu);
        }
        roleMenuService.saveRoleMenus(list);
    }
}
