package com.richfit.mes.produce.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.DevicePerson;
import com.richfit.mes.common.model.base.OperationTypeSpec;
import com.richfit.mes.common.model.base.RouterCheck;
import com.richfit.mes.common.model.base.SequenceSite;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.sys.Attachment;
import com.richfit.mes.common.model.sys.QualityInspectionRules;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.model.util.ActionUtil;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.aop.OperationLogAspect;
import com.richfit.mes.produce.dao.TrackCheckCountMapper;
import com.richfit.mes.produce.enmus.IdEnum;
import com.richfit.mes.produce.enmus.PublicCodeEnum;
import com.richfit.mes.produce.entity.BatchAddScheduleDto;
import com.richfit.mes.produce.entity.CountDto;
import com.richfit.mes.produce.entity.QueryQualityTestingDetailsVo;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.MaterialInspectionServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.*;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @author mafeng
 * @Description 跟单工序审核
 */
@Slf4j
@Api(value = "跟单质检审核和跟单调度审核", tags = {"跟单质检审核和跟单调度审核"})
@RestController
@RequestMapping("/api/produce/trackcheck")
public class TrackCheckController extends BaseController {

    @Autowired
    public TrackItemService trackItemService;
    @Autowired
    private TrackHeadService trackHeadService;
    @Autowired
    public TrackCheckService trackCheckService;
    @Autowired
    public TrackCheckDetailService trackCheckDetailService;
    @Autowired
    public TrackAssignService trackAssignService;
    @Autowired
    private TrackCheckCountMapper trackCheckCountMapper;
    @Autowired
    private LineStoreService lineStoreService;
    @Autowired
    private PlanService planService;
    @Resource
    private BaseServiceClient baseServiceClient;
    @Resource
    private NextProcessService nextProcessService;
    @Resource
    private PublicService publicService;
    @Resource
    private SystemServiceClient systemServiceClient;
    @Resource
    private TrackCompleteService trackCompleteService;
    @Resource
    private ProduceRoleOperationService roleOperationService;
    @Autowired
    private PhysChemOrderService physChemOrderService;
    @Autowired
    private MaterialInspectionServiceClient materialInspectionServiceClient;
    @Autowired
    private TrackHeadFlowService trackHeadFlowService;
    @Autowired
    private ActionService actionService;


