package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.Device;
import com.richfit.mes.base.service.DeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;

import java.util.*;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.ApiImplicitParams;
import java.io.File;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author 马峰
 * @Description 设备Controller
 */
@Slf4j
@Api("设备管理")
@RestController
@RequestMapping("/api/base/device")
public class DeviceController extends BaseController {

    @Autowired
    private DeviceService deviceService;

    /**
     * ***
     * 分页查询
     *
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "设备", notes = "设备")
    @ApiImplicitParams({
            @ApiImplicitParam(name="limit",value="每页条数",required=true,paramType="query",dataType="int"),
            @ApiImplicitParam(name="page",value="页码",required=true,paramType="query",dataType="int"),
            @ApiImplicitParam(name="code",value="编码",required=true,paramType="query",dataType="string"),
            @ApiImplicitParam(name="name",value="名称",required=true,paramType="query",dataType="string")
    })
    @GetMapping("/page")
    public CommonResult<IPage<Device>> page(int page, int limit,String code, String name, String parentId, String type, String branchCode, String tenantId) {
        try {
             QueryWrapper<Device> queryWrapper = new QueryWrapper<Device>();
        if(!StringUtils.isNullOrEmpty(parentId)){
            queryWrapper.eq("parent_id", parentId);
        }
        if(!StringUtils.isNullOrEmpty(code)){
            queryWrapper.eq("code", code);
        }
        if(!StringUtils.isNullOrEmpty(name)){
            queryWrapper.like("name", "%" + name + "%");
        }
         if(!StringUtils.isNullOrEmpty(type)){
            queryWrapper.like("type", "%" + type + "%");
        }
         if (!StringUtils.isNullOrEmpty(branchCode)) {
                queryWrapper.like("branch_code", "%" + branchCode + "%");
        }
        if (!StringUtils.isNullOrEmpty(tenantId)) {
                queryWrapper.like("tenant_id", "%" + tenantId + "%");
        }
         queryWrapper.orderByDesc(new String[] {"type","create_time"});
            IPage<Device> devices = deviceService.page(new Page<Device>(page, limit),queryWrapper);
            return CommonResult.success(devices);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
    
    @ApiOperation(value = "新增设备", notes = "新增设备")
    @ApiImplicitParam(name = "device", value = "设备", required = true, dataType = "Device", paramType = "path")
    @PostMapping("/add")
    public CommonResult<Device> addDevice(@RequestBody Device device){
        if(StringUtils.isNullOrEmpty(device.getName())){
            return CommonResult.failed("编码不能为空！");
        } else {
              if(null!=SecurityUtils.getCurrentUser()) {
            device.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
           
            device.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
           
              }
              device.setCreateTime(new Date());
               device.setModifyTime(new Date());
            boolean bool = deviceService.save(device);
            if(bool){
                return CommonResult.success(device, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

    @ApiOperation(value = "修改设备", notes = "修改设备")
    @ApiImplicitParam(name = "device", value = "设备", required = true, dataType = "Device", paramType = "path")
    @PostMapping("/update")
    public CommonResult<Device> updateDevice( @RequestBody Device device){
        if(StringUtils.isNullOrEmpty(device.getName())){
            return CommonResult.failed("编码不能为空！");
        } else {
              if(null!=SecurityUtils.getCurrentUser()) {           
            device.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
              }
            device.setModifyTime(new Date());
            boolean bool = deviceService.updateById(device);
            if(bool){
                return CommonResult.success(device, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

    @ApiOperation(value = "查询设备", notes = "根据编码获得设备")
    @ApiImplicitParam(name = "deviceCode", value = "编码", required = true, dataType = "String", paramType = "path")
    @GetMapping("/find")
    public CommonResult<List<Device>> find(String id,String code, String name, String parentId, String type, String branchCode, String tenantId){
        QueryWrapper<Device> queryWrapper = new QueryWrapper<Device>();
        if(!StringUtils.isNullOrEmpty(id)){
            queryWrapper.eq("id", id);
        }   
           if(!StringUtils.isNullOrEmpty(parentId)){
            queryWrapper.eq("parent_id", parentId);
        }   
           
        if(!StringUtils.isNullOrEmpty(code)){
            queryWrapper.eq("code", code);
        }
        if(!StringUtils.isNullOrEmpty(name)){
            queryWrapper.like("name", "%" + name + "%");
        }
        if(!StringUtils.isNullOrEmpty(type)){
            queryWrapper.like("type", "%" + type + "%");
        }
            if (!StringUtils.isNullOrEmpty(branchCode)) {
                queryWrapper.like("branch_code", "%" + branchCode + "%");
            }
        if (!StringUtils.isNullOrEmpty(tenantId)) {
                queryWrapper.like("tenant_id", "%" + tenantId + "%");
        }
        List<Device> result = deviceService.list(queryWrapper);
        return CommonResult.success(result, "操作成功！");
    }

    @ApiOperation(value = "查询全部设备", notes = "查询全部设备")
    @ApiImplicitParam(name = "deviceCode", value = "编码", required = true, dataType = "String", paramType = "path")
    @GetMapping("/findAll")
    public CommonResult<List<Map<String, Object>>> findAllDevice(String branchCode,String tenantId){
        List<Map<String, Object>> result = new ArrayList<>();
        QueryWrapper<Device> queryWrapper = new QueryWrapper<Device>();
         if (!StringUtils.isNullOrEmpty(branchCode)) {
                queryWrapper.like("branch_code", "%" + branchCode + "%");
            }
       if (!StringUtils.isNullOrEmpty(tenantId)) {
                queryWrapper.like("tenant_id", "%" + tenantId + "%");
        }
        List<Device> list = deviceService.list(queryWrapper);

        List<Device> parentList = list.stream().filter(device -> StringUtils.isNullOrEmpty(device.getParentId())).collect(Collectors.toList());

        List<Device> childrenList = list.stream().filter(device -> !StringUtils.isNullOrEmpty(device.getParentId())).collect(Collectors.toList());


        /*data.data[i].label = data.data[i].name
        data.data[i].isLeaf = true
        if (data.data[i].type === '1') {
            data.data[i].icon = 'el-icon-folder-opened'
        } else {
            data.data[i].icon = 'el-icon-tickets'
        }*/

