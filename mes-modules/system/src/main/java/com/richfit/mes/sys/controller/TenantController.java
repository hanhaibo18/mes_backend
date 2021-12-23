package com.richfit.mes.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.log.annotation.SysLog;
import com.richfit.mes.common.model.sys.Tenant;
import com.richfit.mes.sys.service.TenantService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author sun
 * @Description 租户Controller
 */
@Slf4j
@Api("租户信息管理")
@RestController
@RequestMapping("/api/sys/tenant")
public class TenantController  extends BaseController {

    @Autowired
    private TenantService tenantService;

    //TODO 权限控制  条件约束验证   userToken补充
    /**
     * 新增租户
     */
    @ApiOperation(value = "新增租户信息", notes = "新增租户信息")
    @ApiImplicitParam(name = "tenant", value = "租户", required = true, dataType = "Tenant", paramType = "body")
    @SysLog(type = "SYS_TENANT",title = "新建租户")
    @PostMapping("/save")
    public CommonResult<Boolean> saveTenant(@Valid @RequestBody Tenant tenant)throws GlobalException{
        return CommonResult.success(tenantService.save(tenant));
    }

    /**
     * 根据ID获取租户
     *
     */
    @ApiOperation(value = "获取租户信息", notes = "根据租户id获取租户详细信息")
    @ApiImplicitParam(name = "id", value = "租户ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/{id}")
    public CommonResult<Tenant> tenant(@PathVariable String id) throws GlobalException {
        checkTenantId(id);
        return CommonResult.success(tenantService.getById(id));
    }

    /**
     * 更新tenant
     */
    @ApiOperation(value = "修改租户信息", notes = "修改租户信息")
    @ApiImplicitParam(name = "tenant", value = "租户", required = true, dataType = "Tenant", paramType = "body")
    @SysLog(type = "SYS_TENANT",title = "更新租户")
    @PutMapping("/update")
    public CommonResult<Boolean> updateTenant(@RequestBody Tenant tenant) throws GlobalException{
            if(tenant.getTenantStatus()!=null){
                tenant.setTenantStatus(null);
            }
            return CommonResult.success(tenantService.updateById(tenant));
    }

    /**
     * 删除tenant
     */
    @ApiOperation(value = "删除租户信息", notes = "根据租户id删除租户记录")
    @ApiImplicitParam(name = "id", value = "租户id", required = true, dataType = "String", paramType = "path")
    @SysLog(type = "SYS_TENANT",title = "删除租户")
    @DeleteMapping("/delete/{id}")
    public CommonResult<Boolean> delTenantById(@PathVariable String id) throws GlobalException{
            //TODO 租户下有用户时，不能删除
        checkTenantId(id);
        return CommonResult.success(tenantService.removeById(id));
    }

    /**
     * 分页查询tenant
     */
    @ApiOperation(value = "查询租户信息", notes = "根据查询条件返回租户信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="limit",value="每页条数",required=true,paramType="query",dataType="int"),
            @ApiImplicitParam(name="page",value="页码",required=true,paramType="query",dataType="int"),
            @ApiImplicitParam(name="tenantName",value="租户名称",required=false,paramType="query")
    })
    @GetMapping("/query/page")
    public CommonResult queryByCondition(@RequestParam(value = "tenantName",required = false) String tenantName,
                                         @RequestParam(value = "limit") int limit,
                                         @RequestParam(value = "page") int page) throws GlobalException{
        IPage<Tenant> tenants = tenantService.page(new Page<Tenant>(page, limit),
                new QueryWrapper<Tenant>()
                        .like(StringUtils.hasText(tenantName), "tenant_name", tenantName)
                        .orderByDesc("modify_time")
        );
        return CommonResult.success(tenants);
    }

    /**
     * 停用/开启整个租户
     */
    @ApiOperation(value = "停用/开启整个租户", notes = "停用/开启整个租户")
    @ApiImplicitParams({
            @ApiImplicitParam(name="flag",value="动作值(0/1)",required=true,dataType="int" ,paramType="path"),
            @ApiImplicitParam(name="tenantId",value="租户Id",required=true,dataType="String" ,paramType="path")
    })
    @SysLog(type = "SYS_TENANT",title = "停用/开启租户")
    @PutMapping ("/status/{tenantId}/{flag}")
    public CommonResult<Boolean> setStatus(@PathVariable String tenantId, @PathVariable int flag) throws GlobalException {

        checkTenantId(tenantId);
        Tenant tenant = new Tenant();
        tenant.setTenantStatus(flag);

        boolean opResult = tenantService.update(tenant,
                new UpdateWrapper<Tenant>()
                .eq("id",tenantId)
        );

        return CommonResult.success(opResult);
    }

    /**
     * 查询租户附加信息
     */
    @ApiOperation(value = "配置租户附加信息", notes = "停用/配置租户附加信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="tenantId",value="租户Id",required=true,dataType="String" ,paramType="path")
    })
    @GetMapping ("/addInfo/{tenantId}")
    public CommonResult  getAdditionalInfo(@PathVariable String tenantId){
        checkTenantId(tenantId);
        try {
            JsonNode jsonNode = tenantService.getAdditionalInfo(tenantId);
            return CommonResult.success(jsonNode);
        }catch (JsonProcessingException e ){
            return CommonResult.failed("数据非json格式，解析错误");
        }

    }

    /**
     * 保存租户附加信息
     */
    @ApiOperation(value = "配置租户附加信息", notes = "配置租户附加信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="addInfo",value="附加信息",required=true,dataType="JsonNode" ,paramType="query"),
            @ApiImplicitParam(name="tenantId",value="租户Id",required=true,dataType="String" ,paramType="path")
    })
    @SysLog(type = "SYS_TENANT",title = "配置租户附加信息")
    @PutMapping ("/addInfo/{tenantId}")
    public CommonResult saveAdditionalInfo(@RequestBody JsonNode addInfo,@PathVariable String tenantId){

        //TODO 非系统管理员禁止操作
        checkTenantId(tenantId);
        return tenantService.saveAdditionalInfo(addInfo,tenantId);
    }


    protected void checkTenantId(String tenantId){

        if(StringUtils.isEmpty(tenantId)){
            throw new GlobalException("租户Id不得为空", ResultCode.INVALID_ARGUMENTS);
        }

    }

}