package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.TrackCheck;
import com.richfit.mes.common.model.produce.TrackFlow;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.TrackCheckCountMapper;
import com.richfit.mes.produce.dao.TrackCheckMapper;
import com.richfit.mes.produce.entity.CountDto;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.MaterialInspectionServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author mafeng
 * @Description 质检结果
 */
@Service
public class TrackCheckServiceImpl extends ServiceImpl<TrackCheckMapper, TrackCheck> implements TrackCheckService {

    @Autowired
    private TrackCheckMapper trackCheckMapper;

    @Autowired
    private TrackCheckCountMapper trackCheckCountMapper;

    @Resource
    private TrackItemService trackItemService;


    @Autowired
    private TrackHeadService trackHeadService;
    @Autowired
    public TrackCheckService trackCheckService;
    @Autowired
    public TrackCheckDetailService trackCheckDetailService;
    @Autowired
    public TrackAssignService trackAssignService;
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

    public List<CountDto> count(String dateType, String startTime, @Param("endTime") String endTime) {
        return trackCheckCountMapper.count(dateType, startTime, endTime);
    }

    @Override
    public List<TrackItem> getItemList(String tiId) {
        TrackItem trackItem = trackItemService.getById(tiId);
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("flow_id", trackItem.getFlowId());
        queryWrapper.gt("opt_sequence", trackItem.getOptSequence());
        queryWrapper.orderByAsc("opt_sequence");
        return trackItemService.list(queryWrapper);
    }

    @Override
    public Integer qualityTestingNumber(String branchCode) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_exist_quality_check", 1);
        queryWrapper.eq("is_quality_complete", 0);
        queryWrapper.eq("quality_check_by", SecurityUtils.getCurrentUser().getUsername());
        queryWrapper.eq("branch_code", branchCode);
        return trackItemService.count(queryWrapper);
    }

    @Override
    public IPage<TrackCheck> queryCheckPage(Page<TrackCheck> page, QueryWrapper<TrackCheck> qw) {
        return trackCheckMapper.queryTrackCheckPage(page, qw);
    }

    @Override
    public Boolean countQueryRules(String rulesId) {
        QueryWrapper<TrackCheck> queryWrapper = new QueryWrapper<TrackCheck>();
        queryWrapper.eq("result", rulesId);
        return this.count(queryWrapper) > 0;
    }

    @Override
    public CommonResult<IPage<TrackItem>> queryQualityPage(int page, int limit, String branchCode, int isExistQualityCheck, String isScheduleComplete, String startTime, String endTime, String trackNo, String productNo, String tenantId, Boolean isRecheck, String drawingNo, String order, String orderCol) {
        try {
            QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<TrackItem>();
            publicQuery(isScheduleComplete, startTime, endTime, trackNo, productNo, tenantId, drawingNo, queryWrapper);
            //复检
            if (Boolean.TRUE.equals(isRecheck)) {
                queryWrapper.eq("is_quality_complete", 1);
                queryWrapper.eq("is_recheck", 1);
            } else if (Boolean.FALSE.equals(isRecheck)) {
                //未质检
                queryWrapper.eq("is_quality_complete", 0);
                queryWrapper.and(wrapper -> wrapper.isNull("is_recheck").or().eq("is_recheck", 0));
            }
            queryWrapper.and(wrapper -> wrapper.and(w -> w.eq("is_exist_quality_check", isExistQualityCheck).eq("is_operation_complete", 1).and(w2 -> w2.eq("quality_check_by", SecurityUtils.getCurrentUser().getUsername()).or(wrapper1 -> wrapper1.eq("quality_check_by", "/").eq("branch_code", branchCode)))).or(w3 -> w3.eq("opt_type", 5).eq("branch_code", branchCode)));
            queryWrapper.eq("is_current", 1);
            OrderUtil.query(queryWrapper, orderCol, order);
            IPage<TrackItem> assigns = trackItemService.page(new Page<TrackItem>(page, limit), queryWrapper);
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
            }
            return CommonResult.success(assigns);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @Override
    public CommonResult<IPage<TrackItem>> queryDispatchPage(int page, int limit, String isExistScheduleCheck, String isScheduleComplete, String startTime, String endTime, String trackNo, String productNo, String branchCode, String tenantId, String drawingNo, String order, String orderCol) {
        try {
            QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<TrackItem>();
            publicQuery(isScheduleComplete, startTime, endTime, trackNo, productNo, tenantId, drawingNo, queryWrapper);
            //车间
            if (!StringUtils.isNullOrEmpty(branchCode)) {
                queryWrapper.eq("branch_code", branchCode);
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
            if ("1".equals(isExistScheduleCheck)) {
                queryWrapper.and(wrapper -> wrapper.eq("is_quality_complete", 1).or().eq("is_exist_quality_check", 0));
//                queryWrapper.inSql("id", "SELECT id FROM produce_track_item WHERE is_quality_complete = 1 OR is_exist_quality_check = 0");
            }
            queryWrapper.eq("is_current", 1)
                    .eq("is_operation_complete", 1);
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

    private void publicQuery(String isScheduleComplete, String startTime, String endTime, String trackNo, String productNo, String tenantId, String drawingNo, QueryWrapper<TrackItem> queryWrapper) throws ParseException {
        //图号
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            DrawingNoUtil.queryLike(queryWrapper, "drawing_no", drawingNo);
        }
        if (!StringUtils.isNullOrEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
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
    }
}
