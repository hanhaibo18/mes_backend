package com.richfit.mes.base.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.entity.TreeVo;
import com.richfit.mes.base.service.BranchService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 王瑞
 * @Description 组织结构Controller
 */
@Slf4j
@Api(value = "组织机构管理", tags = {"组织机构管理"})
@RestController
@RequestMapping("/api/base/branch")
public class BranchController extends BaseController {

    @Autowired
    private BranchService branchService;


    public static String BRANCH_ID_NULL_MESSAGE = "机构ID不能为空！";
    public static String BRANCH_CODE_NULL_MESSAGE = "机构编码不能为空！";
    public static String BRANCH_SUCCESS_MESSAGE = "操作成功！";
    public static String BRANCH_FAILED_MESSAGE = "操作失败，请重试！";

    @ApiOperation(value = "初始化租户组织机构", notes = "初始化租户组织机构")
    @GetMapping("/initBranch")
    public CommonResult<Branch> initBranch(String tenantId, String branchCode, String branchName) {
        if (StringUtils.isNullOrEmpty(branchCode)) {
            return CommonResult.failed(BRANCH_CODE_NULL_MESSAGE);
        } else {
            QueryWrapper<Branch> queryWrapper = new QueryWrapper<Branch>();
            queryWrapper.eq("branch_code", branchCode);
            queryWrapper.eq("tenant_id", tenantId);
            Branch oldBranch = branchService.getOne(queryWrapper);
            if (oldBranch != null && !StringUtils.isNullOrEmpty(oldBranch.getBranchCode())) {
                return CommonResult.success(oldBranch, "组织结构编号已存在！");
            } else {
                Branch branch = new Branch();
                branch.setBranchCode(branchCode);
                branch.setBranchName(branchName);
                branch.setTenantId(tenantId);
                branch.setBranchType("A");
                branch.setOrderNo(0);
                branch.setIsUse("1");
                boolean bool = branchService.save(branch);
                if (bool) {
                    return CommonResult.success(branch, BRANCH_SUCCESS_MESSAGE);
                } else {
                    return CommonResult.failed(BRANCH_FAILED_MESSAGE);
                }
            }
        }
    }

    @ApiOperation(value = "新增组织机构", notes = "新增组织机构")
    @ApiImplicitParam(name = "branch", value = "组织机构", required = true, dataType = "Branch", paramType = "path")
    @PostMapping("/branch")
    public CommonResult<Branch> addBranch(@RequestBody Branch branch) {
        if (StringUtils.isNullOrEmpty(branch.getBranchCode())) {
            return CommonResult.failed(BRANCH_CODE_NULL_MESSAGE);
        } else {
            QueryWrapper<Branch> queryWrapper = new QueryWrapper<Branch>();
            queryWrapper.eq("branch_code", branch.getBranchCode());
            if (StringUtils.isNullOrEmpty(branch.getTenantId())) {
                branch.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            }
            queryWrapper.eq("tenant_id", branch.getTenantId());
            Branch oldBranch = branchService.getOne(queryWrapper);
            if (oldBranch != null && !StringUtils.isNullOrEmpty(oldBranch.getBranchCode())) {
                return CommonResult.failed("组织结构编号已存在！");
            } else {
                branch.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                branch.setCreateTime(new Date());
                boolean bool = branchService.save(branch);
                if (bool) {
                    return CommonResult.success(branch, BRANCH_SUCCESS_MESSAGE);
                } else {
                    return CommonResult.failed(BRANCH_FAILED_MESSAGE);
                }
            }
        }
    }

