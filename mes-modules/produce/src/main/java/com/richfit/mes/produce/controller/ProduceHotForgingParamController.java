package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.model.produce.HotForgingParam;
import com.richfit.mes.produce.service.ProduceHotForgingParamService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api(value = "仿型参数", tags = {"仿型参数"})
@RestController
@RequestMapping("/api/produce/HotForgingParam")
public class ProduceHotForgingParamController {


    @Autowired
    private ProduceHotForgingParamService produceHotForgingParamService;

    @ApiOperation(value = "新增仿型参数", notes = "新增仿型参数")
    @PostMapping("/addHotForgingParam")
    public CommonResult addHotForgingParam(@RequestBody HotForgingParam hotForgingParam) {
        boolean save = produceHotForgingParamService.save(hotForgingParam);
        if (save == true) return CommonResult.success(true);
        return CommonResult.failed();
    }

    @ApiOperation(value = "仿型参数列表", notes = "仿型参数列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "forgingStoreId", value = "仿型库表ID", required = true, paramType = "query")
    })
    @GetMapping("/hotForgingParamList")
    public CommonResult<List<HotForgingParam>> selectHotForgingParamList(@RequestParam String forgingStoreId) {
        QueryWrapper<HotForgingParam> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("forging_store_id", forgingStoreId);
        return CommonResult.success(produceHotForgingParamService.list(queryWrapper), ResultCode.SUCCESS.getMessage());
    }


    @ApiOperation(value = "修改仿型参数", notes = "修改仿型参数")
    @PostMapping("/updateHotForgingParamById")
    public CommonResult updateHotForgingStoreHotForgingParam(@RequestBody HotForgingParam hotForgingParam) {
        boolean b = produceHotForgingParamService.updateById(hotForgingParam);
        if (b==true) return CommonResult.success(ResultCode.SUCCESS);
        return CommonResult.failed();
    }

    @ApiOperation(value = "删除仿型参数", notes = "删除仿型参数")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "hotForgingParamIdList", value = "仿型参数IdList", required = true, paramType = "query")
    })
    @PostMapping("/deleteHotForgingParamByIds")
    public CommonResult deleteHotForgingParam(@RequestBody List<String> hotForgingParamIdList) {
        boolean b = produceHotForgingParamService.removeByIds(hotForgingParamIdList);
        if (b==true) return CommonResult.success(ResultCode.SUCCESS);
        return CommonResult.failed();
    }





}
