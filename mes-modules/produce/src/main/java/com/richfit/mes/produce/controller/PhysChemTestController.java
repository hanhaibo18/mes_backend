package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.PhysChemOrder;
import com.richfit.mes.common.model.produce.PhysChemResult;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.phyChemTestVo.PhyChemTaskVo;
import com.richfit.mes.produce.service.PhyChemTestService;
import com.richfit.mes.produce.service.PhysChemOrderService;
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
    private PhysChemOrderService physChemOrderService;
    @Autowired
    private PhyChemTestService phyChemTestService;
    @Autowired
    private PhysChemResultService physChemResultService;

    @ApiOperation(value = "创建或修改理化检测委托单", notes = "创建或修改理化检测委托单")
    @ApiImplicitParam(name = "physChemOrder", value = "委托单", paramType = "body", dataType = "PhysChemOrder")
    @PostMapping("/producePhysChemOrder/save")
    public CommonResult<Boolean> save(@RequestBody PhysChemOrder physChemOrder) throws Exception{
        return phyChemTestService.save(physChemOrder);
    }

    @ApiOperation(value = "查询委托单列表", notes = "查询委托单列表")
    @ApiImplicitParam(name = "phyChemTaskVo", value = "检验任务查询实体", paramType = "body", dataType = "PhyChemTaskVo")
    @PostMapping("/producePhysChemOrder/selectOrderList")
    public CommonResult selectOrderList(@RequestBody PhyChemTaskVo phyChemTaskVo){
        return CommonResult.success(physChemOrderService.selectOrderList(phyChemTaskVo));
    }

    @ApiOperation(value = "分公司发送委托单接口", notes = "分公司发送委托单接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "委托单id", paramType = "query", dataType = "String")
    })
    @GetMapping("/producePhysChemOrder/sendOrderToZj")
    public CommonResult sendOrderToZj(String id){
        return CommonResult.success(phyChemTestService.sendOrderToZj(id));
    }

    @ApiOperation(value = "材料质检部确认或者拒绝委托单接口", notes = "材料质检部确认或者拒绝委托单接口")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "orderId", value = "委托单id", paramType = "query", dataType = "String"),
            @ApiImplicitParam(name = "status", value = "委托单状态（2、确认,3、拒绝）", paramType = "query", dataType = "String")
    })
    @GetMapping("/producePhysChemOrder/zJConfirm")
    public CommonResult zJConfirm(String id,String status){
        return CommonResult.success(phyChemTestService.zJConfirm(id,status));
    }


    @ApiOperation(value = "分页查询检测任务列表", notes = "分页查询检测任务列表")
    @ApiImplicitParam(name = "phyChemTaskVo", value = "检验任务查询实体", paramType = "body", dataType = "PhyChemTaskVo")
    @PostMapping("/page")
    public CommonResult<IPage<PhysChemOrder>> page(@RequestBody PhyChemTaskVo phyChemTaskVo) {
        return CommonResult.success(phyChemTestService.page(phyChemTaskVo));
    }


    @ApiOperation(value = "根据炉批号同步试验结果", notes = "根据炉批号同步试验结果")
    @ApiImplicitParam(name = "batchNos", value = "炉批号", required = true, paramType = "body", dataType = "list")
    @PostMapping("/syncResult")
    public void syncResult(@RequestBody List<String> batchNos){
        phyChemTestService.syncResult(batchNos);
    }


   @ApiOperation(value = "根据炉批号查询实验结果", notes = "根据炉批号查询实验结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "branchCode", value = "组织结构code", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "batchNo", value = "跟单炉批号", required = true, paramType = "query", dataType = "string"),
    })
    @GetMapping("/queryResultByBatchNo")
    public CommonResult<IPage<PhysChemResult>> queryResultByItemId(int page,int limit,String batchNo,String branchCode){
        QueryWrapper<PhysChemResult> queryWrapper = new QueryWrapper<>();
           queryWrapper.eq("batch_no",batchNo)
                   .eq("branch_code", branchCode)
                   .eq("tenant_id",SecurityUtils.getCurrentUser().getTenantId());
        return CommonResult.success(physChemResultService.page(new Page<>(page, limit), queryWrapper));
    }

    @ApiOperation(value = "理化检测报告导出", notes = "理化检测报告导出")
    @ApiImplicitParam(name = "hid", value = "跟单id", required = true, paramType = "query", dataType = "String")
    @GetMapping("/exportReport")
    public void exoprtReport(HttpServletResponse response, String hid) throws IOException, TemplateException, GlobalException {
        phyChemTestService.exoprtReport(response,hid);
    }

}
