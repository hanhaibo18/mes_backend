package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.base.service.BranchService;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * @author 王瑞
 * @Description 组织结构Controller
 */
@Slf4j
@Api("组织机构管理")
@RestController
@RequestMapping("/api/base/branch")
public class BranchController extends BaseController {

    @Autowired
    private BranchService branchService;

    public static String BRANCH_ID_NULL_MESSAGE = "机构ID不能为空！";
    public static String BRANCH_CODE_NULL_MESSAGE = "机构编码不能为空！";
    public static String BRANCH_SUCCESS_MESSAGE = "操作成功！";
    public static String BRANCH_FAILED_MESSAGE = "操作失败，请重试！";

    @ApiOperation(value = "新增组织机构", notes = "新增组织机构")
    @ApiImplicitParam(name = "branch", value = "组织机构", required = true, dataType = "Branch", paramType = "path")
    @PostMapping("/branch")
    public CommonResult<Branch> addBranch(@RequestBody Branch branch){
        if(StringUtils.isNullOrEmpty(branch.getBranchCode())){
            return CommonResult.failed(BRANCH_CODE_NULL_MESSAGE);
        } else {
            QueryWrapper<Branch> queryWrapper = new QueryWrapper<Branch>();
            queryWrapper.eq("branch_code", branch.getBranchCode());
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            Branch oldBranch = branchService.getOne(queryWrapper);
            if(oldBranch != null && !StringUtils.isNullOrEmpty(oldBranch.getId())){
                return CommonResult.failed("组织结构编号已存在！");
            } else {
                branch.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                branch.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                branch.setCreateTime(new Date());
                boolean bool = branchService.save(branch);
                if(bool){
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
    public CommonResult<Branch> updateBranch( @RequestBody Branch branch){
        if(StringUtils.isNullOrEmpty(branch.getBranchCode())){
            return CommonResult.failed(BRANCH_CODE_NULL_MESSAGE);
        } else {
            branch.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            branch.setModifyTime(new Date());
            boolean bool = branchService.updateById(branch);
            if(bool){
                return CommonResult.success(branch, BRANCH_SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(BRANCH_FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "查询组织机构详细信息", notes = "根据机构编码获得组织机构详细信息")
    @GetMapping("/branch")
    public CommonResult<Branch> selectBranchByCode(String branchCode, String id){
        if(!StringUtils.isNullOrEmpty(branchCode)){
            QueryWrapper<Branch> queryWrapper = new QueryWrapper<Branch>();
            queryWrapper.eq("branch_code", branchCode);

            if(!StringUtils.isNullOrEmpty(id)){
                queryWrapper.ne("id", id);
            }

            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            queryWrapper.orderByAsc("order_no");

            Branch result = branchService.getOne(queryWrapper);
            return CommonResult.success(result, BRANCH_SUCCESS_MESSAGE);
        }
        return CommonResult.failed(BRANCH_CODE_NULL_MESSAGE);
    }

    @ApiOperation(value = "查询组织机构", notes = "根据机构编码获得组织机构")
    @ApiImplicitParam(name = "branchCode", value = "机构编码", required = true, dataType = "String", paramType = "path")
    @GetMapping("/select_branches_by_code")
    public CommonResult<List<Branch>> selectBranchesByCode(String branchCode, String branchName){
        QueryWrapper<Branch> queryWrapper = new QueryWrapper<Branch>();
        if(!StringUtils.isNullOrEmpty(branchCode)){
            queryWrapper.eq("main_branch_code", branchCode);
        } else {
           // queryWrapper.isNull("main_branch_code");
        }
        if(!StringUtils.isNullOrEmpty(branchName)){
            queryWrapper.like("branch_name", "%" + branchName + "%");
        }
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.orderByAsc("order_no");
        List<Branch> result = branchService.list(queryWrapper);
        return CommonResult.success(result, BRANCH_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询组织机构", notes = "根据机构编码获得组织机构")
    @GetMapping("/select_branch_children_by_code")
    public CommonResult<List<Branch>> selectBranchChildByCode(String branchCode){
        QueryWrapper<Branch> queryWrapper = new QueryWrapper<Branch>();
        if(!StringUtils.isNullOrEmpty(branchCode)){
            queryWrapper.apply(branchCode != null,"FIND_IN_SET(branch_code, getBranchChildList('" +branchCode+ "'))");
        }
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.orderByAsc("order_no");
        List<Branch> result = branchService.list(queryWrapper);
        return CommonResult.success(result, BRANCH_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "删除组织机构", notes = "根据机构id删除组织机构")
    @ApiImplicitParam(name = "branchCode", value = "机构编码", required = true, dataType = "String", paramType = "path")
    @DeleteMapping("/branch/{id}")
    public CommonResult<Branch> deleteBranchById(@PathVariable String id){
        if(StringUtils.isNullOrEmpty(id)){
            return CommonResult.failed(BRANCH_ID_NULL_MESSAGE);
        } else {
            boolean bool = branchService.removeById(id);
            if(bool){
                return CommonResult.success(null, BRANCH_SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(BRANCH_FAILED_MESSAGE);
            }
        }
    }

}
