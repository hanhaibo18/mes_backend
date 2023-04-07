package com.kld.mes.pdm.controller;

import com.kld.mes.pdm.service.ProductToBsWmsService;
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
 * @author wcy
 * @date 2023/3/15 14:55
 */
@Slf4j
@Api("北石dpm系统集成接口")
@RestController
@RequestMapping("/api/integration/pdm")
public class PdmsBsController {

    @Autowired
    private ProductToBsWmsService productToBsWmsService;

    @ApiOperation(value = "北石配料申请单上传", notes = "配料申请单上传接口")
    @PostMapping("/anApplicationForm")
    public CommonResult<ApplicationResult> anApplicationForm(@RequestBody IngredientApplicationDto ingredientApplicationDto) throws Exception {
        return new CommonResult(productToBsWmsService.anApplicationForm(ingredientApplicationDto));
    }

    @ApiOperation(value = "查询仓储信息", notes = "根据物料编号参数查询仓储信息接口")
    @GetMapping("/queryMaterialCount")
    public CommonResult<Integer> queryMaterialCount(@RequestParam("materialNo") String materialNo) throws Exception {
        return new CommonResult(productToBsWmsService.queryMaterialCount(materialNo));
    }

    @ApiOperation(value = "生产交库", notes = "根据合格证将产品信息推送给仓储条码接口")
    @PostMapping("/send_scjk")
    public CommonResult<Boolean> sendJkInfo(@ApiParam(value = "合格证") @RequestBody Certificate cert) {

        log.debug("receive send bsScjk request [{}]", cert);
        boolean b = productToBsWmsService.sendRequest(cert);
        log.debug("after send bsScjk request,send result:[{}]", b);

        return new CommonResult(b);

    }
}
