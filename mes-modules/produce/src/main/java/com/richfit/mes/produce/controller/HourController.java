package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.Hour;
import com.richfit.mes.produce.service.HourService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;

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
        queryWrapper.eq("ver_id", verId);

        queryWrapper.orderByDesc("modify_time");

        return CommonResult.success(hourService.page(new Page<Hour>(page, limit), queryWrapper));

    }

    /**
     * 新增工时版本信息
     */
    @ApiOperation(value = "新增工时", notes = "新增工时")
    @PostMapping("/save")
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

    @ApiOperation(value = "工时导入", notes = "根据Excel文档导入工时")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "__file", paramType = "form"),
            @ApiImplicitParam(name = "branchCode", value = "公司", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "verId", value = "公司", required = true, paramType = "query", dataType = "string")
    })
    @PostMapping("/import_excel")
    public CommonResult importExcel(HttpServletRequest request, @RequestParam("file") MultipartFile file, String branchCode, String verId) {
        return  hourService.importExcel(file,branchCode,verId);
    }


}
