package com.tc.mes.plm.controller;

import com.richfit.mes.common.model.produce.Notice;
import com.tc.mes.plm.common.Result;
import com.tc.mes.plm.service.impl.ProductToPdmServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api("PDM plm-tc接口")
@RestController
//TODO  这个是什么接口  是否要进行细化
@RequestMapping("/api/integration/plm")
public class ProductToPdmController {

    @Autowired
    private ProductToPdmServiceImpl productToPdmService;

    @ApiOperation(value = "生产排产单同步", notes = "MES生产排产单同步PLM")
    @PostMapping("/production_scheduling_sync")
    public Result productionSchedulingSync(@RequestBody Notice notice) {
        return productToPdmService.productionSchedulingSync(notice);
    }

    @ApiOperation(value = "用户登录plm", notes = "用户登录plm")
    @PostMapping("/login")
    public Result login() {
        return productToPdmService.login();
    }

}
