package com.kld.mes.wms.controller;

import com.kld.mes.wms.service.ProductToWmsService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.Certificate;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public CommonResult<Boolean> sendJkInfo(@ApiParam(value = "合格证") @RequestBody Certificate cert) {

        log.debug("receive send Scjk request [{}]", cert);
        boolean b = productToWmsService.sendRequest(cert);
        log.debug("after send Scjk request,send result:[{}]", b);

        return new CommonResult(b);

    }
}
