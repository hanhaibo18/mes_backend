package com.richfit.mes.produce.controller;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.InspectionPower;
import com.richfit.mes.produce.entity.ProduceInspectionRecordDto;
import com.richfit.mes.produce.entity.quality.InspectionPowerVo;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.ProduceInspectionRecordService;
import com.richfit.mes.produce.service.quality.InspectionPowerService;
import freemarker.template.TemplateException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;


/**
 * 质量管理模块接口
 *
 * @Author: renzewen
 * @Date: 2022/8/22 13:10
 */
@Slf4j
@Api(tags = "质量管理(探伤管理)")
@RestController
@RequestMapping("/api/produce/inspectionRecord")
public class ProduceInspectionRecordController extends BaseController {


    private final static int IS_STATUS = 1;

    @Autowired
    private ProduceInspectionRecordService produceInspectionRecordService;
    @Autowired
    private InspectionPowerService inspectionPowerService;
    @Autowired
    private SystemServiceClient systemServiceClient;


    /**
     * ***
     * 分页查询待探伤工序
     *
     * @param inspectionPowerVo
     * @return
     */
    @ApiOperation(value = "分页查询待探伤工序", notes = "分页查询待探伤工序")
    @ApiImplicitParam(name = "探伤任务查询类VO", value = "InspectionPowerVo", paramType = "body", dataType = "InspectionPowerVo")
    @GetMapping("/page")
    public CommonResult<IPage<InspectionPower>> page(InspectionPowerVo inspectionPowerVo) {
        return CommonResult.success(produceInspectionRecordService.page(inspectionPowerVo));
    }

    /**
     * ***
     * 分页查询待探伤工序
     *
     * @return
     */
    @ApiOperation(value = "分页查询探伤记录列表", notes = "分页查询探伤记录列表")
    @ApiImplicitParam(name = "探伤任务查询类VO", value = "InspectionPowerVo", paramType = "body", dataType = "InspectionPowerVo")
    @PostMapping("/page/queryAuditRecord")
    public CommonResult<Object> queryAuditRecord(@RequestBody InspectionPowerVo inspectionPowerVo) {
        return CommonResult.success(produceInspectionRecordService.queryRecordByAuditBy(inspectionPowerVo));
    }


    @ApiOperation(value = "保存探伤记录", notes = "保存探伤记录")
    @PostMapping("/save")
    public CommonResult saveRecord(@RequestBody ProduceInspectionRecordDto produceInspectionRecordDto) throws Exception {
        return CommonResult.success(produceInspectionRecordService.saveRecords(produceInspectionRecordDto));
    }

