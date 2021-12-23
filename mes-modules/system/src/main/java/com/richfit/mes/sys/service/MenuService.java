package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.sys.Menu;
import com.richfit.mes.common.model.sys.dto.MenuTree;

import java.util.List;
import java.util.Set;

/**
 * <p>
 * 系统菜单 服务类
 * </p>
 *
 * @author gaoliang
 * @since 2020-05-25
 */
public interface MenuService extends IService<Menu> {
    /**
     * 通过角色编号查询URL 权限
     * @param roleId 角色ID
     * @return 菜单列表
     */
    List<Menu> findMenuByRoleId(String roleId);
    /**
     * 查询菜单
     * @param menuSet
     * @param parentId
     * @return
     */
    List<MenuTree> filterMenu(Set<Menu> menuSet, String parentId);
}
