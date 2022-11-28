package com.richfit.mes.produce.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.CompleteDto;
import com.richfit.mes.produce.entity.ProduceInspectionRecordDto;
import com.richfit.mes.produce.entity.quality.InspectionPowerVo;
import com.richfit.mes.produce.service.ProduceInspectionRecordService;
import com.richfit.mes.produce.service.quality.InspectionPowerService;
import com.richfit.mes.produce.utils.OrderUtil;
import freemarker.template.TemplateException;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
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

    private final static int IS_STATUS = 1;
    private final static int NO_STATUS = 0;

    @Autowired
    private ProduceInspectionRecordService produceInspectionRecordService;
    @Autowired
    private InspectionPowerService inspectionPowerService;


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
    public CommonResult<IPage<InspectionPower>> page(int page, int limit, String startTime, String endTime, String trackNo, String productName,String productNo, String branchCode, String tenantId, String isAudit) {
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

    @ApiOperation(value = "分页查询委托单", notes = "分页查询委托单")
    @ApiImplicitParam(name = "inspectionPowerVo", value = "委托单", paramType = "body", dataType = "InspectionPowerVo")
    @PostMapping("/inspectionPower/page")
    public CommonResult<IPage> queryPowerOrderPage(@RequestBody InspectionPowerVo inspectionPowerVo) throws Exception {
        QueryWrapper<InspectionPower> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(inspectionPowerVo.getOrderNo())){
            queryWrapper.eq("order_no",inspectionPowerVo.getOrderNo());
        }
        if(!StringUtils.isEmpty(inspectionPowerVo.getInspectionDepart())){
            queryWrapper.eq("inspection_depart",inspectionPowerVo.getInspectionDepart());
        }
        if(!StringUtils.isEmpty(inspectionPowerVo.getSampleName())){
            queryWrapper.eq("sample_name",inspectionPowerVo.getSampleName());
        }
        if (!StringUtils.isEmpty(inspectionPowerVo.getStartTime())) {
            queryWrapper.ge("date_format(power_time, '%Y-%m-%d')", inspectionPowerVo.getStartTime());
        }
        if (!StringUtils.isEmpty(inspectionPowerVo.getEndTime())) {
            queryWrapper.le("date_format(power_time, '%Y-%m-%d')", inspectionPowerVo.getEndTime());
        }
        if(!StringUtils.isEmpty(inspectionPowerVo.getDrawNo())){
            queryWrapper.eq("draw_no",inspectionPowerVo.getDrawNo());
        }
        if(!StringUtils.isEmpty(inspectionPowerVo.getStatus())){
            queryWrapper.in("status",inspectionPowerVo.getStatus().split(","));
        }
        if(!StringUtils.isEmpty(inspectionPowerVo.getBranchCode())){
            queryWrapper.eq("branch_code",inspectionPowerVo.getBranchCode());
        }
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.eq("consignor",SecurityUtils.getCurrentUser().getUserId());
        if(!StringUtils.isEmpty(inspectionPowerVo.getOrderCol())){
            OrderUtil.query(queryWrapper, inspectionPowerVo.getOrderCol(), inspectionPowerVo.getOrder());
        }else{
            queryWrapper.orderByDesc("power_time");
        }



        return CommonResult.success(inspectionPowerService.page(new Page<InspectionPower>(inspectionPowerVo.getPage(),inspectionPowerVo.getLimit()),queryWrapper));
    }

    @ApiOperation(value = "探伤站派工页面分页查询委托单", notes = "探伤站派工页面分页查询委托单")
    @ApiImplicitParam(name = "inspectionPowerVo", value = "委托单", paramType = "body", dataType = "InspectionPowerVo")
    @PostMapping("/inspectionPower/pageZj")
    public CommonResult<IPage> queryPowerOrderPageZj(@RequestBody InspectionPowerVo inspectionPowerVo) throws Exception {
        QueryWrapper<InspectionPower> queryWrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(inspectionPowerVo.getOrderNo())){
            queryWrapper.eq("order_no",inspectionPowerVo.getOrderNo());
        }
        if(!StringUtils.isEmpty(inspectionPowerVo.getInspectionDepart())){
            queryWrapper.eq("inspection_depart",inspectionPowerVo.getInspectionDepart());
        }
        if(!StringUtils.isEmpty(inspectionPowerVo.getSampleName())){
            queryWrapper.eq("sample_name",inspectionPowerVo.getSampleName());
        }
        if (!StringUtils.isEmpty(inspectionPowerVo.getStartTime())) {
            queryWrapper.ge("date_format(assign_time, '%Y-%m-%d')", inspectionPowerVo.getStartTime());
        }
        if (!StringUtils.isEmpty(inspectionPowerVo.getEndTime())) {
            queryWrapper.le("date_format(assign_time, '%Y-%m-%d')", inspectionPowerVo.getEndTime());
        }
        if(!StringUtils.isEmpty(inspectionPowerVo.getDrawNo())){
            queryWrapper.eq("draw_no",inspectionPowerVo.getDrawNo());
        }
        if(!StringUtils.isEmpty(inspectionPowerVo.getAssignStatus())){
            queryWrapper.in("assign_status",Integer.parseInt(inspectionPowerVo.getAssignStatus()));
        }
        if(!StringUtils.isEmpty(inspectionPowerVo.getBranchCode())){
            //此处换南北探伤站查询  和传的barnchCode比较
            //queryWrapper.eq("inspection_depart",inspectionPowerVo.getBranchCode());
        }
        if(!StringUtils.isEmpty(inspectionPowerVo.getTenantId())){
            queryWrapper.eq("tenant_id",inspectionPowerVo.getTenantId());
        }
        //只返回已委托的
        queryWrapper.eq("status",1);
        if(!StringUtils.isEmpty(inspectionPowerVo.getOrderCol())){
            OrderUtil.query(queryWrapper, inspectionPowerVo.getOrderCol(), inspectionPowerVo.getOrder());
        }else{
            queryWrapper.orderByDesc("assign_time");
        }

        return CommonResult.success(inspectionPowerService.page(new Page<InspectionPower>(inspectionPowerVo.getPage(),inspectionPowerVo.getLimit()),queryWrapper));
    }

    @ApiOperation(value = "保存委托单", notes = "保存委托单")
    @ApiImplicitParam(name = "inspectionPower", value = "委托单", paramType = "body", dataType = "InspectionPower")
    @PostMapping("inspectionPower/saveInspectionPower")
    public CommonResult<Boolean> saveInspectionPower(@RequestBody InspectionPower inspectionPower) throws Exception {
        return produceInspectionRecordService.saveInspectionPower(inspectionPower);
    }

    @ApiOperation(value = "批量委托", notes = "批量委托")
    @ApiImplicitParam(name = "ids", value = "委托单", paramType = "body", dataType = "List")
    @PostMapping("inspectionPower/powerOrder")
    public CommonResult<Boolean> powerOrder(@RequestBody List<String> ids) throws Exception {
        return CommonResult.success(produceInspectionRecordService.powerOrder(ids));
    }

    @ApiOperation(value = "批量委托撤回", notes = "批量委托撤回")
    @ApiImplicitParam(name = "id", value = "委托单id", paramType = "body", dataType = "List")
    @PostMapping("inspectionPower/backOutOrder")
    public CommonResult<Boolean> backOutOrder(@RequestBody List<String> ids) throws Exception {
        return CommonResult.success(produceInspectionRecordService.backOutOrder(ids));
    }

    @ApiOperation(value = "删除委托单", notes = "删除委托单")
    @ApiImplicitParam(name = "id", value = "委托单id", paramType = "path", dataType = "String")
    @DeleteMapping("inspectionPower/delete/{id}")
    public CommonResult<Boolean> powerOrder(@PathVariable String id) throws Exception {
        InspectionPower inspectionPower = inspectionPowerService.getById(id);
        if(inspectionPower.getStatus() == IS_STATUS){
            return CommonResult.failed("该委托单已经委托，不能删除");
        }
        return CommonResult.success(inspectionPowerService.removeById(id));
    }


    @ApiOperation(value = "探伤委托单指派", notes = "探伤委托单指派")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "委托单批量委托id", required = true, paramType = "body", dataType = "List"),
            @ApiImplicitParam(name = "assignBy", value = "指给谁", required = true,paramType = "query", dataType = "string")
    })
    @PostMapping("inspectionPower/assignPower")
    public CommonResult<Boolean>  assignPower(@RequestBody List<String> ids , @RequestParam String assignBy){
       return CommonResult.success(produceInspectionRecordService.assignPower(ids,assignBy));
    }
}
