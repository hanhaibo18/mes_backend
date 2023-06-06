package com.richfit.mes.sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.sys.Menu;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.model.sys.RoleMenu;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.sys.service.MenuService;
import com.richfit.mes.sys.service.RoleMenuService;
import com.richfit.mes.sys.service.RoleService;
import com.richfit.mes.sys.service.TenantMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Collectors;

/**
 * <p>
 * 系统菜单 前端控制器
 * </p>
 *
 * @author gaoliang
 * @since 2020-05-25
 */
@Slf4j
@Api("菜单信息管理")
@RestController
@RequestMapping("/api/sys/menu")
public class MenuController extends BaseController {

    @Autowired
    private MenuService menuService;

    @Autowired
    private RoleMenuService roleMenuService;

    @Autowired
    private TenantMenuService tenantMenuService;

    @Autowired
    private RoleService roleService;

    //TODO 权限控制  条件约束验证   userToken补充

    /**
     * 新增菜单
     */
    @ApiOperation(value = "新增菜单信息", notes = "新增菜单信息")
    @ApiImplicitParam(name = "menu", value = "菜单", required = true, dataType = "Menu", paramType = "body")
    @PostMapping("/save")
    public CommonResult<Boolean> saveMenu(@RequestBody Menu menu) throws GlobalException {
        return CommonResult.success(menuService.save(menu));
    }

    /**
     * 根据ID获取菜单
     */
    @ApiOperation(value = "获取菜单信息", notes = "根据菜单id获取菜单详细信息")
    @ApiImplicitParam(name = "id", value = "菜单ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/{id}")
    public CommonResult<Menu> getMenu(@PathVariable String id) throws GlobalException {
        return CommonResult.success(menuService.getById(id));
    }

    /**
     * 更新菜单
     */
    @ApiOperation(value = "修改菜单信息", notes = "修改菜单信息")
    @ApiImplicitParam(name = "menu", value = "菜单", required = true, dataType = "Menu", paramType = "body")
    @PutMapping("/update")
    public CommonResult<Boolean> updateMenu(@RequestBody Menu menu) throws GlobalException {

        return CommonResult.success(menuService.updateById(menu));
    }

    /**
     * 删除菜单
     */
    @ApiOperation(value = "删除菜单信息", notes = "根据菜单id删除记录")
    @ApiImplicitParam(name = "id", value = "菜单id", required = true, dataType = "String", paramType = "path")
    @DeleteMapping("/delete/{id}")
    public CommonResult<Boolean> delMenuById(@PathVariable String id) throws GlobalException {
        //TODO 菜单已分配 不能删除
        return CommonResult.success(menuService.removeById(id));
    }

    /**
     * 根据父菜单Id获取子菜单
     */
    @ApiOperation(value = "根据父菜单Id获取子菜单", notes = "根据父菜单Id获取子菜单")
    @ApiImplicitParam(name = "id", value = "菜单id", required = true, dataType = "String", paramType = "path")
    @GetMapping("/queryMenuByPId/{id}")
    public CommonResult queryMenuByPId(@PathVariable String id, @RequestParam String roleId) throws GlobalException {


        List<Menu> menus = menuService.list(new QueryWrapper<Menu>()
                .eq("parent_id", id)
                .orderByAsc("menu_order")
        );

        // 非系统管理员，要根据租户分配的菜单进行一次过滤
        if (!SecurityUtils.getCurrentUser().isSysAdmin()) {
            List<Menu> tenantMenus = tenantMenuService.queryTenantMenuByPId(SecurityUtils.getCurrentUser().getTenantId(), id);

            menus = menus.stream().filter(a -> {
                for (Menu tenantMenu : tenantMenus) {
                    if (tenantMenu.getId().equals(a.getId()) && tenantMenu.isChecked()) {
                        return true;
                    }
                }
                return false;
            }).collect(Collectors.toList());

        }

        List<Menu> result = new ArrayList<>();

        if (!StringUtils.isNullOrEmpty(roleId) && menus != null && menus.size() > 0) {
            List<String> menuIds = menus.stream().map(Menu::getId).collect(Collectors.toList());
            QueryWrapper<RoleMenu> wrapper = new QueryWrapper<>();
            wrapper.eq("role_id", roleId);
            wrapper.in("menu_id", menuIds);
            List<RoleMenu> roles = roleMenuService.list(wrapper);
            for (Menu menu : menus) {
                menu.setCheckedButton(new HashMap<>());
                for (RoleMenu rm : roles) {
                    if (rm.getMenuId().equals(menu.getId())) {
                        Map<String, Boolean> map = new HashMap<>();
                        if (!StringUtils.isNullOrEmpty(rm.getPermission())) {
                            String[] btns = rm.getPermission().split(",");
                            for (String str : btns) {
                                map.put(str, true);
                            }
                        }

                        menu.setChecked(true);
                        menu.setCheckedButton(map);
                    }
                }
                result.add(menu);
            }
        }

        return CommonResult.success(menus);
    }

