package com.richfit.mes.produce.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Device;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.TrackAssignMapper;
import com.richfit.mes.produce.dao.TrackAssignPersonMapper;
import com.richfit.mes.produce.dao.TrackHeadMapper;
import com.richfit.mes.produce.enmus.IdEnum;
import com.richfit.mes.produce.enmus.InspectionRecordTypeEnum;
import com.richfit.mes.produce.enmus.PublicCodeEnum;
import com.richfit.mes.produce.entity.CompleteDto;
import com.richfit.mes.produce.entity.ProduceInspectionRecordDto;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.quality.InspectionPowerService;
import com.richfit.mes.produce.utils.Code;
import com.richfit.mes.produce.utils.WordUtil;
import freemarker.template.TemplateException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/***
 * 探伤记录
 * @author renzewen
 */
@Service
@Slf4j
public class ProduceInspectionRecordService {

    private final static String CHECK = "check";

    private final static String AUDIT = "audit";

    private final static int IS_STATUS = 1;
    private final static int NO_STATUS = 0;
    private final static int BACKOUT_STATUS = 2;
    private final static int IS_SCHEDULE = 1;
    @Autowired
    private ProduceInspectionRecordMtService produceInspectionRecordMtService;
    @Autowired
    private ProduceInspectionRecordRtService produceInspectionRecordRtService;
    @Autowired
    private ProduceInspectionRecordPtService produceInspectionRecordPtService;
    @Autowired
    private ProduceInspectionRecordUtService produceInspectionRecordUtService;

    @Autowired
    private ProduceDefectsInfoService produceDefectsInfoService;
    @Autowired
    private ProduceItemInspectInfoService produceItemInspectInfoService;
    @Autowired
    private WordUtil wordUtil;
    @Autowired
    private SystemServiceClient systemServiceClient;
    @Autowired
    private TrackItemInspectionService trackItemInspectionService;
    @Autowired
    private TrackHeadMapper trackHeadMapper;
    @Autowired
    private ProbeInfoService probeInfoService;
    @Autowired
    private CodeRuleService codeRuleService;
    @Autowired
    private TrackHeadService trackHeadService;
    @Resource
    private TrackItemService trackItemService;
    @Resource
    private TrackAssignService trackAssignService;
    @Resource
    public PublicService publicService;
    @Resource
    private TrackCompleteCacheService trackCompleteCacheService;
    @Autowired
    private TrackCompleteService trackCompleteService;
    @Autowired
    private TrackAssignPersonMapper trackAssignPersonMapper;
    @Autowired
    private TrackAssignPersonService trackAssignPersonService;
    @Autowired
    private TrackAssignMapper trackAssignMapper;
    @Autowired
    private InspectionPowerService inspectionPowerService;


    /**
     * 查询跟单工序探伤列表
     *
     * @param page
     * @param limit
     * @param startTime
     * @param endTime
     * @param trackNo
     * @param productName
     * @param branchCode
     * @param tenantId
     * @param isAudit
     * @return
     */
    public IPage<TrackItemInspection> page(int page, int limit, String startTime, String endTime, String trackNo, String productName, String productNo, String branchCode, String tenantId, String isAudit) {

        //跟单工序查询
        QueryWrapper<TrackItemInspection> queryWrapper = getTrackItemInspectionQueryWrapper(startTime, endTime, trackNo, productName, productNo, branchCode, tenantId, isAudit);

        queryWrapper.inSql("id", "select id from  produce_track_item_inspection where id in ( select ti_id from produce_assign where user_id like'%" + SecurityUtils.getCurrentUser().getUsername() + "%')");

        IPage<TrackItemInspection> trackItemInspections = trackItemInspectionService.page(new Page<TrackItemInspection>(page, limit), queryWrapper);
        //为跟单工序赋跟单的一些属性
        setHeadInfoToItem(trackItemInspections);
        return trackItemInspections;
    }

    private void setHeadInfoToItem(IPage<TrackItemInspection> trackItemInspections) {
        for (TrackItemInspection trackItemInspection : trackItemInspections.getRecords()) {
            TrackHead trackHead = trackHeadMapper.selecProjectNametById(trackItemInspection.getTrackHeadId());
            if (!ObjectUtil.isEmpty(trackHead)) {
                trackItemInspection.setTrackNo(trackHead.getTrackNo());
                trackItemInspection.setDrawingNo(trackHead.getDrawingNo());
                trackItemInspection.setQty(trackHead.getNumber());
                trackItemInspection.setProductName(trackHead.getProductName());
                trackItemInspection.setWorkNo(trackHead.getWorkNo());
                trackItemInspection.setTrackType(trackHead.getTrackType());
                trackItemInspection.setTexture(trackHead.getTexture());
                trackItemInspection.setPartsName(trackHead.getMaterialName());
                trackItemInspection.setProjectName(trackHead.getProjectName());
            }
        }
    }

