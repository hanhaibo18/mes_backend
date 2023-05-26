package com.kld.mes.wms.controller;

import com.kld.mes.wms.service.ProductToWmsService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.ApplicationResult;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.IngredientApplicationDto;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/25 10:36
 */
@Slf4j
@Api("wms 仓储接口")
@RestController
@RequestMapping("/api/integration/wms")
public class WmsController {

    @Autowired
    ProductToWmsService productToWmsService;

    @ApiOperation(value = "生产交库", notes = "根据合格证将产品信息推送给仓储条码接口")
    @PostMapping("/send_scjk")
    public CommonResult sendJkInfo(@ApiParam(value = "合格证") @RequestBody Certificate cert) {
        log.debug("receive send Scjk request [{}]", cert);
        return productToWmsService.sendRequest(cert);
    }

    @ApiOperation(value = "查询仓储信息", notes = "根据物料编号参数查询仓储信息接口")
    @GetMapping("/queryMaterialCount")
    public CommonResult<Integer> queryMaterialCount(@RequestParam("materialNo") String materialNo) throws Exception {
        return new CommonResult(productToWmsService.queryMaterialCount(materialNo));
    }

    @ApiOperation(value = "配料申请单上传", notes = "配料申请单上传接口")
    @PostMapping("/anApplicationForm")
    public CommonResult<ApplicationResult> anApplicationForm(@RequestBody IngredientApplicationDto ingredientApplicationDto) throws Exception {
        return new CommonResult(productToWmsService.anApplicationForm(ingredientApplicationDto));
    }
}
