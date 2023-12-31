package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.RgDevice;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.produce.service.RgDeviceService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: renzewen
 */
@Slf4j
@Api(tags = "热工设备管理")
@RestController
@RequestMapping("/api/produce/rg/device")
public class RgDeviceController {

    @Autowired
    private RgDeviceService rgDeviceService;

    /**
     * 分页查询
     */
    @ApiOperation(value = "分页查询设备类别", notes = "分页查询设备类别")
    @GetMapping("/getTypePage")
    public CommonResult<IPage> getTypePage(String typeCode, String typeName, int page, int limit, String orderCol, String order) throws GlobalException {
        QueryWrapper<RgDevice> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(typeCode)) {
            queryWrapper.eq("type_code", typeCode);
        }
        if (!StringUtils.isNullOrEmpty(typeName)) {
            queryWrapper.eq("type_name", typeName);
        }
        //父节点为空 即为类别信息
        queryWrapper.isNull("type_id");

        if (!StringUtils.isNullOrEmpty(orderCol)) {
            //排序
            if(orderCol.equals("typeNo")){
                OrderUtil.query(queryWrapper, orderCol+"+0", order);
            }else{
                OrderUtil.query(queryWrapper, orderCol, order);
            }
        } else {
            //升序排列
            queryWrapper.orderByAsc("type_no+0");
        }

        return CommonResult.success(rgDeviceService.page(new Page<>(page, limit), queryWrapper));
    }

    /**
     * 设备类别list
     */
    @ApiOperation(value = "设备类别list", notes = "设备类别list")
    @GetMapping("/list")
    public CommonResult<List> list() throws GlobalException {
        QueryWrapper<RgDevice> queryWrapper = new QueryWrapper<>();
        //父节点为空 即为类别信息
        queryWrapper.isNull("type_id");

        //升序排列
        queryWrapper.orderByAsc("type_no+0");

        return CommonResult.success(rgDeviceService.list(queryWrapper));
    }


    /**
     * 新增类别操作信息
     */
    @ApiOperation(value = "新增或修改操作信息", notes = "新增或修改操作信息")
    @PostMapping("/addOrUpdate")
    public CommonResult<Boolean> addOrUpdate(@RequestBody RgDevice rgDevice) throws GlobalException {
        if (!StringUtils.isNullOrEmpty(rgDevice.getTypeId())) {
            rgDevice.setDeviceType(rgDeviceService.getById(rgDevice.getTypeId()).getTypeName());
        }
        return CommonResult.success(rgDeviceService.saveOrUpdate(rgDevice));
    }

    /**
     * 删除操作信息
     */
    @ApiOperation(value = "删除操作信息", notes = "根据id删除操作信息")
    @DeleteMapping("/delete/{id}")
    public CommonResult<Boolean> delById(@PathVariable String id) throws GlobalException {
        //父节点需要删除关联的子节点数据
        QueryWrapper<RgDevice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("id", id)
                .or(wrapper -> wrapper.eq("type_id", id));
        return CommonResult.success(rgDeviceService.remove(queryWrapper));
    }


    /**
     * 根据类别ID获取关联设备信息
     */
    @ApiOperation(value = "根据类别ID分页获取关联设备信息", notes = "根据类别ID分页获取关联设备信息")
    @GetMapping("/getListInfoById")
    public CommonResult<IPage> getListInfoById(String typeId, String order, String orderCol, int page, int limit) throws GlobalException {
        QueryWrapper<RgDevice> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type_id", typeId);
        if (!StringUtils.isNullOrEmpty(orderCol)) {
            //排序
            OrderUtil.query(queryWrapper, orderCol, order);
        } else {
            //升序排列
            queryWrapper.orderByDesc("modify_time");
        }
        return CommonResult.success(rgDeviceService.page(new Page<RgDevice>(page, limit), queryWrapper));
    }

    /**
     * 根据类别code获取关联设备信息
     */
    @ApiOperation(value = "根据类别code获取关联设备信息", notes = "根据类别code获取关联设备信息")
    @GetMapping("/getDeviceListByTypeCode")
    public CommonResult<List> getListInfoById(String typeCode) throws GlobalException {
        List<RgDevice> rgDevices = new ArrayList<>();
        QueryWrapper<RgDevice> rgDeviceQueryWrapper = new QueryWrapper<>();
        rgDeviceQueryWrapper.eq("type_code", typeCode);
        List<RgDevice> list = rgDeviceService.list(rgDeviceQueryWrapper);
        if (list.size() > 0) {
            QueryWrapper<RgDevice> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("type_id", list.get(0).getId());
            //升序排列
            queryWrapper.orderByDesc("device_name");
            rgDevices = rgDeviceService.list(queryWrapper);
        }
        return CommonResult.success(rgDevices);
    }


}
