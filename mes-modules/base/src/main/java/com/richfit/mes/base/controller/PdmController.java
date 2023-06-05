package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.base.service.PdmObjectService;
import com.richfit.mes.common.core.api.CommonResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HanHaiBo
 * @date 2023/6/5 10:11
 */
@Slf4j
@Api("PDM")
@RestController
@RequestMapping("/api/base/pdm")
public class PdmController {
    @Autowired
    private PdmObjectService pdmObjectService;

    @GetMapping
    public CommonResult<IPage> getPdm(@ApiParam(value = "图号") @RequestParam(required = false) String drawNo, @ApiParam(value = "图纸 = draw 工艺 = router BOM = bom") String type,
                                      @ApiParam(value = "车间编码") String branchCode, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int limit) {
        return CommonResult.success(pdmObjectService.getPdmPage(drawNo, type, branchCode, page, limit));
    }
}
