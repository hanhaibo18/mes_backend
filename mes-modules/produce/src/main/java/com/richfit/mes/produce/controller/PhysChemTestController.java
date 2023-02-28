package com.richfit.mes.produce.controller;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.provider.MaterialInspectionServiceClient;
import com.richfit.mes.produce.service.CodeRuleService;
import com.richfit.mes.produce.service.PhyChemTestService;
import com.richfit.mes.produce.service.PhysChemResultService;
import com.richfit.mes.produce.utils.Code;
import freemarker.template.TemplateException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


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
    @Autowired
    private MaterialInspectionServiceClient materialInspectionServiceClient;
    @Autowired
    private CodeRuleService codeRuleService;

    @ApiOperation(value = "创建或修改理化检测委托单", notes = "创建或修改理化检测委托单")
    @ApiImplicitParam(name = "physChemOrderInner", value = "委托单", paramType = "body", dataType = "physChemOrderInner")
    @PostMapping("/producePhysChemOrder/save")
    public CommonResult<Boolean> save(@RequestBody PhysChemOrderInner physChemOrderInner) throws Exception{
        //委托单填写数据校验
        phyChemTestService.checkOrderInfo(physChemOrderInner);
        //力学性能参数集合
        List<PhysChemOrderImpactDto> impacts = physChemOrderInner.getImpacts();
        //要保存的数据
        List<PhysChemOrderInner> physChemOrderInners = new ArrayList<>();
        //新增委托（需要生产委托单号、报告号）
        if(!StringUtils.isNullOrEmpty(physChemOrderInner.getStatus()) && physChemOrderInner.getStatus().equals("1") && StringUtils.isNullOrEmpty(physChemOrderInner.getOrderNo())){
            //获取号
            String orderNo = codeRuleService.gerCode("order_no", null, null, SecurityUtils.getCurrentUser().getTenantId(), physChemOrderInner.getBranchCode()).getCurValue();
            String reportNo = codeRuleService.gerCode("m_report_no", null, null, SecurityUtils.getCurrentUser().getTenantId(), physChemOrderInner.getBranchCode()).getCurValue();
            physChemOrderInner.setOrderNo(orderNo);
            physChemOrderInner.setReportNo(reportNo);
        }
        //合并数据
        if(!ObjectUtil.isEmpty(impacts)){
            for (PhysChemOrderImpactDto impact : impacts) {
                PhysChemOrderInner addNew = new PhysChemOrderInner();
                BeanUtils.copyProperties(physChemOrderInner,addNew,new String[]{"id"});
                addNew.setForceImpactTemp(impact.getForceImpactTemp());
                addNew.setForceImpactGap(impact.getForceImpactGap());
                addNew.setForceImpactDirection(impact.getForceImpactDirection());
                if(StringUtils.isNullOrEmpty(physChemOrderInner.getId())){
                    addNew.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                }
                addNew.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                physChemOrderInners.add(addNew);
            }
        }

        if(physChemOrderInners.size() == 0){
            physChemOrderInners.add(physChemOrderInner);
        }
        return phyChemTestService.saveOrder(physChemOrderInners);
    }

    @ApiOperation(value = "委托任务列表", notes = "委托任务列表")
    @ApiImplicitParam(name = "phyChemTaskVo", value = "检验任务查询实体", paramType = "body", dataType = "PhyChemTaskVo")
    @PostMapping("/page")
    public CommonResult page(@RequestBody PhyChemTaskVo phyChemTaskVo) {
        return CommonResult.success(phyChemTestService.page(phyChemTaskVo));
    }

    @ApiOperation(value = "批量委托", notes = "批量委托")
    @ApiImplicitParam(name = "groupIds", value = "要委托的委托组id集合", required = true, paramType = "body", dataType = "list")
    @PostMapping("/changeOrderStatus")
    public CommonResult changeOrderStatus(@RequestBody JSONObject jsonObject) throws Exception {
        return CommonResult.success(phyChemTestService.changeOrderStatus(jsonObject));
    }


    @ApiOperation(value = "根据报告号同步试验结果", notes = "根据报告号同步试验结果")
    @ApiImplicitParam(name = "reportNos", value = "报告号", required = true, paramType = "body", dataType = "list")
    @PostMapping("/syncResult")
    public CommonResult<Boolean> syncResult(@RequestBody List<String> reportNos){
        return phyChemTestService.syncResult(reportNos);
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
    public void exportExcel(HttpServletResponse response, String orderNo) throws GlobalException {
        phyChemTestService.exportExcel(response,orderNo);
    }

    @ApiOperation(value = "已同步理化检测委托单审核", notes = "已同步理化检测委托单审核")
    @PostMapping("/auditSnyPhysChemOrder")
    public CommonResult<Boolean> auditPhysChemOrder(@RequestBody JSONObject jsonObject){
        List<String> reportNos = JSON.parseArray(JSONObject.toJSONString(jsonObject.get("reportNos")), String.class);
        return materialInspectionServiceClient.auditSnyPhysChemOrder(reportNos, jsonObject.getString("isAudit"),SecurityUtils.getCurrentUser().getUsername());
    }

    @ApiOperation(value = "委托单合不合格判定", notes = "委托单合不合格判定")
    @PostMapping("/isStandard")
    public CommonResult<Boolean> isStandard(@RequestBody JSONObject jsonObject){
        List<String> reportNos = JSON.parseArray(JSONObject.toJSONString(jsonObject.get("reportNos")), String.class);
        return materialInspectionServiceClient.isStandard(reportNos, jsonObject.getString("isStandard"),SecurityUtils.getCurrentUser().getUsername());
    }

    @ApiOperation(value = "委托单复制", notes = "委托单复制")
    @ApiImplicitParam(name = "groupId", value = "要复制的委托单组id", required = true, paramType = "query", dataType = "String")
    @GetMapping("/copyOrder")
    public CommonResult<Boolean> copyOrder(@RequestParam("groupId") String groupId) throws GlobalException {
        return CommonResult.success(phyChemTestService.copyOrder(groupId));
    }

}
