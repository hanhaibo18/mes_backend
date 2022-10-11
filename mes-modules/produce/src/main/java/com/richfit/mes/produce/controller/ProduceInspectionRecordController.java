package com.richfit.mes.produce.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.produce.entity.CompleteDto;
import com.richfit.mes.produce.entity.ProduceInspectionRecordDto;
import com.richfit.mes.produce.service.ProduceInspectionRecordService;
import freemarker.template.TemplateException;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


/**
 * 质量管理模块接口
 * @Author: renzewen
 * @Date: 2022/8/22 13:10
 */
@Slf4j
@Api(tags = "质量管理(探伤管理)")
@RestController
@RequestMapping("/api/produce/inspectionRecord")
public class ProduceInspectionRecordController extends BaseController {

    private static final Integer YES_OPERA = 1; //已报工
    private static final Integer NO_OPERA = 0; //未报工

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
            @ApiImplicitParam(name = "branchCode", value = "组织机构编码", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "tenantId", value = "组织机构id", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "endTime", value = "截至时间", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "trackNo", value = "跟单号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "productName", value = "产品名称", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "productNo", value = "产品编号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "isAudit", value = "审核状态（待审核0、已审核1）", paramType = "query", dataType = "Integer"),
    })
    @GetMapping("/page")
    public CommonResult<IPage<TrackItemInspection>> page(int page, int limit, String startTime, String endTime, String trackNo, String productName,String productNo, String branchCode, String tenantId, String isAudit) {
        return CommonResult.success(produceInspectionRecordService.page(page,limit,startTime,endTime,trackNo,productName,productNo,branchCode,tenantId,isAudit));
    }

    /**
     * ***
     * 分页查询待探伤工序
     *
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "分页查询探伤记录审核 跟单工序列表", notes = "分页查询探伤记录审核 跟单工序列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "branchCode", value = "组织机构编码", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "tenantId", value = "组织机构id", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "endTime", value = "截至时间", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "trackNo", value = "跟单号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "productName", value = "产品名称", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "productNo", value = "产品编号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "isAudit", value = "审核状态（待审核0、已审核1）", paramType = "query", dataType = "Integer"),
    })
    @GetMapping("/page/queryItemByAuditBy")
    public CommonResult<IPage<TrackItemInspection>> queryItemByAuditBy(int page, int limit, String startTime, String endTime, String trackNo, String productName,String productNo, String branchCode, String tenantId, String isAudit) {

        return CommonResult.success(produceInspectionRecordService.queryItemByAuditBy(page,limit,startTime,endTime,trackNo,productName,productNo,branchCode,tenantId,""));
    }

    /**
     * ***
     * 分页查询待探伤工序
     *
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "分页查询探伤派工信息", notes = "分页查询探伤派工信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "branchCode", value = "组织机构编码", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "tenantId", value = "组织机构id", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "endTime", value = "截至时间", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "trackNo", value = "跟单号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "productName", value = "产品名称", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "productNo", value = "产品编号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "isOperationComplete", value = "报工状态（0、未报工 1、已报工）", paramType = "query", dataType = "Integer"),
    })
    @GetMapping("/assginPage")
    public Object assginPage(int page, int limit, String startTime, String endTime, String trackNo, String productName,String productNo, String branchCode, String tenantId,Integer isOperationComplete) {
        //判断
        if(YES_OPERA.equals(isOperationComplete)){
            return produceInspectionRecordService.pageTrackComplete(page, limit, productNo,trackNo, startTime,endTime,branchCode);
        }else if(NO_OPERA.equals(isOperationComplete)){
            return CommonResult.success(produceInspectionRecordService.assginPage(page,limit,startTime,endTime,trackNo,productName,productNo,branchCode,tenantId,isOperationComplete));
        }
        return null;
    }

    @ApiOperation(value = "保存探伤记录", notes = "保存探伤记录")
    @PostMapping("/save")
    public CommonResult saveRecord(@RequestBody ProduceInspectionRecordDto produceInspectionRecordDto) throws Exception {
        return CommonResult.success(produceInspectionRecordService.saveRecord(produceInspectionRecordDto));
    }

    @ApiOperation(value = "根据工序id查询探伤记录列表", notes = "根据工序id查询探伤记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "itemId", value = "工序id", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "checkOrAudit", value = "探伤记录页面or探伤记录审核页面", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "isAudit", value = "审核状态", paramType = "query", dataType = "string"),
    })
    @GetMapping("/queryRecordByItemId")
    public CommonResult queryRecordByItemId(String itemId,String checkOrAudit,String isAudit){
        return CommonResult.success(produceInspectionRecordService.queryRecordByItemId(itemId,checkOrAudit,isAudit));
    }

    @ApiOperation(value = "根据工序id查询最近一条探伤记录", notes = "根据工序id查询最近一条探伤记录")
    @ApiImplicitParam(name = "itemId", value = "工序id", required = true, paramType = "path", dataType = "string")
    @GetMapping("/queryLastInfoByItemId/{itemId}")
    public CommonResult queryLastInfoByItemId(@PathVariable String itemId){
        return CommonResult.success(produceInspectionRecordService.queryLastInfoByItemId(itemId));
    }

    @ApiOperation(value = "审核提交探伤记录", notes = "审核提交探伤记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "itemId", value = "工序id", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "remark", value = "探伤备注", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "flawDetection", value = "探伤结果(0,1)", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "tempType", value = "探伤记录模板类型",required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "recordNo", value = "探伤记录编号", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "checkBy", value = "探伤检验人", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "auditBy", value = "探伤审核人", paramType = "query", dataType = "string")
    })
    @GetMapping("/auditSubmitRecord")
    public CommonResult auditSubmitRecord(String itemId,String flawDetectioRemark,Integer flawDetection,String tempType,String recordNo,String checkBy,String auditBy){
        return CommonResult.success(produceInspectionRecordService.auditSubmitRecord(itemId,flawDetectioRemark,flawDetection,tempType,recordNo,checkBy,auditBy));
    }

    @ApiOperation(value = "报告预览", notes = "报告预览")
    @ApiImplicitParam(name = "id", value = "探伤记录id", paramType = "query", dataType = "string")
    @GetMapping("/exoprtReport")
    public void exoprtReport(HttpServletResponse response, String id) throws IOException, TemplateException, GlobalException {
        produceInspectionRecordService.exoprtReport(response,id);
    }

    @ApiOperation(value = "新增报工(新)", notes = "新增报工(新)")
    @PostMapping("/saveComplete")
    public CommonResult<Boolean> saveComplete(@RequestBody List<CompleteDto> completeDto) {
        return produceInspectionRecordService.saveComplete(completeDto);
    }

    @ApiOperation(value = "保存报工(新)", notes = "保存报工(新)")
    @PostMapping("/saveCompleteCache")
    public CommonResult<Boolean> saveCompleteCache(@RequestBody List<CompleteDto> completeDtoList) {
        return produceInspectionRecordService.saveCompleteCache(completeDtoList);
    }

    @ApiOperation(value = "修改报工(新)", notes = "修改报工(新)")
    @PutMapping("/updateComplete")
    public CommonResult<Boolean> updateComplete(@RequestBody CompleteDto completeDto) {
        return produceInspectionRecordService.updateComplete(completeDto);
    }


    @ApiOperation(value = "开工", notes = "开工")
    @ApiImplicitParam(name = "assign", value = "派工", required = true, dataType = "Assign", paramType = "path")
    @PostMapping("/updateAssign")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Assign> updateAssign(@RequestBody Assign assign) {
        return produceInspectionRecordService.updateAssign(assign);
    }

    @ApiOperation(value = "回滚(新)", notes = "回滚(新)")
    @ApiImplicitParam(name = "id", value = "报工Id", required = true, dataType = "String", paramType = "query")
    @GetMapping("rollBack")
    public CommonResult<Boolean> rollBack(String id) {
        return produceInspectionRecordService.rollBack(id);
    }

    @ApiOperation(value = "探伤记录审核", notes = "探伤记录审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "探伤记录id", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "tempType", value = "模板类型", required = true,paramType = "query", dataType = "string")
    })
    @GetMapping("auditRecord")
    public CommonResult<Boolean> auditByRecordId(String id,String tempType,String isAudit,String auditRemark) {
        return CommonResult.success(produceInspectionRecordService.auditByRecord(id,tempType,isAudit,auditRemark));
    }
}
