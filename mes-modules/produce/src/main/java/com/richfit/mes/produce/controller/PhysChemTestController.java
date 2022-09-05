package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.PhysChemOrder;
import com.richfit.mes.common.model.produce.TrackItemInspection;
import com.richfit.mes.produce.service.PhyChemTestService;
import com.richfit.mes.produce.service.PhysChemOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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

    @PostMapping("/producePhysChemOrder/save")
    public CommonResult<Boolean> saveOrUpdate(@RequestBody PhysChemOrder physChemOrder){
        return CommonResult.success(physChemOrderService.saveOrUpdate(physChemOrder));
    }

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
        return CommonResult.success(phyChemTestService.page(page,limit,startTime,endTime,trackNo,productName,branchCode,tenantId,isAudit));
    }
}
