package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.DeviceRecordService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.DeviceRecord;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author 马峰
 * @Description 设备记录Controller
 */
@Slf4j
@Api("设备记录管理")
@RestController
@RequestMapping("/api/base/devicerecord")
public class DeviceRecordController extends BaseController {

    @Autowired
    private DeviceRecordService deviceRecordService;

    /**
     * ***
     * 分页查询
     *
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "设备记录", notes = "设备记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "code", value = "编码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "name", value = "名称", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/page")
    public CommonResult<IPage<DeviceRecord>> page(int page, int limit, String code, String name, String typeClass, String type, String deviceId) {
        try {
            QueryWrapper<DeviceRecord> queryWrapper = new QueryWrapper<DeviceRecord>();
            if (!StringUtils.isNullOrEmpty(typeClass)) {
                queryWrapper.eq("type_class", typeClass);
            }
            if (!StringUtils.isNullOrEmpty(code)) {
                queryWrapper.eq("code", code);
            }
            if (!StringUtils.isNullOrEmpty(name)) {
                queryWrapper.like("name", name);
            }
            if (!StringUtils.isNullOrEmpty(type)) {
                queryWrapper.like("type", type);
            }
            if (!StringUtils.isNullOrEmpty(deviceId)) {
                queryWrapper.like("device_id", deviceId);
            }
            IPage<DeviceRecord> devices = deviceRecordService.page(new Page<DeviceRecord>(page, limit), queryWrapper);
            return CommonResult.success(devices);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "新增设备记录", notes = "新增设备记录")
    @ApiImplicitParam(name = "device", value = "设备记录", required = true, dataType = "DeviceRecord", paramType = "path")
    @PostMapping("/add")
    public CommonResult<DeviceRecord> addDeviceRecord(@RequestBody DeviceRecord device) {
        if (StringUtils.isNullOrEmpty(device.getName())) {
            return CommonResult.failed("编码不能为空！");
        } else {
            device.setModifyBy("test");
            device.setModifyTime(new Date());
            device.setCreateBy("test");
            device.setCreateTime(new Date());
            boolean bool = deviceRecordService.save(device);
            if (bool) {
                return CommonResult.success(device, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

    @ApiOperation(value = "修改设备记录", notes = "修改设备记录")
    @ApiImplicitParam(name = "device", value = "设备记录", required = true, dataType = "DeviceRecord", paramType = "path")
    @PostMapping("/update")
    public CommonResult<DeviceRecord> updateDeviceRecord(@RequestBody DeviceRecord device) {
        if (StringUtils.isNullOrEmpty(device.getName())) {
            return CommonResult.failed("编码不能为空！");
        } else {
            device.setModifyBy("test");
            device.setModifyTime(new Date());
            boolean bool = deviceRecordService.updateById(device);
            if (bool) {
                return CommonResult.success(device, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

    @ApiOperation(value = "查询设备记录", notes = "根据编码获得设备记录")
    @ApiImplicitParam(name = "deviceCode", value = "编码", required = true, dataType = "String", paramType = "path")
    @GetMapping("/find")
    public CommonResult<List<DeviceRecord>> find(String id, String code, String name, String typeClass, String type, String deviceId, String status) {
        QueryWrapper<DeviceRecord> queryWrapper = new QueryWrapper<DeviceRecord>();
        if (!StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (!StringUtils.isNullOrEmpty(typeClass)) {
            queryWrapper.eq("type_class", typeClass);
        }
        if (!StringUtils.isNullOrEmpty(code)) {
            queryWrapper.eq("code", code);
        }
        if (!StringUtils.isNullOrEmpty(name)) {
            queryWrapper.like("name", name);
        }
        if (!StringUtils.isNullOrEmpty(type)) {
            queryWrapper.like("type", type);
        }
        if (!StringUtils.isNullOrEmpty(deviceId)) {
            queryWrapper.like("device_id", deviceId);
        }
        if (!StringUtils.isNullOrEmpty(status)) {
            queryWrapper.like("status", Integer.parseInt(status));
        }
        queryWrapper.orderByAsc("principal_time");
        List<DeviceRecord> result = deviceRecordService.list(queryWrapper);
        return CommonResult.success(result, "操作成功！");
    }

    @ApiOperation(value = "删除设备记录", notes = "根据id删除设备记录")
    @ApiImplicitParam(name = "ids", value = "ID", required = true, dataType = "String[]", paramType = "path")
    @PostMapping("/delete")
    public CommonResult<DeviceRecord> delete(@RequestBody String[] ids) {

        boolean bool = deviceRecordService.removeByIds(java.util.Arrays.asList(ids));
        if (bool) {
            return CommonResult.success(null, "删除成功！");
        } else {
            return CommonResult.failed("操作失败，请重试！");
        }

    }


}