    /**
     * 根据所有菜单
     */
    @ApiOperation(value = "查询所有菜单", notes = "查询所有菜单")
    @ApiImplicitParam()
    @GetMapping("/queryAllMenus")
    public CommonResult queryAllMenus() throws GlobalException {

        List<Menu> menus = menuService.list(new QueryWrapper<Menu>()
                .orderByAsc("menu_order")
        );
        return CommonResult.success(menus);

    }

    /**
     * 根据角色所有菜单
     */
    @ApiOperation(value = "根据角色获取菜单ID", notes = "根据角色获取菜单ID")
    @ApiImplicitParam(name = "roleId", value = "角色id", required = true, dataType = "String", paramType = "path")
    @GetMapping("/tree/{roleId}")
    public CommonResult getRoleMenu(@PathVariable String roleId) throws GlobalException {
        List<Menu> menus = menuService.findMenuByRoleId(roleId, SecurityUtils.getCurrentUser().getTenantId());
        return CommonResult.success(menus.stream().map(Menu::getId).collect(Collectors.toList()));
    }

    /**
     * 获取当前用户所有菜单
     */
    @ApiOperation(value = "获取当前用户菜单", notes = "获取当前用户菜单")
    @ApiImplicitParam(name = "parentId", value = "父菜单id", dataType = "String", paramType = "query")
    @GetMapping
    public CommonResult getUserMenu(String parentId, String branchCode) throws GlobalException {

        Set<Menu> all = new HashSet<>();

        //系统管理员 返回所有菜单
        if (SecurityUtils.getCurrentUser().isSysAdmin()) {
            List<Menu> menus = menuService.list(new QueryWrapper<Menu>()
                    .orderByAsc("menu_order")
                    .ne("menu_type",2));
            all.addAll(menus);
            return CommonResult.success(menuService.filterMenu(all, parentId));
        }

        //TODO 租户管理员及以下需要增加分配给这个租户里面查询
        //TODO 两个分厂的公共菜单 只关联一个租户下的相同角色即可（角色关联到根组织机构），不需要配置两遍
        /**
         *  目前的实现  分厂A roleA 关联 1 2 3    分厂B roleB 关联  2 3 4  =》 用户关联 roleA  roleB
         *  还有一种实现方式  分厂A roleA 关联 1  分厂B roleB 关联  4  租户级roleC  关联 2 3  =》 用户关联 roleA  roleB  roleC
         */
        SecurityUtils.getRoles().forEach(roleId -> {

            //判断该角色是否是该组织机构的，如果是，才把相关菜单加入
            if (!StringUtils.isNullOrEmpty(branchCode) && !isTenantAdmin()) {
                Role role = roleService.get(roleId);
                if (branchCode.equals(role.getOrgId())) {
                    all.addAll(menuService.findMenuByRoleId(roleId, SecurityUtils.getCurrentUser().getTenantId()));
                }
            } else {
                all.addAll(menuService.findMenuByRoleId(roleId, SecurityUtils.getCurrentUser().getTenantId()));
            }
        });
        return CommonResult.success(menuService.filterMenu(all, parentId));
    }


    private boolean isTenantAdmin() {
        AtomicBoolean isAdmin = new AtomicBoolean(false);
        SecurityUtils.getRoles().forEach(roleId -> {
            if (!isAdmin.get() && roleService.isTenantAdminRole(roleId)) {
                isAdmin.set(true);
            }
        });
        return isAdmin.get();
    }
}