    private QueryWrapper<TrackItemInspection> getTrackItemInspectionQueryWrapper(String startTime, String endTime, String trackNo, String productName, String productNo, String branchCode, String tenantId, String isAudit) {
        QueryWrapper<TrackItemInspection> queryWrapper = new QueryWrapper<TrackItemInspection>();
        if (!StringUtils.isEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        if (!StringUtils.isEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }
        //已审核
        if ("1".equals(isAudit)) {
            queryWrapper.isNotNull("audit_by");
        } else if ("0".equals(isAudit)) {
            //未审核
            queryWrapper.isNull("audit_by");
        }

        if (!StringUtils.isEmpty(trackNo)) {
            trackNo = trackNo.replaceAll(" ", "");
            queryWrapper.inSql("id", "select id from  produce_track_item_inspection where track_head_id in ( select id from produce_track_head where replace(replace(replace(track_no, char(13), ''), char(10), ''),' ', '') LIKE '" + trackNo + '%' + "')");
        }
        if (!StringUtils.isEmpty(productName)) {
            queryWrapper.inSql("id", "select id from  produce_track_item_inspection where track_head_id in ( select id from produce_track_head where product_name LIKE '" + productName + '%' + "')");
        }
        if (!StringUtils.isEmpty(productNo)) {
            queryWrapper.likeLeft("productNo", productNo);
        }
        if (!StringUtils.isEmpty(startTime)) {
            queryWrapper.ge("date_format(modify_time, '%Y-%m-%d')", startTime);
        }
        if (!StringUtils.isEmpty(endTime)) {
            queryWrapper.le("date_format(modify_time, '%Y-%m-%d')", endTime);
        }
        queryWrapper.orderByDesc("modify_time");
        return queryWrapper;
    }

    /**
     * 分页查询探伤派工信息
     *
     * @param page
     * @param limit
     * @param startTime
     * @param endTime
     * @param trackNo
     * @param productName
     * @param branchCode
     * @param tenantId
     * @return
     */
    public IPage<Assign> assginPage(int page, int limit, String startTime, String endTime, String trackNo, String productName, String productNo, String branchCode, String tenantId, Integer isOperationComplete) {
        QueryWrapper<TrackItemInspection> queryWrapper = new QueryWrapper<TrackItemInspection>();
        if (!StringUtils.isEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        if (!StringUtils.isEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }

        if (!StringUtils.isEmpty(trackNo)) {
            trackNo = trackNo.replaceAll(" ", "");
            queryWrapper.inSql("id", "select id from  produce_track_item_inspection where track_head_id in ( select id from produce_track_head where replace(replace(replace(track_no, char(13), ''), char(10), ''),' ', '') LIKE '" + trackNo + '%' + "')");
        }
        if (!StringUtils.isEmpty(productName)) {
            queryWrapper.inSql("id", "select id from  produce_track_item_inspection where track_head_id in ( select id from produce_track_head where product_name LIKE '" + productName + '%' + "')");
        }
        if (!StringUtils.isEmpty(productNo)) {
            queryWrapper.likeLeft("productNo", productNo);
        }
        if (!StringUtils.isEmpty(startTime)) {
            queryWrapper.ge("date_format(modify_time, '%Y-%m-%d')", startTime);
        }
        if (!StringUtils.isEmpty(endTime)) {
            queryWrapper.le("date_format(modify_time, '%Y-%m-%d')", endTime);
        }
        queryWrapper.orderByDesc("modify_time");
        IPage<TrackItemInspection> trackItemInspections = trackItemInspectionService.page(new Page<TrackItemInspection>(page, limit), queryWrapper);

        //工序ids
        List<String> itemIds = trackItemInspections.getRecords().stream().map(TrackItemInspection::getId).collect(Collectors.toList());

        if (itemIds.size() > 0) {
            IPage<Assign> assigns = trackAssignMapper.queryPageNew(new Page<Assign>(page, limit), new QueryWrapper<Assign>().in("ti_id", itemIds).notIn("state",2));
            for (int i = 0; i < assigns.getRecords().size(); i++) {
                assigns.getRecords().get(i).setAssignPersons(trackAssignPersonMapper.selectList(new QueryWrapper<AssignPerson>().eq("assign_id", assigns.getRecords().get(i).getId())));
            }
            if (null != assigns.getRecords()) {
                for (Assign assign : assigns.getRecords()) {
                    TrackHead trackHead = trackHeadService.getById(assign.getTrackId());
                    TrackItem trackItem = trackItemService.getById(assign.getTiId());
                    assign.setWeight(trackHead.getWeight());
                    assign.setWorkNo(trackHead.getWorkNo());
                    assign.setProductName(trackHead.getProductName());
                    assign.setPartsName(trackHead.getMaterialName());
                    assign.setTotalQuantity(trackItem.getNumber());
                    assign.setDispatchingNumber(trackItem.getAssignableQty());
                    assign.setWorkPlanNo(trackHead.getWorkPlanNo());
                }
            }

            return assigns;
        }
        return null;
    }

    @Autowired
    private BaseServiceClient baseServiceClient;
    @Autowired
    private TrackHeadFlowService trackFlowService;

    public CommonResult<IPage<TrackComplete>> pageTrackComplete(int page, int limit, String productNo, String trackNo, String startTime, String endTime, String branchCode) {
        try {
            QueryWrapper<TrackComplete> queryWrapper = new QueryWrapper<TrackComplete>();
//            if (!StringUtils.isNullOrEmpty(userId)) {
//                queryWrapper.apply("(user_id='" + userId + "' or user_name='" + userName + "')");
//            }
            if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(productNo)) {
                queryWrapper.eq("product_no", productNo);
            }

            if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(trackNo)) {
                trackNo = trackNo.replaceAll(" ", "");
                queryWrapper.apply("replace(replace(replace(track_no, char(13), ''), char(10), ''),' ', '') like '%" + trackNo + "%'");
            }

            if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(startTime)) {
                queryWrapper.apply("UNIX_TIMESTAMP(a.modify_time) >= UNIX_TIMESTAMP('" + startTime + "')");
            }
            if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(endTime)) {
                Calendar calendar = new GregorianCalendar();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                calendar.setTime(sdf.parse(endTime));
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                queryWrapper.apply("UNIX_TIMESTAMP(a.modify_time) <= UNIX_TIMESTAMP('" + sdf.format(calendar.getTime()) + "')");

            }
            if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(branchCode)) {
                queryWrapper.eq("branch_code", branchCode);
            }

            queryWrapper.eq("user_id", SecurityUtils.getCurrentUser().getUsername());

            queryWrapper.apply("ti_id in (select id from produce_track_item_inspection)");

            queryWrapper.orderByDesc("modify_time");
           /* if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(orderCol)) {
                if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(order)) {
                    if (order.equals("desc")) {
                        queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
                    } else if (order.equals("asc")) {
                        queryWrapper.orderByAsc(StrUtil.toUnderlineCase(orderCol));
                    }
                } else {
                    queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
                }
            } else {

            }*/
            IPage<TrackComplete> completes = trackCompleteService.queryPage(new Page<TrackComplete>(page, limit), queryWrapper);
            try {
                for (TrackComplete track : completes.getRecords()) {
                    CommonResult<TenantUserVo> tenantUserVo = systemServiceClient.queryByUserAccount(track.getUserId());
                    track.setUserName(tenantUserVo.getData().getEmplName());
                    CommonResult<Device> device = baseServiceClient.getDeviceById(track.getDeviceId());
                    track.setDeviceName(device.getData().getName());
                    TrackItem trackItem = trackItemService.getById(track.getTiId());
                    //查询产品编号
                    TrackFlow trackFlow = trackFlowService.getById(trackItem.getFlowId());
                    track.setProductNo(trackFlow.getProductNo());
                    //增加判断返回是否能修改
                    TrackHead trackHead = trackHeadService.getById(track.getTrackId());
                    track.setProductName(trackHead.getProductName());
                    //条件一 需要质检 并且已质检
                    if (1 == trackItem.getIsExistQualityCheck() && 1 == trackItem.getIsQualityComplete()) {
                        track.setIsUpdate(1);
                        continue;
                    }
                    //条件二 需要调度 并且以调度
                    if (1 == trackItem.getIsExistScheduleCheck() && 1 == trackItem.getIsScheduleComplete()) {
                        track.setIsUpdate(1);
                        continue;
                    }
                    //条件三 不质检 不调度
                    if (0 == trackItem.getIsExistQualityCheck() && 0 == trackItem.getIsExistScheduleCheck()) {
                        track.setIsUpdate(1);
                        continue;
                    }
                    //条件四 当前操作人不是开工人
                    if (!SecurityUtils.getCurrentUser().getUsername().equals(trackItem.getStartDoingUser())) {
                        track.setIsUpdate(1);
                        continue;
                    }
                    if (null == track.getIsUpdate()) {
                        track.setIsUpdate(0);
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return CommonResult.success(completes);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }


    /**
     * 保存探伤记录
     *
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public CommonResult saveRecord(ProduceInspectionRecordDto produceInspectionRecordDto) throws Exception {
        //获取模板类型
        String tempType = produceInspectionRecordDto.getTempType();
        //要保存的记录实体
        JSONObject jsonObject = produceInspectionRecordDto.getInspectionRecord();
        //检验员赋值（当前登陆人）
        jsonObject.put("check_by", SecurityUtils.getCurrentUser().getUserId());
        //使用上一次数据的情况过滤数据
        jsonObject.remove("modifyTime");
        jsonObject.remove("modifyBy");
        jsonObject.remove("createTime");
        jsonObject.remove("createBy");
        jsonObject.remove("isAudit");
        jsonObject.remove("auditRemark");
        //缺陷记录
        List<ProduceDefectsInfo> produceDefectsInfos = JSON.parseArray(JSONObject.toJSONString(jsonObject.get("defectsInfoList")), ProduceDefectsInfo.class);
        //探头信息
        List<ProbeInfo> probeInfoList = JSON.parseArray(JSONObject.toJSONString(jsonObject.get("probeInfoList")), ProbeInfo.class);
        //工序ids
        List<String> itemIds = produceInspectionRecordDto.getItemIds();
        //探伤记录id
        String recordId = null;
        //branchCode
        String branchCode = produceInspectionRecordDto.getBranchCode();

        //报告编号（确认后再放开）
        /*String reportNo = Code.value("报告编码", SecurityUtils.getCurrentUser().getTenantId(), branchCode, codeRuleService);
        jsonObject.put("report",reportNo);
        //保存报告编号
        Code.update("报告编码",reportNo,SecurityUtils.getCurrentUser().getTenantId(), branchCode,codeRuleService);*/

        if (InspectionRecordTypeEnum.MT.getType().equals(tempType)) {
            //保存探伤记录
            ProduceInspectionRecordMt produceInspectionRecordMt = jsonObject.toJavaObject(ProduceInspectionRecordMt.class);
            produceInspectionRecordMtService.save(produceInspectionRecordMt);
            recordId = produceInspectionRecordMt.getId();
        } else if (InspectionRecordTypeEnum.PT.getType().equals(tempType)) {
            ProduceInspectionRecordPt produceInspectionRecordPt = jsonObject.toJavaObject(ProduceInspectionRecordPt.class);
            produceInspectionRecordPtService.save(produceInspectionRecordPt);
            recordId = produceInspectionRecordPt.getId();
        } else if (InspectionRecordTypeEnum.RT.getType().equals(tempType)) {
            ProduceInspectionRecordRt produceInspectionRecordRt = jsonObject.toJavaObject(ProduceInspectionRecordRt.class);
            produceInspectionRecordRtService.save(produceInspectionRecordRt);
            recordId = produceInspectionRecordRt.getId();
        } else if (InspectionRecordTypeEnum.UT.getType().equals(tempType)) {
            ProduceInspectionRecordUt produceInspectionRecordUt = jsonObject.toJavaObject(ProduceInspectionRecordUt.class);
            produceInspectionRecordUtService.save(produceInspectionRecordUt);
            recordId = produceInspectionRecordUt.getId();
        } else {
            throw new GlobalException(ResultCode.INVALID_ARGUMENTS.getMessage(), ResultCode.INVALID_ARGUMENTS);
        }

        //保存流水号
        if (!StringUtils.isEmpty(tempType) && !ObjectUtil.isEmpty(jsonObject.get("recordNo"))) {
            codeRuleService.updateCode("inspection_code_" + tempType, null, jsonObject.get("recordNo").toString(), null, SecurityUtils.getCurrentUser().getTenantId(), branchCode);
        }

        //工序and探伤记录绑定操作
        List<ProduceItemInspectInfo> produceItemInspectInfos = new ArrayList<>();
        for (String itemId : itemIds) {
            ProduceItemInspectInfo produceItemInspectInfo = new ProduceItemInspectInfo();
            produceItemInspectInfo.setTrackItemId(itemId);
            produceItemInspectInfo.setInspectRecordId(recordId);
            produceItemInspectInfo.setTempType(tempType);

            produceItemInspectInfo.setAuditBy(String.valueOf(jsonObject.get("auditBy")));
            //检验人为当前新建探伤记录的人
            produceItemInspectInfo.setCheckBy(SecurityUtils.getCurrentUser().getUserId());
            //状态为未审核
            produceItemInspectInfo.setIsAudit("0");

            produceItemInspectInfos.add(produceItemInspectInfo);
        }
        produceItemInspectInfoService.saveBatch(produceItemInspectInfos);

        //ut保存探头
        if (!ObjectUtil.isEmpty(probeInfoList) && InspectionRecordTypeEnum.UT.getType().equals(tempType)) {
            for (ProbeInfo probeInfo : probeInfoList) {
                probeInfo.setRecordId(recordId);
            }
            probeInfoService.saveBatch(probeInfoList);
        }


        //rt保存缺陷记录
        if (!ObjectUtil.isEmpty(produceDefectsInfos) && InspectionRecordTypeEnum.RT.getType().equals(tempType)) {
            for (ProduceDefectsInfo produceDefectsInfo : produceDefectsInfos) {
                produceDefectsInfo.setRecordId(recordId);
            }
            produceDefectsInfoService.saveBatch(produceDefectsInfos);
        }

        return null;
    }

    /**
     * 根据工序id查询探伤记录
     *
     * @param itemId
     * @return
     */
    public List<Map<String, Object>> queryRecordByItemId(String itemId, String checkOrAudit, String isAudit) {
        //在探伤主表里过滤
        QueryWrapper<ProduceItemInspectInfo> itemInspectInfoQueryWrapper = new QueryWrapper<>();
        itemInspectInfoQueryWrapper.eq("track_item_id", itemId);

        if (!StringUtils.isEmpty(isAudit)) {
            itemInspectInfoQueryWrapper.eq("is_audit", isAudit);
        }


        if (CHECK.equals(checkOrAudit)) {
            //探伤记录填写页面 根据登陆人 = 检验人查询
            itemInspectInfoQueryWrapper.eq("check_by", SecurityUtils.getCurrentUser().getUserId());
        } else if (AUDIT.equals(checkOrAudit)) {
            //探伤记录审核页面 根据登陆人 = 审核人查询
            itemInspectInfoQueryWrapper.and(wrapper->wrapper.eq("audit_by", SecurityUtils.getCurrentUser().getUserId()).or(wrapper2->wrapper2.eq("audit_by","/")));
        }


        List<ProduceItemInspectInfo> list = produceItemInspectInfoService.list(itemInspectInfoQueryWrapper);
        //按照模板类型分组  key->模板类型  value->探伤记录id
        Map<String, List<String>> tempValues = list.stream().collect(Collectors.groupingBy(ProduceItemInspectInfo::getTempType, Collectors.mapping(ProduceItemInspectInfo::getInspectRecordId, Collectors.toList())));
        //要返回的集合
        List<Object> returnList = new ArrayList<>();
        DateTime date1 = DateUtil.date();
        tempValues.forEach((tempType, ids) -> {
            if (InspectionRecordTypeEnum.MT.getType().equals(tempType)) {
                returnList.addAll(produceInspectionRecordMtService.queryListByIds(ids));
            } else if (InspectionRecordTypeEnum.PT.getType().equals(tempType)) {
                returnList.addAll(produceInspectionRecordPtService.queryListByIds(ids));
            } else if (InspectionRecordTypeEnum.RT.getType().equals(tempType)) {
                returnList.addAll(produceInspectionRecordRtService.queryListByIds(ids));
            } else if (InspectionRecordTypeEnum.UT.getType().equals(tempType)) {
                returnList.addAll(produceInspectionRecordUtService.queryListByIds(ids));
            }
        });
        log.info("时间:" + String.valueOf(DateUtil.betweenMs(date1, DateUtil.date())));
        DateTime date2 = DateUtil.date();

        List<Map<String, Object>> listMap = new ArrayList<>();
        //转换map 顺便file详情查询
        for (Object o : returnList) {
            listMap.add(objectToMap(o));
        }
        log.info("时间:" + String.valueOf(DateUtil.betweenMs(date2, DateUtil.date())));
        //根据修改时间排序
        if (listMap.size() > 0) {
            listMap.sort((t1, t2) ->
                    t2.get("modifyTime").toString().compareTo(t1.get("modifyTime").toString())
            );
        }

        return listMap;
    }

    /**
     * 返回最近一条记录
     *
     * @param itemId
     * @return
     */
    public Object queryLastInfoByItemId(String itemId) {
        //所有记录
        List<Map<String, Object>> inspects = queryRecordByItemId(itemId, "check", null);
        if (inspects.size() > 0) {
            return inspects.get(0);
        }
        return null;
    }

    /**
     * 探伤记录审核 跟单工序列表
     *
     * @param page
     * @param limit
     * @param startTime
     * @param endTime
     * @param trackNo
     * @param productName
     * @param productNo
     * @param branchCode
     * @param tenantId
     * @param isAudit
     * @return
     */
    public IPage<TrackItemInspection> queryItemByAuditBy(int page, int limit, String startTime, String endTime, String trackNo, String productName, String productNo, String branchCode, String tenantId, String isAudit) {
        //从中间表查询审核人是当前用户的数据
        QueryWrapper<ProduceItemInspectInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("audit_by", SecurityUtils.getCurrentUser().getUserId()).or(warpper -> warpper.eq("audit_by", "/"));

        List<ProduceItemInspectInfo> list = produceItemInspectInfoService.list(queryWrapper);
        //跟单工序
        Set<String> itemIds = list.stream().map(ProduceItemInspectInfo::getTrackItemId).collect(Collectors.toSet());


        //跟单工序查询
        QueryWrapper<TrackItemInspection> queryWrapper2 = getTrackItemInspectionQueryWrapper(startTime, endTime, trackNo, productName, productNo, branchCode, tenantId, isAudit);


        if (itemIds.size() > 0) {
            queryWrapper2.in("id", itemIds);

        } else {
            queryWrapper2.in("id", "");
        }
        IPage<TrackItemInspection> trackItemInspections = trackItemInspectionService.page(new Page<TrackItemInspection>(page, limit), queryWrapper2);
        //为跟单工序赋跟单的一些属性
        setHeadInfoToItem(trackItemInspections);
        return trackItemInspections;
    }


    /**
     * 审核提交探伤记录
     *
     * @param itemId             探伤工序id
     * @param itemId             探伤工序id
     * @param flawDetectioRemark 探伤备注
     * @param flawDetection      探伤结果
     * @param tempType           探伤记录模板
     * @param recordNo           探伤记录编号
     * @param checkBy            探伤检验人
     * @param auditBy            探伤审核人
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean auditSubmitRecord(String itemId, String flawDetectioRemark, Integer flawDetection, String tempType, String recordNo, String checkBy, String auditBy) {
        TrackItemInspection trackItemInspection = new TrackItemInspection();
        trackItemInspection.setId(itemId);
        trackItemInspection.setFlawDetectionRemark(flawDetectioRemark);
        trackItemInspection.setFlawDetection(flawDetection);
        trackItemInspection.setTempType(tempType);
        trackItemInspection.setInspectRecordNo(recordNo);
        trackItemInspection.setCheckBy(checkBy);
        trackItemInspection.setAuditBy(auditBy);
        //这里生成报告号

        trackItemInspectionService.updateById(trackItemInspection);

        //同步回跟单工序表
        TrackItem trackItem = trackItemService.getById(itemId);
        trackItem.setFlawDetectionRemark(flawDetectioRemark);
        trackItem.setFlawDetection(flawDetection);
        trackItem.setTempType(tempType);
        trackItem.setInspectRecordNo(recordNo);
        trackItem.setCheckBy(checkBy);
        trackItem.setAuditBy(auditBy);
        //质检字段赋值
        //探伤工序不需要质检  有调度直接走调度
        trackItem.setIsExistQualityCheck(0);
        //更改状态 标识当前工序完成
        trackItem.setIsDoing(2);
        trackItem.setIsOperationComplete(1);
        trackItem.setIsQualityComplete(1);

        //调用工序激活方法
        boolean next = trackItem.getIsExistQualityCheck().equals(0) && trackItem.getIsExistScheduleCheck().equals(0);
        if (next) {
            trackItem.setIsFinalComplete(String.valueOf(1));
        }
        trackItemService.updateById(trackItem);

        //派工状态设置为完成
        UpdateWrapper<Assign> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("ti_id", trackItem.getId());
        //state = 2 (已完工)
        updateWrapper.set("state", 2);
        trackAssignService.update(updateWrapper);

        //判断是否需要质检和调度审核 再激活下工序
        if (next) {
            Map<String, String> map = new HashMap<String, String>(1);
            map.put(IdEnum.FLOW_ID.getMessage(), trackItem.getFlowId());
            publicService.activationProcess(map);
        }

        return trackItemInspectionService.updateById(trackItemInspection);
    }

    /**
     * 报告导出doc
     */
    public void exoprtReport(HttpServletResponse response, String id) throws IOException, TemplateException {
        //探伤记录、探伤工序、探伤模板信息
        ProduceItemInspectInfo produceItemInspectInfo = new ProduceItemInspectInfo();
        //根据探伤记录id定位
        QueryWrapper<ProduceItemInspectInfo> itemInspectInfoQueryWrapper = new QueryWrapper<>();
        itemInspectInfoQueryWrapper.eq("inspect_record_id", id);
        List<ProduceItemInspectInfo> produceItemInspectInfos = produceItemInspectInfoService.list(itemInspectInfoQueryWrapper);
        if (produceItemInspectInfos.size() > 0) {
            produceItemInspectInfo = produceItemInspectInfos.get(0);
        } else {
            throw new GlobalException(ResultCode.ITEM_NOT_FOUND.getMessage(), ResultCode.ITEM_NOT_FOUND);
        }

        //跟单信息
        TrackHead trackHead = null;
        //探伤记录
        Map<String, Object> recordInfo = new HashMap<>();
        //查询探伤工序
        TrackItemInspection trackItemInspection = trackItemInspectionService.getById(produceItemInspectInfo.getTrackItemId());

        if (!ObjectUtil.isEmpty(trackItemInspection)) {
            trackHead = trackHeadService.getById(trackItemInspection.getTrackHeadId());
            List<Map<String, Object>> list = new ArrayList<>();
            if (InspectionRecordTypeEnum.MT.getType().equals(produceItemInspectInfo.getTempType())) {
                list = produceInspectionRecordMtService.listMaps(new QueryWrapper<ProduceInspectionRecordMt>().eq("id", produceItemInspectInfo.getInspectRecordId()));
            } else if (InspectionRecordTypeEnum.PT.getType().equals(produceItemInspectInfo.getTempType())) {
                list = produceInspectionRecordPtService.listMaps(new QueryWrapper<ProduceInspectionRecordPt>().eq("id", produceItemInspectInfo.getInspectRecordId()));
            } else if (InspectionRecordTypeEnum.RT.getType().equals(produceItemInspectInfo.getTempType())) {
                list = produceInspectionRecordRtService.listMaps(new QueryWrapper<ProduceInspectionRecordRt>().eq("id", produceItemInspectInfo.getInspectRecordId()));
            } else if (InspectionRecordTypeEnum.UT.getType().equals(produceItemInspectInfo.getTempType())) {
                list = produceInspectionRecordUtService.listMaps(new QueryWrapper<ProduceInspectionRecordUt>().eq("id", produceItemInspectInfo.getInspectRecordId()));
            }
            if (!CollectionUtil.isEmpty(list)) {
                recordInfo = list.get(0);
            }
        }

        if (!ObjectUtil.isEmpty(recordInfo)) {
            //缺陷记录
            QueryWrapper<ProduceDefectsInfo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("record_id", recordInfo.get("id")).orderByAsc("serial_number");
            List<ProduceDefectsInfo> defectsInfoList = produceDefectsInfoService.list(queryWrapper);
            recordInfo.put("defectsInfoList", defectsInfoList);
            //探头
            List<ProbeInfo> probeInfoList = probeInfoService.list(new QueryWrapper<ProbeInfo>().eq("record_id", recordInfo.get("id")).orderByAsc("serial_num"));
            recordInfo.put("probeInfoList", probeInfoList);
        }


        Map<String, Object> dataMap = new HashMap<>();
        //填充数据
        createDataMap(trackHead, recordInfo, dataMap, produceItemInspectInfo.getTempType());

        //根据模板类型获取模板和导出文件名
        Map<String, String> tempNameAndDocNameMap = checkTempNameAndDocName(produceItemInspectInfo.getTempType());
        //导出
        wordUtil.exoprtReport(response, dataMap, tempNameAndDocNameMap.get("tempName"), tempNameAndDocNameMap.get("docName"));
    }

    /**
     * 构造导出填充数据dataMap
     *
     * @param trackHead
     * @param recordInfo
     * @param dataMap
     * @param tempType
     */
    private void createDataMap(TrackHead trackHead, Map<String, Object> recordInfo, Map<String, Object> dataMap, String tempType) throws IOException {
        if (InspectionRecordTypeEnum.MT.getType().equals(tempType)) {
            createMtDataMap(recordInfo, dataMap);
        } else if (InspectionRecordTypeEnum.PT.getType().equals(tempType)) {
            createPtDataMap(recordInfo, dataMap);
        } else if (InspectionRecordTypeEnum.RT.getType().equals(tempType)) {
            createRtDataMap(recordInfo, dataMap);
        } else if (InspectionRecordTypeEnum.UT.getType().equals(tempType)) {
            createUtDataMap(recordInfo, dataMap);
        }
        //图号
        dataMap.put("drawingNo", trackHead.getDrawingNo());
        //零件名称
        dataMap.put("materialName", trackHead.getMaterialName());
        //材质
        dataMap.put("texture", trackHead.getTexture());
        dataMap.put("year", String.valueOf(DateUtil.year(DateUtil.date())));
        dataMap.put("month", DateUtil.thisMonth() + 1);
        dataMap.put("day", DateUtil.dayOfMonth(DateUtil.date()));
    }

    //mt模板填充
    private void createMtDataMap(Map<String, Object> recordInfo, Map<String, Object> dataMap) throws IOException {
        //mt探伤记录
        ProduceInspectionRecordMt produceInspectionRecordMt = JSON.parseObject(JSON.toJSONString(recordInfo), ProduceInspectionRecordMt.class);

        //人员信息转换
        if (!ObjectUtil.isEmpty(produceInspectionRecordMt.getAuditBy())) {
            TenantUserVo data = systemServiceClient.getUserById(produceInspectionRecordMt.getAuditBy()).getData();
            produceInspectionRecordMt.setAuditBy(data.getUserAccount());
        }
        if (!ObjectUtil.isEmpty(produceInspectionRecordMt.getCheckBy())) {
            TenantUserVo data = systemServiceClient.getUserById(produceInspectionRecordMt.getCheckBy()).getData();
            produceInspectionRecordMt.setCheckBy(data.getUserAccount());
        }

        dataMap.putAll(JSON.parseObject(JSON.toJSONString(produceInspectionRecordMt), Map.class));
        /*//记录编号
        dataMap.put("recordNo",produceInspectionRecordMt.getRecordNo());
        //种类
        dataMap.put("type",produceInspectionRecordMt.getType());
        //提升力
        dataMap.put("liftPower",produceInspectionRecordMt.getLiftPower());
        //磁化方法
        dataMap.put("magneticMethod",produceInspectionRecordMt.getMagneticMethod());
        //检测方法
        dataMap.put("detectionMethod",produceInspectionRecordMt.getDetectionMethod());
        //磁粉载体
        dataMap.put("magneticCarrier",produceInspectionRecordMt.getMagneticCarrier());
        //磁化方向
        dataMap.put("magneticDirection",produceInspectionRecordMt.getMagneticDirection());
        //荧光/非荧光
        dataMap.put("fluorescent",produceInspectionRecordMt.getFluorescent());
        //退磁
        dataMap.put("isMagnetic",produceInspectionRecordMt.getIsMagnetic());
        //温度
        dataMap.put("tempera",produceInspectionRecordMt.getTempera());
        //室温
        dataMap.put("isRoomTemp",produceInspectionRecordMt.getIsRoomTemp());
        //粗糙度
        dataMap.put("roughness",produceInspectionRecordMt.getRoughness());
        //试验规范
        dataMap.put("testSpecification",produceInspectionRecordMt.getTestSpecification());
        //试验标准
        dataMap.put("acceptanceCriteria",produceInspectionRecordMt.getAcceptanceCriteria());
        //灵敏度试片
        dataMap.put("sensitivityTestPiece",produceInspectionRecordMt.getSensitivityTestPiece());
        //检验员
        dataMap.put("checkBy",produceInspectionRecordMt.getCheckBy());
        //审核人
        dataMap.put("auditBy",produceInspectionRecordMt.getAuditBy());
        //检验结果
        dataMap.put("inspectionResults",produceInspectionRecordMt.getInspectionResults());
        //见证
        dataMap.put("witnesses",produceInspectionRecordMt.getWitnesses());*/
        //图片base64编码
        if (!StringUtils.isEmpty(produceInspectionRecordMt.getDiagramAttachmentId())) {
            dataMap.put("img", systemServiceClient.getBase64Code(produceInspectionRecordMt.getDiagramAttachmentId()).getData());
        }

    }

    //rt模板填充
    private void createRtDataMap(Map<String, Object> recordInfo, Map<String, Object> dataMap) throws IOException {
        //mt探伤记录
        ProduceInspectionRecordRt produceInspectionRecordRt = JSON.parseObject(JSON.toJSONString(recordInfo), ProduceInspectionRecordRt.class);

        if (!ObjectUtil.isEmpty(produceInspectionRecordRt.getAuditBy())) {
            TenantUserVo data = systemServiceClient.getUserById(produceInspectionRecordRt.getAuditBy()).getData();
            produceInspectionRecordRt.setAuditBy(data.getUserAccount());
        }
        if (!ObjectUtil.isEmpty(produceInspectionRecordRt.getCheckBy())) {
            TenantUserVo data = systemServiceClient.getUserById(produceInspectionRecordRt.getCheckBy()).getData();
            produceInspectionRecordRt.setCheckBy(data.getUserAccount());
        }

        dataMap.putAll(JSON.parseObject(JSON.toJSONString(produceInspectionRecordRt), Map.class));
        //图片base64编码
        if (!StringUtils.isEmpty(produceInspectionRecordRt.getDiagramAttachmentId())) {
            dataMap.put("img", systemServiceClient.getBase64Code(produceInspectionRecordRt.getDiagramAttachmentId()).getData());
        }
    }

    //pt模板填充
    private void createPtDataMap(Map<String, Object> recordInfo, Map<String, Object> dataMap) throws IOException {
        //pt探伤记录
        ProduceInspectionRecordPt produceInspectionRecordPt = JSON.parseObject(JSON.toJSONString(recordInfo), ProduceInspectionRecordPt.class);

        if (!ObjectUtil.isEmpty(produceInspectionRecordPt.getAuditBy())) {
            TenantUserVo data = systemServiceClient.getUserById(produceInspectionRecordPt.getAuditBy()).getData();
            produceInspectionRecordPt.setAuditBy(data.getUserAccount());
        }
        if (!ObjectUtil.isEmpty(produceInspectionRecordPt.getCheckBy())) {
            TenantUserVo data = systemServiceClient.getUserById(produceInspectionRecordPt.getCheckBy()).getData();
            produceInspectionRecordPt.setCheckBy(data.getUserAccount());
        }
        dataMap.putAll(JSON.parseObject(JSON.toJSONString(produceInspectionRecordPt), Map.class));

        if (!StringUtils.isEmpty(produceInspectionRecordPt.getDiagramAttachmentId())) {
            //图片base64编码
            dataMap.put("img", systemServiceClient.getBase64Code(produceInspectionRecordPt.getDiagramAttachmentId()).getData());
        }

    }

    //ut模板填充
    private void createUtDataMap(Map<String, Object> recordInfo, Map<String, Object> dataMap) throws IOException {
        //ut探伤记录
        ProduceInspectionRecordUt produceInspectionRecordUt = JSON.parseObject(JSON.toJSONString(recordInfo), ProduceInspectionRecordUt.class);

        if (!ObjectUtil.isEmpty(produceInspectionRecordUt.getAuditBy())) {
            TenantUserVo data = systemServiceClient.getUserById(produceInspectionRecordUt.getAuditBy()).getData();
            produceInspectionRecordUt.setAuditBy(data.getUserAccount());
        }
        if (!ObjectUtil.isEmpty(produceInspectionRecordUt.getCheckBy())) {
            TenantUserVo data = systemServiceClient.getUserById(produceInspectionRecordUt.getCheckBy()).getData();
            produceInspectionRecordUt.setCheckBy(data.getUserAccount());
        }
        dataMap.putAll(JSON.parseObject(JSON.toJSONString(produceInspectionRecordUt), Map.class));

        //图片base64编码
        if (!StringUtils.isEmpty(produceInspectionRecordUt.getDiagramAttachmentId())) {
            dataMap.put("img", systemServiceClient.getBase64Code(produceInspectionRecordUt.getDiagramAttachmentId()).getData());
        }
        //探头列表
        List<ProbeInfo> probeInfoList = probeInfoService.list(new QueryWrapper<ProbeInfo>()
                .eq("record_id", produceInspectionRecordUt.getId())
                .orderByAsc("serial_num"));
        //探头列表
        dataMap.put("probeInfoList", probeInfoList);

    }

    /**
     * 根据模板类型获取模板和导出文件名
     *
     * @param tempType
     * @return
     */
    private Map<String, String> checkTempNameAndDocName(String tempType) {
        Map<String, String> returnMap = new HashMap<>();
        if (InspectionRecordTypeEnum.MT.getType().equals(tempType)) {
            returnMap.put("tempName", "mtTemp.ftl");
            returnMap.put("docName", "磁粉探伤报告");
        } else if (InspectionRecordTypeEnum.PT.getType().equals(tempType)) {
            returnMap.put("tempName", "ptTemp.ftl");
            returnMap.put("docName", "渗透探伤报告");
        } else if (InspectionRecordTypeEnum.RT.getType().equals(tempType)) {
            returnMap.put("tempName", "rtTemp.ftl");
            returnMap.put("docName", "射线探伤报告");
        } else if (InspectionRecordTypeEnum.UT.getType().equals(tempType)) {
            returnMap.put("tempName", "utTemp.ftl");
            returnMap.put("docName", "超声探伤报告");
        } else {
            throw new GlobalException(ResultCode.ITEM_NOT_FOUND.getMessage(), ResultCode.FAILED);
        }
        return returnMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveComplete(List<CompleteDto> completeDtoList) {
        //获取用户所属公司
        String companyCode = SecurityUtils.getCurrentUser().getCompanyCode();
        for (CompleteDto completeDto : completeDtoList) {
            if (com.mysql.cj.util.StringUtils.isNullOrEmpty(completeDto.getQcPersonId())) {
                return CommonResult.failed("质检人员不能为空");
            }
            if (null == completeDto.getTrackCompleteList() && completeDto.getTrackCompleteList().isEmpty()) {
                return CommonResult.failed("报工人员不能为空");
            }
            if (com.mysql.cj.util.StringUtils.isNullOrEmpty(completeDto.getTiId())) {
                return CommonResult.failed("工序Id不能为空");
            }
            TrackItem trackItem = trackItemService.getById(completeDto.getTiId());
            //检验人
            trackItem.setQualityCheckBy(completeDto.getQcPersonId());
            //根据工序Id删除缓存表数据
            QueryWrapper<TrackCompleteCache> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("ti_id", completeDto.getTiId());
            double numDouble = 0.00;
            for (TrackComplete trackComplete : completeDto.getTrackCompleteList()) {
                if (trackComplete.getReportHours() > trackItem.getSinglePieceHours()) {
                    return CommonResult.failed("报工工时不能大于额定工时");
                }
                trackComplete.setId(null);
                trackComplete.setAssignId(completeDto.getAssignId());
                trackComplete.setTiId(completeDto.getTiId());
                trackComplete.setTrackId(completeDto.getTrackId());
                trackComplete.setTrackNo(completeDto.getTrackNo());
                trackComplete.setProdNo(completeDto.getProdNo());
                trackComplete.setCompleteBy(SecurityUtils.getCurrentUser().getUsername());
                trackComplete.setCompleteTime(new Date());
                trackComplete.setDetectionResult("-");
                trackComplete.setTenantId(SecurityUtils.getCurrentUser().getTenantId());

                numDouble += trackComplete.getCompletedQty();
            }
            Assign assign = trackAssignService.getById(completeDto.getAssignId());
            //跟新工序完成数量
            trackItem.setCompleteQty(trackItem.getCompleteQty() + numDouble);
            double intervalNumber = assign.getQty() + 0.0;
            if (numDouble > assign.getQty()) {
                return CommonResult.failed("报工数量:" + numDouble + ",派工数量:" + assign.getQty() + "完工数量不得大于" + assign.getQty());
            }
            if (numDouble < intervalNumber - 0.1) {
                return CommonResult.failed("报工数量:" + numDouble + ",派工数量:" + assign.getQty() + "完工数量不得少于" + (intervalNumber - 0.1));
            }
            if (assign.getQty() >= numDouble && intervalNumber - 0.1 <= numDouble) {
                //调用工序激活方法
                Map<String, String> map = new HashMap<>(3);
                map.put(IdEnum.FLOW_ID.getMessage(), trackItem.getFlowId());
                map.put(IdEnum.TRACK_HEAD_ID.getMessage(), completeDto.getTrackId());
                map.put(IdEnum.TRACK_ITEM_ID.getMessage(), completeDto.getTiId());
                map.put(IdEnum.ASSIGN_ID.getMessage(), completeDto.getAssignId());
                publicService.publicUpdateState(map, PublicCodeEnum.COMPLETE.getCode());
                //更改状态 标识当前工序完成
                trackItem.setIsDoing(2);
                trackItem.setIsOperationComplete(1);
                trackItemService.updateById(trackItem);
                TrackItemInspection trackItemInspection = new TrackItemInspection();
                BeanUtil.copyProperties(trackItem, trackItemInspection);
                //修改探伤工序表
                trackItemInspectionService.updateById(trackItemInspection);
                trackCompleteCacheService.remove(queryWrapper);
            }
            log.error(completeDto.getTrackCompleteList().toString());
            trackCompleteService.saveBatch(completeDto.getTrackCompleteList());
        }
        return CommonResult.success(true);
    }


    /**
     * 将Object对象里面的属性和值转化成Map对象
     *
     * @param obj
     * @return
     * @throws IllegalAccessException
     */
    public Map<String, Object> objectToMap(Object obj) {
        JSONObject jsonObject = JSONObject.parseObject(JSON.toJSONString(obj));

        //插入附件信息
        /*if(!ObjectUtil.isEmpty(jsonObject.get("diagramAttachmentId"))){
            List<Object> fileInfos = new ArrayList<>();
            String[] diagramAttachmentIds = jsonObject.get("diagramAttachmentId").toString().split(",");
            for (String diagramAttachmentId : diagramAttachmentIds) {
                Attachment data = systemServiceClient.attachment(diagramAttachmentId).getData();
                data.setPreviewUrl(String.valueOf(systemServiceClient.getPreviewUrl(diagramAttachmentId).getData()));
            }
            jsonObject.put("fileList", fileInfos);
        }*/

        if (!ObjectUtil.isEmpty(jsonObject.get("auditBy"))) {
            String auditBy = jsonObject.get("auditBy").toString();
            if(!"/".equals(auditBy)){
                TenantUserVo data = systemServiceClient.getUserById(auditBy).getData();
                jsonObject.put("auditByInfo", data);
            }
        }
        if (!ObjectUtil.isEmpty(jsonObject.get("checkBy"))) {
            String checkBy = jsonObject.get("checkBy").toString();
            if(!"/".equals(checkBy)){
                TenantUserVo data = systemServiceClient.getUserById(checkBy).getData();
                jsonObject.put("checkByInfo", data);
            }
        }

        return jsonObject;
    }

    public CommonResult updateAssign(Assign assign) {
        try {
            if (com.mysql.cj.util.StringUtils.isNullOrEmpty(assign.getTiId())) {
                return CommonResult.failed("关联工序ID编码不能为空！");
            } else {
                TrackItem trackItem = trackItemService.getById(assign.getTiId());

                if (trackItem.getIsExistQualityCheck() == 1 && trackItem.getIsQualityComplete() == 1) {
                    return CommonResult.failed("跟单工序【" + trackItem.getOptName() + "】已质检完成，报工无法取消！");
                }
                if (trackItem.getIsExistScheduleCheck() == 1 && trackItem.getIsScheduleComplete() == 1) {
                    return CommonResult.failed("跟单工序【" + trackItem.getOptName() + "】已调度完成，报工无法取消！");
                }
                // 判断后置工序是否已派工，否则无法修改
                List<Assign> cs = this.find(null, null, null, null, null, trackItem.getFlowId());
                for (int j = 0; j < cs.size(); j++) {
                    TrackItem cstrackItem = trackItemService.getById(cs.get(j).getTiId());
                    if (cstrackItem.getOptSequence() > trackItem.getOptSequence()) {
                        return CommonResult.failed("无法回滚，需要先取消后序工序【" + cstrackItem.getOptName() + "】的派工");
                    }
                }
                // 判断修改的派工数量是否在合理范围
                Assign oldassign = trackAssignService.getById(assign.getId());
                if (null != trackItem) {
                    if (trackItem.getAssignableQty() < (assign.getQty() - oldassign.getQty())) {
                        return CommonResult.failed(trackItem.getOptName() + " 工序可派工数量不足, 最大数量为" + trackItem.getAssignableQty());
                    }
                }
                // 设置派工时间，人员，工序可派工数
                if (null != SecurityUtils.getCurrentUser()) {

                    assign.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                    assign.setAssignBy(SecurityUtils.getCurrentUser().getUsername());
                }
                assign.setAssignTime(new Date());
                assign.setModifyTime(new Date());
                assign.setAvailQty(assign.getQty());
                boolean bool = trackAssignService.updateById(assign);
                QueryWrapper<AssignPerson> queryWrapper = new QueryWrapper<AssignPerson>();
                queryWrapper.eq("assign_id", assign.getId());
                trackAssignPersonService.remove(queryWrapper);
                for (AssignPerson person : assign.getAssignPersons()) {
                    person.setModifyTime(new Date());
                    person.setAssignId(assign.getId());
                    trackAssignPersonService.save(person);
                }
                trackItem.setAssignableQty(trackItem.getAssignableQty() - (assign.getQty() - oldassign.getQty()));
                if (assign.getState() == 1) {
                    trackItem.setIsDoing(1);
                    trackItem.setStartDoingTime(new Date());
                    trackItem.setStartDoingUser(SecurityUtils.getCurrentUser().getUsername());
                } else {
                    trackItem.setIsSchedule(0);
                    trackItem.setIsDoing(0);
                }
                trackItemService.updateById(trackItem);
                //修改探伤报工信息
                TrackItemInspection trackItemInspection = new TrackItemInspection();
                BeanUtil.copyProperties(trackItem, trackItemInspection);
                trackItemInspectionService.updateById(trackItemInspection);
            }
            return CommonResult.success(assign, "操作成功！");
        } catch (Exception e) {
            return CommonResult.failed("操作失败，请重试！" + e.getMessage());
        }
    }

    /**
     * 派工查询
     *
     * @param id
     * @param tiId
     * @param state
     * @param trackId
     * @param trackNo
     * @param flowId
     * @return
     */
    public List<Assign> find(String id, String tiId, String state, String trackId, String trackNo, String flowId) {
        QueryWrapper<Assign> queryWrapper = new QueryWrapper<Assign>();
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(tiId)) {
            queryWrapper.eq("ti_id", tiId);
        }
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(state)) {
            queryWrapper.eq("state", Integer.parseInt(state));
        }
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(trackId)) {
            queryWrapper.eq("track_id", trackId);
        }
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(trackNo)) {
            queryWrapper.eq("track_no", trackNo);
        }
        if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(flowId)) {
            queryWrapper.eq("flow_id", flowId);
        }
        queryWrapper.orderByAsc("modify_time");
        return trackAssignService.list(queryWrapper);
    }

    /**
     * 保存报工
     *
     * @param completeDtoList
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveCompleteCache(List<CompleteDto> completeDtoList) {
        //获取用户所属公司
        String companyCode = SecurityUtils.getCurrentUser().getCompanyCode();
        for (CompleteDto completeDto : completeDtoList) {
            TrackItem trackItem = trackItemService.getById(completeDto.getTiId());
            //检验人
            trackItem.setQualityCheckBy(completeDto.getQcPersonId());
            trackItem.setQualityCheckBranch(completeDto.getQualityCheckBranch());
            //根据工序Id先删除,在重新新增数据
            QueryWrapper<TrackCompleteCache> removeCache = new QueryWrapper<>();
            removeCache.eq("ti_id", completeDto.getTiId());
            trackCompleteCacheService.remove(removeCache);

            List<TrackCompleteCache> trackCompleteCacheList = new ArrayList<>();
            for (TrackComplete trackComplete : completeDto.getTrackCompleteList()) {
                if (trackComplete.getReportHours() > trackItem.getSinglePieceHours()) {
                    return CommonResult.failed("报工工时不能大于额定工时");
                }
                TrackCompleteCache trackCompleteCache = new TrackCompleteCache();
                trackCompleteCache.setAssignId(completeDto.getAssignId());
                trackCompleteCache.setTiId(completeDto.getTiId());
                trackCompleteCache.setTrackId(completeDto.getTrackId());
                trackCompleteCache.setTrackNo(completeDto.getTrackNo());
                trackCompleteCache.setProdNo(completeDto.getProdNo());
                trackCompleteCache.setCompleteBy(SecurityUtils.getCurrentUser().getUsername());
                trackCompleteCache.setCompleteTime(new Date());
                trackCompleteCache.setUserId(trackComplete.getUserId());
                trackCompleteCache.setDeviceId(trackComplete.getDeviceId());
                trackCompleteCache.setCompletedHours(trackComplete.getCompletedHours());
                trackCompleteCache.setActualHours(trackComplete.getActualHours());
                trackCompleteCache.setReportHours(trackComplete.getReportHours());
                trackCompleteCache.setStaticHours(trackComplete.getStaticHours());
                trackCompleteCache.setCompletedQty(trackComplete.getCompletedQty());
                trackCompleteCache.setRejectQty(trackComplete.getRejectQty());
                trackCompleteCache.setDetectionResult(trackComplete.getDetectionResult());
                trackCompleteCacheList.add(trackCompleteCache);
            }
            trackItemService.updateById(trackItem);
            //修改探伤报工信息
            TrackItemInspection trackItemInspection = new TrackItemInspection();
            BeanUtil.copyProperties(trackItem, trackItemInspection);
            trackItemInspectionService.updateById(trackItemInspection);

            trackCompleteCacheService.saveBatch(trackCompleteCacheList);
        }
        return CommonResult.success(true);
    }


    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> updateComplete(CompleteDto completeDto) {
        //获取用户所属公司
        String companyCode = SecurityUtils.getCurrentUser().getCompanyCode();
        if (com.mysql.cj.util.StringUtils.isNullOrEmpty(completeDto.getTiId())) {
            return CommonResult.failed("工序Id不能为空");
        }
        if (com.mysql.cj.util.StringUtils.isNullOrEmpty(completeDto.getQcPersonId())) {
            return CommonResult.failed("质检人员不能为空");
        }
        if (null == completeDto.getTrackCompleteList() && completeDto.getTrackCompleteList().isEmpty()) {
            return CommonResult.failed("报工人员不能为空");
        }
        TrackItem trackItem = trackItemService.getById(completeDto.getTiId());
        //检验人
        trackItem.setQualityCheckBy(completeDto.getQcPersonId());
        trackItem.setQualityCheckBranch(completeDto.getQualityCheckBranch());
        //根据工序Id先删除,在重新新增数据
        QueryWrapper<TrackComplete> removeComplete = new QueryWrapper<>();
        removeComplete.eq("ti_id", completeDto.getTiId());
        trackCompleteService.remove(removeComplete);
        double numDouble = 0.00;
        Assign assign = trackAssignService.getById(completeDto.getAssignId());
        double intervalNumber = assign.getQty() + 0.0;
        for (TrackComplete trackComplete : completeDto.getTrackCompleteList()) {
            if (trackComplete.getReportHours() > trackItem.getSinglePieceHours()) {
                return CommonResult.failed("报工工时不能大于额定工时");
            }
            trackComplete.setAssignId(completeDto.getAssignId());
            trackComplete.setTiId(completeDto.getTiId());
            trackComplete.setTrackId(completeDto.getTrackId());
            trackComplete.setTrackNo(completeDto.getTrackNo());
            trackComplete.setProdNo(completeDto.getProdNo());
            trackComplete.setCompleteBy(SecurityUtils.getCurrentUser().getUsername());
            trackComplete.setCompleteTime(new Date());
            numDouble += trackComplete.getCompletedQty();
        }
        //报工数量判断
        if (numDouble > assign.getQty()) {
            return CommonResult.failed("报工数量:" + numDouble + ",派工数量:" + assign.getQty() + ",完工数量不得大于" + assign.getQty());
        }
        if (numDouble < intervalNumber - 0.1) {
            return CommonResult.failed("报工数量:" + numDouble + ",派工数量:" + assign.getQty() + ",完工数量不得少于" + +(intervalNumber - 0.1));
        }
        //跟新工序完成数量
        trackItem.setCompleteQty(trackItem.getCompleteQty() + numDouble);
        trackItemService.updateById(trackItem);
        //修改探伤报工信息
        TrackItemInspection trackItemInspection = new TrackItemInspection();
        BeanUtil.copyProperties(trackItem, trackItemInspection);
        trackItemInspectionService.updateById(trackItemInspection);
        return CommonResult.success(trackCompleteService.saveOrUpdateBatch(completeDto.getTrackCompleteList()));
    }

    public CommonResult<Boolean> rollBack(String id) {
        String msg = "";
        TrackComplete trackComplete = trackCompleteService.getById(id);
        Assign assign = new Assign();
        if (null != trackComplete.getAssignId()) {
            assign = trackAssignService.getById(trackComplete.getAssignId());
        }
        TrackItem trackItem = new TrackItem();
        if (null == assign) {
            trackItem = trackItemService.getById(trackComplete.getTiId());
        } else {
            trackItem = trackItemService.getById(assign.getTiId());
        }
        if (null == trackItem) {
            removeComplete(trackComplete.getTiId());
        } else {
            QueryWrapper<TrackComplete> queryWrapper = new QueryWrapper<TrackComplete>();
            if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(id)) {
                queryWrapper.eq("track_id", id);
            } else {
                queryWrapper.eq("track_id", "-1");
            }
            queryWrapper.orderByAsc("modify_time");
            List<TrackComplete> cs = trackCompleteService.list(queryWrapper);
            //判断跟单号已质检完成，报工无法取消
            if (trackItem.getIsExistQualityCheck() == 1 && trackItem.getIsQualityComplete() == 1) {
                msg += "跟单号已质检完成，报工无法取消！";
            }
            //判断跟单号已质检完成，报工无法取消
            if (trackItem.getIsExistScheduleCheck() == 1 && trackItem.getIsScheduleComplete() == 1) {
                msg += "跟单号已调度完成，报工无法取消！";
            }
            //判断后置工序是否已派工，否则不可回滚
            QueryWrapper<Assign> queryWrapperAssign = new QueryWrapper<Assign>();
            queryWrapperAssign.eq("ti_id", trackItem.getId());
            List<Assign> assigns = trackAssignService.list(queryWrapperAssign);
            for (int j = 0; j < assigns.size(); j++) {
                TrackItem cstrackItem = trackItemService.getById(assigns.get(j).getTiId());
                if (cstrackItem.getOptSequence() > trackItem.getOptSequence()) {
                    return CommonResult.failed("无法取消报工，已有后序工序【" + cstrackItem.getOptName() + "】已派工，需要先取消后序工序");
                }
            }
            //判断后置工序是否已报工，否则不可回滚
            for (int j = 0; j < cs.size(); j++) {
                TrackItem cstrackItem = trackItemService.getById(cs.get(j).getTiId());
                if (cstrackItem.getOptSequence() > trackItem.getOptSequence()) {
                    return CommonResult.failed("无法取消报工，已有后序工序【" + cstrackItem.getOptName() + "】已报工，需要先取消后序工序");
                }
            }

            //将后置工序IS_CURRENT设置为否，状态为1
            List<TrackItem> items = trackItemService.list(new QueryWrapper<TrackItem>().eq("track_head_id", trackItem.getTrackHeadId()).orderByAsc("opt_sequence"));
            for (TrackItem trackItems : items) {
                if (trackItems.getOptSequence() > trackItem.getOptSequence() && trackItems.getIsCurrent() == 1) {
                    trackItems.setIsCurrent(0);
                    trackItems.setIsDoing(0);
                    trackItems.setIsFinalComplete("0");
                    trackItemService.updateById(trackItems);
                    //修改探伤
                    TrackItemInspection trackItemInspection = new TrackItemInspection();
                    BeanUtil.copyProperties(trackItems, trackItemInspection);
                    trackItemInspectionService.updateById(trackItemInspection);

                }
            }
            //将当前工序设置为激活
            if (msg.equals("")) {
                trackItem.setIsDoing(0);
                trackItem.setIsCurrent(1);
                trackItem.setIsFinalComplete("0");
                trackItem.setIsOperationComplete(0);
                trackItem.setAssignableQty(trackItem.getAssignableQty() + trackComplete.getCompletedQty().intValue());
                trackItemService.updateById(trackItem);
                //修改探伤
                TrackItemInspection trackItemInspection = new TrackItemInspection();
                BeanUtil.copyProperties(trackItem, trackItemInspection);
                trackItemInspectionService.updateById(trackItemInspection);

                TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
                trackHead.setStatus("1");
                trackHeadService.updateById(trackHead);
                if (null != assign) {
                    assign.setAvailQty(assign.getQty() + trackComplete.getCompletedQty().intValue());
                    assign.setState(0);
                    trackAssignService.updateById(assign);
                }
                removeComplete(trackComplete.getTiId());
            }
        }


        if (msg.equals("")) {
            return CommonResult.success(null, "删除成功！");
        } else {
            return CommonResult.failed("操作失败，请重试！" + msg);
        }
    }

    private Boolean removeComplete(String tiId) {
        QueryWrapper<TrackComplete> removeComplete = new QueryWrapper<>();
        removeComplete.eq("ti_id", tiId);
        return trackCompleteService.remove(removeComplete);
    }

    /**
     * 探伤记录审核
     *
     * @param id
     * @param tempType
     * @param isAudit
     * @return
     */
    public boolean auditByRecord(String id, String tempType, String isAudit, String auditRemark) {
        if (InspectionRecordTypeEnum.MT.getType().equals(tempType)) {
            UpdateWrapper<ProduceInspectionRecordMt> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", id).set("is_audit", isAudit).set("audit_remark", auditRemark).set("audit_by",SecurityUtils.getCurrentUser().getUserId());
            produceInspectionRecordMtService.update(updateWrapper);
        } else if (InspectionRecordTypeEnum.PT.getType().equals(tempType)) {
            UpdateWrapper<ProduceInspectionRecordPt> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", id).set("is_audit", isAudit).set("audit_remark", auditRemark).set("audit_by",SecurityUtils.getCurrentUser().getUserId());
            produceInspectionRecordPtService.update(updateWrapper);
        } else if (InspectionRecordTypeEnum.RT.getType().equals(tempType)) {
            UpdateWrapper<ProduceInspectionRecordRt> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", id).set("is_audit", isAudit).set("audit_remark", auditRemark).set("audit_by",SecurityUtils.getCurrentUser().getUserId());
            produceInspectionRecordRtService.update(updateWrapper);
        } else if (InspectionRecordTypeEnum.UT.getType().equals(tempType)) {
            UpdateWrapper<ProduceInspectionRecordUt> updateWrapper = new UpdateWrapper<>();
            updateWrapper.eq("id", id).set("is_audit", isAudit).set("audit_remark", auditRemark).set("audit_by",SecurityUtils.getCurrentUser().getUserId());
            produceInspectionRecordUtService.update(updateWrapper);
        }
        //修改中间表的数据
        UpdateWrapper<ProduceItemInspectInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("inspect_record_id",id).set("is_audit", isAudit).set("audit_by",SecurityUtils.getCurrentUser().getUserId());
        produceItemInspectInfoService.update(updateWrapper);

        return true;
    }

    /**
     * 保存委托单
     */
    public CommonResult saveInspectionPower(InspectionPower inspectionPower) throws Exception {
        inspectionPower.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        if(StringUtils.isEmpty(inspectionPower.getId())){
            //保存探伤委托单号
            Code.update("order_no",inspectionPower.getOrderNo(),SecurityUtils.getCurrentUser().getTenantId(), inspectionPower.getBranchCode(),codeRuleService);
            //委托人赋值
            inspectionPower.setConsignor(SecurityUtils.getCurrentUser().getUserId());
        }else{
            InspectionPower byId = inspectionPowerService.getById(inspectionPower.getId());
            if(byId.getStatus()==IS_STATUS){
                return CommonResult.failed("该委托单已经发起委托，不能修改");
            }
        }
        //如果是跟单派工发起的委托，修改跟单工序为已派工
        if(!StringUtils.isEmpty(inspectionPower.getItemId())){
            TrackItem trackItem = new TrackItem();
            trackItem.setId(inspectionPower.getItemId());
            //已经派工状态
            trackItem.setIsSchedule(IS_SCHEDULE);
            trackItemService.updateById(trackItem);
        }
        return CommonResult.success(inspectionPowerService.saveOrUpdate(inspectionPower));
    }

    /**
     * 批量委托
     */
    public boolean powerOrder(List<String> ids){
        if(ids.size()>0){
            UpdateWrapper<InspectionPower> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("id",ids)
                    .set("status",IS_STATUS);
            return inspectionPowerService.update(updateWrapper);
        }
        return true;
    }

    /**
     * 批量撤回
     */
    public boolean backOutOrder(List<String> ids){
        if(ids.size()>0){
            UpdateWrapper<InspectionPower> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("id",ids)
                    .set("status",BACKOUT_STATUS);
            return inspectionPowerService.update(updateWrapper);
        }
        return true;
    }

    /**
     * 探伤委托指派人
     */
    public void assignPower(List<String> ids , String assignBy) throws GlobalException{
        if(ids.size()>0){
            QueryWrapper<InspectionPower> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id",ids);
            List<InspectionPower> list = inspectionPowerService.list(queryWrapper);
            //校验 已经派工的不能再次指派
            List<InspectionPower> assginByNullList = list.stream().filter(item -> StringUtils.isEmpty(item.getAssignBy())).collect(Collectors.toList());
            if(assginByNullList.size()>0){
                throw new GlobalException("选中的委托单中，有已经指派的委托单", ResultCode.FORBIDDEN);
            }
            //指派派工人
            UpdateWrapper<InspectionPower> updateWrapper = new UpdateWrapper<>();
            updateWrapper.in("id",ids)
                    .set("assign_by",assignBy);
            inspectionPowerService.update(updateWrapper);
        }
    }

}