    @ApiOperation(value = "根据探伤任务id查询探伤记录列表", notes = "根据探伤任务id查询探伤记录列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "powerId", value = "探伤任务id", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "isAudit", value = "审核状态", paramType = "query", dataType = "string"),
    })
    @GetMapping("/queryRecordByPowerId")
    public CommonResult queryRecordByPowerId(String powerId, String isAudit) {
        return CommonResult.success(produceInspectionRecordService.queryLastInfoByPowerId(powerId, isAudit, "0"));
    }

    @ApiOperation(value = "根据探伤任务id查询最近一条探伤记录", notes = "根据探伤任务id查询最近一条探伤记录")
    @ApiImplicitParam(name = "powerId", value = "探伤任务id", required = true, paramType = "path", dataType = "string")
    @GetMapping("/queryLastInfoByPowerId/{powerId}")
    public CommonResult queryLastInfoByPowerId(@PathVariable String powerId) {
        return CommonResult.success(produceInspectionRecordService.queryLastInfoByPowerId(powerId));
    }

    @ApiOperation(value = "根据记录id查询探伤记录详情", notes = "根据记录id查询探伤记录详情")
    @ApiImplicitParam(name = "id", value = "记录id", required = true, paramType = "path", dataType = "string")
    @GetMapping("/queryInfoByRecordId/{id}")
    public CommonResult<Object> queryInfoByRecordId(@PathVariable String id) {
        return CommonResult.success(produceInspectionRecordService.queryInfoByRecordId(id));
    }

    @ApiOperation(value = "撤回记录", notes = "撤回记录")
    @ApiImplicitParam(name = "powerIds", value = "探索任务ids", required = true, dataType = "List", paramType = "body")
    @PostMapping("/backoutRecord")
    public CommonResult<Boolean> backoutRecord(@RequestBody List<String> powerIds) {
        return CommonResult.success(produceInspectionRecordService.backoutRecord(powerIds));
    }

    @ApiOperation(value = "审核提交探伤记录", notes = "审核提交探伤记录")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "itemId", value = "工序id", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "remark", value = "探伤备注", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "flawDetection", value = "探伤结果(0,1)", required = true, paramType = "query", dataType = "Integer"),
            @ApiImplicitParam(name = "tempType", value = "探伤记录模板类型", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "recordNo", value = "探伤记录编号", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "checkBy", value = "探伤检验人", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "auditBy", value = "探伤审核人", paramType = "query", dataType = "string")
    })
    @GetMapping("/auditSubmitRecord")
    public CommonResult auditSubmitRecord(String itemId, String flawDetectioRemark, Integer flawDetection, String tempType, String recordNo, String checkBy, String auditBy) {
        return CommonResult.success(produceInspectionRecordService.auditSubmitRecord(itemId, flawDetectioRemark, flawDetection, tempType, recordNo, checkBy, auditBy));
    }

    @ApiOperation(value = "报告预览", notes = "报告预览")
    @ApiImplicitParam(name = "id", value = "探伤记录id", paramType = "query", dataType = "string")
    @GetMapping("/exoprtReport")
    public void exoprtReport(HttpServletResponse response, String id) throws IOException, TemplateException, GlobalException {
        produceInspectionRecordService.exoprtReport(response, id);
    }

    @ApiOperation(value = "探伤任务开工", notes = "探伤任务开工")
    @ApiImplicitParam(name = "ids", value = "探索任务ids", required = true, dataType = "List", paramType = "body")
    @PostMapping("/startsWork")
    public CommonResult<Boolean> startsWork(@RequestBody List<String> ids) {
        return CommonResult.success(produceInspectionRecordService.startsWork(ids));
    }

    @ApiOperation(value = "探伤记录审核", notes = "探伤记录审核")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "探伤记录id", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "tempType", value = "模板类型", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "isAudit", value = "审核状态", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "auditRemark", value = "审核备注", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "inspector", value = "指派人", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/auditRecord")
    public CommonResult<Boolean> auditByRecordId(String id, String tempType, String isAudit, String auditRemark, String inspector, String checkBranch) {
        return CommonResult.success(produceInspectionRecordService.auditByRecord(id, tempType, isAudit, auditRemark, inspector, checkBranch));
    }

    @ApiOperation(value = "分页查询委托单", notes = "分页查询委托单")
    @ApiImplicitParam(name = "inspectionPowerVo", value = "委托单", paramType = "body", dataType = "InspectionPowerVo")
    @PostMapping("/inspectionPower/page")
    public CommonResult<IPage> queryPowerOrderPage(@RequestBody InspectionPowerVo inspectionPowerVo) {
        return CommonResult.success(produceInspectionRecordService.queryPowerOrderPage(inspectionPowerVo));
    }

    @ApiOperation(value = "分页查询委托单", notes = "分页查询委托单")
    @ApiImplicitParam(name = "inspectionPowerVo", value = "委托单", paramType = "body", dataType = "InspectionPowerVo")
    @PostMapping("/inspectionPower/pageByCompany")
    public CommonResult<IPage> queryPowerOrderPageByCompany(@RequestBody InspectionPowerVo inspectionPowerVo) {
        return CommonResult.success(produceInspectionRecordService.queryPowerOrderPageByCompany(inspectionPowerVo));
    }


    @ApiOperation(value = "批量保存委托单", notes = "批量保存委托单")
    @ApiImplicitParam(name = "inspectionPowers", value = "委托单", paramType = "body", dataType = "List")
    @PostMapping("inspectionPower/saveInspectionPowers")
    public CommonResult<Boolean> saveInspectionPowers(@RequestBody JSONObject jsonObject) throws Exception {
        List<String> itemIds = JSON.parseArray(JSONObject.toJSONString(jsonObject.get("itemIds")), String.class);
        List<InspectionPower> inspectionPowers = JSON.parseArray(JSONObject.toJSONString(jsonObject.get("inspectionPowers")), InspectionPower.class);
        return produceInspectionRecordService.saveInspectionPowers(itemIds,inspectionPowers);
    }

    @ApiOperation(value = "保存单个委托单", notes = "保存单个委托单")
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
    public CommonResult<Boolean> backOutOrder(@RequestBody JSONObject jsonObject){
        List<String> ids = JSON.parseArray(JSONObject.toJSONString(jsonObject.get("ids")), String.class);
        String backRemark = jsonObject.getString("backRemark");
        return CommonResult.success(produceInspectionRecordService.backOutOrder(ids,backRemark));
    }

    @ApiOperation(value = "删除委托单", notes = "删除委托单")
    @ApiImplicitParam(name = "id", value = "委托单id", paramType = "path", dataType = "String")
    @DeleteMapping("inspectionPower/delete/{id}")
    public CommonResult<Boolean> powerOrder(@PathVariable String id) throws Exception {
        InspectionPower inspectionPower = inspectionPowerService.getById(id);
        if (inspectionPower.getStatus() == IS_STATUS) {
            return CommonResult.failed("该委托单已经委托，不能删除");
        }
        //同工序有开工的委托 不能删除
        String itemId = inspectionPower.getItemId();
        if (!StringUtils.isEmpty(itemId)) {
            QueryWrapper<InspectionPower> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("item_id", itemId);
            List<InspectionPower> list = inspectionPowerService.list();
            List<InspectionPower> isDoingList = list.stream().filter(item -> "1".equals(item.getIsDoing())).collect(Collectors.toList());
            if (isDoingList.size() > 0) {
                return CommonResult.failed("关联跟单工序已经开工，不能删除委托单");
            }
        }

        return CommonResult.success(inspectionPowerService.removeById(id));
    }

    @ApiOperation(value = "导出委托单", notes = "导出委托单信息")
    @PostMapping("/inspectionPower/export_excel")
    public void exportExcel(@RequestBody InspectionPowerVo inspectionPowerVo, HttpServletResponse rsp) {
        produceInspectionRecordService.exportExcel(inspectionPowerVo, rsp);
    }

    @ApiOperation(value = "导入委托单", notes = "导入委托单")
    @PostMapping("/inspectionPower/importPowerInfosExcel")
    public CommonResult importPowerInfosExcel(MultipartFile file, String branchCode) {
        return produceInspectionRecordService.importPowerInfosExcel(file, branchCode);
    }


    /*@ApiOperation(value = "探伤委托单指派", notes = "探伤委托单指派")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "ids", value = "委托单批量委托id", required = true, paramType = "body", dataType = "List"),
            @ApiImplicitParam(name = "assignBy", value = "指给谁", required = true,paramType = "query", dataType = "string")
    })
    @PostMapping("inspectionPower/assignPower")
    public CommonResult<Boolean>  assignPower(@RequestBody List<String> ids , @RequestParam String assignBy){
       return CommonResult.success(produceInspectionRecordService.assignPower(ids,assignBy));
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
    }*/
}


