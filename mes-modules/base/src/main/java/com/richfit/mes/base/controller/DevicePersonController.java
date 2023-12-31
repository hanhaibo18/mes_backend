package com.richfit.mes.base.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.DevicePersonService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.Device;
import com.richfit.mes.common.model.base.DevicePerson;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 马峰
 * @Description 设备人员记录Controller
 */
@Slf4j
@Api(value = "设备人员记录管理", tags = {"设备人员记录管理"})
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
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "deviceId", value = "设备ID", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "userId", value = "人员ID", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/page")
    public CommonResult<IPage<DevicePerson>> page(int page, int limit, String deviceId, String userId, String tenantId, String code, String branchCode) {
        try {
            QueryWrapper<DevicePerson> queryWrapper = new QueryWrapper<DevicePerson>();
            if (!StringUtils.isNullOrEmpty(userId)) {
                queryWrapper.eq("user_id", userId);
            }
            if (!StringUtils.isNullOrEmpty(deviceId)) {

                //queryWrapper.apply("(device_id = '" + deviceId + "' or device_id = '" + code + "') ");
                queryWrapper.eq("device_id",deviceId);
            }
            if (!StringUtils.isNullOrEmpty(tenantId)) {
                queryWrapper.eq("tenant_id", tenantId);
            }
            if (!StringUtils.isNullOrEmpty(branchCode)) {
                queryWrapper.eq("branch_code", branchCode);
            }
            queryWrapper.orderByAsc("order_no");
            IPage<DevicePerson> devices = devicePersonService.selectPage(new Page<DevicePerson>(page, limit), queryWrapper);
            return CommonResult.success(devices);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "新增设备人员记录", notes = "新增设备人员记录")
    @ApiImplicitParam(name = "device", value = "设备人员记录", required = true, dataType = "DevicePerson", paramType = "path")
    @PostMapping("/add")
    public CommonResult<DevicePerson[]> addDevicePerson(@RequestBody DevicePerson[] devicePersons) {
        //根据设备id查询已经关联人员
        List<String> deviceIds = new ArrayList<>();
        if(devicePersons.length>0){
            for (int i=0;i<devicePersons.length;i++){
                deviceIds.add(devicePersons[i].getDeviceId());
            }
        }
        List<DevicePerson> devicePersonByDeviceId = devicePersonService.list(new QueryWrapper<DevicePerson>().in("device_id", deviceIds));
        //根据设备id分组
        Map<String, Set<String>> devicePersonMap = devicePersonByDeviceId.stream().collect(Collectors.groupingBy(DevicePerson::getDeviceId, Collectors.mapping(DevicePerson::getUserId, Collectors.toSet())));

        for (DevicePerson devicePerson : devicePersons) {
            if (StringUtils.isNullOrEmpty(devicePerson.getDeviceId())) {
                return CommonResult.failed("编码不能为空！");
            } else {
                devicePerson.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                devicePerson.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                devicePerson.setCreateTime(new Date());
                devicePerson.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                devicePerson.setModifyTime(new Date());
                devicePerson.setBranchCode(devicePerson.getBranchCode());
                //保存没有被关联过的用户
                if(ObjectUtil.isEmpty(devicePersonMap.get(devicePerson.getDeviceId())) || !devicePersonMap.get(devicePerson.getDeviceId()).contains(devicePerson.getUserId())){
                    boolean bool = devicePersonService.save(devicePerson);
                }
            }
        }
        return CommonResult.success(devicePersons, "操作成功！");
    }

    @ApiOperation(value = "修改设备人员记录", notes = "修改设备人员记录")
    @ApiImplicitParam(name = "device", value = "设备人员记录", required = true, dataType = "DevicePerson", paramType = "path")
    @PostMapping("/update")
    public CommonResult<DevicePerson> updateDevicePerson(@RequestBody DevicePerson devicePerson) {
        if (StringUtils.isNullOrEmpty(devicePerson.getDeviceId())) {
            return CommonResult.failed("编码不能为空！");
        } else {

            devicePerson.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            devicePerson.setModifyTime(new Date());
            boolean bool = devicePersonService.updateById(devicePerson);
            if (bool) {
                return CommonResult.success(devicePerson, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

    @ApiOperation(value = "查询设备人员记录", notes = "根据编码获得设备人员记录")
    @ApiImplicitParam(name = "deviceId", value = "设备ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/find")
    public CommonResult<List<DevicePerson>> find(String deviceId, String userId, String branchCode, String tenantId, String isDefault) {

        QueryWrapper<DevicePerson> queryWrapper = new QueryWrapper<DevicePerson>();
        if (!StringUtils.isNullOrEmpty(userId)) {
            queryWrapper.eq("user_id", userId);
        }
        if (!StringUtils.isNullOrEmpty(deviceId)) {
            queryWrapper.eq("device_id", deviceId);
        }
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        if (!StringUtils.isNullOrEmpty(tenantId)) {
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
    public CommonResult<DevicePerson> delete(@RequestBody String[] ids) {

        boolean bool = devicePersonService.removeByIds(java.util.Arrays.asList(ids));
        if (bool) {
            return CommonResult.success(null, "删除成功！");
        } else {
            return CommonResult.failed("操作失败，请重试！");
        }

    }

    @ApiOperation(value = "根据人员Id查找记录列表", notes = "根据人员Id查找记录列表")
    @ApiImplicitParam(name = "userId", required = true, dataType = "String", paramType = "query")
    @PostMapping("/queryDeviceByUserId")
    public CommonResult<List<Device>> queryDeviceByUserId(String userId,String branchCode) {
        return CommonResult.success(devicePersonService.queryDeviceByUserId(userId,branchCode));
    }

    @ApiOperation(value = "根据人员Ids查找交集设备", notes = "根据人员Ids查找交集设备")
    @PostMapping("/queryDeviceByUserIds")
    public CommonResult<List<Device>> queryDeviceByUserIds(@RequestBody JSONObject jsonObject) {
        //人员id
        List<String> userIds = JSON.parseArray(JSONObject.toJSONString(jsonObject.get("userIds")), String.class);
        //组织机构
        String branchCode = jsonObject.getString("branchCode");
        return CommonResult.success(devicePersonService.queryDeviceByUserIds(userIds,branchCode));
    }
}
