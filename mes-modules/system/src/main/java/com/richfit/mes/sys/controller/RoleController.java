package com.richfit.mes.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.log.annotation.SysLog;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.model.sys.RoleMenu;
import com.richfit.mes.common.security.annotation.Inner;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.sys.entity.param.RoleQueryPageParam;
import com.richfit.mes.sys.entity.param.RoleQueryParam;
import com.richfit.mes.sys.service.RoleMenuService;
import com.richfit.mes.sys.service.RoleService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author sun
 * @Description 角色Controller
 */
@RestController
@RequestMapping("/api/sys/role")
@Api(value = "角色管理接口", tags = {"角色接口"})
@Slf4j
public class RoleController extends BaseController {


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
    @SysLog(type = "SYS_ROLE", title = "添加角色")
    @PostMapping
    public CommonResult saveRole(@Valid @RequestBody Role role) {
        //TODO 统一处理tenantId
        //如果是系统管理员，那其不能创建新角色
        if (SecurityUtils.getCurrentUser().isSysAdmin()) {
            return CommonResult.forbidden("超级管理员不能创建角色，请使用租户管理员操作");
        }
        //编码名称重复
        QueryWrapper<Role> codeWrapper = new QueryWrapper<>();
        codeWrapper.eq("role_code", role.getRoleCode())
                .eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        List<Role> codes = roleService.list(codeWrapper);
        //编码重复
        QueryWrapper<Role> nameWrapper = new QueryWrapper<>();
        nameWrapper.eq("role_name", role.getRoleName())
                .eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        List<Role> names = roleService.list(nameWrapper);
        if (names.size() > 0) {
            throw new GlobalException("角色名称重复，请检查", ResultCode.FAILED);
        }
        if (codes.size() > 0) {
            throw new GlobalException("角色编码重复，请检查", ResultCode.FAILED);
        }
        role.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        return CommonResult.success(roleService.add(role));
    }

    @ApiOperation(value = "修改角色", notes = "修改角色")
    @ApiImplicitParam(name = "role", value = "角色", required = true, dataType = "Role", paramType = "body")
    @SysLog(type = "SYS_ROLE", title = "修改角色")
    @PutMapping
    public CommonResult updateRole(@Valid @RequestBody Role role) {
        //如果是系统管理员，那其不能创建新角色
        if (SecurityUtils.getCurrentUser().isSysAdmin()) {
            return CommonResult.forbidden("超级管理员不能更新角色，请使用租户管理员操作");
        }

        //非系统管理员 不能修改租户管理员角色
        if (!SecurityUtils.getCurrentUser().isSysAdmin() && TENANT_ROLE_CODE.equals(role.getRoleCode())) {
            return CommonResult.forbidden("租户管理员角色为系统默认,请勿修改");
        }
        //编码名称重复
        QueryWrapper<Role> codeWrapper = new QueryWrapper<>();
        codeWrapper.eq("role_code", role.getRoleCode())
                .eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId())
                .ne("id", role.getId());
        List<Role> codes = roleService.list(codeWrapper);
        //编码重复
        QueryWrapper<Role> nameWrapper = new QueryWrapper<>();
        nameWrapper.eq("role_name", role.getRoleName())
                .eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId())
                .ne("id", role.getId());
        List<Role> names = roleService.list(nameWrapper);
        if (names.size() > 0) {
            throw new GlobalException("角色名称重复，请检查", ResultCode.FAILED);
        }
        if (codes.size() > 0) {
            throw new GlobalException("角色编码重复，请检查", ResultCode.FAILED);
        }
        return CommonResult.success(roleService.update(role));
    }

    @ApiOperation(value = "删除角色", notes = "根据url的id来指定删除角色")
    @ApiImplicitParam(paramType = "path", name = "id", value = "角色ID", required = true, dataType = "String")
    @SysLog(type = "SYS_ROLE", title = "删除角色")
    @DeleteMapping(value = "/{id}")
    public CommonResult deleteRole(@PathVariable String id) {

        //如果是系统管理员，那其不能创建新角色
        if (SecurityUtils.getCurrentUser().isSysAdmin()) {
            return CommonResult.forbidden("超级管理员不能删除角色，请使用租户管理员登录");
        }
        //非系统管理员 不能修改租户管理员角色
        Role role = roleService.get(id);
        if (!SecurityUtils.getCurrentUser().isSysAdmin() && TENANT_ROLE_CODE.equals(role.getRoleCode())) {
            return CommonResult.forbidden("租户管理员角色为系统默认,请勿删除");
        }

        return CommonResult.success(roleService.delete(id));
    }

    @ApiOperation(value = "分页查询角色", notes = "分页查询角色")
    @GetMapping("/page")
    public CommonResult queryRole(@Valid RoleQueryPageParam roleQueryPageParam) {
        //如果是系统管理员，那其不能查询角色
        if (SecurityUtils.getCurrentUser().isSysAdmin()) {
            return CommonResult.forbidden("超级管理员不能查询角色，请使用租户管理员登录");
        }
        RoleQueryParam roleQueryParam = roleQueryPageParam.toParam(RoleQueryParam.class);
        roleQueryParam.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        return CommonResult.success(roleService.query(roleQueryPageParam.toPage(), roleQueryParam));
    }

    @ApiOperation(value = "查询角色", notes = "查询角色")
    @GetMapping("/list")
    public CommonResult queryRoleList(@Valid RoleQueryPageParam roleQueryPageParam) {
        //如果是系统管理员，那其不能查询角色
        if (SecurityUtils.getCurrentUser().isSysAdmin()) {
            return CommonResult.success(null);
        }
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        return CommonResult.success(roleService.list(queryWrapper));
    }

    @ApiOperation(value = "查询用户已分配角色", notes = "")
    @GetMapping(value = "/user/{userId}")
    @Inner
    public CommonResult query(@PathVariable String userId) {
        log.debug("query with userId:{}", userId);
        return CommonResult.success(roleService.query(userId));
    }

    @ApiOperation(value = "更新角色菜单", notes = "更新角色菜单")
    @SysLog(type = "SYS_ROLE", title = "更新角色菜单")
    @PutMapping("/menu")
    public CommonResult saveRoleMenus(@RequestBody List<RoleMenu> menus) {
        return CommonResult.success(roleMenuService.saveRoleMenus(menus));
    }

    @ApiOperation(value = "查询用户角色列表", notes = "查询用户角色列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "userId", value = "用户ID", dataType = "string")
    })
    @GetMapping(value = "/queryRolesByUserId/{userId}")
    public List<Role> queryRolesByUserId(@PathVariable String userId) {
        return roleService.queryRolesByUserId(userId);
    }

}
