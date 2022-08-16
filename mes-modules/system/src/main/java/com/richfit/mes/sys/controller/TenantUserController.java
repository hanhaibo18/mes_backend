package com.richfit.mes.sys.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.constant.CommonConstant;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.model.sys.TenantUser;
import com.richfit.mes.common.model.sys.dto.TenantUserDto;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.security.annotation.Inner;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.sys.entity.dto.TenantUpdateUserDto;
import com.richfit.mes.sys.entity.param.TenantUserQueryParam;
import com.richfit.mes.sys.service.RoleService;
import com.richfit.mes.sys.service.TenantService;
import com.richfit.mes.sys.service.TenantUserService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

/**
 * <p>
 * 租户用户 前端控制器
 * </p>
 *
 * @author gaoliang
 * @since 2020-05-25
 */
@Slf4j
@Api(value = "租户信息管理", tags = {"租户信息管理"})
@RestController
@RequestMapping("/api/sys/user")
public class TenantUserController extends BaseController {

    @Autowired
    TenantUserService tenantUserService;

    @Autowired
    TenantService tenantService;

    @Autowired
    RoleService roleService;

    /**
     * 新增用户
     */
    @ApiOperation(value = "新增用户信息", notes = "新增用户信息")
    @ApiImplicitParam(name = "tenantUserDto", value = "租户用户", required = true, dataType = "TenantUserDto")
    @PostMapping("/save")
    public CommonResult<Boolean> saveTenantUser(@Valid @RequestBody TenantUserDto tenantUserDto) throws GlobalException {

        //TODO 租户可创建用户数限制
        TenantUser tenantUser = tenantUserDto.toPo(TenantUser.class);
        log.debug("save tenantUser:[{}]", tenantUser);
        return CommonResult.success(tenantUserService.add(tenantUser));
    }


    @ApiOperation(value = "新增管理员信息", notes = "新增管理员信息")
    @ApiImplicitParam(name = "tenantUserDto", value = "租户用户", required = true, dataType = "TenantUserDto")
    @PostMapping("/save-admin")
    public CommonResult<Boolean> saveTenantAdmin(@Valid @RequestBody TenantUserDto tenantUserDto) throws GlobalException {

        TenantUser tenantUser = tenantUserDto.toPo(TenantUser.class);

        //查询该租户下的 role_tenant_admin 角色
        Role role = roleService.getAdminRole(tenantUser.getTenantId());
        tenantUser.setRoleIds(new HashSet<>(Arrays.asList(role.getId())));

        log.debug("save tenantUser:[{}]", tenantUser);
        return CommonResult.success(tenantUserService.add(tenantUser));
    }

    /**
     * 根据ID获取用户
     */
    @ApiOperation(value = "获取用户信息", notes = "根据用户id获取用户详细信息")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/{id}")
    public CommonResult<TenantUserVo> getUser(@PathVariable String id) throws GlobalException {
        return CommonResult.success(tenantUserService.get(id));
    }

    @ApiOperation(value = "获取用户信息", notes = "根据用户id获取用户详细信息")

    @GetMapping("/find_one")
    public CommonResult<TenantUserVo> findOne(@RequestParam(value = "id", required = true) String id) throws GlobalException {
        return CommonResult.success(tenantUserService.findById(id));
    }

    /**
     * 更新用户
     */
    @ApiOperation(value = "修改用户信息", notes = "修改用户信息")
    @ApiImplicitParam(name = "tenantUpdateUserDto", value = "租户用户", required = true, dataType = "TenantUpdateUserDto")
    @PutMapping("/update")
    public CommonResult<Boolean> updateUser(@Valid @RequestBody TenantUpdateUserDto tenantUpdateUserDto) throws GlobalException {
        TenantUser tenantUser = tenantUpdateUserDto.toPo(TenantUser.class);
        return CommonResult.success(tenantUserService.update(tenantUser));
    }

