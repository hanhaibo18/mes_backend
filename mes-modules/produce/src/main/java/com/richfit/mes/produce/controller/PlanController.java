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
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.PlanDto;
import com.richfit.mes.produce.entity.PlanQueryDto;
import com.richfit.mes.produce.entity.PlanSplitDto;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.service.ActionService;
import com.richfit.mes.produce.service.PlanOptWarningService;
import com.richfit.mes.produce.service.PlanService;
import com.richfit.mes.produce.service.TrackHeadService;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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
    private PlanService planService;

    @Autowired
    private PlanOptWarningService planOptWarningService;


    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ActionService actionService;

    @Autowired
    private TrackHeadService trackHeadService;

    @Autowired
    private BaseServiceClient baseServiceClient;


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
    public CommonResult page(BasePageDto<String> queryDto) throws Exception {
        PlanDto planDto = null;
        try {
            planDto = objectMapper.readValue(queryDto.getParam(), PlanDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        QueryWrapper<Plan> queryWrapper = new QueryWrapper<>();
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(planDto.getOrderNo())) {
            queryWrapper.like("order_no", planDto.getOrderNo());
        }
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(planDto.getProjCode())) {
            queryWrapper.like("proj_code", planDto.getProjCode());
        }
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(planDto.getWorkNo())) {
            queryWrapper.like("work_no", planDto.getWorkNo());
        }
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(planDto.getDrawNo())) {
            queryWrapper.like("draw_no", planDto.getDrawNo());
        }
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(planDto.getStartTime())) {
            queryWrapper.ge("start_time", planDto.getStartTime());
        }
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(planDto.getEndTime())) {
            queryWrapper.le("end_time", planDto.getEndTime());
        }
        if (planDto.getStatus() != -1) {
            queryWrapper.eq("status", planDto.getStatus());
        }
        if (planDto.isFiterClose()) {
            queryWrapper.ne("status", 2);
        }
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(planDto.getBranchCode())) {
            queryWrapper.eq("branch_code", planDto.getBranchCode());
        }
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(planDto.getTenantId())) {
            queryWrapper.eq("tenant_id", planDto.getTenantId());
        }
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
            if (plan.getTrackHeadNumber() > 0) {
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
    public CommonResult importExcel(@RequestParam("file") MultipartFile file) {
        planService.exportPlan(file);
        return null;
    }

}
