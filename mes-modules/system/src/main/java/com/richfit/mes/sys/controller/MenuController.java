package com.richfit.mes.sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.api.R;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.sys.Menu;
import com.richfit.mes.common.model.sys.RoleMenu;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.sys.service.MenuService;
import com.richfit.mes.sys.service.RoleMenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
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

    //TODO 权限控制  条件约束验证   userToken补充
    /**
     * 新增菜单
     */
    @ApiOperation(value = "新增菜单信息", notes = "新增菜单信息")
    @ApiImplicitParam(name = "menu", value = "菜单", required = true, dataType = "Menu", paramType = "body")
    @PostMapping("/save")
    public CommonResult<Boolean> saveMenu(@RequestBody Menu menu) throws GlobalException{
        return CommonResult.success(menuService.save(menu));
    }

    /**
     * 根据ID获取菜单
     *
     */
    @ApiOperation(value = "获取菜单信息", notes = "根据菜单id获取菜单详细信息")
    @ApiImplicitParam(name = "id", value = "菜单ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/{id}")
    public CommonResult<Menu> getMenu(@PathVariable String id) throws GlobalException {
        return CommonResult.success(menuService.getById(id));
    }

    /**
     * 更新菜单
     *
     */
    @ApiOperation(value = "修改菜单信息", notes = "修改菜单信息")
    @ApiImplicitParam(name = "menu", value = "菜单", required = true, dataType = "Menu", paramType = "body")
    @PutMapping("/update")
    public CommonResult<Boolean> updateMenu(@RequestBody Menu menu) throws GlobalException{

        return CommonResult.success(menuService.updateById(menu));
    }

    /**
     * 删除菜单
     */
    @ApiOperation(value = "删除菜单信息", notes = "根据菜单id删除记录")
    @ApiImplicitParam(name = "id", value = "菜单id", required = true, dataType = "String", paramType = "path")
    @DeleteMapping("/delete/{id}")
    public CommonResult<Boolean> delMenuById(@PathVariable String id) throws GlobalException{
        //TODO 菜单已分配 不能删除
        return CommonResult.success(menuService.removeById(id));
    }

    /**
     * 根据父菜单Id获取子菜单
     */
    @ApiOperation(value = "根据父菜单Id获取子菜单", notes = "根据父菜单Id获取子菜单")
    @ApiImplicitParam(name = "id", value = "菜单id", required = true, dataType = "String", paramType = "path")
    @GetMapping("/queryMenuByPId/{id}")
    public CommonResult queryMenuByPId(@PathVariable String id, @RequestParam String roleId) throws GlobalException{

        List<Menu> menus =  menuService.list( new QueryWrapper<Menu>()
                .eq("parent_id",id)
                .orderByAsc("menu_order")
        );

        List<Menu> result = new ArrayList<>();

        if (!StringUtils.isNullOrEmpty(roleId) && menus != null && menus.size() > 0) {
            List<String> menuIds = menus.stream().map(Menu::getId).collect(Collectors.toList());
            QueryWrapper<RoleMenu> wrapper = new QueryWrapper<>();
            wrapper.eq("role_id", roleId);
            wrapper.in("menu_id", menuIds);
            List<RoleMenu> roles = roleMenuService.list(wrapper);
            for (Menu menu : menus) {
                menu.setCheckedButton(new HashMap<>());
                for (RoleMenu rm: roles) {
                    if (rm.getMenuId().equals(menu.getId())) {
                        Map<String, Boolean> map = new HashMap<>();
                        if (!StringUtils.isNullOrEmpty(rm.getPermission())) {
                            String[] btns = rm.getPermission().split(",");
                            for (String str: btns) {
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
    public CommonResult queryAllMenus() throws GlobalException{

        List<Menu> menus =  menuService.list( new QueryWrapper<Menu>()
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
    public CommonResult getRoleMenu(@PathVariable String roleId)throws GlobalException{
        List<Menu> menus =  menuService.findMenuByRoleId(roleId);
        return CommonResult.success(menus.stream().map(Menu::getId).collect(Collectors.toList()));
    }

    /**
     * 获取当前用户所有菜单
     */
    @ApiOperation(value = "获取当前用户菜单", notes = "获取当前用户菜单")
    @ApiImplicitParam(name = "parentId", value = "父菜单id", dataType = "String", paramType = "query")
    @GetMapping
    public CommonResult getUserMenu(String parentId)throws GlobalException{

        Set<Menu> all = new HashSet<>();
        SecurityUtils.getRoles().forEach(roleId -> all.addAll(menuService.findMenuByRoleId(roleId)));
        return CommonResult.success(menuService.filterMenu(all, parentId));
    }
}

