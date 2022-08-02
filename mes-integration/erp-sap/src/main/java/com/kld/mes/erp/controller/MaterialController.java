package com.kld.mes.erp.controller;


import com.kld.mes.erp.service.MaterialService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Product;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 从ERP查询物料
 *
 * @Author: mafeng02
 * @Date: 2022/8/1 09:00:00
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

        List<Product> list = materialService.getMaterial(date, erpCode);
        return CommonResult.success(list);

    }


}
