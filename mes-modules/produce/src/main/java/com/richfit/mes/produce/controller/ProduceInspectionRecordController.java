package com.richfit.mes.produce.controller;


import cn.hutool.core.util.ObjectUtil;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.produce.entity.ProduceInspectionRecordDto;
import com.richfit.mes.produce.service.ProduceInspectionRecordService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


/**
 * 质量管理模块接口
 * @Author: renzewen
 * @Date: 2022/8/22 13:10
 */
@Slf4j
@Api(tags = "质量管理/探伤")
@RestController
@RequestMapping("/api/produce/inspectionRecord")
public class ProduceInspectionRecordController extends BaseController {

    @Autowired
    private ProduceInspectionRecordService produceInspectionRecordService;

    @ApiOperation(value = "保存探伤记录", notes = "保存探伤记录")
    @PostMapping("/save")
    public CommonResult saveRecord(@RequestBody ProduceInspectionRecordDto produceInspectionRecordDto){
        return CommonResult.success(produceInspectionRecordService.saveOrUpdateRecord(produceInspectionRecordDto));
    }

    @ApiOperation(value = "根据工序id查询探伤记录列表", notes = "根据工序id查询探伤记录列表")
    @ApiImplicitParam(name = "itemId", value = "工序id", required = true, paramType = "path", dataType = "string")
    @GetMapping("/queryRecordByItemId/{itemId}")
    public CommonResult queryRecordByItemId(@PathVariable String itemId){
        return CommonResult.success(produceInspectionRecordService.queryRecordByItemId(itemId));
    }




}
