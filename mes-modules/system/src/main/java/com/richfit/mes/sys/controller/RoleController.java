package com.richfit.mes.sys.controller;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.log.annotation.SysLog;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.model.sys.RoleMenu;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.sys.entity.param.RoleQueryPageParam;
import com.richfit.mes.sys.entity.param.RoleQueryParam;
import com.richfit.mes.sys.service.RoleMenuService;
import com.richfit.mes.sys.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author sun
 * @Description 角色Controller
 */
@RestController
@RequestMapping("/api/sys/role")
@Api("role")
@Slf4j
public class RoleController {

    @Autowired
    private RoleService roleService;

    @Autowired
    private RoleMenuService roleMenuService;

    @ApiOperation(value = "获取角色", notes = "获取指定角色信息")
    @ApiImplicitParam(paramType = "path", name = "id", value = "角色ID", required = true, dataType = "String")
    @GetMapping(value = "/{id}")
    public CommonResult get(@PathVariable String id) {
        return CommonResult.success(roleService.get(id));
    }

    @ApiOperation(value = "新增角色", notes = "新增角色")
    @ApiImplicitParam(name = "role", value = "角色", required = true, dataType = "Role", paramType = "body")
    @SysLog(type = "SYS_ROLE",title = "添加角色")
    @PostMapping
    public CommonResult saveRole(@Valid @RequestBody Role role) {
        //TODO 统一处理tenantId
        role.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        return CommonResult.success(roleService.add(role));
    }

    @ApiOperation(value = "修改角色", notes = "修改角色")
    @ApiImplicitParam(name = "role", value = "角色", required = true, dataType = "Role", paramType = "body")
    @SysLog(type = "SYS_ROLE",title = "修改角色")
    @PutMapping
    public CommonResult updateRole(@Valid @RequestBody Role role) {
        return CommonResult.success(roleService.update(role));
    }

    @ApiOperation(value = "删除角色", notes = "根据url的id来指定删除角色")
    @ApiImplicitParam(paramType = "path", name = "id", value = "角色ID", required = true, dataType = "String")
    @SysLog(type = "SYS_ROLE",title = "删除角色")
    @DeleteMapping(value = "/{id}")
    public CommonResult deleteRole(@PathVariable String id) {
        return CommonResult.success(roleService.delete(id));
    }

    @ApiOperation(value = "分页查询角色", notes = "分页查询角色")
    @GetMapping("/page")
    public CommonResult queryRole(@Valid RoleQueryPageParam roleQueryPageParam) {
        //TODO 统一处理tenantId
        RoleQueryParam roleQueryParam = roleQueryPageParam.toParam(RoleQueryParam.class);
        roleQueryParam.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        return CommonResult.success(roleService.query(roleQueryPageParam.toPage(), roleQueryParam));
    }

    @ApiOperation(value = "查询用户已分配角色", notes = "")
    @GetMapping(value = "/user/{userId}")
    public CommonResult query(@PathVariable String userId) {
        log.debug("query with userId:{}", userId);
        return CommonResult.success(roleService.query(userId));
    }

    @ApiOperation(value = "更新角色菜单", notes = "更新角色菜单")
    @SysLog(type = "SYS_ROLE",title = "更新角色菜单")
    @PutMapping("/menu")
    public CommonResult saveRoleMenus(@RequestBody List<RoleMenu> menus) {
        return CommonResult.success(roleMenuService.saveRoleMenus(menus));
    }

}