    /**
     * 删除用户
     */
    @ApiOperation(value = "删除用户信息", notes = "根据用户id删除用户记录")
    @ApiImplicitParam(name = "id", value = "用户ID", required = true, dataType = "String", paramType = "path")
    @DeleteMapping("/delete/{id}")
    public CommonResult<Boolean> delUserById(@PathVariable String id) throws GlobalException {
        return CommonResult.success(tenantUserService.delete(id));
    }

    /**
     * 分页查询用户
     */
    @ApiOperation(value = "查询用户信息", notes = "根据查询条件返回用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = CommonConstant.PAGE_SIZE, value = "每页条数", defaultValue = CommonConstant.PAGE_SIZE_DEFAULT, required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = CommonConstant.PAGE_NUM, value = "页码", defaultValue = CommonConstant.PAGE_NUM_DEFAULT, required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "userAccount", value = "用户账号", required = false, paramType = "query"),
            @ApiImplicitParam(name = "emplName", value = "人员姓名", required = false, paramType = "query"),
            @ApiImplicitParam(name = "orgId", value = "组织机构", required = false, paramType = "query")
    })
    @GetMapping("/query/page")
    public CommonResult queryByCondition(@RequestParam(value = "orgId", required = false) String orgId,
                                         @RequestParam(value = "userAccount", required = false) String userAccount,
                                         @RequestParam(value = "emplName", required = false) String emplName,
                                         @RequestParam(value = CommonConstant.PAGE_SIZE, defaultValue = CommonConstant.PAGE_SIZE_DEFAULT) int limit,
                                         @RequestParam(value = CommonConstant.PAGE_NUM, defaultValue = CommonConstant.PAGE_NUM_DEFAULT) int page) throws GlobalException {
        TenantUserQueryParam userQueryForm = new TenantUserQueryParam();
        userQueryForm.setUserAccount(userAccount);
        userQueryForm.setEmplName(emplName);
        userQueryForm.setOrgId(orgId);
        userQueryForm.setTenantId(SecurityUtils.getCurrentUser().getTenantId());

        IPage<TenantUserVo> users = tenantUserService.query(new Page<TenantUser>(page, limit), userQueryForm);
        return CommonResult.success(users);
    }

    @ApiOperation(value = "查询租户管理员信息", notes = "根据查询条件返回租户管理员信息")
    @GetMapping("/query/page/admin")
    public CommonResult queryAdminByCondition(@RequestParam(value = "tenantId", required = false) String tenantId,
                                              @RequestParam(value = "userAccount", required = false) String userAccount,
                                              @RequestParam(value = "emplName", required = false) String emplName,
                                              @RequestParam(value = CommonConstant.PAGE_SIZE, defaultValue = CommonConstant.PAGE_SIZE_DEFAULT) int limit,
                                              @RequestParam(value = CommonConstant.PAGE_NUM, defaultValue = CommonConstant.PAGE_NUM_DEFAULT) int page) throws GlobalException {

        if (!SecurityUtils.getCurrentUser().isSysAdmin()) {
            return CommonResult.forbidden("无权访问该接口");
        }

        TenantUserQueryParam userQueryForm = new TenantUserQueryParam();
        userQueryForm.setUserAccount(userAccount);
        userQueryForm.setEmplName(emplName);
        userQueryForm.setTenantId(tenantId);

        IPage<TenantUserVo> users = tenantUserService.queryAdmin(new Page<TenantUser>(page, limit), userQueryForm);
        return CommonResult.success(users);
    }

    @ApiOperation(value = "查询用户信息", notes = "返回名称相同的用户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = CommonConstant.PAGE_SIZE, value = "每页条数", defaultValue = CommonConstant.PAGE_SIZE_DEFAULT, required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = CommonConstant.PAGE_NUM, value = "页码", defaultValue = CommonConstant.PAGE_NUM_DEFAULT, required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "userAccount", value = "用户账号", required = false, paramType = "query")
    })
    @GetMapping("/query/userAccount")
    public CommonResult queryByName(@RequestParam(value = "userAccount", required = true) String userAccount,
                                    @RequestParam(value = CommonConstant.PAGE_SIZE, defaultValue = CommonConstant.PAGE_SIZE_DEFAULT) int limit,
                                    @RequestParam(value = CommonConstant.PAGE_NUM, defaultValue = CommonConstant.PAGE_NUM_DEFAULT) int page) throws GlobalException {

        IPage<TenantUserVo> users = tenantUserService.queryByName(new Page<TenantUser>(page, limit), userAccount, SecurityUtils.getCurrentUser().getTenantId());
        return CommonResult.success(users);
    }

    /**
     * 停用/开启用户
     */
    @ApiOperation(value = "停用/开启用户", notes = "停用/开启用户")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "flag", value = "动作值(false/true)", required = true, dataType = "Boolean", paramType = "path"),
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "path")
    })
    @GetMapping("/status/{userId}/{flag}")
    public CommonResult<Boolean> setStatus(@PathVariable String userId, @PathVariable Boolean flag) throws GlobalException {

        TenantUser user = new TenantUser();
        user.setId(userId);
        user.setStatus(flag);
        boolean opResult = tenantUserService.update(user);
        return CommonResult.success(opResult);
    }

    /**
     * 重置用户密码
     **/
    @ApiOperation(value = "重置用户密码", notes = "重置用户密码")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "oldPassword", value = "原密码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "newPassword", value = "新密码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "userId", value = "用户Id", required = true, dataType = "String", paramType = "query")
    })
    @PostMapping("/resetpass")
    public CommonResult<Boolean> resetPass(@RequestParam(value = "oldPassword", required = true) String oldPassword,
                                           @RequestParam(value = "newPassword", required = true) String newPassword,
                                           @RequestParam(value = "userId", required = true) String userId) throws GlobalException {

        return CommonResult.success(tenantUserService.updatePassword(userId, oldPassword, newPassword));
    }

    @ApiOperation(value = "获取用户", notes = "根据用户唯一标识（username or mobile）获取用户信息")
    @ApiImplicitParam(paramType = "query", name = "uniqueId", value = "用户唯一标识", required = true, dataType = "string")
    @ApiResponses(@ApiResponse(code = 200, message = "处理成功", response = CommonResult.class))
    @GetMapping
    @Inner
    public CommonResult query(@RequestParam String uniqueId) throws GlobalException {
        log.debug("query with username {}", uniqueId);
        return CommonResult.success(tenantUserService.getByUniqueId(uniqueId));
    }

    @ApiOperation(value = "获取当前用户信息", notes = "获取当前用户信息")
    @ApiResponses(@ApiResponse(code = 200, message = "处理成功", response = CommonResult.class))
    @GetMapping("/current/profile")
    public CommonResult currentUser() throws GlobalException {
        return CommonResult.success(SecurityUtils.getCurrentUser());
    }

    @ApiOperation(value = "根据组织机构获取用户列表", notes = "根据组织机构获取用户列表")
    @ApiImplicitParam(name = "branchCode", value = "组织机构", required = true, dataType = "query")
    @GetMapping("/queryUserByBranchCode")
    public CommonResult<List<TenantUserVo>> queryUserByBranchCode(String branchCode) {
        return CommonResult.success(tenantUserService.queryUserByBranchCode(branchCode));
    }


    @ApiOperation(value = "根据用户编码获取人员信息", notes = "根据用户编码获取人员信息")
    @ApiImplicitParam(name = "userAccount", value = "用户编码", required = true, dataType = "query")
    @GetMapping("/queryByUserAccount")
    public CommonResult<TenantUserVo> queryByUserAccount(String userAccount) {
        return CommonResult.success(tenantUserService.queryByUserAccount(userAccount));
    }

    @ApiOperation(value = "根据组织机构获取质检人员", notes = "根据组织机构获取质检人员")
    @ApiImplicitParam(name = "branchCode", value = "组织机构", required = true, dataType = "query")
    @GetMapping("/queryByBranchCode")
    public List<TenantUserVo> queryByBranchCode(String branchCode) {
        return tenantUserService.queryByBranchCode(branchCode);
    }
}

