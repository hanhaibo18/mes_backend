package com.richfit.mes.produce.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.model.produce.Action;
import com.richfit.mes.common.model.produce.TrackCertificate;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.*;
import com.richfit.mes.produce.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
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
    private TrackHeadService trackHeadService;

    @Autowired
    private TrackCertificateService trackCertificateService;

    @Autowired
    private TrackItemService trackItemService;

    @Autowired
    private LineStoreService lineStoreService;

    @Autowired
    private ActionService actionService;

    public static String TRACK_HEAD_ID_NULL_MESSAGE = "跟单ID不能为空！";
    public static String TRACK_HEAD_NO_NULL_MESSAGE = "跟单编号不能为空！";
    public static String TRACK_HEAD_SUCCESS_MESSAGE = "操作成功！";
    public static String TRACK_HEAD_FAILED_MESSAGE = "操作失败，请重试！";


    @ApiOperation(value = "跟单号查询跟单", notes = "跟单号查询跟单、返回对应跟单信息")
    @GetMapping("/select_by_track_no")
    public CommonResult<TrackHead> selectByTrackNo(@ApiParam(value = "跟单号", required = true) @RequestParam String trackNo,
                                                   @ApiParam(value = "工厂代码", required = true) @RequestParam String branchCode) throws Exception {
        return CommonResult.success(trackHeadService.selectByTrackNo(trackNo, branchCode));
    }

    @ApiOperation(value = "新增跟单", notes = "新增跟单")
    @PostMapping("/track_head")
    public CommonResult<TrackHead> addTrackHead(@ApiParam(value = "跟单信息", required = true) @RequestBody TrackHead trackHead) {

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

                /*if(trackHead.getTrackType().equals("0")){ //单件
                    QueryWrapper<LineStore> queryWrapper = new QueryWrapper<LineStore>();
                    queryWrapper.eq("workblank_no", trackHead.getProductNo());
                    queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
                    List<LineStore> lineStores = lineStoreService.list(queryWrapper);

                    if(lineStores != null && lineStores.size() > 0){
                        return CommonResult.failed("产品编号已存在！");
                    } else {
                        LineStore lineStore = new LineStore();
                        lineStore.setTenantId(trackHead.getTenantId());
                        lineStore.setDrawingNo(trackHead.getDrawingNo());
                        lineStore.setMaterialNo(trackHead.getMaterialNo());
                        lineStore.setWorkblankNo(trackHead.getProductNo());
                        lineStore.setNumber(trackHead.getNumber());
                        lineStore.setUserNum(0);
                        lineStore.setStatus("0");
                        lineStore.setTrackNo(trackHead.getTrackNo());
                        lineStore.setMaterialType("1");
                        lineStore.setTrackType(trackHead.getTrackType());
                        lineStore.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                        lineStore.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                        lineStore.setCreateTime(new Date());
                        list.add(lineStore);
                    }
                } else if (trackHead.getTrackType().equals("1")){ //批次
                    for(int i = trackHead.getStartNo(); i <= trackHead.getEndNo(); i++){
                        LineStore lineStore = new LineStore();
                        lineStore.setTenantId(trackHead.getTenantId());
                        lineStore.setDrawingNo(trackHead.getDrawingNo());
                        lineStore.setMaterialNo(trackHead.getMaterialNo());

                        String productNo = trackHead.getProductNo() + " " + i;
                        if(!StringUtils.isNullOrEmpty(trackHead.getSuffixNo())){
                            productNo += " " + trackHead.getSuffixNo();
                        }
                        lineStore.setWorkblankNo(productNo);
                        lineStore.setNumber(1);
                        lineStore.setUserNum(0);
                        lineStore.setStatus("0");
                        lineStore.setTrackNo(trackHead.getTrackNo());
                        lineStore.setMaterialType("1");
                        lineStore.setTrackType("0");
                        lineStore.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                        lineStore.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                        lineStore.setCreateTime(new Date());
                        list.add(lineStore);
                    }
                }*/
                bool = trackHeadService.saveTrackHead(trackHead);
                if (bool) {

                    return CommonResult.success(trackHead, TRACK_HEAD_SUCCESS_MESSAGE);
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
    public CommonResult<TrackHead> updateTrackHead(@ApiParam(value = "跟单信息", required = true) @RequestBody TrackHead trackHead) {
        if (StringUtils.isNullOrEmpty(trackHead.getTrackNo())) {
            return CommonResult.failed(TRACK_HEAD_NO_NULL_MESSAGE);
        } else if (StringUtils.isNullOrEmpty(trackHead.getId())) {
            return CommonResult.failed(TRACK_HEAD_ID_NULL_MESSAGE);
        } else {
            boolean bool = trackHeadService.updataTrackHead(trackHead, trackHead.getTrackItems());
            if (bool) {
                //添加日志
                Action action = new Action();
                action.setActionType("1");
                action.setActionItem("2");
                action.setRemark("跟单号：" + trackHead.getTrackNo());
                actionService.saveAction(action);
                return CommonResult.success(trackHead, TRACK_HEAD_SUCCESS_MESSAGE);
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
        if (type.equals("0")) { //修改审批状态
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
            actionService.saveAction(action);
            return CommonResult.success(trackHeads, TRACK_HEAD_SUCCESS_MESSAGE);
        } else {
            return CommonResult.failed(TRACK_HEAD_FAILED_MESSAGE);
        }
    }

    @ApiOperation(value = "分页查询跟单", notes = "根据跟单号、计划号、产品编号、物料编码以及跟单状态分页查询跟单")
    @GetMapping("/track_head")
    public CommonResult<IPage<TrackHead>> selectTrackHead(@ApiParam(value = "开始时间") @RequestParam(required = false) String startDate,
                                                          @ApiParam(value = "结束时间") @RequestParam(required = false) String endDate,
                                                          @ApiParam(value = "id") @RequestParam(required = false) String id,
                                                          @ApiParam(value = "跟单编码") @RequestParam(required = false) String trackNo,
                                                          @ApiParam(value = "工作计划号") @RequestParam(required = false) String workPlanNo,
                                                          @ApiParam(value = "生产编码") @RequestParam(required = false) String productNo,
                                                          @ApiParam(value = "物料号码") @RequestParam(required = false) String materialNo,
                                                          @ApiParam(value = "跟单状态") @RequestParam(required = false) String status,
                                                          @ApiParam(value = "审批状态") @RequestParam(required = false) String approvalStatus,
                                                          @ApiParam(value = "排序方式") @RequestParam(required = false) String order,
                                                          @ApiParam(value = "排序列") @RequestParam(required = false) String orderCol,
                                                          @ApiParam(value = "是否试棒跟单 0否、1是") @RequestParam(required = false) String isTestBar,
                                                          @ApiParam(value = "工厂代码") @RequestParam(required = false) String branchCode,
                                                          @ApiParam(value = "租户id") @RequestParam(required = false) String tenantId,
                                                          @ApiParam(value = "图号") @RequestParam(required = false) int page,
                                                          @ApiParam(value = "图号") @RequestParam(required = false) int limit) {
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<TrackHead>();
        if (!StringUtils.isNullOrEmpty(startDate)) {
            queryWrapper.ge("create_time", startDate);
        }
        if (!StringUtils.isNullOrEmpty(endDate)) {
            queryWrapper.le("create_time", endDate);
        }
        if (!StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (!StringUtils.isNullOrEmpty(trackNo)) {
            queryWrapper.like("track_no", "%" + trackNo + "%");
        }
        if (!StringUtils.isNullOrEmpty(workPlanNo)) {
            queryWrapper.like("work_plan_no", "%" + workPlanNo + "%");
        }
        if (!StringUtils.isNullOrEmpty(productNo)) {
            queryWrapper.like("product_no", "%" + productNo + "%");
        }
        if (!StringUtils.isNullOrEmpty(materialNo)) {
            queryWrapper.like("material_no", "%" + materialNo + "%");
        }
        if (!StringUtils.isNullOrEmpty(status)) {
            queryWrapper.eq("status", status);
        }
        if (!StringUtils.isNullOrEmpty(approvalStatus)) {
            queryWrapper.eq("approval_status", approvalStatus);
        }
        if (!StringUtils.isNullOrEmpty(isTestBar)) {
            queryWrapper.eq("is_test_bar", isTestBar);
        }
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        if (!StringUtils.isNullOrEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        } else {
            //queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        }
        if (!StringUtils.isNullOrEmpty(orderCol)) {
            if (!StringUtils.isNullOrEmpty(order)) {
                if (order.equals("desc")) {
                    queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
                } else if (order.equals("asc")) {
                    queryWrapper.orderByAsc(StrUtil.toUnderlineCase(orderCol));
                }
            } else {
                queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
            }
        } else {
            queryWrapper.orderByDesc("modify_time");
        }
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        return CommonResult.success(trackHeadService.page(new Page<TrackHead>(page, limit), queryWrapper), TRACK_HEAD_SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "工艺跟踪", notes = "根据图号、工艺版本号分页查询跟单工艺信息")
    @GetMapping("/track_head/router")
    public CommonResult<IPage<TrackHead>> selectTrackHead(@ApiParam(value = "图号") @RequestParam(required = false) String drawingNo,
                                                          @ApiParam(value = "工艺版本") @RequestParam(required = false) String optVer,
                                                          @ApiParam(value = "页码", required = true) @RequestParam int page,
                                                          @ApiParam(value = "每页条数", required = true) @RequestParam int limit) {
        QueryWrapper<TrackHead> queryWrapper = new QueryWrapper<TrackHead>();
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            queryWrapper.like("th.drawing_no", "%" + drawingNo + "%");
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
            queryWrapper.like("th.track_no", trackNo);
        }
        if (!StringUtils.isNullOrEmpty(status)) {
            queryWrapper.eq("th.status", status);
        }
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            queryWrapper.like("th.drawing_no", drawingNo);
        }
        if (!StringUtils.isNullOrEmpty(productNo)) {
            queryWrapper.eq("th.product_no", productNo);
        }
        //增加逻辑判断，只查询合格证号为空的记录
        if (noCertNo) {
            queryWrapper.ne("th.certificate_No", null);
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

}
