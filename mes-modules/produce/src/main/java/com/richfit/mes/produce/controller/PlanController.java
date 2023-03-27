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
import com.richfit.mes.common.model.produce.Plan;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.util.ActionUtil;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.aop.OperationLog;
import com.richfit.mes.produce.aop.OperationLogAspect;
import com.richfit.mes.produce.entity.PlanDto;
import com.richfit.mes.produce.entity.PlanSplitDto;
import com.richfit.mes.produce.service.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
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

    @Autowired
    private PlanExtendService planExtendService;

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
                                    @ApiParam(value = "条数") @RequestParam(required = false) int limit,
                                    @ApiParam(value = "排序列") @RequestParam(required = false) String orderCol,
                                    @ApiParam(value = "排序方式") @RequestParam(required = false) String order) {
        QueryWrapper<Plan> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(projCode)) {
            queryWrapper.eq("proj_code", projCode);
        }
        if (!StringUtils.isNullOrEmpty(workNo)) {
            queryWrapper.eq("work_no", workNo);
        }
        if (!StringUtils.isNullOrEmpty(drawNo)) {
            DrawingNoUtil.queryEq(queryWrapper, "draw_no", drawNo);
        }
        if (!StringUtils.isNullOrEmpty(projCode)) {
            queryWrapper.eq("proj_code", projCode);
        }
        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryWrapper.ge("start_time", startTime + " 00:00:00");
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryWrapper.le("end_time", endTime + " 23:59:59");
        }
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        //排序工具
        OrderUtil.query(queryWrapper, orderCol, order);
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.ne("status",-1);
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
            DrawingNoUtil.queryLike(queryWrapper, "draw_no", planDto.getDrawNo());
        }
        if (!StringUtils.isNullOrEmpty(planDto.getStartTime())) {
            queryWrapper.ge("start_time", planDto.getStartTime() + " 00:00:00");
        }
        if (!StringUtils.isNullOrEmpty(planDto.getEndTime())) {
            queryWrapper.le("end_time", planDto.getEndTime() + " 23:59:59");
        }
        if (planDto.getStatus() != -1) {
            queryWrapper.eq("status", planDto.getStatus());
        }
        if (planDto.isFiterClose()) {
            queryWrapper.ne("status", 2);
        }
        if (!StringUtils.isNullOrEmpty(planDto.getProjType())) {
            queryWrapper.like("proj_type", planDto.getProjType());//计划类型 1新制  2 返修'
        }
        if (!StringUtils.isNullOrEmpty(planDto.getBranchCode())) {
            queryWrapper.eq("branch_code", planDto.getBranchCode());
        }
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        OrderUtil.query(queryWrapper, planDto.getOrderCol(), planDto.getOrder());
        IPage<Plan> planList = planService.page(new Page(queryDto.getPage(), queryDto.getLimit()), queryWrapper);
        planService.planPackageRouter(planList.getRecords());
        planService.planPackageExtend(planList.getRecords());//完善扩展表信息
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

    @ApiOperation(value = "热工--封装计划信息工序预警状态", notes = "热工--封装计划信息工序预警状态")
    @PostMapping("/warning_hot")
    public CommonResult warningHot(@ApiParam(value = "计划列表", required = true) @RequestBody List<Plan> planList) throws Exception {
        for (Plan plan : planList) {
            planOptWarningService.warningHot(plan);
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
    @OperationLog(actionType = "0", actionItem = "1")
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
    @OperationLog(actionType = "1", actionItem = "1")
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
    @OperationLog(actionType = "1", actionItem = "1")
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
    public CommonResult<Boolean> delPlanById(@PathVariable String id, HttpServletRequest request) throws GlobalException {
        //计划状态为‘0’ 未开始时，才能删除
        Plan plan = planService.getById(id);
        if (0 != plan.getStatus()) {
            return CommonResult.failed("计划已开始，不能删除!");
        }

        actionService.saveAction(ActionUtil.buildAction
                (plan.getBranchCode(), "2", "1", "计划号：" + plan.getProjNum() + "，图号:" + plan.getDrawNo(), OperationLogAspect.getIpAddress(request)));
        //删除扩展字段
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("plan_id", id);
        planExtendService.removeByMap(paramMap);
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
    public CommonResult<Object> splitPlan(@RequestBody PlanSplitDto planSplitDto, HttpServletRequest request) throws GlobalException {
        actionService.saveAction(ActionUtil.buildAction
                (planSplitDto.getOldPlan().getBranchCode(), "4", "1", "拆分计划，原计划号：" + planSplitDto.getOldPlan().getProjNum(), OperationLogAspect.getIpAddress(request)));
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
    public CommonResult<Object> backoutPlan(@PathVariable String id, HttpServletRequest request) throws GlobalException {
        Plan plan = planService.getById(id);
        actionService.saveAction(ActionUtil.buildAction
                (plan.getBranchCode(), "3", "1", "撤销计划，原计划id:" + plan.getProjNum(), OperationLogAspect.getIpAddress(request)));
        return planService.backoutPlan(id);
    }

    @ApiOperation(value = "导入计划", notes = "根据Excel文档导入计划")
    @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "path")
    @PostMapping("/import_excel")
    public CommonResult importExcel(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        planService.exportPlan(file, request);
        return CommonResult.success(null);
    }

    @ApiOperation(value = "导入计划--模型车间", notes = "根据Excel文档导入计划--模型车间")
    @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "path")
    @PostMapping("/import_excel_MX")
    public CommonResult importExcelMX(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        planService.importPlanMX(file, request);
        return CommonResult.success(null);
    }
    @ApiOperation(value = "导入计划--锻造车间", notes = "根据Excel文档导入计划--锻造车间")
    @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "path")
    @PostMapping("/import_excel_DZ")
    public CommonResult importExcelDZ(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        planService.importPlanDZ(file, request);
        return CommonResult.success(null);
    }
    @ApiOperation(value = "导入计划--铸钢车间", notes = "根据Excel文档导入计划--铸钢车间")
    @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "path")
    @PostMapping("/import_excel_ZG")
    public CommonResult importExcelZG(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        planService.importPlanZG(file, request);
        return CommonResult.success(null);
    }


    @ApiOperation(value = "导入计划--冶炼车间", notes = "根据Excel文档导入计划--冶炼车间")
    @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "path")
    @PostMapping("/import_excel_YL")
    public CommonResult importExcelYL(@RequestParam("file") MultipartFile file, HttpServletRequest request) throws IOException {
        planService.importPlanYL(file, request);
        return CommonResult.success(null);
    }



    @ApiOperation(value = "计划数据维护", notes = "计划数据维护")
    @ApiImplicitParam(name = "planList", value = "计划列表", required = true)
    @PostMapping("/data/maintenance")
    public CommonResult<Object> data(@RequestBody List<Plan> planList) throws GlobalException {
        for (Plan plan : planList) {
            planService.planData(plan.getId());
        }
        return CommonResult.success(null);
    }



    @ApiOperation(value = "发布计划", notes = "发布几计划")
    @ApiImplicitParam(name = "planList", value = "计划列表", required = true)
    @PostMapping("/publish")
    public CommonResult<Object> publish(@RequestBody List<String> planIdList) throws GlobalException {
        return CommonResult.success(planService.publish(planIdList));
    }



}
