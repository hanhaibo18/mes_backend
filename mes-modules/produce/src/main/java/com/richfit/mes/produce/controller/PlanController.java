package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.base.BasePageDto;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.Action;
import com.richfit.mes.common.model.produce.Plan;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.PlanDto;
import com.richfit.mes.produce.entity.PlanSplitDto;
import com.richfit.mes.produce.service.ActionService;
import com.richfit.mes.produce.service.PlanOptWarningService;
import com.richfit.mes.produce.service.PlanService;
import com.richfit.mes.produce.service.TrackHeadService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Date;
import java.util.List;

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
    private PlanService planService;

    @Autowired
    private PlanOptWarningService planOptWarningService;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ActionService actionService;

    @Autowired
    private TrackHeadService trackHeadService;


    /**
     * 分页查询plan
     */
    @ApiOperation(value = "分页查询缺件计划信息", notes = "分页查询缺件计划信息")
    @GetMapping("/page_missing")
    public CommonResult pageMissing(@ApiParam(value = "计划编码") @RequestParam(required = false) String projCode,
                                    @ApiParam(value = "工作号") @RequestParam(required = false) String workNo,
                                    @ApiParam(value = "图号") @RequestParam(required = false) String drawNo,
                                    @ApiParam(value = "开始时间") @RequestParam(required = false) String startTime,
                                    @ApiParam(value = "结束时间") @RequestParam(required = false) String endTime,
                                    @ApiParam(value = "工厂代码") @RequestParam(required = false) String branchCode,
                                    @ApiParam(value = "页码") @RequestParam(required = false) int page,
                                    @ApiParam(value = "条数") @RequestParam(required = false) int limit) {
        QueryWrapper<Plan> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(projCode)) {
            queryWrapper.eq("proj_code", projCode);
        }
        if (!StringUtils.isNullOrEmpty(workNo)) {
            queryWrapper.eq("work_no", workNo);
        }
        if (!StringUtils.isNullOrEmpty(drawNo)) {
            queryWrapper.eq("draw_no", drawNo);
        }
        if (!StringUtils.isNullOrEmpty(projCode)) {
            queryWrapper.eq("proj_code", projCode);
        }
        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryWrapper.ge("start_time", startTime);
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryWrapper.le("end_time", endTime);
        }
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.gt("missing_num", 0);
        queryWrapper.orderByDesc("priority");
        queryWrapper.orderByDesc("modify_time");
        PageHelper.startPage(page, limit);
        List<Plan> planList = planService.list(queryWrapper);
        PageInfo<Plan> planPageInfo = new PageInfo(planList);
        log.debug("plan page_missing return is [{}]", planPageInfo);
        return CommonResult.success(planPageInfo);
    }

    /**
     * 分页查询plan
     */
    @ApiOperation(value = "查询计划信息", notes = "根据查询条件返回计划信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryDto", value = "计划属性", paramType = "BasePageDto")
    })
    @GetMapping("/page")
    public CommonResult page(BasePageDto<String> queryDto) {
        PlanDto planDto = null;
        try {
            planDto = objectMapper.readValue(queryDto.getParam(), PlanDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        QueryWrapper<Plan> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(planDto.getOrderNo())) {
            queryWrapper.like("order_no", planDto.getOrderNo());
        }
        if (!StringUtils.isNullOrEmpty(planDto.getProjCode())) {
            queryWrapper.like("proj_code", planDto.getProjCode());
        }
        if (!StringUtils.isNullOrEmpty(planDto.getWorkNo())) {
            queryWrapper.like("work_no", planDto.getWorkNo());
        }
        if (!StringUtils.isNullOrEmpty(planDto.getDrawNo())) {
            queryWrapper.like("draw_no", planDto.getDrawNo());
        }
        if (!StringUtils.isNullOrEmpty(planDto.getStartTime())) {
            queryWrapper.ge("start_time", planDto.getStartTime());
        }
        if (!StringUtils.isNullOrEmpty(planDto.getEndTime())) {
            queryWrapper.le("end_time", planDto.getEndTime());
        }
        if (planDto.getStatus() != -1) {
            queryWrapper.eq("status", planDto.getStatus());
        }
        if (planDto.isFiterClose()) {
            queryWrapper.ne("status", 2);
        }
        if (!StringUtils.isNullOrEmpty(planDto.getBranchCode())) {
            queryWrapper.eq("branch_code", planDto.getBranchCode());
        }
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.orderByDesc("priority");
        queryWrapper.orderByDesc("modify_time");
        IPage<Plan> planList = planService.page(new Page(queryDto.getPage(), queryDto.getLimit()), queryWrapper);
        planService.planPackageRouter(planList.getRecords());
        return CommonResult.success(planList);
    }

    @ApiOperation(value = "封装计划信息工序预警状态", notes = "封装计划信息工序预警状态")
    @PostMapping("/warning")
    public CommonResult warning(@ApiParam(value = "计划列表", required = true) @RequestBody List<Plan> planList) throws Exception {
        for (Plan plan : planList) {
            if (plan.getTrackHeadNumber() != null && plan.getTrackHeadNumber() > 0) {
                planOptWarningService.warning(plan);
            }
        }
        return CommonResult.success(planList);
    }

    @ApiOperation(value = "入库品数量统计", notes = "入库品数量统计")
    @PostMapping("/select_track_store_count")
    public CommonResult selectTrackStoreCount(@ApiParam(value = "计划列表", required = true) @RequestBody List<Plan> planList) {
        planService.planPackageStore(planList);
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
        plan.setStoreNumber(0);
        plan.setProcessNum(0);
        plan.setDeliveryNum(0);
        plan.setMissingNum(plan.getProjNum());
        plan.setTrackHeadNumber(0);
        plan.setTrackHeadFinishNumber(0);
        plan.setOptNumber(0);
        plan.setOptFinishNumber(0);
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
     * 关闭计划
     */
    @ApiOperation(value = "计划关闭", notes = "计划关闭")
    @ApiImplicitParam(name = "plan", value = "计划", required = true, dataType = "Plan", paramType = "body")
    @PostMapping("/close")
    public CommonResult<Object> close(@RequestBody Plan plan) throws GlobalException {
        TenantUserDetails user = SecurityUtils.getCurrentUser();
        plan.setTenantId(user.getTenantId());
        plan.setModifyBy(user.getUserId());
        plan.setModifyTime(new Date());
        return CommonResult.success(planService.updateById(plan));
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

    @ApiOperation(value = "物料齐套性检查", notes = "物料齐套性检查")
    @ApiImplicitParam(name = "planList", value = "计划列表", required = true)
    @PostMapping("/completeness/list")
    public CommonResult<Object> completeness_list(@RequestBody List<Plan> planList) throws GlobalException {
        return CommonResult.success(planService.completenessList(planList));
    }

    /**
     * 拆分计划
     */
    @ApiOperation(value = "拆分计划", notes = "拆分计划")
    @ApiImplicitParam(name = "planSplitDto", value = "原计划", required = true, dataType = "PlanSplitDto", paramType = "body")
    @PostMapping("/splitPlan")
    public CommonResult<Object> splitPlan(@RequestBody PlanSplitDto planSplitDto) throws GlobalException {
        return planService.splitPlan(planSplitDto);
    }

    /**
     * 根据计划id查询所有的跟单信息
     */
    @ApiOperation(value = "根据计划id查询所有的跟单信息", notes = "根据计划id查询所有的跟单信息")
    @ApiImplicitParam(name = "id", value = "计划id", required = true, dataType = "String", paramType = "path")
    @GetMapping("/queryTrackHeadListByPlanId/{id}")
    public CommonResult<Object> queryTrackHeadListByPlanId(@PathVariable String id) throws GlobalException {
        return CommonResult.success(trackHeadService.list(new QueryWrapper<TrackHead>().eq("work_plan_id", id)));
    }

    /**
     * 撤销计划
     */
    @ApiOperation(value = "撤销计划", notes = "撤销计划")
    @ApiImplicitParam(name = "id", value = "计划id", required = true, dataType = "String", paramType = "path")
    @GetMapping("/backoutPlan/{id}")
    public CommonResult<Object> backoutPlan(@PathVariable String id) throws GlobalException {
        return planService.backoutPlan(id);
    }

    @ApiOperation(value = "导入计划", notes = "根据Excel文档导入计划")
    @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "path")
    @PostMapping("/import_excel")
    public CommonResult importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        planService.exportPlan(file);
        return CommonResult.success(null);
    }
}
