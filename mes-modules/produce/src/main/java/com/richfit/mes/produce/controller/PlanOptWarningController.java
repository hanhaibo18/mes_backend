package com.richfit.mes.produce.controller;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.PlanOptWarning;
import com.richfit.mes.produce.service.PlanOptWarningService;
import com.richfit.mes.produce.service.PlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: zhiqiang.lu
 * @Date: 2020/8/8 9:59
 */
@Slf4j
@Api(tags = "计划管理")
@RestController
@RequestMapping("/api/produce/plan_opt_warning")
public class PlanOptWarningController extends BaseController {

    @Autowired
    PlanService planService;

    @Autowired
    PlanOptWarningService planOptWarningService;

    @ApiOperation(value = "工序预警", notes = "查询计划工序预警信息")
    @GetMapping("/query")
    public CommonResult query(@ApiParam(value = "计划id", required = true) @RequestParam String planId) throws Exception {
        return CommonResult.success(planOptWarningService.queryList(planId));
    }

    @ApiOperation(value = "保存&更新", notes = "工序预警保存&更新")
    @PostMapping("/save_or_update")
    public CommonResult saveOrUpdate(@ApiParam(value = "工序预警信息", required = true) @RequestParam PlanOptWarning planOptWarning) throws Exception {
        return CommonResult.success(planOptWarningService.saveOrUpdate(planOptWarning));
    }
}
