package com.richfit.mes.produce.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.base.BasePageDto;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.Action;
import com.richfit.mes.common.model.produce.Plan;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.PlanDto;
import com.richfit.mes.produce.entity.PlanQueryDto;
import com.richfit.mes.produce.service.ActionService;
import com.richfit.mes.produce.service.PlanService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/14 9:45
 */
@Slf4j
@Api(tags = "计划管理")
@RestController
@RequestMapping("/api/produce/plan")
public class PlanController extends BaseController {

    @Autowired
    PlanService planService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ActionService actionService;

    /**
     * 分页查询plan
     */
    @ApiOperation(value = "查询计划信息", notes = "根据查询条件返回计划信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryDto", value = "计划属性", paramType = "BasePageDto")
    })
    @GetMapping("/query/page")
    public CommonResult queryByCondition(BasePageDto<String> queryDto) throws GlobalException {

        PlanDto planDto = null;
        try {
            planDto = objectMapper.readValue(queryDto.getParam(), PlanDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (null == planDto) {
            planDto = new PlanDto();
        }
        if (StringUtils.hasText(planDto.getOrderCol())) {
            planDto.setOrderCol(StrUtil.toUnderlineCase(planDto.getOrderCol()));
        } else {
            planDto.setOrderCol("status");
            planDto.setOrder("desc");
        }


        IPage<Plan> planList = planService.queryPage(new Page<Plan>(queryDto.getPage(), queryDto.getLimit()), planDto);

        return CommonResult.success(planList);
    }


    /**
     * 分页查询plan
     */
    @ApiOperation(value = "查询计划信息", notes = "根据查询条件返回计划信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryDto", value = "计划属性", paramType = "BasePageDto")
    })
    @GetMapping("/page")
    public CommonResult page(BasePageDto<String> queryDto) throws GlobalException {
        PlanDto planDto = null;
        try {
            planDto = objectMapper.readValue(queryDto.getParam(), PlanDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        QueryWrapper<Plan> queryWrapper = new QueryWrapper<>();
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(planDto.getProjCode())) {
            queryWrapper.eq("proj_code", planDto.getProjCode());
        }
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(planDto.getWorkNo())) {
            queryWrapper.eq("work_no", planDto.getWorkNo());
        }
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(planDto.getDrawNo())) {
            queryWrapper.eq("draw_no", planDto.getDrawNo());
        }
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(planDto.getStartTime()) || !com.mysql.cj.util.StringUtils.isNullOrEmpty(planDto.getEndTime())) {
            if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(planDto.getStartTime())) {
                planDto.setStartTime("1990-01-01 00:00:00");
            }
            if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(planDto.getEndTime())) {
                planDto.setEndTime("2100-01-01 00:00:00");
            }
            queryWrapper.between("start_time", planDto.getStartTime(), planDto.getEndTime());
        }
        if (planDto.getStatus() != -1) {
            queryWrapper.eq("status", planDto.getStatus());
        }
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(planDto.getBranchCode())) {
            queryWrapper.eq("branch_code", planDto.getBranchCode());
        }
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(planDto.getTenantId())) {
            queryWrapper.eq("tenant_id", planDto.getTenantId());
        }
        IPage<Plan> planList = planService.page(new Page(queryDto.getPage(), queryDto.getLimit()), queryWrapper);
        return CommonResult.success(planList);
    }


    /**
     * 新增计划
     */
    @ApiOperation(value = "新增计划信息", notes = "新增计划信息")
    @ApiImplicitParam(name = "plan", value = "计划", required = true, dataType = "Plan", paramType = "body")
    @PostMapping("/save")
    public CommonResult<Object> savePlan(@RequestBody Plan plan) throws GlobalException {
        TenantUserDetails user = SecurityUtils.getCurrentUser();
        plan.setTenantId(user.getTenantId());
        return planService.addPlan(plan);
    }

    /**
     * 根据ID获取计划
     */
    @ApiOperation(value = "获取计划信息", notes = "根据id获取计划详细信息")
    @ApiImplicitParam(name = "id", value = "计划ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/{id}")
    public CommonResult<Plan> getPlan(@PathVariable String id) throws GlobalException {
        Plan plan = planService.getById(id);
        planService.findBranchName(plan);
        return CommonResult.success(plan);
    }

    /**
     * 更新plan
     */
    @ApiOperation(value = "修改计划信息", notes = "修改计划信息")
    @ApiImplicitParam(name = "plan", value = "计划", required = true, dataType = "Plan", paramType = "body")
    @PutMapping("/update")
    public CommonResult<Object> updatePlan(@RequestBody Plan plan) throws GlobalException {
        TenantUserDetails user = SecurityUtils.getCurrentUser();
        plan.setTenantId(user.getTenantId());
        return planService.updatePlan(plan);
    }

    /**
     * 删除plan
     */
    @ApiOperation(value = "删除计划信息", notes = "根据计划id删除计划记录")
    @ApiImplicitParam(name = "id", value = "计划id", required = true, dataType = "String", paramType = "path")
    @DeleteMapping("/delete/{id}")
    public CommonResult<Boolean> delPlanById(@PathVariable String id) throws GlobalException {
        //计划状态为‘0’ 未开始时，才能删除
        Plan plan = planService.getById(id);
        if (0 != plan.getStatus()) {
            return CommonResult.failed("计划已开始，不能删除!");
        }

        Action action = new Action();
        action.setActionType("2");
        action.setActionItem("1");
        action.setRemark("计划号：" + plan.getProjNum() + "，图号:" + plan.getDrawNo());
        actionService.saveAction(action);

        return CommonResult.success(planService.delPlan(plan));
    }


    public CommonResult<Boolean> updatePlanStatus(@PathVariable String id) throws GlobalException {

        boolean flag = planService.updatePlanStatus(id, "");

        return CommonResult.success(flag);

    }

    /**
     * 获取计划进展评估数据
     */
    @ApiOperation(value = "计划进展评估数据", notes = "计划进展评估数据")
    @ApiImplicitParam(name = "id", value = "计划id", required = true, dataType = "String", paramType = "path")
    @GetMapping("/getPlanNeedHour/{id}")
    public CommonResult<Map> getPlanNeedHour(@PathVariable String id) throws GlobalException {

        Plan plan = planService.getById(id);

        return CommonResult.success(planService.computePlanNeedHour(plan));

    }

    /**
     * 功能描述: 根据时间区间 和 图号批量获取计划
     *
     * @param planQueryDto 查询对象
     * @Author: xinYu.hou
     * @Date: 2022/4/20 15:27
     * @return: List<Map < String, String>>
     **/
    @PostMapping("/queryPlan")
    public List<Map<String, String>> getPlanList(@RequestBody PlanQueryDto planQueryDto) {
        return planService.getPlanList(planQueryDto.getStartTime(), planQueryDto.getEndTime(), planQueryDto.getDrawingNo(), planQueryDto.getTenantId(), planQueryDto.getBranchCode());
    }

    /**
     * 功能描述: 物料齐套性检查
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/7 11:37
     **/
    @ApiOperation(value = "物料齐套性检查", notes = "物料齐套性检查")
    @ApiImplicitParam(name = "id", value = "计划id", required = true, dataType = "String", paramType = "path")
    @GetMapping("/completeness/{id}")
    public CommonResult<Object> completeness(@PathVariable String id) throws GlobalException {
        return CommonResult.success(planService.completeness(id));
    }
}
