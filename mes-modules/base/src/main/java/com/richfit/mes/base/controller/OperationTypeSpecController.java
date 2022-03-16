package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.base.service.OperationDeviceService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.OperationTypeSpec;
import com.richfit.mes.base.service.OperationTypeSpecService;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.mysql.cj.util.StringUtils;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.web.bind.annotation.*;

/**
 * @author 马峰
 * @Description 工艺类型与质量资料Controller
 */
@Slf4j
@Api("工艺类型与质量资料")
@RestController
@RequestMapping("/api/base/opttypespec")
public class OperationTypeSpecController extends BaseController {

    @Autowired
    private OperationTypeSpecService operatiponTypeSpecService;

  

    /**
     * ***
     * 分页查询
     *
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "工艺类型与质量资料", notes = "工艺类型与质量资料")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
        @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
        @ApiImplicitParam(name = "routerId", value = "工艺ID", required = true, paramType = "query", dataType = "string"),
        @ApiImplicitParam(name = "optCode", value = "工序字典编码", required = true, paramType = "query", dataType = "string"),
        @ApiImplicitParam(name = "optName", value = "工序字典名称", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/page")
    public CommonResult<IPage<OperationTypeSpec>> page(int page, int limit, String routerId, String optTypeCode, String optTypeName, String optType, String branchCode, String tenantId) {
        try {
            
            QueryWrapper<OperationTypeSpec> queryWrapper = new QueryWrapper<OperationTypeSpec>();
            
            if (!StringUtils.isNullOrEmpty(tenantId)) {
                queryWrapper.eq("tenant_id", tenantId);
            }
            if (!StringUtils.isNullOrEmpty(branchCode)) {
                queryWrapper.eq("branch_code", branchCode);
            }
            if (!StringUtils.isNullOrEmpty(optTypeCode)) {
                queryWrapper.eq("opt_type_code", optTypeCode);
            }
            if (!StringUtils.isNullOrEmpty(optTypeName)) {
                queryWrapper.like("opt_type_name", "%" + optTypeName + "%");
            }
             if (!StringUtils.isNullOrEmpty(optType)) {
               queryWrapper.eq("opt_type", Integer.parseInt(optType));
            }
            
            IPage<OperationTypeSpec> routers = operatiponTypeSpecService.page(new Page<OperationTypeSpec>(page, limit),queryWrapper);
            return CommonResult.success(routers);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "新增工艺类型与质量资料", notes = "新增工艺类型与质量资料")
    @ApiImplicitParam(name = "operatipon", value = "工序字典", required = true, dataType = "OperationTypeSpec", paramType = "path")
    @PostMapping("/add")
    public CommonResult<OperationTypeSpec> addOperationTypeSpec(@RequestBody OperationTypeSpec operatiponTypeSpec) {
        if (StringUtils.isNullOrEmpty(operatiponTypeSpec.getOptTypeCode())) {
            return CommonResult.failed("编码不能为空！");
        } else {
            boolean bool = operatiponTypeSpecService.save(operatiponTypeSpec);
            if (bool) {
                return CommonResult.success(operatiponTypeSpec, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

    @ApiOperation(value = "修改工序字典", notes = "修改工序字典")
    @ApiImplicitParam(name = "operatipon", value = "工序字典", required = true, dataType = "OperationTypeSpec", paramType = "path")
    @PostMapping("/update")
    public CommonResult<OperationTypeSpec> updateOperationTypeSpec(@RequestBody OperationTypeSpec operatiponTypeSpec) {
        if (StringUtils.isNullOrEmpty(operatiponTypeSpec.getOptTypeCode())) {
            return CommonResult.failed("机构编码不能为空！");
        } else {
            operatiponTypeSpec.setModifyBy("test");
            operatiponTypeSpec.setModifyTime(new Date());
            boolean bool = operatiponTypeSpecService.updateById(operatiponTypeSpec);
            if (bool) {
                return CommonResult.success(operatiponTypeSpec, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }
    
    
    @ApiOperation(value = "批量保存工艺类型与质量资料", notes = "批量保存工艺类型与质量资料")
    @ApiImplicitParam(name = "operatiponTypeSpec", value = "工艺类型与质量资料", required = true, dataType = "OperationTypeSpec", paramType = "path")
    @PostMapping("/batch/save")
    public CommonResult<List<OperationTypeSpec>> addOperationTypeSpec(@RequestBody List<OperationTypeSpec> operatiponTypeSpecs,String optTypeCode,String branchCode,String tenantId) {
        
        QueryWrapper<OperationTypeSpec> queryWrapper = new QueryWrapper<OperationTypeSpec>();
        queryWrapper.eq("tenant_id", tenantId);
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.eq("opt_type_code", optTypeCode);
        List<OperationTypeSpec> oldOperatiponTypeSpecs = operatiponTypeSpecService.list(queryWrapper);
        for(int ii=0;ii<operatiponTypeSpecs.size();ii++) {
        
            boolean isExist = false;
               for(int i=0;i<oldOperatiponTypeSpecs.size();i++) {
               if(oldOperatiponTypeSpecs.get(i).getId().equals(operatiponTypeSpecs.get(ii).getId())) {
                   isExist =true;
               }
            }
            if(!isExist) {
                if (!StringUtils.isNullOrEmpty(operatiponTypeSpecs.get(ii).getId())) { 
                    operatiponTypeSpecService.removeById(operatiponTypeSpecs.get(ii).getId());
                }
                else {
                     operatiponTypeSpecService.save(operatiponTypeSpecs.get(ii));
                }
            }
        }
         return CommonResult.success(operatiponTypeSpecService.list(queryWrapper));
        
        
    }

    @ApiOperation(value = "查询工序字典", notes = "根据编码获得工序字典")
    @ApiImplicitParam(name = "operatiponCode", value = "编码", required = true, dataType = "String", paramType = "path")
    @GetMapping("/find")
    public CommonResult<List<OperationTypeSpec>> find(String id,String optTypeCode, String optTypeName, String optType,String branchCode, String tenantId) {
        QueryWrapper<OperationTypeSpec> queryWrapper = new QueryWrapper<OperationTypeSpec>();
        if(!StringUtils.isNullOrEmpty(id)){
            queryWrapper.eq("id", id);
        }  
        if (!StringUtils.isNullOrEmpty(optTypeCode)) {
            queryWrapper.like("opt_type_code", "%" + optTypeCode + "%");
        }
        if(!StringUtils.isNullOrEmpty(optTypeName)){
            queryWrapper.like("opt_type_name", "%" + optTypeName + "%");
        }
                if(!StringUtils.isNullOrEmpty(optType)){
            queryWrapper.like("opt_type", "%" + optType + "%");
        }
           
        if (!StringUtils.isNullOrEmpty(tenantId)) {
                queryWrapper.eq("tenant_id", tenantId);
            }
            if (!StringUtils.isNullOrEmpty(branchCode)) {
                queryWrapper.eq("branch_code", branchCode);
            }
        List<OperationTypeSpec> result = operatiponTypeSpecService.list(queryWrapper);
        return CommonResult.success(result, "操作成功！");
    }

    

    @ApiOperation(value = "删除工序字典", notes = "根据id删除工序字典")
    @ApiImplicitParam(name = "id", value = "ids", required = true, dataType = "String", paramType = "path")
    @PostMapping("/delete")
    public CommonResult<OperationTypeSpec> deleteById(@RequestBody String[] ids){
            
            boolean bool = operatiponTypeSpecService.removeByIds(java.util.Arrays.asList(ids));
            if(bool){
                return CommonResult.success(null, "删除成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
       
    }
}
