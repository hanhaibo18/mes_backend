package com.richfit.mes.produce.controller;

import com.richfit.mes.common.core.base.BaseController;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/14 9:45
 */
@Slf4j
@Api(tags = "计划管理")
@RestController
@RequestMapping("/api/produce/role_operation")
public class RoleOperationController extends BaseController {
    /**
     * 新增计划
     */
//    @ApiOperation(value = "新增计划信息", notes = "新增计划信息")
//    @ApiImplicitParam(name = "plan", value = "计划", required = true, dataType = "Plan", paramType = "body")
//    @PostMapping("/save")
//    public CommonResult<Object> savePlan(@RequestBody Plan plan) throws GlobalException {
//        return planService.addPlan(plan);
//    }
}