    @ApiOperation(value = "修改组织机构", notes = "修改组织机构")
    @ApiImplicitParam(name = "branch", value = "组织机构", required = true, dataType = "Branch", paramType = "path")
    @PutMapping("/branch")
    public CommonResult<Branch> updateBranch(@RequestBody Branch branch) {
        if (StringUtils.isNullOrEmpty(branch.getBranchCode())) {
            return CommonResult.failed(BRANCH_CODE_NULL_MESSAGE);
        } else {
            branch.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            branch.setModifyTime(new Date());
            boolean bool = branchService.updateById(branch);
            if (bool) {
                return CommonResult.success(branch, BRANCH_SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(BRANCH_FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "查询组织机构详细信息", notes = "根据机构编码和租户ID获得组织机构详细信息")
    @GetMapping("/branch/one")
    public CommonResult<Branch> selectBranchByCodeAndTenantId(String branchCode, String tenantId) {
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            QueryWrapper<Branch> queryWrapper = new QueryWrapper<Branch>();
            queryWrapper.eq("branch_code", branchCode);

            if (!StringUtils.isNullOrEmpty(tenantId)) {
                queryWrapper.eq("tenant_id", tenantId);
            }
            queryWrapper.orderByAsc("order_no");

            Branch result = branchService.getOne(queryWrapper);
            return CommonResult.success(result, BRANCH_SUCCESS_MESSAGE);
        }
        return CommonResult.failed(BRANCH_CODE_NULL_MESSAGE);
    }

    @ApiOperation(value = "查询组织机构详细信息", notes = "根据机构编码获得组织机构详细信息")
    @GetMapping("/branch")
    public CommonResult<Branch> selectBranchByCode(String branchCode, String id) {
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            try {
                QueryWrapper<Branch> queryWrapper = new QueryWrapper<Branch>();
                queryWrapper.eq("branch_code", branchCode);

                if (!StringUtils.isNullOrEmpty(id)) {
                    queryWrapper.ne("id", id);
                }
                queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
                queryWrapper.orderByAsc("order_no");
                List<Branch> branchs = branchService.list(queryWrapper);
                return CommonResult.success(branchs.size() > 0 ? branchs.get(0) : null, BRANCH_SUCCESS_MESSAGE);
            } catch (Exception e) {
                e.printStackTrace();
                throw e;
            }
        }
        return CommonResult.failed(BRANCH_CODE_NULL_MESSAGE);
    }

    @ApiOperation(value = "查询组织机构", notes = "根据机构编码获得组织机构")
    @ApiImplicitParam(name = "branchCode", value = "机构编码", required = true, dataType = "String", paramType = "path")
    @GetMapping("/select_branches_by_code")
    public CommonResult<List<Branch>> selectBranchesByCode(String branchCode, String branchName, Boolean isFindTop) {
        QueryWrapper<Branch> queryWrapper = new QueryWrapper<Branch>();
        if (!StringUtils.isNullOrEmpty(branchName)) {
            queryWrapper.like("branch_name", branchName);
        }
        List<GrantedAuthority> authorities = new ArrayList<>(SecurityUtils.getCurrentUser().getAuthorities());
        boolean isSysAdmin = SecurityUtils.getCurrentUser().isSysAdmin();
        if (!isSysAdmin) {
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            if (isFindTop != null && isFindTop) {
                String belongOrgId = SecurityUtils.getCurrentUser().getBelongOrgId();
                queryWrapper.eq("branch_code", belongOrgId);
            } else {
                queryWrapper.eq("main_branch_code", branchCode);
            }
        } else {
            if (!StringUtils.isNullOrEmpty(branchCode)) {
                queryWrapper.eq("main_branch_code", branchCode);
            } else {
                queryWrapper.isNull("main_branch_code");
            }
        }
        queryWrapper.orderByAsc("order_no");
        List<Branch> result = branchService.list(queryWrapper);

        //非平台管理员才查询
        if (!isSysAdmin) {
            for (Branch b : result) {
                branchService.branchErpCode(b);
            }
        }
        return CommonResult.success(result, BRANCH_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询组织机构", notes = "根据机构编码获得组织机构")
    @GetMapping("/select_branch_children_by_code")
    public CommonResult<List<Branch>> selectBranchChildByCode(String branchCode) {
        QueryWrapper<Branch> queryWrapper = new QueryWrapper<Branch>();
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            //没有找到getBranchChildList方法，报错先注释掉
            //queryWrapper.apply(branchCode != null, "FIND_IN_SET(branch_code, getBranchChildList('" + branchCode + "'))");
            queryWrapper.apply(branchCode != null, "FIND_IN_SET(branch_code, '" + branchCode + "')");
        }
        // queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.orderByAsc("order_no");
        List<Branch> result = branchService.list(queryWrapper);
        return CommonResult.success(result, BRANCH_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "删除组织机构", notes = "根据机构id删除组织机构")
    @ApiImplicitParam(name = "branchCode", value = "机构编码", required = true, dataType = "String", paramType = "path")
    @DeleteMapping("/branch/{id}")
    public CommonResult<Branch> deleteBranchById(@PathVariable String id) {
        if (StringUtils.isNullOrEmpty(id)) {
            return CommonResult.failed(BRANCH_ID_NULL_MESSAGE);
        } else {
            boolean bool = branchService.removeById(id);
            if (bool) {
                return CommonResult.success(null, BRANCH_SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(BRANCH_FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "查询下级组织机构", notes = "查询下级组织机构")
    @ApiImplicitParam(name = "branchCode", value = "机构编码", dataType = "String", paramType = "query")
    @GetMapping("/queryCodeList")
    public CommonResult<List<Branch>> queryCode(String branchCode) {
        return CommonResult.success(branchService.queryCode(branchCode));
    }

    @ApiOperation(value = "查询组织机构+质检人员", notes = "查询组织机构+质检人员")
    @ApiImplicitParam(name = "branchCode", value = "机构编码", dataType = "String", paramType = "query")
    @GetMapping("/queryUserList")
    public CommonResult<List<TenantUserVo>> queryUserList(String branchCode) {
        return CommonResult.success(branchService.queryUserList(branchCode));
    }

    @ApiOperation(value = "查询质量检测部质检人员", notes = "查询质量检测部质检人员")
    @ApiImplicitParam(name = "userId", value = "人员id", dataType = "String", paramType = "query")
    @GetMapping("/queryUsers")
    public CommonResult<List<TenantUserVo>> queryUsers(String auditBy) {
        return CommonResult.success(branchService.queryUsers(auditBy));
    }

    @ApiOperation(value = "查询所有组织机构", notes = "查询所有组织机构")
    @GetMapping("/queryAllCode")
    public CommonResult<List<Branch>> queryAllCode() {
        return CommonResult.success(branchService.queryAllCode());
    }

    /**
     * @Author: zhiqiang.lu
     * @Date: 2022.8.24
     */
    @ApiOperation(value = "查询登录用的车间列表", notes = "查询登录用的车间列表")
    @GetMapping("/login_branch_list")
    public CommonResult<List<Branch>> loginBranchList() {
        List<Branch> branchList = new ArrayList<>();
        TenantUserDetails tenantUserDetails = SecurityUtils.getCurrentUser();
        QueryWrapper<Branch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("branch_type", "0");
        queryWrapper.eq("tenant_id", tenantUserDetails.getTenantId());
        queryWrapper.ne("main_branch_code", "");
        List<Branch> result = branchService.list(queryWrapper);
        //当前机构的代码等于顶级机构的代码时返回所有的工厂列表
        if (StrUtil.isBlank(tenantUserDetails.getOrgId()) || tenantUserDetails.getBelongOrgId().equals(tenantUserDetails.getOrgId())) {
            return CommonResult.success(result, BRANCH_SUCCESS_MESSAGE);
        } else {
            for (Branch branch : result) {
                if (tenantUserDetails.getBelongOrgId().replaceAll("_", "").startsWith(branch.getBranchCode().replaceAll("_", ""))) {
                    branchList.add(branch);
                }
            }
            return CommonResult.success(branchList, BRANCH_SUCCESS_MESSAGE);
        }
    }

    @ApiOperation(value = "查询质检人员(新)/树形结构")
    @ApiImplicitParam(name = "branchCodeList", value = "机构编码", dataType = "List<String>", paramType = "body")
    @PostMapping("/queryUserTreeList")
    public CommonResult<List<TreeVo>> queryUserTreeList(@RequestBody List<String> branchCodeList) {
        return CommonResult.success(branchService.queryUserTreeList(branchCodeList));
    }

    @ApiOperation(value = "多车间组成树形结构")
    @ApiImplicitParam(name = "branchCodeList", value = "机构编码", dataType = "List<String>", paramType = "body")
    @PostMapping("/queryBranchCodeList")
    public CommonResult<List<Branch>> queryBranchCodeList(@RequestBody List<String> branchCodeList) {
        return CommonResult.success(branchService.queryBranchCodeList(branchCodeList));
    }

    @ApiOperation(value = "查询tenantId", notes = "根据BranchCode查询租户Id")
    @GetMapping("/queryTenantIdByBranchCode")
    public String queryTenantIdByBranchCode(String branchCode) {
        QueryWrapper<Branch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("branch_code", branchCode);
        Branch branch = branchService.getOne(queryWrapper);
        return branch.getTenantId();
    }

}
