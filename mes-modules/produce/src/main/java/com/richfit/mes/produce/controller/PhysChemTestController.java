package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.service.PhyChemTestService;
import com.richfit.mes.produce.service.PhysChemResultService;
import freemarker.template.TemplateException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;


/**
 * @author renzewen
 * @Description 理化试验接口
 */
@Slf4j
@Api(tags = "质量管理(理化检测)")
@RestController
@RequestMapping("/api/produce/producePhysChemTest")
public class PhysChemTestController extends BaseController {

    @Autowired
    private PhyChemTestService phyChemTestService;
    @Autowired
    private PhysChemResultService physChemResultService;

    @ApiOperation(value = "创建或修改理化检测委托单", notes = "创建或修改理化检测委托单")
    @ApiImplicitParam(name = "physChemOrderInner", value = "委托单", paramType = "body", dataType = "physChemOrderInner")
    @PostMapping("/producePhysChemOrder/save")
    public CommonResult<Boolean> save(@RequestBody PhysChemOrderInner physChemOrderInner) throws Exception{
        //力学性能参数集合
        List<PhysChemOrderImpactDto> impacts = physChemOrderInner.getImpacts();
        if(StringUtils.isNullOrEmpty(physChemOrderInner.getId())){
            physChemOrderInner.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
        }
        physChemOrderInner.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
        return phyChemTestService.saveOrder(physChemOrderInner);
    }

    @ApiOperation(value = "委托任务列表", notes = "委托任务列表")
    @ApiImplicitParam(name = "phyChemTaskVo", value = "检验任务查询实体", paramType = "body", dataType = "PhyChemTaskVo")
    @PostMapping("/page")
    public CommonResult page(@RequestBody PhyChemTaskVo phyChemTaskVo) {
        return CommonResult.success(phyChemTestService.page(phyChemTaskVo));
    }


    @ApiOperation(value = "根据报告号同步试验结果", notes = "根据报告号同步试验结果")
    @ApiImplicitParam(name = "reportNos", value = "报告号", required = true, paramType = "body", dataType = "list")
    @PostMapping("/syncResult")
    public void syncResult(@RequestBody List<String> reportNos){
        phyChemTestService.syncResult(reportNos);
    }


   @ApiOperation(value = "根据报告号查询实验结果", notes = "根据报告号查询实验结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "reportNo", value = "报告号", required = true, paramType = "query", dataType = "string"),
    })
    @GetMapping("/queryResultByReportNo")
    public CommonResult<IPage<PhysChemResult>> queryResultByReportNo(int page,int limit,String reportNo){
        QueryWrapper<PhysChemResult> queryWrapper = new QueryWrapper<>();
           queryWrapper.eq("report_no",reportNo);
        return CommonResult.success( physChemResultService.page(new Page<>(page, limit), queryWrapper));
    }

    @ApiOperation(value = "理化检测报告导出", notes = "理化检测报告导出")
    @ApiImplicitParam(name = "reportNo", value = "报告号", required = true, paramType = "query", dataType = "String")
    @GetMapping("/exportReport")
    public void exoprtReport(HttpServletResponse response, String reportNo) throws IOException, TemplateException, GlobalException {
        phyChemTestService.exoprtReport(response,reportNo);
    }

    @ApiOperation(value = "理化检测委托单导出", notes = "理化检测委托单导出")
    @ApiImplicitParam(name = "orderNo", value = "委托单号", required = true, paramType = "query", dataType = "String")
    @GetMapping("/exportExcel")
    public void exportExcel(HttpServletResponse response, String orderNo) throws IOException, TemplateException, GlobalException {
        phyChemTestService.exportExcel(response,orderNo);
    }

}
