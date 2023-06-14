package com.richfit.mes.produce.controller.wms;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.produce.service.CertificateService;
import com.richfit.mes.produce.service.erp.WorkHoursService;
import com.richfit.mes.produce.service.wms.InventoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 功能描述:工时管理
 *
 * @Author: zhiqiang.lu
 * @Date: 2023/05/26 16:27
 **/
@Slf4j
@Api(value = "合格证管理", tags = {"合格证管理"})
@RestController
@RequestMapping("/api/produce/wms/inventory")
public class InventoryController {

    @Autowired
    CertificateService certificateService;

    @Autowired
    InventoryService inventoryService;

    @ApiOperation(value = "生产人库", notes = "推送工时")
    @PostMapping("/handOver")
    public CommonResult<Object> handOver(@ApiParam(value = "合格证", required = true) @RequestBody List<Certificate> certificateList) {
        StringBuilder message = new StringBuilder();
        for (Certificate certificate : certificateList) {
            try {
                inventoryService.handOver(certificateList);
            } catch (Exception e) {
                message.append(e.getMessage());
            }
        }
        return CommonResult.success(message.toString());
    }
}
