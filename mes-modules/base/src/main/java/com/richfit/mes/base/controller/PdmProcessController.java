package com.richfit.mes.base.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.base.service.*;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.PdmMesProcess;
import com.richfit.mes.common.model.base.PdmProcess;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
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
    @Transactional(rollbackFor = Exception.class)
    public void synctomes(String id) {
        PdmProcess pdmProcess = pdmProcessService.getById(id);
        pdmProcess.setItemStatus("已同步");
        PdmMesProcess pdmMesProcess = JSON.parseObject(JSON.toJSONString(pdmProcess), PdmMesProcess.class);
        pdmMesProcess.setItemStatus("待发布");
        pdmMesProcess.setModifyTime(new Date());
        pdmMesProcess.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
        pdmMesProcessService.saveOrUpdate(pdmMesProcess);
        pdmProcessService.updateById(pdmProcess);
    }
}
