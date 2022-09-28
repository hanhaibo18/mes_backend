package com.kld.mes.erp.controller;


import com.kld.mes.erp.service.MaterialService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Product;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 从ERP查询物料
 *
 * @Author: fengxy
 * @Date: 2022年9月9日15:22:06
 */
@Slf4j
@Api(value = "从ERP查询物料", tags = {"从ERP查询物料"})
@RestController
@RequestMapping("/api/integration/erp/material")
public class MaterialController {

    @Autowired
    MaterialService materialService;

    @ApiOperation(value = "从ERP查询物料库存", notes = "从ERP查询物料库存")
    @GetMapping("/getMaterial")
    public CommonResult<List<Product>> getMaterial(@ApiParam(value = "日期") @RequestParam String date,
                                                   @ApiParam(value = "erp代号") @RequestParam String erpCode) {


        return CommonResult.success(materialService.getMaterial(date, erpCode));

    }


}
