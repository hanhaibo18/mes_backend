package com.richfit.mes.produce.controller;


import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.entity.ProduceInspectionRecordDto;
import com.richfit.mes.produce.service.ProduceInspectionRecordService;
import freemarker.template.TemplateException;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


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
        return CommonResult.success(produceInspectionRecordService.saveRecord(produceInspectionRecordDto));
    }

    @ApiOperation(value = "根据工序id查询探伤记录列表", notes = "根据工序id查询探伤记录列表")
    @ApiImplicitParam(name = "itemId", value = "工序id", required = true, paramType = "path", dataType = "string")
    @GetMapping("/queryRecordByItemId/{itemId}")
    public CommonResult queryRecordByItemId(@PathVariable String itemId){
        return CommonResult.success(produceInspectionRecordService.queryRecordByItemId(itemId));
    }

    @ApiOperation(value = "审核提交探伤记录", notes = "审核提交探伤记录")
    @PostMapping("/auditSubmitRecord")
    public CommonResult auditSubmitRecord(@RequestBody TrackItem trackItem){
        return CommonResult.success(produceInspectionRecordService.auditSubmitRecord(trackItem));
    }

    @ApiOperation(value = "报告预览", notes = "报告预览")
    @GetMapping("/exoprtReport")
    public void exoprtReport(HttpServletResponse response) throws IOException, TemplateException, GlobalException {
        produceInspectionRecordService.exoprtReport(response);
    }




}
