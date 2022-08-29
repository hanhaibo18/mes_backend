package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.sys.RoleMenu;
import com.richfit.mes.sys.dao.RoleMenuMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.TreeSet;
import java.util.stream.Collectors;

/**
 * @author sun
 * @Description 角色菜单表 服务实现类
 */
@Service
public class RoleMenuServiceImpl extends ServiceImpl<RoleMenuMapper, RoleMenu> implements RoleMenuService {

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveRoleMenus(List<RoleMenu> menus) {
        if (menus != null && menus.isEmpty()) {
            return Boolean.TRUE;
        }

        ArrayList<RoleMenu> collect = menus.stream().collect(Collectors.collectingAndThen(
                Collectors.toCollection(() -> new TreeSet<>(
                        Comparator.comparing(
                                RoleMenu::getRoleId))), ArrayList::new));
        collect.forEach(menu -> {
            this.remove(Wrappers.<RoleMenu>query().lambda().eq(RoleMenu::getRoleId, menu.getRoleId()));
        });

        return this.saveBatch(menus);
    }
}
