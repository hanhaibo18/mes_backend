package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.base.entity.ReceiptDTO;
import com.richfit.mes.base.service.BaseProductReceiptService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.BaseProductReceipt;
import com.richfit.mes.common.model.base.BaseProductReceiptDetail;
import com.richfit.mes.common.model.base.BaseProductReceiptExtend;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author wangchenyu
 * @Description BOM交接单据Controller
 */
@Slf4j
@Api(value = "BOM交接单据", tags = {"BOM交接单据"})
@RestController
@RequestMapping("/api/base/connect")
public class BaseProductReceiptController extends BaseController {


    @Autowired
    private BaseProductReceiptService baseProductReceiptService;

    @ApiOperation(value = "交接单据列表")
    @PostMapping("/page")
    public CommonResult<Page<BaseProductReceipt>> query(@ApiParam(value = "查询条件") @RequestBody ReceiptDTO receiptDTO) {
        return CommonResult.success(baseProductReceiptService.queryReceiptInfo(receiptDTO));
    }

    @ApiOperation(value = "交接单据详情列表")
    @GetMapping("/page/detail")
    public CommonResult<List<BaseProductReceiptExtend>> queryDetail(@ApiParam(value = "查询条件") @RequestParam(value = "connectId") String connectId,
                                                                    @ApiParam(value = "交接单数量") @RequestParam(value = "number") Integer number,
                                                                    @ApiParam(value = "工作号") @RequestParam(value = "workNo") String workNo,
                                                                    @ApiParam(value = "图号") @RequestParam(value = "drawNo") String drawNo,
                                                                    @ApiParam(value = "branchCode") @RequestParam(value = "branchCode") String branchCode,
                                                                    @ApiParam(value = "tenantId") @RequestParam(value = "tenantId") String tenantId,
                                                                    @ApiParam(value = "区分编辑回显还是详情::1：编辑；2：详情") @RequestParam(value = "operate", required = false) String operate
    ) {
        return CommonResult.success(baseProductReceiptService.queryReceiptDetailInfo(connectId, number, workNo, drawNo, branchCode, tenantId, operate));
    }

    @ApiOperation(value = "新增交接单信息")
    @PostMapping("/insert")
    public CommonResult insertReceipt(@ApiParam(value = "新增交接单据") @RequestBody ReceiptDTO receiptDTO) {
        baseProductReceiptService.insertReceipt(receiptDTO);
        return CommonResult.success("添加成功");
    }

    @ApiOperation(value = "编辑交接单信息")
    @PostMapping("/edit")
    public CommonResult editReceipt(@ApiParam(value = "编辑交接单据") @RequestBody ReceiptDTO receiptDTO) {
        baseProductReceiptService.editReceipt(receiptDTO);
        return CommonResult.success("修改成功");
    }

    @ApiOperation(value = "物料确认接收")
    @PostMapping("/receive")
    public CommonResult receive(@ApiParam(value = "编辑交接单据") @RequestBody ReceiptDTO receiptDTO) {
        baseProductReceiptService.receive(receiptDTO);
        return CommonResult.success("接收成功");
    }

    @ApiOperation(value = "物料单据拒收")
    @GetMapping("/rejection")
    public CommonResult rejection(@ApiParam(value = "connectId") @RequestParam(value = "connectId") String connectId) {
        baseProductReceiptService.rejection(connectId);
        return CommonResult.success("已拒收");
    }

    @ApiOperation(value = "返回待接收")
    @GetMapping("/returnBack")
    public CommonResult returnBack(@ApiParam(value = "connectId") @RequestParam(value = "connectId") String connectId) {
        baseProductReceiptService.returnBack(connectId);
        return CommonResult.success("返回接收成功");
    }

    @ApiOperation(value = "物料接收列表")
    @PostMapping("/receive/page")
    public CommonResult<IPage<BaseProductReceipt>> receivePage(@ApiParam(value = "查询条件") @RequestBody ReceiptDTO receiptDTO) {
        return CommonResult.success(baseProductReceiptService.receivePage(receiptDTO));
    }

    @ApiOperation(value = "物料接收明细列表")
    @GetMapping("/receive/detail")
    public CommonResult<List<BaseProductReceiptDetail>> receiveDetail(@ApiParam(value = "工作号") @RequestParam(value = "workNo") String workNo,
                                                                      @ApiParam(value = "图号") @RequestParam(value = "drawNo") String drawNo,
                                                                      @ApiParam(value = "branchCode", required = false) @RequestParam(value = "branchCode", required = false) String branchCode,
                                                                      @ApiParam(value = "tenantId", required = false) @RequestParam(value = "tenantId", required = false) String tenantId) {
        return CommonResult.success(baseProductReceiptService.receiveDetail(workNo, drawNo, branchCode, tenantId));
    }
}
