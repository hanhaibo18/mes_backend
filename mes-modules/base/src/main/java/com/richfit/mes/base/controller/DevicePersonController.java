package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.DevicePerson;
import com.richfit.mes.base.service.DevicePersonService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import java.util.Date;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.ApiImplicitParams;
import org.springframework.web.bind.annotation.*;

/**
 * @author 马峰
 * @Description 设备人员记录Controller
 */
@Slf4j
@Api("设备人员记录管理")
@RestController
@RequestMapping("/api/base/deviceperson")
public class DevicePersonController extends BaseController {

    @Autowired
    private DevicePersonService devicePersonService;

    /**
     * ***
     * 分页查询
     *
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "设备人员记录", notes = "设备人员记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name="limit",value="每页条数",required=true,paramType="query",dataType="int"),
            @ApiImplicitParam(name="page",value="页码",required=true,paramType="query",dataType="int"),
            @ApiImplicitParam(name="deviceId",value="设备ID",required=true,paramType="query",dataType="string"),
            @ApiImplicitParam(name="userId",value="人员ID",required=true,paramType="query",dataType="string")
    })
    @GetMapping("/page")
    public CommonResult<IPage<DevicePerson>> page(int page, int limit,String deviceId, String userId, String tenantId) {
        try {
             QueryWrapper<DevicePerson> queryWrapper = new QueryWrapper<DevicePerson>();
        if(!StringUtils.isNullOrEmpty(userId)){
            queryWrapper.eq("user_id", userId);
        }        
          if(!StringUtils.isNullOrEmpty(deviceId)){
            queryWrapper.eq("device_id", deviceId);
              queryWrapper.apply("(device_id = '"+deviceId+"' or code = '"+deviceId+"') ");
        }
           if(!StringUtils.isNullOrEmpty(tenantId)){
            queryWrapper.eq("tenant_id", tenantId);
        }
          
          queryWrapper.orderByAsc("order_no");
            IPage<DevicePerson> devices = devicePersonService.selectPage(new Page<DevicePerson>(page, limit),queryWrapper);
            return CommonResult.success(devices);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
    
    @ApiOperation(value = "新增设备人员记录", notes = "新增设备人员记录")
    @ApiImplicitParam(name = "device", value = "设备人员记录", required = true, dataType = "DevicePerson", paramType = "path")
    @PostMapping("/add")
    public CommonResult<DevicePerson[]> addDevicePerson(@RequestBody DevicePerson[] devicePersons){
        for(DevicePerson devicePerson:devicePersons)
        {
        if(StringUtils.isNullOrEmpty(devicePerson.getDeviceId())){
            return CommonResult.failed("编码不能为空！");
        } else {
            devicePerson.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            devicePerson.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
            devicePerson.setCreateTime(new Date());
            devicePerson.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            devicePerson.setModifyTime(new Date());
            boolean bool = devicePersonService.save(devicePerson);
            
        }
        }
        return CommonResult.success(devicePersons, "操作成功！");
    }

    @ApiOperation(value = "修改设备人员记录", notes = "修改设备人员记录")
    @ApiImplicitParam(name = "device", value = "设备人员记录", required = true, dataType = "DevicePerson", paramType = "path")
    @PostMapping("/update")
    public CommonResult<DevicePerson> updateDevicePerson( @RequestBody DevicePerson devicePerson){
        if(StringUtils.isNullOrEmpty(devicePerson.getDeviceId())){
            return CommonResult.failed("编码不能为空！");
        } else {
          
            devicePerson.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            devicePerson.setModifyTime(new Date());
            boolean bool = devicePersonService.updateById(devicePerson);
            if(bool){
                return CommonResult.success(devicePerson, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

    @ApiOperation(value = "查询设备人员记录", notes = "根据编码获得设备人员记录")
    @ApiImplicitParam(name = "deviceId", value = "设备ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/find")
    public CommonResult<List<DevicePerson>> find(String deviceId, String userId, String branchCode, String tenantId,String isDefault) {
        
             QueryWrapper<DevicePerson> queryWrapper = new QueryWrapper<DevicePerson>();
        if(!StringUtils.isNullOrEmpty(userId)){
            queryWrapper.eq("user_id", userId);
        }        
          if(!StringUtils.isNullOrEmpty(deviceId)){
            queryWrapper.eq("device_id", deviceId);
        }
          if(!StringUtils.isNullOrEmpty(branchCode)){
            queryWrapper.eq("branch_code", branchCode);
        } 
            if(!StringUtils.isNullOrEmpty(tenantId)){
            queryWrapper.eq("tenant_id", tenantId);
        } 
               if (!StringUtils.isNullOrEmpty(isDefault)) {
                queryWrapper.eq("is_default", Integer.parseInt(isDefault));
            }
         queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());  
          queryWrapper.orderByAsc("order_no");
        List<DevicePerson> result = devicePersonService.list(queryWrapper);
        return CommonResult.success(result, "操作成功！");
    }

    @ApiOperation(value = "删除设备人员记录", notes = "根据id删除设备人员记录")
    @ApiImplicitParam(name = "ids", value = "ID", required = true, dataType = "String[]", paramType = "path")
    @PostMapping("/delete")
    public CommonResult<DevicePerson> delete(@RequestBody String[] ids){
            
            boolean bool = devicePersonService.removeByIds(java.util.Arrays.asList(ids));
            if(bool){
                return CommonResult.success(null, "删除成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
       
    }

    
}
