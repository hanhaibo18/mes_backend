package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.base.service.*;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.PdmProcess;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    @Autowired
    private PdmBomService pdmBomService;
    @Autowired
    private PdmObjectService pdmObjectService;
    @Autowired
    private PdmOptionService pdmOptionService;
    @Autowired
    private PdmDrawService pdmDrawService;
    @Autowired
    public RouterService routerService;
    @Autowired
    public SequenceService sequenceService;
    @Autowired
    public OperatiponService OperationService;

    @Autowired
    private PdmMesProcessService pdmMesProcessService;

    @PostMapping(value = "/query/list")
    @ApiOperation(value = "工艺查询", notes = "工艺查询")
    @ApiImplicitParam(name = "pdmProcess", value = "工艺VO", required = true, dataType = "PdmProcess", paramType = "body")
    public CommonResult<List<PdmProcess>> getList(PdmProcess pdmProcess) {
        List<PdmProcess> list = pdmProcessService.queryList(pdmProcess);
        return CommonResult.success(list);
    }

    @GetMapping("/query/pageList")
    @ApiOperation(value = "工艺分页查询", notes = "工艺分页查询")
    @ApiImplicitParam(name = "pdmProcess", value = "工艺VO", required = true, dataType = "PdmProcess", paramType = "body")
    public CommonResult<IPage<PdmProcess>> getPageList(int page, int limit, PdmProcess pdmProcess) {
        return CommonResult.success(pdmProcessService.queryPageList(page, limit, pdmProcess));
    }


    @PostMapping("/synctomes")
    @ApiOperation(value = "同步到MES", notes = "同步到MES")
    public void synctomes(@ApiParam(value = "同步的工艺列表") @RequestBody List<PdmProcess> pdmProcesses) throws Exception {
        for (PdmProcess p : pdmProcesses) {
            pdmProcessService.synctomes(p.getDrawIdGroup());
        }
    }
}
