package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.base.service.PdmProcessService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.PdmProcess;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author rzw
 * @date 2022-01-04 13:27
 */
@Slf4j
@Api("工艺")
@RestController
@RequestMapping("/api/base/pdmProcess")
public class PdmProcessController {

    @Autowired
    private PdmProcessService pdmProcessService;

    @PostMapping(value = "/query/list")
    @ApiOperation(value = "工艺查询", notes = "工艺查询")
    @ApiImplicitParam(name = "pdmProcess", value = "工艺VO", required = true, dataType = "PdmProcess", paramType = "body")
    public CommonResult<List<PdmProcess>> getList(PdmProcess pdmProcess){
        List<PdmProcess> list = pdmProcessService.queryList(pdmProcess);
        return CommonResult.success(list);
    }

    @GetMapping("/query/pageList")
    @ApiOperation(value = "工艺分页查询", notes = "工艺分页查询")
    @ApiImplicitParam(name = "pdmProcess", value = "工艺VO", required = true, dataType = "PdmProcess", paramType = "body")
    public CommonResult<IPage<PdmProcess>> getPageList(int page, int limit,PdmProcess pdmProcess){
        return CommonResult.success(pdmProcessService.queryPageList(page, limit,pdmProcess));
    }



}
