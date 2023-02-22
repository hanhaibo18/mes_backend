package com.richfit.mes.produce.controller;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.model.util.TimeUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.*;
import com.richfit.mes.produce.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author 王瑞
 * @Description 跟单Controller
 */
@Slf4j
@Api(tags = "跟单管理")
@RestController
@RequestMapping("/api/produce/track_head")
public class TrackHeadController extends BaseController {

    @Autowired
    private PlanService planService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TrackHeadService trackHeadService;

    @Autowired
    private TrackHeadFlowService trackFlowService;

    @Autowired
    private TrackCertificateService trackCertificateService;


    @Autowired
    private ActionService actionService;

    public static String TRACK_HEAD_ID_NULL_MESSAGE = "跟单ID不能为空！";
    public static String TRACK_HEAD_NO_NULL_MESSAGE = "跟单编号不能为空！";
    public static String TRACK_HEAD_SUCCESS_MESSAGE = "操作成功！";
    public static String TRACK_HEAD_FAILED_MESSAGE = "操作失败，请重试！";


    @ApiOperation(value = "分页查询跟单台账", notes = "分页查询跟单台账")
    @PostMapping("/track_head/account")
    public CommonResult<PageInfo<TrackHead>> selectTrackHeadAccount(@ApiParam(value = "跟单查询条件") @RequestBody(required = false) TeackHeadDto trackHead
    ) {
        PageHelper.startPage(trackHead.getPage(), trackHead.getLimit());
        if (StrUtil.isNotBlank(trackHead.getTrackNo())) {
            trackHead.setTrackNo(trackHead.getTrackNo().replaceAll(" ", ""));
        }
        if (StrUtil.isNotBlank(trackHead.getOrderCol())) {
            PageHelper.orderBy(trackHead.getOrderCol() + " " + trackHead.getOrder());
        }
        List<TrackHead> trackHeadList = trackHeadService.selectTrackHeadAccount(trackHead);
        PageInfo<TrackHead> trackFlowPage = new PageInfo(trackHeadList);
        return CommonResult.success(trackFlowPage, TRACK_HEAD_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "取消计划", notes = "通过跟单id、取消计划")
    @PostMapping("/plan_cancel/{id}")
    public void planCancel(
            @ApiParam(value = "跟单号", required = true) @PathVariable String id) throws Exception {
        TrackHead trackHead = trackHeadService.getById(id);
        String workPlanId = trackHead.getWorkPlanId();
        trackHead.setWorkPlanId("");
        trackHead.setWorkPlanNo("");
        trackHead.setProductionOrder("");
        trackHeadService.updateById(trackHead);
        planService.planData(workPlanId);
    }

    @ApiOperation(value = "其他资料", notes = "通过跟单分流id、查看其他资料")
    @GetMapping("/other_data/{flowId}")
    public CommonResult<List<LineStore>> otherData(
            @ApiParam(value = "分流id", required = true) @PathVariable String flowId) throws Exception {
        return CommonResult.success(trackHeadService.otherData(flowId));
    }

    @ApiOperation(value = "下载完工资料", notes = "通过跟单id、下载完工资料")
    @GetMapping("/downloads_completion_data/{id}")
    public void downloadsCompletionData(@ApiIgnore HttpServletResponse response,
                                        @ApiParam(value = "跟单号", required = true) @PathVariable String id) throws Exception {
        String path = trackHeadService.completionDataZip(id);
        File file = new File(path);
        InputStream inputStream = new FileInputStream(file);
        response.reset();
        response.setContentType("application/octet-stream");
        response.addHeader("Content-Disposition", "attachment; filename=" + URLEncoder.encode(id, "UTF-8"));
        ServletOutputStream outputStream = response.getOutputStream();
        byte[] b = new byte[1024];
        int len;
        // 从输入流中读取一定数量的字节，并将其存储在缓冲区字节数组中，读到末尾返回-1
        while ((len = inputStream.read(b)) > 0) {
            outputStream.write(b, 0, len);
        }
        inputStream.close();
    }

    @ApiOperation(value = "生成完工资料", notes = "通过跟单id、生成完工资料")
    @GetMapping("/completion_data/{flowId}")
    public void completionData(@ApiParam(value = "跟单号", required = true) @PathVariable String flowId) {
        trackHeadService.completionData(flowId);
    }

    @ApiOperation(value = "跟单号查询跟单", notes = "跟单号查询跟单、返回对应跟单信息")
    @GetMapping("/select_by_track_no")
    public CommonResult<TrackHead> selectByTrackNo(@ApiParam(value = "跟单号", required = true) @RequestParam String trackNo,
                                                   @ApiParam(value = "工厂代码", required = true) @RequestParam String branchCode) throws Exception {
        return CommonResult.success(trackHeadService.selectByTrackNo(trackNo, branchCode));
    }

    @ApiOperation(value = "新增跟单", notes = "新增跟单")
    @PostMapping("/track_head")
    public CommonResult<Boolean> addTrackHead(@ApiParam(value = "跟单信息", required = true) @RequestBody TrackHeadPublicDto trackHead) {

        try {
            if (StringUtils.isNullOrEmpty(trackHead.getTrackNo())) {
                return CommonResult.failed(TRACK_HEAD_NO_NULL_MESSAGE);
            } else {

                boolean bool = false;

                if (!"4".equals(trackHead.getStatus())) {
                    trackHead.setStatus("0");
                }
                trackHead.setApprovalStatus("0");
                trackHead.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                trackHead.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                trackHead.setCreateTime(new Date());

                bool = trackHeadService.saveTrackHead(trackHead);
                if (bool) {
                    return CommonResult.success(true, TRACK_HEAD_SUCCESS_MESSAGE);
                } else {
                    return CommonResult.failed(TRACK_HEAD_FAILED_MESSAGE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "修改跟单", notes = "修改跟单")
    @PutMapping("/track_head")
    public CommonResult<Boolean> updateTrackHead(@ApiParam(value = "跟单信息", required = true) @RequestBody TrackHeadPublicDto trackHeadPublicDto) {
        if (StringUtils.isNullOrEmpty(trackHeadPublicDto.getTrackNo())) {
            return CommonResult.failed(TRACK_HEAD_NO_NULL_MESSAGE);
        } else if (StringUtils.isNullOrEmpty(trackHeadPublicDto.getId())) {
            return CommonResult.failed(TRACK_HEAD_ID_NULL_MESSAGE);
        } else {
            boolean bool = trackHeadService.updataTrackHead(trackHeadPublicDto, trackHeadPublicDto.getTrackItems());
            if (bool) {
                //添加日志
                Action action = new Action();
                action.setActionType("1");
                action.setActionItem("2");
                action.setRemark("跟单号：" + trackHeadPublicDto.getTrackNo());
                actionService.saveAction(action);
                return CommonResult.success(true, TRACK_HEAD_SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(TRACK_HEAD_FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "修改跟单状态", notes = "修改跟单状态")
    @PutMapping("/track_head/change_status")
    public CommonResult changeTrackHeadStatus(@ApiParam(value = "0修改审批状态  1修改跟单状态", required = true) @RequestParam String type,
                                              @ApiParam(value = "状态代码", required = true) @RequestParam String status,
                                              @ApiParam(value = "跟新信息列表", required = true) @RequestBody List<TrackHead> trackHeads) {
        if ("0".equals(type)) { //修改审批状态
            trackHeads.stream().forEach(trackHead -> {
                trackHead.setApprovalStatus(status);
                trackHead.setApprovalTime(new Date());
            });
        } else if (type.equals("1")) {
            trackHeads.stream().forEach(trackHead -> {
                trackHead.setStatus(status);
            });
        }

        boolean bool = trackHeadService.updateBatchById(trackHeads);
        if (bool) {
            return CommonResult.success(trackHeads, TRACK_HEAD_SUCCESS_MESSAGE);
        } else {
            return CommonResult.failed(TRACK_HEAD_FAILED_MESSAGE);
        }
    }

    @ApiOperation(value = "删除跟单", notes = "删除跟单")
    @DeleteMapping("/track_head")
    public CommonResult deleteTrackHead(@ApiParam(value = "跟单信息列表", required = true) @RequestBody List<TrackHead> trackHeads) {
        boolean bool = trackHeadService.deleteTrackHead(trackHeads);
        if (bool) {
            Action action = new Action();
            action.setActionType("2");
            action.setActionItem("2");
            String trackNos = "";
            for (TrackHead trackHead : trackHeads) {
                trackNos += trackHead.getTrackNo() + ",";
            }
            action.setRemark("跟单号：" + trackNos);
            actionService.saveAction(action);
            return CommonResult.success(trackHeads, TRACK_HEAD_SUCCESS_MESSAGE);
        } else {
            return CommonResult.failed(TRACK_HEAD_FAILED_MESSAGE);
        }
    }

    @ApiOperation(value = "分页查询跟单", notes = "根据跟单号、计划号、产品编号、物料编码以及跟单状态分页查询跟单")
    @GetMapping("/track_head")
    public CommonResult<IPage<TrackHeadPublicVo>> selectTrackHead(@ApiParam(value = "开始时间") @RequestParam(required = false) String startTime,
                                                                  @ApiParam(value = "结束时间") @RequestParam(required = false) String endTime,
                                                                  @ApiParam(value = "id") @RequestParam(required = false) String id,
                                                                  @ApiParam(value = "跟单编码") @RequestParam(required = false) String trackNo,

                                                                  @ApiParam(value = "图号") @RequestParam(required = false) String drawingNo,

                                                                  @ApiParam(value = "订单编号") @RequestParam(required = false) String productionOrder,

                                                                  @ApiParam(value = "工作计划id") @RequestParam(required = false) String workPlanId,
                                                                  @ApiParam(value = "工作计划号") @RequestParam(required = false) String workPlanNo,
                                                                  @ApiParam(value = "生产编码") @RequestParam(required = false) String productNo,
                                                                  @ApiParam(value = "物料号码") @RequestParam(required = false) String materialNo,
                                                                  @ApiParam(value = "跟单状态") @RequestParam(required = false) String status,

                                                                  @ApiParam(value = "跟单类型") @RequestParam(required = false) String trackType,
                                                                  @ApiParam(value = "审批状态") @RequestParam(required = false) String approvalStatus,
                                                                  @ApiParam(value = "排序方式") @RequestParam(required = false) String order,
                                                                  @ApiParam(value = "排序列") @RequestParam(required = false) String orderCol,
                                                                  @ApiParam(value = "是否试棒跟单 0否、1是") @RequestParam(required = false) String isTestBar,
                                                                  @ApiParam(value = "工艺id") @RequestParam(required = false) String routerId,
                                                                  @ApiParam(value = "工厂代码") @RequestParam(required = false) String branchCode,
                                                                  @ApiParam(value = "租户id") @RequestParam(required = false) String tenantId,
                                                                  @ApiParam(value = "页码") @RequestParam(required = false) int page,
                                                                  @ApiParam(value = "条数") @RequestParam(required = false) int limit,
                                                                  @ApiParam(value = "跟单分类：1机加  2装配 3热处理 4钢结构") @RequestParam(required = false) String classes,
                                                                  @ApiParam(value = "是否绑定工艺") @RequestParam(required = false) String isBindRouter) {
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<TrackHead>();
        if (!StringUtils.isNullOrEmpty(classes)) {
            queryWrapper.ge("classes", classes);
        }
        if (!StringUtils.isNullOrEmpty(startTime)) {
            TimeUtil.queryStartTime(queryWrapper, startTime);
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            TimeUtil.queryEndTime(queryWrapper, endTime);
        }
        if (!StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (!StringUtils.isNullOrEmpty(trackNo)) {
            trackNo = trackNo.replaceAll(" ", "");
            queryWrapper.apply("replace(replace(replace(track_no, char(13), ''), char(10), ''),' ', '') like '%" + trackNo + "%'");
        }
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            DrawingNoUtil.queryLike(queryWrapper, "drawing_no", drawingNo);
        }
        if (!StringUtils.isNullOrEmpty(productionOrder)) {
            queryWrapper.like("production_order", productionOrder);
        }
        if (!StringUtils.isNullOrEmpty(workPlanId)) {
            queryWrapper.like("work_plan_id", workPlanId);
        }
        if (!StringUtils.isNullOrEmpty(workPlanNo)) {
            queryWrapper.like("work_plan_No", workPlanNo);
        }
        if (!StringUtils.isNullOrEmpty(productNo)) {
            queryWrapper.like("product_no_desc", productNo);
        }
        if (!StringUtils.isNullOrEmpty(materialNo)) {
            queryWrapper.like("material_no", materialNo);
        }
        if (!StringUtils.isNullOrEmpty(status)) {
            queryWrapper.eq("status", status);
        }
        if (!StringUtils.isNullOrEmpty(trackType)) {
            queryWrapper.eq("track_type", trackType);
        }
        if (!StringUtils.isNullOrEmpty(approvalStatus)) {
            queryWrapper.eq("approval_status", approvalStatus);
        }
        if (!StringUtils.isNullOrEmpty(isTestBar)) {
            queryWrapper.eq("is_test_bar", isTestBar);
        }
        if (!StringUtils.isNullOrEmpty(routerId)) {
            queryWrapper.eq("router_id", routerId);
        }
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        //热工是否绑定工艺
        if (!StringUtils.isNullOrEmpty(isBindRouter)) {
            if (isBindRouter.equals("0")) {
                queryWrapper.and(wrapper -> wrapper.isNull("router_id").or(wrapper2 -> wrapper2.eq("router_id", "")));
            } else {
                queryWrapper.and(wrapper -> wrapper.isNotNull("router_id").and(wrapper2 -> wrapper2.ne("router_id", "")));
            }
        }
        if (!StringUtils.isNullOrEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        } else {
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        }
        //排序工具
        OrderUtil.query(queryWrapper, orderCol, order);
        return CommonResult.success(trackHeadService.queryPage(new Page<TrackHead>(page, limit), queryWrapper), TRACK_HEAD_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "分页查询跟单分流表", notes = "根据跟单号、计划号、产品编号、物料编码以及跟单状态分页查询跟单分流信息")
    @GetMapping("/track_flow_page")
    public CommonResult<PageInfo<TrackHead>> selectTrackFLow(
            @ApiParam(value = "页码") @RequestParam(required = false) int page,
            @ApiParam(value = "条数") @RequestParam(required = false) int limit,
            @ApiParam(value = "排序列") @RequestParam(required = false) String orderCol,
            @ApiParam(value = "排序方式") @RequestParam(required = false) String order,
            @ApiParam(value = "开始时间") @RequestParam(required = false) String startTime,
            @ApiParam(value = "结束时间") @RequestParam(required = false) String endTime,
            @ApiParam(value = "打印模板编码") @RequestParam(required = false) String templateCode,
            @ApiParam(value = "跟单状态") @RequestParam(required = false) String status,
            @ApiParam(value = "完工资料生成") @RequestParam(required = false) String isCompletionData,
            @ApiParam(value = "合格证生成/Y以生产 N未生成") @RequestParam(required = false) String isCertificate,
            @ApiParam(value = "产品编码") @RequestParam(required = false) String productNo,
            @ApiParam(value = "跟单编码") @RequestParam(required = false) String trackNo,
            @ApiParam(value = "工作号") @RequestParam(required = false) String workNo,
            @ApiParam(value = "图号") @RequestParam(required = false) String drawingNo,
            @ApiParam(value = "炉批号") @RequestParam(required = false) String batchNo,
            @ApiParam(value = "生成订单号") @RequestParam(required = false) String productionOrder,
            @ApiParam(value = "计划id") @RequestParam(required = false) String workPlanId,
            @ApiParam(value = "工厂类型") @RequestParam(required = false) String classes,
            @ApiParam(value = "工厂代码") @RequestParam(required = false) String branchCode
    ) throws Exception {
        Map<String, String> map = new HashMap<>();
        TrackFlow.param(startTime,
                endTime,
                null,
                null,
                templateCode,
                status,
                isCompletionData,
                isCertificate,
                productNo,
                trackNo,
                workNo,
                drawingNo,
                batchNo,
                productionOrder,
                workPlanId,
                classes,
                branchCode,
                SecurityUtils.getCurrentUser().getTenantId(), orderCol, order, map);
        PageHelper.startPage(page, limit);
        List trackFlowList = trackHeadService.selectTrackFlowList(map);
        PageInfo<TrackHead> trackFlowPage = new PageInfo(trackFlowList);
        return CommonResult.success(trackFlowPage, TRACK_HEAD_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询跟单分流表List", notes = "根据跟单号、计划号、产品编号、物料编码以及跟单状态查询跟单分流表List信息")
    @GetMapping("/track_flow_List")
    public CommonResult<List<TrackHead>> selectTrackFLowList(
            @ApiParam(value = "排序列") @RequestParam(required = false) String orderCol,
            @ApiParam(value = "排序方式") @RequestParam(required = false) String order,
            @ApiParam(value = "开始时间") @RequestParam(required = false) String startTime,
            @ApiParam(value = "结束时间") @RequestParam(required = false) String endTime,
            @ApiParam(value = "打印模板编码") @RequestParam(required = false) String templateCode,
            @ApiParam(value = "跟单状态") @RequestParam(required = false) String status,
            @ApiParam(value = "完工资料生成") @RequestParam(required = false) String isCompletionData,
            @ApiParam(value = "合格证生成/Y以生产 N未生成") @RequestParam(required = false) String isCertificate,
            @ApiParam(value = "产品编码") @RequestParam(required = false) String productNo,
            @ApiParam(value = "跟单编码") @RequestParam(required = false) String trackNo,
            @ApiParam(value = "工作号") @RequestParam(required = false) String workNo,
            @ApiParam(value = "图号") @RequestParam(required = false) String drawingNo,
            @ApiParam(value = "炉批号") @RequestParam(required = false) String batchNo,
            @ApiParam(value = "生成订单号") @RequestParam(required = false) String productionOrder,
            @ApiParam(value = "计划id") @RequestParam(required = false) String workPlanId,
            @ApiParam(value = "工厂代码") @RequestParam(required = false) String branchCode) throws Exception {
        Map<String, String> map = new HashMap<>();
        TrackFlow.param(startTime,
                endTime,
                null,
                null,
                templateCode,
                status,
                isCompletionData,
                isCertificate,
                productNo,
                trackNo,
                workNo,
                drawingNo,
                batchNo,
                productionOrder,
                workPlanId,
                null,
                branchCode,
                SecurityUtils.getCurrentUser().getTenantId(), orderCol, order, map);
        return CommonResult.success(trackHeadService.selectTrackFlowList(map), TRACK_HEAD_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "工艺跟踪", notes = "根据图号、工艺版本号分页查询跟单工艺信息")
    @GetMapping("/track_head/router")
    public CommonResult<IPage<TrackHead>> selectTrackHead(@ApiParam(value = "图号") @RequestParam(required = false) String drawingNo,
                                                          @ApiParam(value = "工艺版本") @RequestParam(required = false) String optVer,
                                                          @ApiParam(value = "页码", required = true) @RequestParam int page,
                                                          @ApiParam(value = "每页条数", required = true) @RequestParam int limit) {
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<TrackHead>();
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            DrawingNoUtil.queryLike(queryWrapper, "th.drawing_no", drawingNo);
        }
        if (!StringUtils.isNullOrEmpty(optVer)) {
            queryWrapper.eq("ti.opt_ver", optVer);
        }
        queryWrapper.eq("th.tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        return CommonResult.success(trackHeadService.selectTrackHeadRouter(new Page<TrackHead>(page, limit), queryWrapper), TRACK_HEAD_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询跟单及当前工序", notes = "根据跟单编号、图号、产品编号分页查询跟单及当前工序")
    @GetMapping("/track_head/current")
    public CommonResult<IPage<TrackHead>> selectTrackHeadCurrentRouter(String startDate, String endDate, String trackNo, String status, String drawingNo, String productNo, String certificateType, String certificateNo,
                                                                       String certificateId, String branchCode, String tenantId, Boolean isEdit, Boolean noCertNo,
                                                                       int page, int limit) {
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<TrackHead>();

        if (isEdit) {
            QueryWrapper<TrackCertificate> query = new QueryWrapper<TrackCertificate>();
            query.eq("certificate_id", certificateId);
            List<TrackCertificate> trackCertificates = trackCertificateService.list(query);
            List<String> thIds = trackCertificates.stream().map(TrackCertificate::getThId).collect(Collectors.toList());
            queryWrapper.in("id", thIds);
            return CommonResult.success(trackHeadService.page(new Page<TrackHead>(page, limit), queryWrapper), TRACK_HEAD_SUCCESS_MESSAGE);
        }

        if (!StringUtils.isNullOrEmpty(startDate)) {
            queryWrapper.ge("th.create_time", startDate);
        }

        if (!StringUtils.isNullOrEmpty(endDate)) {
            queryWrapper.le("th.create_time", endDate);
        }
        if (!StringUtils.isNullOrEmpty(trackNo)) {
            trackNo = trackNo.replaceAll(" ", "");
//            queryWrapper.apply("replace(replace(replace(th.track_no, char(13), ''), char(10), ''),' ', '') like '%" + trackNo + "%'").or().apply("replace(replace(replace(th.original_track_no, char(13), ''), char(10), ''),' ', '') like '%" + trackNo + "%'");
//            queryWrapper.like("th.track_no", trackNo);
            queryWrapper.apply("replace(replace(replace(track_no, char(13), ''), char(10), ''),' ', '') = '" + trackNo + "'");
        }
        if (!StringUtils.isNullOrEmpty(status)) {
            queryWrapper.eq("th.status", status);
        }
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            DrawingNoUtil.queryLike(queryWrapper, "th.drawing_no", drawingNo);
        }
        if (!StringUtils.isNullOrEmpty(productNo)) {
            queryWrapper.like("th.product_no", productNo);
        }
        queryWrapper.eq("ti.is_current", "1");
        //增加逻辑判断，只查询合格证号为空的记录
        if (noCertNo) {
            queryWrapper.and(wapper -> wapper.eq("th.certificate_No", "").or().isNull("th.certificate_No"));
            if ("0".equals(certificateType)) {
                queryWrapper.and(wapper -> wapper.eq("ti.certificate_No", "").or().isNull("ti.certificate_No"));
            }
        }


        /*if(!StringUtils.isNullOrEmpty(certificateType)){
            if(certificateType.equals("0")){ //工序合格证
                if(isEdit){ //修改时查询
                    queryWrapper.and(wrapper -> wrapper.isNull("ti.certificate_no").or().eq("ti.certificate_no", "").or().eq("ti.certificate_no", certificateNo));
                } else {
                    queryWrapper.and(wrapper -> wrapper.isNull("ti.certificate_no").or().eq("ti.certificate_no", ""));
                }
            } else if(certificateType.equals("1")){ //完工合格证
                if(isEdit){ //修改时查询
                    queryWrapper.and(wrapper -> wrapper.isNull("th.certificate_no").or().eq("th.certificate_no", "").or().eq("th.certificate_no", certificateNo));
                } else {
                    queryWrapper.and(wrapper -> wrapper.isNull("th.certificate_no").or().eq("th.certificate_no", ""));
                }
            }
        }*/
        queryWrapper.eq("th.tenant_id", tenantId);
        queryWrapper.eq("th.branch_code", branchCode);
        return CommonResult.success(trackHeadService.selectTrackHeadCurrentRouter(new Page<TrackHead>(page, limit), queryWrapper), TRACK_HEAD_SUCCESS_MESSAGE);
    }

    @PostMapping("/plan")
    @ApiOperation(value = "修改跟单与计划管理", notes = "根据跟单ID和计划ID进行计划关联")
    public CommonResult<Boolean> updateTrackHeadPlan(@ApiParam(value = "跟单信息列表", required = true) @RequestBody List<TrackHead> trackHeads) {
        return CommonResult.success(trackHeadService.updateTrackHeadPlan(trackHeads));
    }

    @GetMapping("/queryMaterialListPage")
    public CommonResult<IPage<IncomingMaterialVO>> queryMaterialList(Integer page, Integer size, String certificateNo, String drawingNo, String branchCode, String tenantId) {
        return CommonResult.success(trackHeadService.queryMaterialList(page, size, certificateNo, drawingNo, branchCode, tenantId));
    }

    @ApiOperation(value = "导出工作清单", notes = "通过Excel文档导出信息")
    @GetMapping("/export_excel")
    public void exportExcel(QueryDto<QueryWork> queryWork, HttpServletResponse rsp) {
        IPage<WorkDetailedListVo> workDetailedListVo = trackHeadService.queryWorkDetailedList(queryWork);
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");
        String fileName = "物料同步_" + format.format(new Date()) + ".xlsx";
        String[] columnHeaders = {"优先级", "工作号", "图号", "名称", "数量", "编号", "试棒", "工艺", "下工序", "重量/KG", "材质", "跟单号", "来料日期", "派工日期"};
        String[] fieldNames = {"priority", "WorkNo", "drawingNo", "productName", "number", "", "testBarType", "optName", "nextOptSequence", "weight", "texture", "track_no", "", ""};
        //export
        try {
            ExcelUtils.exportExcel(fileName, workDetailedListVo.getRecords(), columnHeaders, fieldNames, rsp);
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * 功能描述: 跟单台账分页查询
     *
     * @param standingBookDto 查询对象
     * @Author: xinYu.hou
     * @Date: 2022/4/27 22:49
     * @return: IPage<TrackHead>
     **/
    @ApiOperation(value = "查询跟单台账分页", notes = "查询跟单台账分页")
    @PostMapping("/queryTrackHeadPage")
    public CommonResult<IPage<TrackHead>> queryTrackHeadPage(QueryDto<StandingBookDto> standingBookDto) {
        return CommonResult.success(trackHeadService.queryTrackHeadPage(standingBookDto));
    }

    /**
     * 功能描述: 更改优先级
     *
     * @param trackNo
     * @param priority
     * @Author: xinYu.hou
     * @Date: 2022/5/8 6:43
     * @return: Boolean
     **/
    @ApiOperation(value = "更改优先级", notes = "更改优先级")
    @PostMapping("updateWorkDetailed")
    public CommonResult<Boolean> updateWorkDetailed(String trackNo, String priority) {
        return CommonResult.success(trackHeadService.updateWorkDetailed(trackNo, priority));
    }

    /**
     * 功能描述: 跟踪调度 跟单列表查询
     *
     * @param afterDto
     * @Author: xinYu.hou
     * @Date: 2022/5/8 8:13
     * @return: IPage<TailAfterVo>
     **/
    @ApiOperation(value = "跟踪调度 跟单列表查询", notes = "跟踪调度 跟单列表查询")
    @PostMapping("queryTailAfterList")
    public CommonResult<IPage<TailAfterVo>> queryTailAfterList(QueryDto<QueryTailAfterDto> afterDto) {
        return CommonResult.success(trackHeadService.queryTailAfterList(afterDto));
    }

    @ApiOperation(value = "合格证关联跟单查询", notes = "根据合格证Id查询关联的跟单")
    @GetMapping("/track_head/query_by_cert")
    public CommonResult<List<TrackHead>> selectTrackHeadbyCert(@ApiParam(value = "合格证Id") String certificateId) {

        return CommonResult.success(trackHeadService.queryListByCertId(certificateId), TRACK_HEAD_SUCCESS_MESSAGE);

    }

    @ApiOperation(value = "跟单回滚查询", notes = "查询关联的跟单")
    @GetMapping("/rollBackSelect")
    public CommonResult<PageInfo<TrackHead>> selectRollBack(
            @ApiParam(value = "排序列") @RequestParam(required = false) String orderCol,
            @ApiParam(value = "排序方式") @RequestParam(required = false) String order,
            @ApiParam(value = "开始时间") @RequestParam(required = false) String startTime,
            @ApiParam(value = "结束时间") @RequestParam(required = false) String endTime,
            @ApiParam(value = "产品编号") @RequestParam(required = false) String productNo,
            @ApiParam(value = "跟单编号") @RequestParam(required = false) String trackNo,
            @ApiParam(value = "工作号") @RequestParam(required = false) String workNo,
            @ApiParam(value = "图号") @RequestParam(required = false) String drawingNo,
            @ApiParam(value = "炉批号") @RequestParam(required = false) String batchNo,
            @ApiParam(value = "生产订单号") @RequestParam(required = false) String orderNo,
            @ApiParam(value = "页码") @RequestParam int page,
            @ApiParam(value = "每页记录数") @RequestParam int limit,
            @ApiParam(value = "分公司") @RequestParam String branchCode) throws Exception {
        Map<String, String> map = new HashMap<>();
        TrackFlow.param(startTime,
                endTime,
                null,
                null,
                null,
                null,
                null,
                null,
                productNo,
                trackNo,
                workNo,
                drawingNo,
                batchNo,
                orderNo,
                null,
                null,
                branchCode,
                SecurityUtils.getCurrentUser().getTenantId(), orderCol, order, map);
        PageHelper.startPage(page, limit);
        List trackFlowList = trackHeadService.selectTrackFlowList(map);
        PageInfo<TrackHead> trackFlowPage = new PageInfo(trackFlowList);
        return CommonResult.success(trackFlowPage, TRACK_HEAD_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询BOM信息", notes = "根据装配信息查询BOM信息")
    @GetMapping("/query_bom/{flowId}")
    public CommonResult<List<TrackHead>> queryBomByTrackAssembly(@PathVariable String flowId) {
        return CommonResult.success(trackHeadService.queryTrackAssemblyByTrackNo(flowId));
    }

    @ApiOperation(value = "查询跟单分流表List", notes = "根据跟单号查询跟单分流信息")
    @GetMapping("/flow_list")
    public CommonResult<List<TrackFlow>> selectFLowList(
            @ApiParam(value = "跟单编码") @RequestParam(required = false) String trackHeadId
    ) throws Exception {
        QueryWrapper<TrackFlow> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_head_id", trackHeadId);
        queryWrapper.orderByAsc("product_no");
        return CommonResult.success(trackFlowService.list(queryWrapper), TRACK_HEAD_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "跟单拆分", notes = "跟单拆分")
    @PostMapping("/split")
    public void trackHeadSplit(@ApiParam(value = "跟单拆分信息", required = true) @RequestBody Map<String, Object> map) throws
            Exception {
        try {
            String id = map.get("id").toString();
            String trackNoNew = map.get("trackNoNew").toString();
            List<TrackFlow> trackFlow = JSON.parseArray(JSON.toJSONString(map.get("trackList")), TrackFlow.class);
            List<TrackFlow> trackFlowNew = JSON.parseArray(JSON.toJSONString(map.get("trackListNew")), TrackFlow.class);
            TrackHeadPublicDto trackHeadPublicDto = trackHeadService.queryDtoById(id);
            if (TrackHead.TRACK_TYPE_0.equals(trackHeadPublicDto.getTrackType())) {
                trackHeadService.trackHeadSplit(trackHeadPublicDto, trackNoNew, trackFlow, trackFlowNew);
            } else {
                trackHeadService.trackHeadBatchSplit(trackHeadPublicDto, trackNoNew, trackFlow, trackFlowNew);
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    @ApiOperation(value = "跟单回收", notes = "跟单回收")
    @PostMapping("/split_back")
    public void trackHeadSplitBack(@ApiParam(value = "回收跟单信息", required = true) @RequestBody List<TrackHeadPublicDto> trackHeadPublicDtoList) throws
            Exception {
        try {
            //跟单号长度降序排序，避免批量还原时候出现中间跟单丢失问题，从最后一个处理
            Collections.sort(trackHeadPublicDtoList, new TrackHeadComparator());
            for (TrackHeadPublicDto TrackHeadPublicDto : trackHeadPublicDtoList) {
                if (TrackHead.TRACK_TYPE_0.equals(TrackHeadPublicDto.getTrackType())) {
                    trackHeadService.trackHeadSplitBack(TrackHeadPublicDto);
                } else {
                    trackHeadService.trackHeadSplitBatchBack(TrackHeadPublicDto);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception(e.getMessage());
        }
    }

    //跟单号List排序
    class TrackHeadComparator implements Comparator<TrackHeadPublicDto> {
        @Override
        public int compare(TrackHeadPublicDto o1, TrackHeadPublicDto o2) {
            return o2.getTrackNo().length() - o1.getTrackNo().length();
        }
    }

    @ApiOperation(value = "跟单拆分跟单号", notes = "跟单拆分跟单号")
    @GetMapping("/split_track_no")
    public CommonResult<String> splitTrackNo(@ApiParam(value = "跟单编号") @RequestParam(required = false) String trackNo,
                                             @ApiParam(value = "工厂代码", required = true) @RequestParam String branchCode) {
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(trackNo)) {
            queryWrapper.eq("original_track_no", trackNo);
        }
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        List<TrackHead> trackHeadList = trackHeadService.list(queryWrapper);
        int index = trackHeadList.size() + 1;
        return CommonResult.success(trackNo + "-" + index);
    }


    @ApiOperation(value = "通过工艺id查询跟单", notes = "通过工艺id查询跟单")
    @GetMapping("/select_by_routerid")
    public CommonResult<List<TrackHead>> selectByRouterId(
            @ApiParam(value = "工艺id") @RequestParam(required = false) String routerId
    ) {
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<TrackHead>();
        if (!StringUtils.isNullOrEmpty(routerId)) {
            queryWrapper.eq("router_id", routerId);
        }
        return CommonResult.success(trackHeadService.list(queryWrapper), TRACK_HEAD_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询BOM是否被使用", notes = "查询项目BOM是否被使用(项目BOM删除查询使用)")
    @GetMapping("/queryCountByWorkNo")
    public int queryCountByWorkNo(String projectBomId) {
        return trackHeadService.queryCountByWorkNo(projectBomId);
    }

    @ApiOperation(value = "数据处理", notes = "数据处理")
    @PostMapping("/data_processing")
    public CommonResult dataProcessing(@ApiParam(value = "跟单信息列表", required = true) @RequestBody List<TrackHead> trackHeads) {
        for (TrackHead trackHead : trackHeads) {
            planService.planData(trackHead.getWorkPlanId());
            orderService.orderDataTrackHead(trackHead);
        }
        return CommonResult.success("操作成功", TRACK_HEAD_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "根据物料号和图号查询跟单", notes = "根据物料号和图号查询跟单")
    @GetMapping("/getTrackHeadByMaterialCodeAndDrawingNo")
    public CommonResult<List<TrackHead>> getTrackHeadByMaterialCodeAndDrawingNo(@ApiParam(value = "物料号") @RequestParam("materialCodes") List<String> materialCodes,
                                                                                @ApiParam(value = "图号", required = true) @RequestParam("drawingNos") List<String> drawingNos,
                                                                                @ApiParam(value = "租户id", required = true) @RequestParam("tenantId") String tenantId) {
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<>();
        if (CollectionUtils.isNotEmpty(materialCodes)) {
            queryWrapper.in("material_no", materialCodes);
        }
        if (CollectionUtils.isNotEmpty(drawingNos)) {
            queryWrapper.or();
            DrawingNoUtil.queryIn(queryWrapper, "drawing_no", drawingNos);
        }
        queryWrapper.eq("tenant_id", tenantId);
        return CommonResult.success(trackHeadService.list(queryWrapper));
    }

    @ApiOperation(value = "热工跟单绑定工艺", notes = "热工跟单绑定工艺")
    @PostMapping("/rgSaveTrackHead")
    public CommonResult<Boolean> rgSaveTrackHead(@RequestBody JSONObject jsonObject) {

        //保存的工序
        List<TrackItem> trackItems = JSON.parseArray(JSONObject.toJSONString(jsonObject.get("trackItems")), TrackItem.class);
        //组织机构
        String trackNo = jsonObject.getString("trackNo");
        //工艺id
        String routerId = jsonObject.getString("routerId");
        //工艺版本
        String routerVer = jsonObject.getString("routerVer");
        return CommonResult.success(trackHeadService.rgSaveTrackHead(trackNo, trackItems, routerId, routerVer));
    }

    @ApiOperation(value = "导出热处理报告", notes = "通过Excel文档导出热处理报告")
    @GetMapping("/exportHeatReport")
    public void exportHeatReport(@ApiParam(value = "跟单工序表ID") String trackHeadId, HttpServletResponse rsp) {
        trackHeadService.exportHeatReport(trackHeadId, rsp);
    }
}
