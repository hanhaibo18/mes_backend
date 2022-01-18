package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.richfit.mes.base.service.PdmBomService;
import com.richfit.mes.base.service.PdmTaskService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.PdmBom;
import com.richfit.mes.common.model.base.PdmTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * @author rzw
 * @date 2022-01-17 13:27
 */
@Slf4j
@Api("手工同步")
@RestController
@RequestMapping("/api/base/pdmTask")
public class PdmTaskController {

    @Autowired
    private PdmTaskService pdmTaskService;

    @PostMapping(value = "/add")
    @ApiOperation(value = "手工同步新增", notes = "手工同步新增")
    @ApiImplicitParam(name = "pdmTask", value = "手工同步任务VO", required = true, dataType = "PdmTask", paramType = "body")
    public CommonResult<Boolean> addPdmTask(PdmTask pdmTask){
        //设置未同步字段
        pdmTask.setStatus("0");
        //保存任务
        boolean b = pdmTaskService.save(pdmTask);
        return CommonResult.success(b);
    }
}
