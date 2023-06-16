package com.richfit.mes.base.controller.wms;

import com.richfit.mes.base.service.wms.MaterialService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Product;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 功能描述:库存管理
 *
 * @Author: zhiqiang.lu
 * @Date: 2023/05/26 16:27
 **/
@Slf4j
@Api(value = "物料管理", tags = {"物料管理"})
@RestController
@RequestMapping("/api/produce/wms/material")
public class MaterialController {

    @Autowired
    MaterialService materialService;

    @ApiOperation(value = "根据勾选数据同步到wms", notes = "根据勾选数据同步到wms")
    @PostMapping("/save_wms_sync")
    public CommonResult<String> saveWmsSync(@RequestBody List<Product> products) {
        materialService.sync(products);
        return CommonResult.success("操作完成");
    }
}
