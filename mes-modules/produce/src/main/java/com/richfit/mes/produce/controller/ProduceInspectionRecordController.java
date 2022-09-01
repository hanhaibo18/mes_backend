package com.richfit.mes.produce.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.model.produce.TrackItemInspection;
import com.richfit.mes.produce.entity.ProduceInspectionRecordDto;
import com.richfit.mes.produce.service.ProduceInspectionRecordService;
import com.richfit.mes.produce.service.TrackHeadService;
import com.richfit.mes.produce.service.TrackItemInspectionService;
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

    /**
     * ***
     * 分页查询待探伤工序
     *
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "分页查询待探伤工序", notes = "分页查询待探伤工序")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "branchCode", value = "组织机构编码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "tenantId", value = "组织机构id", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "endTime", value = "截至时间", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "trackNo", value = "跟单号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "productName", value = "产品名称", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "isAudit", value = "审核状态（false、待审核 true、已审核）", required = true, paramType = "query", dataType = "boolean"),
    })
    @GetMapping("/page")
    public CommonResult<IPage<TrackItemInspection>> page(int page, int limit, String startTime, String endTime, String trackNo, String productName, String branchCode, String tenantId, Boolean isAudit) {
        return CommonResult.success(produceInspectionRecordService.page(page,limit,startTime,endTime,trackNo,productName,branchCode,tenantId,isAudit));
    }

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
    public CommonResult auditSubmitRecord(@RequestBody TrackItemInspection trackItemInspection){
        return CommonResult.success(produceInspectionRecordService.auditSubmitRecord(trackItemInspection));
    }

    @ApiOperation(value = "报告预览", notes = "报告预览")
    @GetMapping("/exoprtReport")
    public void exoprtReport(HttpServletResponse response,String itemId) throws IOException, TemplateException, GlobalException {
        produceInspectionRecordService.exoprtReport(response,itemId);
    }






}
