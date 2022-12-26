package com.richfit.mes.produce.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.Hour;
import com.richfit.mes.common.model.produce.HourStandard;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.service.HourService;
import com.richfit.mes.produce.service.HourStandardService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * @Author: renzewen
 */
@Slf4j
@Api(value = "工时标准", tags = {"工时标准"})
@RestController
@RequestMapping("/api/hour")
public class HourController {

    @Autowired
    private HourService hourService;

    /**
     * 查询工时版本列表
     */
    @ApiOperation(value = "查询工时", notes = "查询工时")
    @GetMapping("/page")
    public CommonResult queryPage(String deviceType,String verId, int page, int limit) throws GlobalException {

        QueryWrapper<Hour> queryWrapper = new QueryWrapper<Hour>();

        queryWrapper.eq(!StringUtils.isNullOrEmpty(deviceType),"device_type", deviceType);
        queryWrapper.eq("verId", verId);

        queryWrapper.orderByDesc("modify_time");

        return CommonResult.success(hourService.page(new Page<Hour>(page, limit), queryWrapper));

    }

    /**
     * 新增工时版本信息
     */
    @ApiOperation(value = "新增工时", notes = "新增工时")
    @PostMapping("/add")
    public CommonResult<Boolean> saveOrUpdate(@RequestBody Hour hour) throws GlobalException {
        return CommonResult.success(hourService.saveOrUpdate(hour));
    }

    /**
     * 删除工时版本信息
     */
    @ApiOperation(value = "删除工时", notes = "根据id删除工时")
    @DeleteMapping("/delById/{id}")
    public CommonResult<Boolean> delById(@PathVariable String id) throws GlobalException {
        return CommonResult.success(hourService.removeById(id));
    }

}
