package com.kld.mes.erp.controller;

import com.alibaba.fastjson.JSON;
import com.kld.mes.erp.entity.router.Zc80Ppif026Response;
import com.kld.mes.erp.service.RouterService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.base.Sequence;
import io.micrometer.shaded.org.pcollections.PSequence;
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
@Api(value = "ERP接口封装", tags = {"工艺推送接口"})
@RestController
@RequestMapping("/api/integration/erp/router")
public class RouterController {

    @Autowired
    RouterService routerService;

    @ApiOperation(value = "工艺推送接口", notes = "根据工艺向ERP推送")
    @PostMapping("/push")
    public CommonResult routerPush(@ApiParam(value = "工艺列表") @RequestBody List<Router> routers) throws Exception {
        Zc80Ppif026Response zc80Ppif026Response = routerService.push(routers);
        if ("S".equals(zc80Ppif026Response.getEType())) {
            return CommonResult.success(zc80Ppif026Response.getEMes());
        } else {
            return CommonResult.failed(zc80Ppif026Response.getEMes());
        }
    }
}
