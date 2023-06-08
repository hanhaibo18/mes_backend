package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.base.entity.ReceiptDTO;
import com.richfit.mes.base.service.BaseProductReceiptService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.BaseProductReceipt;
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
 * @Description bom交接Controller
 */
@Slf4j
@Api(value = "产品交接", tags = {"产品交接"})
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
    public CommonResult<List<BaseProductReceiptExtend>> queryDetail(@ApiParam(value = "查询条件") @RequestParam(value = "connectId", required = true) String connectId,
                                                                    @ApiParam(value = "交接单数量") @RequestParam(value = "number", required = true) Integer number,
                                                                    @ApiParam(value = "工作号") @RequestParam(value = "workNo", required = true) String workNo,
                                                                    @ApiParam(value = "图号") @RequestParam(value = "drawNo", required = true) String drawNo,
                                                                    @ApiParam(value = "branchCode") @RequestParam(value = "branchCode", required = true) String branchCode,
                                                                    @ApiParam(value = "tenantId") @RequestParam(value = "tenantId", required = true) String tenandId
    ) {
        return CommonResult.success(baseProductReceiptService.queryReceiptDetailInfo(connectId, number, workNo, drawNo, branchCode, tenandId));
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
}
