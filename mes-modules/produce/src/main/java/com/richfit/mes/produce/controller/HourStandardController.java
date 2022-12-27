package com.richfit.mes.produce.controller;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.HourStandard;
import com.richfit.mes.common.security.util.SecurityUtils;
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
@Api(value = "工时版本", tags = {"工时版本"})
@RestController
@RequestMapping("/api/hour/standard")
public class HourStandardController {

    @Autowired
    private HourStandardService hourStandardService;

    /**
     * 查询工时版本列表
     */
    @ApiOperation(value = "查询工时版本列表", notes = "查询工时版本列表")
    @GetMapping("/page")
    public CommonResult queryPage(String startTime, String endTime, int page, int limit,String branchCode) throws GlobalException {

        QueryWrapper<HourStandard> queryWrapper = new QueryWrapper<HourStandard>();
        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryWrapper.ge("activate_time", startTime);
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryWrapper.le("activate_time", endTime);
        }
        queryWrapper.eq(!StringUtils.isNullOrEmpty(branchCode),"branch_code", branchCode);
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());

        queryWrapper.orderByDesc("activate_time");

        return CommonResult.success(hourStandardService.page(new Page<HourStandard>(page, limit), queryWrapper));

    }

    /**
     * 新增工时版本信息
     */
    @ApiOperation(value = "新增工时版本信息", notes = "新增工时版本信息")
    @PostMapping("/save")
    public CommonResult<Boolean> saveOrUpdate(@RequestBody HourStandard hourStandard) throws GlobalException {
        hourStandard.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        return CommonResult.success(hourStandardService.saveOrUpdate(hourStandard));
    }


    /**
     * 删除工时版本信息
     */
    @ApiOperation(value = "删除工时版本信息", notes = "根据id删除工时版本信息")
    @DeleteMapping("/delById/{id}")
    public CommonResult<Boolean> delById(@PathVariable String id) throws GlobalException {
        return CommonResult.success(hourStandardService.removeById(id));
    }

    /**
     * 激活
     */
    @ApiOperation(value = "根据id激活工时版本", notes = "根据id激活工时版本")
    @GetMapping("/activate/{id}")
    public CommonResult<Boolean> activate(@PathVariable String id) throws GlobalException {
        HourStandard hourStandard = hourStandardService.getById(id);
        if(ObjectUtil.isEmpty(hourStandard)){
            throw new GlobalException("版本信息不存在", ResultCode.FAILED);
        }
        UpdateWrapper<HourStandard> hourStandardUpdateWrapper = new UpdateWrapper<>();
        hourStandardUpdateWrapper.eq("id",id)
                .set("is_activate","1")
                .set("is_activated","1")
                .set("activate_time",new Date())
                .set("activate_by",SecurityUtils.getCurrentUser().getUsername());
        return CommonResult.success(hourStandardService.update(hourStandardUpdateWrapper));
    }
}