        for (Device device: parentList) {
            Map<String, Object> data = new HashMap<>();
            data.put("id", device.getId());
            data.put("name", device.getName());
            data.put("code", device.getCode());
            data.put("type", device.getType());

            data.put("label", device.getName());
            data.put("isLeaf", true);
            if("1".equals(device.getType())){
                data.put("icon", "el-icon-folder-opened");
            } else {
                data.put("icon", "el-icon-tickets");
            }

            List<Map<String, Object>> children = new ArrayList<>();
            for (Device cDevice: childrenList){
                if(cDevice.getParentId().equals(device.getId())){
                    Map<String, Object> cData = new HashMap<>();
                    cData.put("id", cDevice.getId());
                    cData.put("name", cDevice.getName());
                    cData.put("code", cDevice.getCode());
                    cData.put("icon", cDevice.getIcon());
                    cData.put("type", cDevice.getType());

                    cData.put("label", cDevice.getName());
                    cData.put("isLeaf", true);
                    if("1".equals(cDevice.getType())){
                        cData.put("icon", "el-icon-folder-opened");
                    } else {
                        cData.put("icon", "el-icon-tickets");
                    }
                    children.add(cData);
                }
            }
            if(children.size() > 0){
                data.put("children", children);
            }
            result.add(data);
        }

        return CommonResult.success(result, "操作成功！");
    }

    @ApiOperation(value = "查询设备", notes = "根据ID获得设备")
    @GetMapping("/find_one")
    public CommonResult<Device> find(String id){
        QueryWrapper<Device> queryWrapper = new QueryWrapper<Device>();
        Device result = deviceService.getById(id);
        return CommonResult.success(result, "操作成功！");
    }


    @ApiOperation(value = "删除设备", notes = "根据id删除设备")
    @ApiImplicitParam(name = "ids", value = "ID", required = true, dataType = "String[]", paramType = "path")
    @PostMapping("/delete")
    public CommonResult<Device> delete(@RequestBody String[] ids){
            
            boolean bool = deviceService.removeByIds(java.util.Arrays.asList(ids));
            if(bool){
                return CommonResult.success(null, "删除成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
       
    }
    
    
    @ApiOperation(value = "导入设备", notes = "根据Excel文档导入设备")
    @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "path")
    @PostMapping("/import_excel")
    public CommonResult importExcel(HttpServletRequest request, @RequestParam("file") MultipartFile file,String branchCode,String tenantId) {
        CommonResult result = null;
        //封装证件信息实体类
        java.lang.reflect.Field[] fields = Device.class.getDeclaredFields();
        //封装证件信息实体类
        String[] fieldNames = new String[fields.length];
        for(int i=0;i<fields.length;i++)
        {
            fieldNames[i]= fields[i].getName();
        }
        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        try {
            excelFile = new File(System.getProperty("java.io.tmpdir"),tempName.toString());
            file.transferTo(excelFile);
            //将导入的excel数据生成证件实体类list
            List<Device> list =  ExcelUtils.importExcel(excelFile, Device.class, fieldNames, 1,0,0,tempName.toString());
            FileUtils.delete(excelFile);
             for (int i = 0; i < list.size(); i++) {

                 if (null != SecurityUtils.getCurrentUser()) {

                    list.get(i).setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                }
                 list.get(i).setTenantId(tenantId);
                 list.get(i).setBranchCode(branchCode);
                 List<Device> devices = deviceService.list(new QueryWrapper<Device>().eq("name", list.get(i).getParentId()).eq("type", "1").eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId()));
                if(devices.size()>0) {
                     list.get(i).setParentId(devices.get(0).getId());
                }
                if(StringUtils.isNullOrEmpty(list.get(i).getType())) {
                     list.get(i).setType("0");
                } else if("设备".equals(list.get(i).getType()))
                {
                    list.get(i).setType("0");
                }
                else if("设备组".equals(list.get(i).getType())) {
                    list.get(i).setType("1");
                }
                else {
                      list.get(i).setType("0");
                }
                if("是".equals( list.get(i).getRunStatus())) {
                     list.get(i).setRunStatus("1");
                }
                if("否".equals( list.get(i).getRunStatus())) {
                      list.get(i).setRunStatus("0");
                }
                if("是".equals( list.get(i).getStatus())) {
                     list.get(i).setStatus("1");
                }
                if("否".equals( list.get(i).getStatus())) {
                      list.get(i).setStatus("0");
                }
                
             }
           // list = list.stream().filter(item -> item.getMaterialNo() != null).collect(Collectors.toList());
          

            boolean bool = deviceService.saveBatch(list);
            if(bool){
                return CommonResult.success(null);
            } else {
                return CommonResult.failed();
            }
        }catch (Exception e) {
            return CommonResult.failed();
        }
    }

    
}
