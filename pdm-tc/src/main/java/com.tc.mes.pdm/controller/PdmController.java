package com.tc.mes.pdm.controller;

import com.tc.mes.pdm.entity.PdmResult;
import com.tc.mes.pdm.entity.ProductionSchedulingDto;
import com.tc.mes.pdm.service.ProductToPdmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Api("PDM plm-tc接口")
@RestController
@RequestMapping("/api/integration/plm")
public class PdmController {

    @Autowired
    private ProductToPdmService productToPdmService;

    @ApiOperation(value = "生产排产单同步", notes = "MES生产排产单同步PLM")
    @PostMapping("/production_scheduling_sync")
    public PdmResult productionSchedulingSync(@RequestBody ProductionSchedulingDto productionSchedulingDto) {
        return productToPdmService.productionSchedulingSync(productionSchedulingDto);
    }

    @ApiOperation(value = "用户登录plm", notes = "用户登录plm")
    @PostMapping("/login")
    public PdmResult login() {
        return productToPdmService.login();
    }

}
