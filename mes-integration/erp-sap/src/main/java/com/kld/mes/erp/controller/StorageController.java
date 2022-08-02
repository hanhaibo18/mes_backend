package com.kld.mes.erp.controller;


import com.kld.mes.erp.service.StorageService;
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
 * 向ERP推送工艺
 *
 * @Author: mafeng02
 * @Date: 2022/8/1 09:00:00
 */
@Slf4j
@Api(value = "从ERP查询物料库存", tags = {"从ERP查询物料库存"})
@RestController
@RequestMapping("/api/integration/erp/storage")
public class StorageController {

    @Autowired
    StorageService storageService;

    @ApiOperation(value = "从ERP查询物料库存", notes = "从ERP查询物料库存")
    @GetMapping("/getStorage")
    public CommonResult<List<Product>> getStorage(@ApiParam(value = "物料号") @RequestParam String materialNos,
                                                  @ApiParam(value = "erp代号") @RequestParam String erpCode) {

        List<Product> list = storageService.getStorage(materialNos.split(","), erpCode);
        return CommonResult.success(list);

    }


}
