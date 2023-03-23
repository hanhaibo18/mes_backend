package com.richfit.mes.sys.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.constant.CommonConstant;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.model.sys.TenantUser;
import com.richfit.mes.common.model.sys.dto.TenantUserDto;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.security.annotation.Inner;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.sys.entity.dto.TenantUpdateUserDto;
import com.richfit.mes.sys.entity.param.TenantUserQueryParam;
import com.richfit.mes.sys.provider.BaseServiceClient;
import com.richfit.mes.sys.service.RoleService;
import com.richfit.mes.sys.service.TenantService;
import com.richfit.mes.sys.service.TenantUserService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.*;
import java.util.stream.Collectors;

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

    @Autowired
    BaseServiceClient baseServiceClient;

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

        IPage<TenantUserVo> users = tenantUserService.query(new Page<>(page, limit), userQueryForm);
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

        IPage<TenantUserVo> users = tenantUserService.queryAdmin(new Page<>(page, limit), userQueryForm);
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

    @ApiOperation(value = "根据组织机构list获取用户列表", notes = "根据组织机构list获取用户列表")
    @ApiImplicitParam(name = "branchCodeList", value = "组织机构", required = true, dataType = "query")
    @PostMapping("/queryUserByBranchCodes")
    public CommonResult<List<TenantUserVo>> queryUserByBranchCodes(@RequestBody List<String> branchCodeList) {
        return CommonResult.success(tenantUserService.queryUserByBranchCodeList(branchCodeList));
    }

    @ApiOperation(value = "根据组织机构获取用户列表List", notes = "根据组织机构获取用户列表List")
    @ApiImplicitParam(name = "branchCode", value = "组织机构", required = true, dataType = "query")
    @GetMapping("/queryUserByBranchCodeList")
    public CommonResult<List<TenantUserVo>> queryUserByBranchCodeList(String branchCode) {
        return CommonResult.success(tenantUserService.queryUserByBranchCodePage(branchCode));
    }

    @ApiOperation(value = "质检租户获取质控工程师", notes = "质检租户获取质控工程师")
    @ApiImplicitParam(name = "branchCode", value = "组织机构", required = true, dataType = "query")
    @GetMapping("/queryUserByTenantIdAndRole")
    public CommonResult<List<TenantUserVo>> queryUserByTenantIdAndRole(String tenantId) {
        return CommonResult.success(tenantUserService.queryUserByTenantIdAndRole(tenantId));
    }

    @ApiOperation(value = "分页查询根据组织机构获取用户列表", notes = "分页查询根据组织机构获取用户列表")
    @ApiImplicitParam(name = "branchCode", value = "组织机构", required = true, dataType = "query")
    @GetMapping("/queryUserByBranchCode/page")
    public CommonResult<Map<String, Object>> queryUserByBranchCodePage(String userAccount, String emplName, String branchCode, int page, int limit) {
        Map<String, Object> returnMap = new HashMap<>();
        List<TenantUserVo> tenantUserVos = tenantUserService.queryUserByBranchCodePage(branchCode);

        if (!StringUtils.isNullOrEmpty(userAccount)) {
            tenantUserVos = tenantUserVos.stream().filter(item -> item.getUserAccount().equals(userAccount)).collect(Collectors.toList());
        }
        if (!StringUtils.isNullOrEmpty(emplName)) {
            tenantUserVos = tenantUserVos.stream().filter(item -> item.getEmplName().equals(emplName)).collect(Collectors.toList());
        }

        tenantUserVos.sort((t1, t2) -> t2.getBelongOrgId().compareTo(t1.getBelongOrgId()));
        List<TenantUserVo> subList = tenantUserVos.stream().skip((page - 1) * limit).limit(limit).
                collect(Collectors.toList());

        List<Branch> branchList = baseServiceClient.selectBranchChildByCode("").getData();
        for (TenantUserVo user : subList) {
            for (Branch b : branchList) {
                if (b.getBranchCode().equals(user.getBelongOrgId())) {
                    user.setBelongOrgName(b.getBranchName());
                }
                if (b.getBranchCode().equals(user.getOrgId())) {
                    user.setOrgName(b.getBranchName());
                }
            }
        }
        //总页数
        int pages = tenantUserVos.size() % limit == 0 ? tenantUserVos.size() / limit : tenantUserVos.size() / limit + 1;
        //总数
        int total = tenantUserVos.size();
        returnMap.put("records", subList);
        returnMap.put("pages", pages);
        returnMap.put("total", total);
        return CommonResult.success(returnMap);
    }


    @ApiOperation(value = "根据用户编码获取人员信息", notes = "根据用户编码获取人员信息")
    @ApiImplicitParam(name = "userAccount", value = "用户编码", required = true, dataType = "query")
    @GetMapping("/queryByUserAccount")
    public CommonResult<TenantUserVo> queryByUserAccount(String userAccount) {
        return CommonResult.success(tenantUserService.queryByUserAccount(userAccount));
    }

    @ApiOperation(value = "根据用户编码List获取人员信息", notes = "根据用户编码List获取人员信息")
    @ApiImplicitParam(name = "userAccountList", value = "用户编码List", required = true, dataType = "List<String>")
    @PostMapping("/queryByUserAccountList")
    public Map<String, TenantUserVo> queryByUserAccountList(@RequestBody List<String> userAccountList) {
        return tenantUserService.queryByUserAccountList(userAccountList);
    }

    @ApiOperation(value = "根据用户编码List获取人员信息", notes = "根据用户编码List获取人员信息")
    @ApiImplicitParam(name = "userAccountList", value = "用户编码List", required = true, dataType = "List<String>")
    @PostMapping("/queryByUserAccountListInner")
    @Inner
    public Map<String, TenantUserVo> queryByUserAccountListInner(@RequestBody List<String> userAccountList) {
        return tenantUserService.queryByUserAccountList(userAccountList);
    }

    @ApiOperation(value = "根据组织机构获取质检人员", notes = "根据组织机构获取质检人员")
    @ApiImplicitParam(name = "branchCode", value = "组织机构", required = true, dataType = "query")
    @GetMapping("/queryByBranchCode")
    public CommonResult<List<TenantUserVo>> queryByBranchCode(String branchCode) {
        return CommonResult.success(tenantUserService.queryByBranchCode(branchCode));
    }

    @ApiOperation(value = "获取本公司质检员", notes = "获取本公司质检员")
    @GetMapping("/queryByTendId")
    public CommonResult<List<TenantUserVo>> queryByTendId() {
        return CommonResult.success(tenantUserService.queryByTendId());
    }

    @ApiOperation(value = "根据userId查询用户信息", notes = "根据用户Id获取人员信息")
    @ApiImplicitParam(name = "userId", value = "用户编码", required = true, dataType = "query")
    @GetMapping("/queryByUserId")
    public CommonResult<TenantUserVo> queryByUserId(String userId) {
        return CommonResult.success(tenantUserService.queryByUserId(userId));
    }

    @ApiOperation(value = "根据tenantId查询用户信息", notes = "根据租户id获取人员信息")
    @ApiImplicitParam(name = "tenantId", value = "租户id", required = true, dataType = "query")
    @GetMapping("/queryUserByTenantId")
    public List<TenantUserVo> queryUserByTenantId(String tenantId) {
        return tenantUserService.queryUserByTenantId(tenantId);
    }


    /**
     * 重置默认密码
     **/
    @ApiOperation(value = "重置默认密码", notes = "重置默认密码")
    @ApiImplicitParam(name = "userIds", value = "用户Id集合", paramType = "query", allowMultiple = true, dataType = "List<String>")
    @PostMapping("/defaultPassword")
    public CommonResult<Boolean> defaultPassword(@RequestBody List<String> userIds) throws GlobalException {
        return CommonResult.success(tenantUserService.defaultPassword(userIds));
    }

    @ApiOperation(value = "查询质量检测部质检人员", notes = "为空查询全部质检人员,1查询机加质检人员,2查询调度质检人员(不带本公司)")
    @ApiImplicitParam(name = "classes", value = "车间分类", paramType = "query", dataType = "String")
    @GetMapping("/queryQualityInspectionUser")
    public CommonResult<List<TenantUserVo>> queryQualityInspectionUser(String classes) {
        return CommonResult.success(tenantUserService.queryQualityInspectionUser(classes));
    }

    @ApiOperation(value = "查询质检人员", notes = "为空查询全部质检人员,1查询机加质检人员,2查询调度质检人员(带本公司)")
    @ApiImplicitParam(name = "classes", value = "车间分类", paramType = "query", dataType = "String")
    @GetMapping("/queryAllQualityUser")
    public CommonResult<List<TenantUserVo>> queryAllQualityUser(String classes) {
        return CommonResult.success(tenantUserService.queryAllQualityUser(classes));
    }

    @ApiOperation(value = "查询质检人员", notes = "为空查询全部质检人员")
    @ApiImplicitParam(name = "tenantId", value = "租户id", paramType = "query", dataType = "String")
    @GetMapping("/queryAllQualityUserByTenantId")
    public CommonResult<List<TenantUserVo>> queryAllQualityUserByTenantId(String classes, String tenantId) {
        return CommonResult.success(tenantUserService.queryAllQualityUserByTenantId(classes, tenantId));
    }

    @ApiOperation(value = "查询全部人员账户姓名", notes = "查询全部人员账户姓名")
    @GetMapping("/users_account")
    public CommonResult<Map<String, String>> usersAccount() {
        return CommonResult.success(tenantUserService.usersAccount());
    }

    @ApiOperation(value = "根据车间查询探伤记录审核人", notes = "根据车间查询探伤记录审核人")
    @GetMapping("/query_audit_poeples")
    public CommonResult<List<TenantUserVo>> queryAuditsList(@RequestParam String branchCode) {
        return CommonResult.success(tenantUserService.queryAuditsList(branchCode));
    }

}

