package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.constant.CommonConstant;
import com.richfit.mes.common.model.sys.Menu;
import com.richfit.mes.common.model.sys.dto.MenuTree;
import com.richfit.mes.common.model.sys.vo.MenuTreeUtil;
import com.richfit.mes.sys.dao.MenuMapper;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统菜单 服务实现类
 * </p>
 *
 * @author gaoliang
 * @since 2020-05-25
 */
@Service
public class MenuServiceImpl extends ServiceImpl<MenuMapper, Menu> implements MenuService {

    @Override
    public List<Menu> findMenuByRoleId(String roleId, String tenantId) {
        return baseMapper.listMenusByRoleId(roleId, tenantId);
    }

    @Override
    public List<MenuTree> filterMenu(Set<Menu> menuSet, String parentId) {
        List<MenuTree> menuTreeList = menuSet.stream().map(MenuTree::new)
                .sorted(Comparator.comparingInt(MenuTree::getMenuOrder)).collect(Collectors.toList());
        String parent = parentId == null ? CommonConstant.MENU_TREE_ROOT_ID : parentId;
        return MenuTreeUtil.build(menuTreeList, parent);
    }

}
