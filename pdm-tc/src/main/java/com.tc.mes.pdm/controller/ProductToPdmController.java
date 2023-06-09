package com.tc.mes.pdm.controller;

import com.tc.mes.pdm.entity.PdmResult;
import com.tc.mes.pdm.entity.vo.ProduceNoticeVo;
import com.tc.mes.pdm.service.impl.ProductToPdmServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@Api("PDM plm-tc接口")
@RestController
@RequestMapping("/api/integration/plm")
public class ProductToPdmController {

    @Autowired
    private ProductToPdmServiceImpl productToPdmService;

    @ApiOperation(value = "生产排产单同步", notes = "MES生产排产单同步PLM")
    @PostMapping("/production_scheduling_sync")
    public PdmResult productionSchedulingSync(@RequestBody List<ProduceNoticeVo> produceNoticeDtoList) {
        return productToPdmService.productionSchedulingSync(produceNoticeDtoList);
    }

    @ApiOperation(value = "用户登录plm", notes = "用户登录plm")
    @PostMapping("/login")
    public PdmResult login() {
        return productToPdmService.login();
    }

}
