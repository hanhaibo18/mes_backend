package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.PhysChemOrder;
import com.richfit.mes.common.model.produce.PhysChemResult;
import com.richfit.mes.common.model.produce.TrackItemInspection;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.service.PhyChemTestService;
import com.richfit.mes.produce.service.PhysChemOrderService;
import com.richfit.mes.produce.service.PhysChemResultService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @PostMapping("/producePhysChemOrder/save")
    public CommonResult<Boolean> saveOrUpdate(@RequestBody PhysChemOrder physChemOrder){
        return CommonResult.success(physChemOrderService.saveOrUpdate(physChemOrder));
    }


    @ApiOperation(value = "分页查询检测工序", notes = "分页查询检测工序")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "branchCode", value = "组织机构编码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "tenantId", value = "组织机构id", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "startTime", value = "开始时间", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "endTime", value = "截至时间", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "trackNo", value = "跟单号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "productName", value = "产品名称", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "drawingNo", value = "图号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "isAudit", value = "审核状态（false、待审核 true、已审核）", required = true, paramType = "query", dataType = "boolean"),
    })
    @GetMapping("/page")
    public CommonResult<IPage<TrackItemInspection>> page(int page, int limit, String startTime, String endTime, String trackNo,String drawingNo, String productName, String branchCode, String tenantId, Boolean isAudit) {
        return CommonResult.success(phyChemTestService.page(page,limit,startTime,endTime,trackNo,productName,drawingNo,branchCode,tenantId,isAudit));
    }


    @ApiOperation(value = "根据跟单工序ids同步试验结果", notes = "根据跟单工序ids同步试验结果")
    @ApiImplicitParam(name = "itemIds", value = "跟单工序ids", required = true,allowMultiple = true, paramType = "body", dataType = "String")
    @PostMapping("/syncResult")
    public void syncResult(@RequestBody List<String> itemIds){
        phyChemTestService.syncResult(itemIds);
    }


    @ApiOperation(value = "根据跟单工序id分页查询试验结果", notes = "根据跟单工序id分页查询试验结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "itemId", value = "跟单工序id", required = true, paramType = "query", dataType = "string"),
    })
    @GetMapping("/queryResultByItemId")
    public CommonResult<IPage<PhysChemResult>> queryResultByItemId(int page,int limit,String itemId){
        QueryWrapper<PhysChemResult> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("item_id",itemId)
                .eq("branch_code", SecurityUtils.getCurrentUser().getBelongOrgId())
                .eq("tenant_id",SecurityUtils.getCurrentUser().getTenantId());
        return CommonResult.success(physChemResultService.page(new Page<>(page, limit), queryWrapper));
    }

}