    /**
     * ***
     * 分页查询待质检工序
     *
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "分页查询待质检工序", notes = "分页查询待质检工序")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "tiId", value = "跟单工序项ID", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/page")
    public CommonResult<IPage<TrackItem>> page(int page, int limit, String isExistQualityCheck, String isExistScheduleCheck, String isScheduleComplete, String startTime, String endTime, String trackNo, String productNo, String branchCode, String tenantId, Boolean isRecheck, String drawingNo, String order, String orderCol) {
        try {
            QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<TrackItem>();
            //车间
//            if (!StringUtils.isNullOrEmpty(branchCode)) {
//                queryWrapper.eq("branch_code", branchCode);
//            }
            //图号
            if (!StringUtils.isNullOrEmpty(drawingNo)) {
                DrawingNoUtil.queryLike(queryWrapper, "drawing_no", drawingNo);
            }
            if (!StringUtils.isNullOrEmpty(tenantId)) {
                queryWrapper.eq("tenant_id", tenantId);
            }
            //复检
            if (Boolean.TRUE.equals(isRecheck)) {
                queryWrapper.eq("is_recheck", 1);
            } else if (Boolean.FALSE.equals(isRecheck)) {
                //未质检
                queryWrapper.eq("is_quality_complete", 0);
                queryWrapper.and(wrapper -> wrapper.isNull("is_recheck").or().eq("is_recheck", 0));
            }
            //是否质检
            if (!StringUtils.isNullOrEmpty(isExistQualityCheck)) {
                //质检页面只查询指派给自己的质检信息
                queryWrapper.and(wrapper -> wrapper.eq("quality_check_by", SecurityUtils.getCurrentUser().getUsername()).or().eq("quality_check_by", "/"));
                queryWrapper.eq("is_exist_quality_check", Integer.parseInt(isExistQualityCheck));
            }
            //是否调度
            if (!StringUtils.isNullOrEmpty(isExistScheduleCheck)) {
                queryWrapper.eq("is_exist_schedule_check", Integer.parseInt(isExistScheduleCheck))
                        .eq("is_schedule_complete_show", 1);
            }
            //调度完成
            if (!StringUtils.isNullOrEmpty(isScheduleComplete)) {
                queryWrapper.eq("is_schedule_complete", Integer.parseInt(isScheduleComplete));
            }
            //根据跟单号查询
            if (!StringUtils.isNullOrEmpty(trackNo)) {
                trackNo = trackNo.replaceAll(" ", "");
                if (!StringUtils.isNullOrEmpty(isScheduleComplete)) {
                    queryWrapper.inSql("id", "select id from  produce_track_item where (is_quality_complete=1 or is_exist_quality_check=0) and track_head_id in ( select id from produce_track_head where replace(replace(replace(track_no, char(13), ''), char(10), ''),' ', '') like '%" + trackNo + "%')");
                } else {
                    queryWrapper.inSql("id", "select id from  produce_track_item where track_head_id in ( select id from produce_track_head where replace(replace(replace(track_no, char(13), ''), char(10), ''),' ', '') like '%" + trackNo + "%')");
                }
            }
            //产品编号
            if (!StringUtils.isNullOrEmpty(productNo)) {
                queryWrapper.like("product_no", productNo);
            }
            if (!StringUtils.isNullOrEmpty(startTime)) {
                queryWrapper.apply("UNIX_TIMESTAMP(modify_time) >= UNIX_TIMESTAMP('" + startTime + "')");
            }
            if (!StringUtils.isNullOrEmpty(endTime)) {
                Calendar calendar = new GregorianCalendar();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                calendar.setTime(sdf.parse(endTime));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                queryWrapper.apply("UNIX_TIMESTAMP(modify_time) <= UNIX_TIMESTAMP('" + sdf.format(calendar.getTime()) + " 00:00:00')");
            }
            if ("1".equals(isExistScheduleCheck)) {
                queryWrapper.inSql("id", "SELECT id FROM produce_track_item WHERE is_quality_complete = 1 OR is_exist_quality_check = 0");
            }
            //增加工序过滤(泵业新版，去掉)
//            ProcessFiltrationUtil.filtration(queryWrapper, systemServiceClient, roleOperationService);
            queryWrapper.eq("is_doing", 2);
            queryWrapper.eq("is_operation_complete", 1);
            OrderUtil.query(queryWrapper, orderCol, order);
            IPage<TrackItem> assigns = trackItemService.page(new Page<TrackItem>(page, limit), queryWrapper);
            //收集跟单分流表id 调度审核用
            List<String> flowIdList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(assigns.getRecords())) {
                flowIdList = assigns.getRecords().stream().map(x -> x.getFlowId()).collect(Collectors.toList());
            }
            List<TrackFlow> flowList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(flowIdList)) {
                QueryWrapper<TrackFlow> flowQueryWrapper = new QueryWrapper<>();
                flowQueryWrapper.in("id", flowIdList);
                flowList = trackHeadFlowService.list(flowQueryWrapper);
            }
            Map<String, TrackFlow> flowMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(flowList)) {
                flowMap = flowList.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));
            }
            for (TrackItem item : assigns.getRecords()) {
                TrackHead trackHead = trackHeadService.getById(item.getTrackHeadId());
                item.setTrackNo(trackHead.getTrackNo());
                item.setDrawingNo(trackHead.getDrawingNo());
                item.setQty(item.getNumber());
                item.setProductName(trackHead.getProductName());
                item.setWorkNo(trackHead.getWorkNo());
                item.setTrackType(trackHead.getTrackType());
                item.setTexture(trackHead.getTexture());
                item.setPartsName(trackHead.getMaterialName());
                item.setBatchNo(trackHead.getBatchNo());
                TrackFlow trackFlow = flowMap.get(item.getFlowId());
                item.setProductSourceName(trackFlow == null ? "" : trackFlow.getProductSourceName());//产品来源名称（热工）
            }
            return CommonResult.success(assigns);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * ***
     * 分页查询质检结果
     *
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "分页查询质检结果", notes = "分页查询质检结果")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "tiId", value = "跟单工序项ID", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/pageCheck")
    public CommonResult<IPage<TrackCheck>> pageCheck(int page, int limit, String startTime, String endTime, String trackNo, String productNo, String branchCode, String tenantId, String drawingNo, String order, String orderCol) {
        try {
            QueryWrapper<TrackCheck> queryWrapper = new QueryWrapper<TrackCheck>();
//            if (!StringUtils.isNullOrEmpty(branchCode)) {
//                queryWrapper.eq("track.branch_code", branchCode);
//            }
            if (!StringUtils.isNullOrEmpty(tenantId)) {
                queryWrapper.eq("track.tenant_id", tenantId);
            }
            if (!StringUtils.isNullOrEmpty(drawingNo)) {
                DrawingNoUtil.queryEq(queryWrapper, "track.drawing_no", drawingNo);
            }
            if (!StringUtils.isNullOrEmpty(trackNo)) {
                trackNo = trackNo.replaceAll(" ", "");
                queryWrapper.inSql("ti_id", "select id from  produce_track_item where track_head_id in ( select id from produce_track_head where replace(replace(replace(track_no, char(13), ''), char(10), ''),' ', '') like '%" + trackNo + "%')");
            }
            if (!StringUtils.isNullOrEmpty(productNo)) {
                queryWrapper.inSql("ti_id", "select id from  produce_track_item where product_no ='" + productNo + "'");
            }

            if (!StringUtils.isNullOrEmpty(startTime)) {
                queryWrapper.ge("date_format(track.modify_time, '%Y-%m-%d')", startTime);
            }
            if (!StringUtils.isNullOrEmpty(endTime)) {
                queryWrapper.le("date_format(track.modify_time, '%Y-%m-%d')", endTime);
            }
            queryWrapper.and(wrapper -> wrapper.eq("track.is_show", "1").or().isNull("track.is_show"));
            queryWrapper.eq("item.quality_check_by", SecurityUtils.getCurrentUser().getUsername());
            //增加工序过滤
//            ProcessFiltrationUtil.filtration(queryWrapper, systemServiceClient, roleOperationService);
            OrderUtil.query(queryWrapper, orderCol, order);
            IPage<TrackCheck> checks = trackCheckService.queryCheckPage(new Page<TrackCheck>(page, limit), queryWrapper);
            //收集跟单分流表id
            List<String> flowIdList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(checks.getRecords())) {
                flowIdList = checks.getRecords().stream().map(x -> x.getFlowId()).collect(Collectors.toList());
            }
            List<TrackFlow> flowList = new ArrayList<>();
            if (CollectionUtils.isNotEmpty(flowIdList)) {
                QueryWrapper<TrackFlow> flowQueryWrapper = new QueryWrapper<>();
                flowQueryWrapper.in("id", flowIdList);
                flowList = trackHeadFlowService.list(flowQueryWrapper);
            }
            Map<String, TrackFlow> flowMap = new HashMap<>();
            if (CollectionUtils.isNotEmpty(flowList)) {
                flowMap = flowList.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));
            }

            //查询所有报工数据
            List<String> list = checks.getRecords().stream().map(TrackCheck::getTiId).collect(Collectors.toList());
            //没查询到报工数据不进行查询
            Map<String, List<TrackComplete>> completeUserList = new HashMap<>();
            if (CollectionUtils.isNotEmpty(list)) {
                QueryWrapper<TrackComplete> completeQueryWrapper = new QueryWrapper<>();
                completeQueryWrapper.in("ti_id", list);
                List<TrackComplete> completeList = trackCompleteService.list(completeQueryWrapper);
                //获取所有人员信息
                List<String> collect = completeList.stream().map(TrackComplete::getUserId).collect(Collectors.toList());
                Map<String, TenantUserVo> userAccountList = systemServiceClient.queryByUserAccountList(collect);
                completeList.forEach(complete -> {
                    if (userAccountList.get(complete.getUserId()) != null) {
                        complete.setUserId(userAccountList.get(complete.getUserId()).getEmplName());
                    }
                });
                //根据工序ID分组报工信息
                completeUserList = completeList.stream().collect(Collectors.groupingBy(TrackComplete::getTiId));
            }

            for (TrackCheck check : checks.getRecords()) {
                TrackHead trackHead = trackHeadService.getById(check.getThId());
                TrackItem trackItem = trackItemService.getById(check.getTiId());
                if (null != trackHead) {
                    check.setDrawingNo(trackHead.getDrawingNo());
                    check.setTrackNo(trackHead.getTrackNo());
                    check.setProductName(trackHead.getProductName());
                    check.setWorkNo(trackHead.getWorkNo());
                    check.setMaterialName(trackHead.getMaterialName());
                }
                if (null != trackItem) {
                    check.setNumber(trackItem.getNumber());
                    check.setProductNo(trackItem.getProductNo());
                    check.setOptId(trackItem.getOptId());
                    check.setOptName(trackItem.getOptName());
                    check.setOptNo(trackItem.getOptNo());
                    check.setOptType(trackItem.getOptType());
                    check.setIsCurrent(trackItem.getIsCurrent());
                    check.setDealBy(trackItem.getQualityCheckBy());
                }
                TrackFlow trackFlow = flowMap.get(check.getFlowId());
                check.setProductSourceName(trackFlow == null ? "" : trackFlow.getProductSourceName());//产品来源名称（热工）
                //报工人
                if (null != completeUserList.get(check.getTiId())) {
                    String userName = completeUserList.get(check.getTiId()).stream().map(TrackComplete::getUserId).distinct().collect(Collectors.joining(","));
                    check.setUserName(userName);
                }
            }
            return CommonResult.success(checks);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * ***
     * 分页查询质检明细
     *
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "分页查询质检明细", notes = "分页查询质检明细")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "tiId", value = "跟单工序项ID", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/pageCheckDetail")
    public CommonResult<IPage<TrackCheckDetail>> pageCheckDetail(int page, int limit, String tiId) {
        try {
            QueryWrapper<TrackCheckDetail> queryWrapper = new QueryWrapper<TrackCheckDetail>();

            if (!StringUtils.isNullOrEmpty(tiId)) {
                queryWrapper.eq("ti_id", tiId);
            }
            queryWrapper.orderByDesc("modify_time");
            IPage<TrackCheckDetail> checks = trackCheckDetailService.page(new Page<TrackCheckDetail>(page, limit), queryWrapper);
            return CommonResult.success(checks);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "批量调度审核(新)", notes = "批量调度审核")
    @PostMapping("/batchAddSchedule")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> batchAddSchedule(@RequestBody BatchAddScheduleDto batchAddScheduleDto) {
        return trackCheckService.batchAddSchedule(batchAddScheduleDto);
    }

    @ApiOperation(value = "回滚质检审核", notes = "回滚质检审核")
    @ApiImplicitParam(name = "trackChecks", value = "trackCheck的ID", required = true, dataType = "List<String>", paramType = "query")
    @PostMapping("/rollbackQuality")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> rollbackQuality(@RequestBody List<String> trackChecks) {
        boolean bool = false;
        for (String trackCheckId : trackChecks) {
            TrackCheck trackCheck = trackCheckService.getById(trackCheckId);
            if (StringUtils.isNullOrEmpty(trackCheck.getId())) {
                return CommonResult.failed("关联工序ID编码不能为空！");
            }
            TrackItem item = trackItemService.getById(trackCheck.getTiId());
            if (null == item) {
                trackCheckService.removeById(trackCheck.getId());
                return CommonResult.failed("跟单工序已丢失，该审核信息将自动删除！");
            }
            //调度完成，则无法回滚
            if (item.getIsExistScheduleCheck() == 1 && item.getIsScheduleComplete() == 1) {
                return CommonResult.failed("工序【" + item.getOptName() + "】调度审核已完成，无法回滚质检审核！");
            }
            if (item.getIsExistScheduleCheck() == 0) {

                //判断后置工序是否已派工，如意派工，则无法回滚
                List<TrackItem> items = trackItemService.list(new QueryWrapper<TrackItem>().eq("track_head_id", item.getTrackHeadId()).orderByAsc("opt_sequence"));
                for (int i = 0; i < items.size(); i++) {
                    if (items.get(i).getOptSequence() > item.getOptSequence()) {
                        QueryWrapper<Assign> queryWrapper = new QueryWrapper<Assign>();
                        queryWrapper.eq("ti_id", items.get(i).getId());
                        List<Assign> result = trackAssignService.list(queryWrapper);
                        if (result.size() > 0) {
                            return CommonResult.failed("无法回滚，请先取消工序【" + items.get(i).getOptName() + "】的派工！");
                        } else {
                            //回滚激活当前工序，工序在制状态设置为0
                            for (int j = 0; j < items.size(); j++) {
                                if (Objects.equals(items.get(i).getOptSequence(), items.get(j).getOptSequence())) {
                                    items.get(j).setIsCurrent(0);
                                    items.get(j).setIsDoing(0);
                                    trackItemService.updateById(items.get(j));
                                }
                            }
                        }
                        break;
                    }
                }
            }
            //回滚后，当前工序状态为1，工序在制状态设置为0，是否最终完成为0，是否质检完成为0
            item.setIsCurrent(1);
            item.setCompleteQty(0.0);
            item.setIsFinalComplete("0");
            item.setIsQualityComplete(0);


            item.setQualityResult(-1);
            item.setQualityCheckBy(null);
            item.setQualityCompleteTime(null);
            item.setQualityQty(0);
            item.setQualityUnqty(0);
            trackItemService.updateById(item);

            trackCheckDetailService.remove(new QueryWrapper<TrackCheckDetail>().eq("ti_id", trackCheck.getTiId()));
            trackCheckService.removeById(trackCheck.getId());
            bool = true;
        }
        if (bool) {
            return CommonResult.success(true, "操作成功！");
        } else {
            return CommonResult.failed("操作失败，请重试！");
        }

    }

    @ApiOperation(value = "删除质检明细", notes = "根据id质检明细")
    @ApiImplicitParam(name = "ids", value = "编码", required = true, dataType = "String[]", paramType = "query")
    @PostMapping("/deleteCheckDetails")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<TrackCheckDetail> deleteCheckDetails(@RequestBody String[] ids) {

        for (int i = 0; i < ids.length; i++) {
            trackCheckDetailService.removeById(ids[i]);
        }
        return CommonResult.success(null, "删除成功！");

    }

    @ApiOperation(value = "回滚调度审核", notes = "回滚调度审核")
    @ApiImplicitParam(name = "trackItems", value = "跟单工序项", required = true, dataType = "TrackItem[]", paramType = "query")
    @PostMapping("/rollbackSchedule")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<TrackItem[]> rollbackSchedule(@RequestBody TrackItem[] trackItems) {
        boolean bool = true;
        for (TrackItem item : trackItems) {
            if (StringUtils.isNullOrEmpty(item.getId())) {
                return CommonResult.failed("关联工序ID编码不能为空！");
            }

            //判断下个工序是否已派工，如果以派工，则提醒，无法回滚。否则将下个工序重置
            List<TrackItem> items = trackItemService.list(new QueryWrapper<TrackItem>().eq("track_head_id", item.getTrackHeadId()).orderByAsc("opt_sequence"));
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getOptSequence() > item.getOptSequence()) {
                    QueryWrapper<Assign> queryWrapper = new QueryWrapper<Assign>();
                    queryWrapper.eq("ti_id", items.get(i).getId());
                    List<Assign> result = trackAssignService.list(queryWrapper);
                    if (result.size() > 0) {
                        return CommonResult.failed("无法回滚，请先取消工序【" + items.get(i).getOptName() + "】的派工！");
                    } else {
                        for (int j = 0; j < items.size(); j++) {
                            if (items.get(i).getOptSequence().equals(items.get(j).getOptSequence())) {
                                items.get(j).setIsCurrent(0);
                                items.get(j).setIsDoing(0);
                                trackItemService.updateById(items.get(j));
                            }
                        }
                    }
                    break;
                }
            }
            //回滚后，当前工序状态为1，工序在制状态设置为0，是否最终完成为0，是否调度完成为0
            item.setIsCurrent(1);
            item.setCompleteQty(0.0);
            item.setIsFinalComplete("0");
            item.setIsScheduleComplete(0);
            trackItemService.updateById(item);
        }
        if (bool) {
            return CommonResult.success(trackItems, "操作成功！");
        } else {
            return CommonResult.failed("操作失败，请重试！");
        }

    }


    @ApiOperation(value = "质量统计", notes = "质量统计")
    @GetMapping("/count")
    public CommonResult<List<CountDto>> count(String dateType, String startTime, String endTime) {

        if (dateType.equals("0")) {
            dateType = "%Y%m%d";
        }
        if (dateType.equals("1")) {
            dateType = "%Y%u";
        }
        if (dateType.equals("2")) {
            dateType = "%Y%m";
        }
        List<CountDto> result = trackCheckService.count(dateType, startTime, endTime);
        return CommonResult.success(result, "操作成功！");
    }

    @ApiOperation(value = "统计不合格类型", notes = "统计不合格类型")
    @GetMapping("/countReason")
    public CommonResult<List<CountDto>> countReason(String dateType, String startTime, String endTime) {

        if (dateType.equals("0")) {
            dateType = "%Y%m%d";
        }
        if (dateType.equals("1")) {
            dateType = "%Y%u";
        }
        if (dateType.equals("2")) {
            dateType = "%Y%m";
        }
        List<CountDto> result = trackCheckCountMapper.countReason(startTime, endTime);
        return CommonResult.success(result, "操作成功！");
    }


    @ApiOperation(value = "统计完工", notes = "统计完工")
    @GetMapping("/countComplete")
    public CommonResult<List<CountDto>> countComplete(String dateType, String startTime, String endTime) {

        if (dateType.equals("0")) {
            dateType = "%Y%m%d";
        }
        if (dateType.equals("1")) {
            dateType = "%Y%u";
        }
        if (dateType.equals("2")) {
            dateType = "%Y%m";
        }
        List<CountDto> result = trackCheckCountMapper.countComplete(dateType, startTime, endTime);
        return CommonResult.success(result, "操作成功！");
    }


    @ApiOperation(value = "激活工序", notes = "激活工序")
    @GetMapping("/active_trackitem")
    public CommonResult<List<TrackItem>> activeTrackItem(TrackItem curItem) {
        List<TrackItem> items = trackItemService.list(new QueryWrapper<TrackItem>().eq("track_head_id", curItem.getTrackHeadId()).orderByAsc("opt_sequence"));
        List<TrackItem> activeItems = new ArrayList();

        //可否跳转下个工序
        Boolean curOrderEnable = true;
        //下道激活工序
        int nextOrder = -1;


        if (((curItem.getIsExistQualityCheck() == 1 && curItem.getIsQualityComplete() == 1) || curItem.getIsExistQualityCheck() == 0) && ((curItem.getIsExistScheduleCheck() == 1 && curItem.getIsScheduleComplete() == 1) || curItem.getIsExistScheduleCheck() == 0)) {
        } else {
            curOrderEnable = false;
        }

        //获取下道激活工序的顺序号
        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getOptSequence() > curItem.getOptSequence()) {
                nextOrder = items.get(i).getOptSequence();
                break;
            }
        }
        for (int i = 0; i < items.size(); i++) {
            // 将当前工序设置为完成，当前工序为0，在制状态为0，是否最终完成为1
            if (curItem.getOptSequence() > -1 && curOrderEnable) {
                if (items.get(i).getOptSequence().equals(curItem.getOptSequence())) {
                    items.get(i).setIsCurrent(0);
                    items.get(i).setIsDoing(0);
                    items.get(i).setIsFinalComplete("1");
                    trackItemService.updateById(items.get(i));
                    if (nextOrder == -1) {
                        TrackHead trackHead = trackHeadService.getById(items.get(i).getTrackHeadId());
                        trackHead.setStatus("2");
                        trackHead.setCompleteTime(new Date());
                        trackHeadService.updateById(trackHead);

                        //设置产品完工
                        lineStoreService.changeStatus(trackHead);

                        //设置计划状态
                        planService.updatePlanStatus(trackHead.getWorkPlanNo(), trackHead.getTenantId());
                        /*QueryWrapper<LineStore> queryWrapper = new QueryWrapper<LineStore>();
                        queryWrapper.eq("track_no", trackHead.getTrackNo());
                        List<LineStore> lineStore =  lineStoreService.list(queryWrapper);
                        for(int ii =0;i<lineStore.size();i++) {
                            lineStore.get(ii).setStatus("1");
                            lineStoreService.updateById(lineStore.get(ii));
                        }*/
                    }
                }

                // 将下个工序设置为激活工序，当前工序为1，在制状态为0
                if (nextOrder > -1) {
                    if (items.get(i).getOptSequence() == nextOrder) {
                        items.get(i).setIsCurrent(1);
                        items.get(i).setIsDoing(0);
                        activeItems.add(items.get(i));
                        trackItemService.updateById(items.get(i));
                        //如果自动派工，那么执行派工操作
                        if (null != items.get(i - 1) && null != items.get(i - 1).getIsAutoSchedule() && items.get(i - 1).getIsAutoSchedule() == 1) {
                            String deviceId = "";
                            String userId = "ba7dd26f0a669f9e09343f9b579b0321";
                            List<SequenceSite> sequenceSites = baseServiceClient.getSequenceDevice(items.get(i).getOptId(), null, "device", null, "1").getData();
                            if (sequenceSites.size() == 1) {
                                deviceId = sequenceSites.get(0).getSiteId();
                                if (!StringUtils.isNullOrEmpty(deviceId)) {
                                    List<DevicePerson> devicePersons = baseServiceClient.getDevicePerson(deviceId, null, null, "1").getData();
                                    if (devicePersons.size() == 1) {
                                        userId = devicePersons.get(0).getUserId();
                                    }
                                }
                            }
                            if (!StringUtils.isNullOrEmpty(deviceId) && !StringUtils.isNullOrEmpty(userId)) {
                                TrackHead trackHead = trackHeadService.getById(items.get(i).getTrackHeadId());
                                Assign assign = new Assign();
                                assign.setTiId(items.get(i).getId());
                                assign.setTrackId(items.get(i).getTrackHeadId());
                                assign.setTrackNo(trackHead.getTrackNo());
                                assign.setQty(items.get(i).getNumber());
                                assign.setAvailQty(items.get(i).getNumber());
                                assign.setDeviceId(deviceId);
                                assign.setUserId(userId);
                                assign.setPriority(0);
                                assign.setState(0);
                                assign.setAssignBy("system");
                                assign.setBranchCode(trackHead.getBranchCode());
                                assign.setTenantId(trackHead.getTenantId());
                                if (null != SecurityUtils.getCurrentUser()) {

                                    assign.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                                }
                                assign.setModifyTime(new Date());
                                assign.setCreateTime(new Date());
                                trackAssignService.save(assign);
                                // 将跟单工序可派工数量改为0
                                items.get(i).setAssignableQty(0);
                                trackItemService.updateById(items.get(i));
                                //将跟单状态改为在制
                                trackHead.setStatus("1");
                                trackHeadService.updateById(trackHead);

                            }
                        }
                    }
                }
            }
        }
        return CommonResult.success(activeItems, "");
    }


    @ApiOperation(value = "质检审核(新)", notes = "质检审核")
    @PostMapping("/qualityTesting")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> qualityTesting(@RequestBody List<TrackCheck> trackCheckList) {
        //获取request
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        for (TrackCheck trackCheck : trackCheckList) {
            if (StringUtils.isNullOrEmpty(trackCheck.getThId())) {
                return CommonResult.failed("关联工序ID编码不能为空！");
            }
            if (StringUtils.isNullOrEmpty(trackCheck.getTiId())) {
                return CommonResult.failed("关联跟单ID编码不能为空！");
            }
            String tenantId = SecurityUtils.getCurrentUser().getTenantId();
            trackCheck.setTenantId(tenantId);
            trackCheck.setDealTime(new Date());
            TrackItem item = trackItemService.getById(trackCheck.getTiId());
            //处理下工序
            if (!StringUtils.isNullOrEmpty(trackCheck.getNextProcess())) {
                saveNextProcess(trackCheck.getNextProcess(), trackCheck.getTiId(), trackCheck.getProcessMode());
            }
            item.setIsQualityComplete(1);
            item.setQualityCheckBy(SecurityUtils.getCurrentUser().getUsername());
            item.setQualityCompleteTime(new Date());
            item.setQualityQty(trackCheck.getQualify());
//                item.setQualityUnqty(item.getBatchQty() - trackCheck.getQualify());
            item.setQualityUnqty(trackCheck.getUnqualify());
            //查询质检规则
            CommonResult<QualityInspectionRules> rules = systemServiceClient.queryQualityInspectionRulesById(trackCheck.getResult());
            //通过规则是否下一步控制已质检是否显示
            item.setRuleId(rules.getData().getId());
            item.setRuleName(rules.getData().getStateName());
            if (1 == rules.getData().getIsNext()) {
                trackCheck.setIsShow("1");
                item.setIsRecheck("0");
                item.setIsScheduleCompleteShow(1);
            } else {
                trackCheck.setIsShow("0");
                item.setIsRecheck("1");
                item.setIsScheduleCompleteShow(0);
            }
            if (1 == rules.getData().getIsCancellation()) {
                //调用报废流程
                trackHeadService.trackHeadUseless(item.getTrackHeadId());
            }
            //判断是否还有调度,没有提前赋值最终完成状态
            if (item.getIsExistScheduleCheck() == 0) {
                item.setIsFinalComplete("1");
                item.setFinalCompleteTime(new Date());
            }
            trackItemService.updateById(item);
            trackCheck.setFlowId(item.getFlowId());
            //调用TrackHeadService 获取图号
            TrackHead trackHead = trackHeadService.getById(item.getTrackHeadId());
            trackCheck.setDrawingNo(trackHead.getDrawingNo());
            trackCheck.setRuleName(rules.getData().getStateName());
            if (!StringUtils.isNullOrEmpty(trackCheck.getId())) {
                trackCheck.setModifyTime(new Date());
                trackCheckService.updateById(trackCheck);
            } else {
                trackCheck.setCreateTime(new Date());
                trackCheck.setModifyTime(new Date());
                trackCheckService.save(trackCheck);
            }
            //控制是否下一步
            if (1 == rules.getData().getIsNext() && 1 == item.getIsCurrent()) {
                Map<String, String> map = new HashMap<>(3);
                map.put(IdEnum.FLOW_ID.getMessage(), item.getFlowId());
                map.put(IdEnum.TRACK_ITEM_ID.getMessage(), trackCheck.getTiId());
                map.put(IdEnum.TRACK_HEAD_ID.getMessage(), trackCheck.getThId());
                publicService.publicUpdateState(map, PublicCodeEnum.QUALITY_TESTING.getCode());
            }
            item.setIsPrepare(rules.getData().getIsGiveTime());
            UpdateWrapper<TrackComplete> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("ti_id", item.getId()).set("is_prepare", rules.getData().getIsGiveTime());
            trackCompleteService.update(updateWrapper);
            //处理审核详情信息
            if (null != trackCheck.getCheckDetailsList()) {
                for (TrackCheckDetail trackCheckDetail : trackCheck.getCheckDetailsList()) {
                    trackCheckDetail.setTenantId(tenantId);
                    trackCheckDetail.setBranchCode(trackCheck.getBranchCode());
                    trackCheckDetail.setFlowId(item.getFlowId());
                    trackCheckDetail.setTrackCheckId(trackCheck.getId());
                    if (StringUtils.isNullOrEmpty(trackCheckDetail.getId())) {
                        List<TrackCheckDetail> list = trackCheckDetailService.list(new QueryWrapper<TrackCheckDetail>().eq("ti_id", trackCheckDetail.getTiId()).eq("check_id", trackCheckDetail.getCheckId()));
                        if (!list.isEmpty()) {
                            trackCheckDetailService.updateById(trackCheckDetail);
                        } else {
                            trackCheckDetailService.save(trackCheckDetail);
                        }
                    } else {
                        trackCheckDetailService.updateById(trackCheckDetail);
                    }
                }
            }
            //保存质检记录
            actionService.saveAction(ActionUtil.buildAction
                    (trackCheck.getBranchCode(), "4", "2", "质检审核，跟单号:" + trackHead.getTrackNo() + "，质检结果:" + trackCheck.getResult(),
                            OperationLogAspect.getIpAddress(request)));
        }
        return CommonResult.success(Boolean.TRUE);
    }

    //下工序保存数据
    private boolean saveNextProcess(String nextProcessNumber, String tiId, String processMode) {
        if (!StringUtils.isNullOrEmpty(nextProcessNumber) && !StringUtils.isNullOrEmpty(processMode)) {
            //获取当前工序
            TrackItem trackItem = trackItemService.getById(tiId);
            trackItem.setIsNotarize(1);
            trackItem.setIsExistQualityCheck(1);
            trackItemService.updateById(trackItem);
            //获取下工序
            //用传入下工序获取 下工序位置
            TrackItem nextItem = trackItemService.getById(nextProcessNumber);
            QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("flow_id", trackItem.getFlowId());
            queryWrapper.eq("original_opt_sequence", nextItem.getOriginalOptSequence());
            TrackItem nextTrackItem = trackItemService.getOne(queryWrapper);

            //先删除
            QueryWrapper<NextProcess> removeWrapper = new QueryWrapper<>();
            removeWrapper.eq("current_process_id", tiId);
            nextProcessService.remove(removeWrapper);
            //在新增
            NextProcess nextProcess = new NextProcess();
            nextProcess.setCurrentProcessId(tiId);
            nextProcess.setProcessName(nextTrackItem.getOptName());
            nextProcess.setNextProcessId(nextTrackItem.getId());
            nextProcess.setProcessMode(processMode);
            nextProcess.setOptSequence(nextTrackItem.getSequenceOrderBy().toString());
            return nextProcessService.save(nextProcess);
        }
        return false;
    }


    @ApiOperation(value = "查询质检审核条件详情信息(新)", notes = "查询质检审核条件详情信息(新)")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "optId", value = "工序Id", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "optType", value = "工序Id", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "branchCode", value = "车间", required = true, paramType = "query", dataType = "string"),
    })
    @GetMapping("/queryQualityTestingDetails")
    public CommonResult<QueryQualityTestingDetailsVo> queryQualityTestingDetails(String optId, String optType, String branchCode) {
        //检查内容 质量资料
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        QueryQualityTestingDetailsVo queryQualityTestingDetailsVo = new QueryQualityTestingDetailsVo();
        queryQualityTestingDetailsVo.setRouterCheckList(baseServiceClient.queryRouterList(optId, "检查内容", branchCode, tenantId));
        List<OperationTypeSpec> operationTypeSpecs = baseServiceClient.queryOperationTypeSpecByType(optType, branchCode, tenantId);
        if (CollectionUtils.isNotEmpty(operationTypeSpecs)) {
            List<RouterCheck> routerList = new ArrayList<>();
            for (OperationTypeSpec operationTypeSpec : operationTypeSpecs) {
                RouterCheck routerCheck = new RouterCheck();
                routerCheck.setName(operationTypeSpec.getPropertyName());
                routerCheck.setDefualtValue(operationTypeSpec.getPropertyValue());
                routerList.add(routerCheck);
            }
            queryQualityTestingDetailsVo.setOperationTypeSpecs(routerList);
        } else {
            queryQualityTestingDetailsVo.setOperationTypeSpecs(baseServiceClient.queryRouterList(optId, "质量资料", branchCode, tenantId));
        }
        return CommonResult.success(queryQualityTestingDetailsVo);
    }

    @ApiOperation(value = "查询质检工序列表(新)", notes = "查询质检工序列表(新)")
    @ApiImplicitParam(name = "tiId", value = "跟单工序Id", required = true, paramType = "query", dataType = "string")
    @GetMapping("/queryProcessList")
    public CommonResult<List<NextProcess>> queryProcessLista(String tiId) {
        QueryWrapper<NextProcess> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("next_process_id", tiId);
        queryWrapper.orderByAsc("create_time");
        List<NextProcess> nextProcessList = nextProcessService.list(queryWrapper);
        NextProcess process = new NextProcess();
        process.setCurrentProcessId(tiId);
        nextProcessList.add(process);
        //查询所有报工数据
        List<String> list = nextProcessList.stream().map(NextProcess::getCurrentProcessId).collect(Collectors.toList());
        QueryWrapper<TrackComplete> completeQueryWrapper = new QueryWrapper<>();
        completeQueryWrapper.in("ti_id", list);
        List<TrackComplete> completeList = trackCompleteService.list(completeQueryWrapper);
        //获取所有人员信息
        List<String> collect = completeList.stream().map(TrackComplete::getCompleteBy).collect(Collectors.toList());
        Map<String, TenantUserVo> userAccountList = systemServiceClient.queryByUserAccountList(collect);
        completeList.forEach(complete -> {
            if (userAccountList.get(complete.getUserId()) != null) {
                complete.setUserId(userAccountList.get(complete.getUserId()).getEmplName());
            }
        });
        //根据工序ID分组报工信息
        Map<String, List<TrackComplete>> completeUserList = completeList.stream().collect(Collectors.groupingBy(TrackComplete::getTiId));
        for (NextProcess nextProcess : nextProcessList) {
            TrackItem trackItem = trackItemService.getById(nextProcess.getCurrentProcessId());
            nextProcess.setProcessName(trackItem.getOptName());
            nextProcess.setOptSequence(trackItem.getSequenceOrderBy().toString());
            nextProcess.setOptId(trackItem.getOptId());
            nextProcess.setOptType(trackItem.getOptType());
            String userName = completeUserList.get(nextProcess.getCurrentProcessId()).stream().map(TrackComplete::getUserId).collect(Collectors.joining(","));
            nextProcess.setUserName(userName);
        }
        return CommonResult.success(nextProcessList);
    }

    @ApiOperation(value = "查询质检审核详情(新)", notes = "查询质检审核详情(新)")
    @ApiImplicitParam(name = "tiId", value = "工序Id", required = true, paramType = "query", dataType = "string")
    @GetMapping("/queryQualityTestingResult")
    public CommonResult<TrackCheck> queryQualityTestingResult(String tiId) {
        return CommonResult.success(trackCheckDetailService.queryQualityTestingResult(tiId));
    }

    @ApiOperation(value = "查询质检审核文件(新)", notes = "查询质检审核文件(新)")
    @ApiImplicitParam(name = "tiId", value = "工序Id", required = true, paramType = "query", dataType = "string")
    @GetMapping("/getAttachmentListByTiId")
    public CommonResult<List<Attachment>> getAttachmentListByTiId(String tiId) {
        return CommonResult.success(trackCheckDetailService.getAttachmentListByTiId(tiId));
    }

    @ApiOperation(value = "查询下工序(新)", notes = "查询下工序(新)")
    @ApiImplicitParam(name = "tiId", value = "工序Id", required = true, paramType = "query", dataType = "string")
    @GetMapping("/getItemList")
    public CommonResult<List<TrackItem>> getItemList(String tiId) {
        return CommonResult.success(trackCheckService.getItemList(tiId));
    }

    @ApiOperation(value = "查询首页展示的未质检/未报工/未派工数据(新)", notes = "查询未质检数量(新)")
    @ApiImplicitParam(name = "branchCode", value = "工厂代码", required = true, paramType = "query", dataType = "string")
    @GetMapping("/queryNumber")
    public CommonResult<Map<String, Integer>> queryNumber(String branchCode) {
        Map<String, Integer> num = new HashMap<>(3);
        //没有车间直接返回0
        if (StrUtil.isBlank(branchCode)) {
            //未质检数量
            num.put("qualityTestingNumber", 0);
            //未报工
            num.put("workNumber", 0);
            //未派工
            num.put("dispatchingNumber", 0);
            return CommonResult.success(num);
        }
        //未质检数量
        num.put("qualityTestingNumber", trackCheckService.qualityTestingNumber(branchCode));
        //未报工
        num.put("workNumber", trackAssignService.workNumber(branchCode));
        //未派工
        num.put("dispatchingNumber", trackAssignService.queryDispatchingNumber(branchCode));
        return CommonResult.success(num);
    }

    @ApiOperation(value = "质检信息查询", notes = "质检信息查询")
    @GetMapping("/find")
    public CommonResult<List<TrackCheck>> find(@ApiParam(value = "跟单工序项ID") @RequestParam(required = false) String tiId) {

        QueryWrapper<TrackCheck> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(!StrUtil.isBlank(tiId), "ti_id", tiId);
        queryWrapper.orderByAsc("modify_time");
        List<TrackCheck> result = trackCheckService.list(queryWrapper);
        return CommonResult.success(result, "操作成功！");
    }

    @ApiOperation(value = "查询质检规则是否被使用", notes = "查询质检规则是否被使用")
    @GetMapping("/count_query_rules")
    public CommonResult<Boolean> countQueryRules(String rulesId) {
        return CommonResult.success(trackCheckService.countQueryRules(rulesId));
    }

    @ApiOperation(value = "质检待审核查询(新)", notes = "质检待审核查询(新)")
    @GetMapping("/query_quality_page")
    public CommonResult<IPage<TrackItem>> queryQualityPage(int page, int limit, int isExistQualityCheck, String isScheduleComplete, String startTime, String endTime, String trackNo, String productNo, String tenantId, Boolean isRecheck, String drawingNo, String order, String orderCol, String branchCode) {
        return trackCheckService.queryQualityPage(page, limit, branchCode, isExistQualityCheck, isScheduleComplete, startTime, endTime, trackNo, productNo, tenantId, isRecheck, drawingNo, order, orderCol);
    }


    @ApiOperation(value = "调度待审核/已审核查询(新)", notes = "调度待审核/已审核查询(新)")
    @GetMapping("/query_dispatch_page")
    public CommonResult<IPage<TrackItem>> queryDispatchPage(int page, int limit, String isExistScheduleCheck, String isScheduleComplete, String startTime, String endTime, String trackNo, String productNo, String branchCode, String tenantId, String drawingNo, String order, String orderCol) {
        return trackCheckService.queryDispatchPage(page, limit, isExistScheduleCheck, isScheduleComplete, startTime, endTime, trackNo, productNo, branchCode, tenantId, drawingNo, order, orderCol);
    }

}
