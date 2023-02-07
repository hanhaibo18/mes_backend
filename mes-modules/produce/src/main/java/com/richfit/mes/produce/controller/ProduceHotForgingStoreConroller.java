package com.richfit.mes.produce.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.model.produce.HotForgingParam;
import com.richfit.mes.common.model.produce.HotForgingStore;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.service.ProduceHotForgingParamService;
import com.richfit.mes.produce.service.ProduceHotForgingStoreService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Api(value = "锻件仿型库", tags = {"锻件仿型库"})
@RestController
@RequestMapping("/api/produce/HotForgingStore")
public class ProduceHotForgingStoreConroller {

    @Autowired
    private ProduceHotForgingStoreService hotForgingStoreService;
    @Autowired
    private ProduceHotForgingParamService produceHotForgingParamService;

    @ApiOperation(value = "新增锻件仿型", notes = "新增锻件仿型")
    @PostMapping("/addHotForgingStore")
    public CommonResult addHotForgingStore(@RequestBody HotForgingStore produceHotForgingStore) {
        QueryWrapper<HotForgingStore> queryWrapper = new QueryWrapper<HotForgingStore>();
        queryWrapper.eq("profiling_drawing_no", produceHotForgingStore.getProfilingDrawingNo());
        HotForgingStore hotForgingStore = hotForgingStoreService.getOne(queryWrapper);
        if (ObjectUtils.isNotEmpty(hotForgingStore)) {
            System.out.println(JSON.toJSON(hotForgingStore));
            return CommonResult.failed("图号已存在锻件仿型了");
        }
        produceHotForgingStore.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        boolean save = hotForgingStoreService.save(produceHotForgingStore);
        if (save == true) return CommonResult.success(true);
        return CommonResult.failed();
    }

    @ApiOperation(value = "锻件仿型列表查询", notes = "锻件仿型列表查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "profilingType", value = "所属类型（0：轴类，1：饼类，2：圈类，3：套类，4：四方类）", required = false, paramType = "query"),
            @ApiImplicitParam(name = "profilingDrawingNo", value = "仿型图号", required = false, paramType = "query")
//            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query"),
//            @ApiImplicitParam(name = "limit", value = "每一页显示条数", required = true, paramType = "query")
    })
    @GetMapping("/hotForgingStoreList")
    public CommonResult<List<HotForgingStore>> selectHotForgingStoreList(Integer profilingType, String profilingDrawingNo) {
        QueryWrapper<HotForgingStore> queryWrapper = new QueryWrapper<HotForgingStore>();
        if (ObjectUtils.isNotEmpty(profilingType)) {
            queryWrapper.eq("profiling_type", profilingType);
        }
        if (ObjectUtils.isNotEmpty(profilingDrawingNo)) {
            queryWrapper.eq("profiling_drawing_no", profilingDrawingNo);
        }
        return CommonResult.success(hotForgingStoreService.list(queryWrapper), ResultCode.SUCCESS.getMessage());
    }


    @ApiOperation(value = "修改锻件仿型", notes = "修改锻件仿型")
    @PostMapping("/updateHotForgingStoreById")
    public CommonResult updateHotForgingStore(@RequestBody HotForgingStore hotForgingStore) {
        boolean b = hotForgingStoreService.updateById(hotForgingStore);
        if (b==true) return CommonResult.success(ResultCode.SUCCESS);
        return CommonResult.failed();
    }

    @ApiOperation(value = "删除锻件仿型", notes = "删除锻件仿型")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "forgingStoreIdList", value = "锻件仿型IdList", required = true, paramType = "query")
    })
    @PostMapping("/deleteHotForgingStoreByIds")
    public CommonResult deleteHotForgingStore(@RequestBody List<String> forgingStoreIdList) {
        //先删除仿型参数
        QueryWrapper<HotForgingParam> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("forging_store_id",forgingStoreIdList);
        boolean remove = produceHotForgingParamService.remove(queryWrapper);
        boolean b = hotForgingStoreService.removeByIds(forgingStoreIdList);
        if (b==true) return CommonResult.success(ResultCode.SUCCESS);
        return CommonResult.failed();
    }
}
