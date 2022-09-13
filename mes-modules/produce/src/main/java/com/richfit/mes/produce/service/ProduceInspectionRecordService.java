package com.richfit.mes.produce.service;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.TrackHeadMapper;
import com.richfit.mes.produce.enmus.IdEnum;
import com.richfit.mes.produce.enmus.InspectionRecordTypeEnum;
import com.richfit.mes.produce.enmus.PublicCodeEnum;
import com.richfit.mes.produce.entity.CompleteDto;
import com.richfit.mes.produce.entity.ProduceInspectionRecordDto;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.utils.WordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.*;

import java.util.*;
import java.util.stream.Collectors;
import freemarker.template.*;
import org.springframework.util.StringUtils;

/***
 * 探伤记录
 * @author renzewen
 */
@Service
@Slf4j
public class ProduceInspectionRecordService{

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

    /**
     * 查询跟单工序探伤列表
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
    public IPage<TrackItemInspection> page(int page, int limit, String startTime, String endTime, String trackNo, String productName,String productNo, String branchCode, String tenantId, Integer isAudit,Integer isOperationComplete) {
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
            queryWrapper.inSql("id", "select id from  produce_track_item_inspection where track_head_id in ( select id from produce_track_head where track_no LIKE '" + trackNo + '%' + "')");
        }
        if (!StringUtils.isEmpty(productName)) {
            queryWrapper.inSql("id", "select id from  produce_track_item_inspection where track_head_id in ( select id from produce_track_head where product_name LIKE '" + productName + '%' + "')");
        }
        if (!StringUtils.isEmpty(productNo)) {
            queryWrapper.likeLeft("productNo", productNo);
        }
        if (!StringUtils.isEmpty(isOperationComplete)) {
            queryWrapper.eq("is_operation_complete",isOperationComplete);
        }
        if (!StringUtils.isEmpty(startTime)) {
            queryWrapper.apply("UNIX_TIMESTAMP(modify_time) >= UNIX_TIMESTAMP('" + startTime + "')");
        }
        if (!StringUtils.isEmpty(endTime)) {
            queryWrapper.apply("UNIX_TIMESTAMP(modify_time) >= UNIX_TIMESTAMP('" + endTime + "')");
        }
        queryWrapper.orderByDesc("modify_time");
        IPage<TrackItemInspection> trackItemInspections = trackItemInspectionService.page(new Page<TrackItemInspection>(page, limit), queryWrapper);
        for (TrackItemInspection trackItemInspection : trackItemInspections.getRecords()) {
            TrackHead trackHead = trackHeadMapper.selecProjectNametById(trackItemInspection.getTrackHeadId());
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
        return trackItemInspections;
    }

    /**
     * 保存探伤记录
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public CommonResult saveRecord(ProduceInspectionRecordDto produceInspectionRecordDto) throws GlobalException{
        //获取模板类型
        String tempType = produceInspectionRecordDto.getTempType();
        //要保存的记录实体
        JSONObject jsonObject = produceInspectionRecordDto.getInspectionRecord();
        //探头信息
        List<ProbeInfo> probeInfoList = produceInspectionRecordDto.getProbeInfoList();
        //工序ids
        List<String> itemIds = produceInspectionRecordDto.getItemIds();
        //探伤记录id
        String recordId = null;
        //生成探伤记录号
        CodeRule codeRule = codeRuleService.gerCode("ut_record_no", null, null, SecurityUtils.getCurrentUser().getTenantId(),null);
        if (codeRule == null || com.mysql.cj.util.StringUtils.isNullOrEmpty(codeRule.getCurValue())) {
            throw new GlobalException("获取跟单号出现异常", ResultCode.FAILED);
        }
        jsonObject.put("recordNo",codeRule.getCurValue());
        //保存流水号
        codeRuleService.updateCode("ut_record_no",null, codeRule.getCurValue(), null,SecurityUtils.getCurrentUser().getTenantId(),null);

        if(InspectionRecordTypeEnum.MT.getType().equals(tempType)){
            //保存探伤记录
            ProduceInspectionRecordMt produceInspectionRecordMt = jsonObject.toJavaObject(ProduceInspectionRecordMt.class);
            produceInspectionRecordMtService.save(produceInspectionRecordMt);
            recordId = produceInspectionRecordMt.getId();
        }else if(InspectionRecordTypeEnum.PT.getType().equals(tempType)){
            ProduceInspectionRecordPt produceInspectionRecordPt = jsonObject.toJavaObject(ProduceInspectionRecordPt.class);
            produceInspectionRecordPtService.save(produceInspectionRecordPt);
            recordId = produceInspectionRecordPt.getId();
        }else if(InspectionRecordTypeEnum.RT.getType().equals(tempType)){
            ProduceInspectionRecordRt produceInspectionRecordRt = jsonObject.toJavaObject(ProduceInspectionRecordRt.class);
            produceInspectionRecordRtService.save(produceInspectionRecordRt);
            recordId = produceInspectionRecordRt.getId();
        }else if(InspectionRecordTypeEnum.UT.getType().equals(tempType)){
            ProduceInspectionRecordUt produceInspectionRecordUt = jsonObject.toJavaObject(ProduceInspectionRecordUt.class);
            produceInspectionRecordUtService.save(produceInspectionRecordUt);
            recordId = produceInspectionRecordUt.getId();
            //绑定探头
            for (ProbeInfo probeInfo : probeInfoList) {
                probeInfo.setRecordId(recordId);
            }
            //保存探头
            probeInfoService.saveBatch(probeInfoList);
        }else {
            throw new GlobalException(ResultCode.INVALID_ARGUMENTS.getMessage(),ResultCode.INVALID_ARGUMENTS);
        }
        //工序and探伤记录绑定操作
        List<ProduceItemInspectInfo> produceItemInspectInfos = new ArrayList<>();
        for (String itemId : itemIds) {
            ProduceItemInspectInfo produceItemInspectInfo = new ProduceItemInspectInfo();
            produceItemInspectInfo.setTrackItemId(itemId);
            produceItemInspectInfo.setInspectRecordId(recordId);
            produceItemInspectInfo.setTempType(tempType);
            produceItemInspectInfos.add(produceItemInspectInfo);
        }
        produceItemInspectInfoService.saveBatch(produceItemInspectInfos);
        return  null;
    }

    /**
     *根据工序id查询探伤记录
     * @param itemId
     * @return
     */
    public List<Object> queryRecordByItemId(String itemId){
        QueryWrapper<ProduceItemInspectInfo> itemInspectInfoQueryWrapper = new QueryWrapper<>();
        itemInspectInfoQueryWrapper.eq("track_item_id",itemId);
        List<ProduceItemInspectInfo> list = produceItemInspectInfoService.list(itemInspectInfoQueryWrapper);
        //按照模板类型分组  key->模板类型  value->探伤记录id
        Map<String, List<String>> tempValues = list.stream().collect(Collectors.groupingBy(ProduceItemInspectInfo::getTempType,Collectors.mapping(ProduceItemInspectInfo::getInspectRecordId,Collectors.toList())));
        //要返回的集合
        List<Object> returnList = new ArrayList<>();
        tempValues.forEach((tempType,ids)->{
            if(InspectionRecordTypeEnum.MT.getType().equals(tempType)){
                returnList.addAll(produceInspectionRecordMtService.queryListByIds(ids));
            }else if(InspectionRecordTypeEnum.PT.getType().equals(tempType)){
                returnList.addAll(produceInspectionRecordPtService.queryListByIds(ids));
            }else if(InspectionRecordTypeEnum.RT.getType().equals(tempType)){
                returnList.addAll(produceInspectionRecordRtService.queryListByIds(ids));
            }else if(InspectionRecordTypeEnum.UT.getType().equals(tempType)){
                returnList.addAll(produceInspectionRecordUtService.queryListByIds(ids));
            }
        });
        return  returnList;
    }

    /**
     * 审核提交探伤记录
     * @param itemId 探伤工序id
     * @param remark 探伤备注
     * @param flawDetection 探伤结果
     * @param tempType 探伤记录模板
     * @param recordNo 探伤记录编号
     * @param checkBy 探伤检验人
     * @param auditBy 探伤审核人
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean auditSubmitRecord(String itemId,String remark,Integer flawDetection,String tempType,String recordNo,String checkBy,String auditBy){
        TrackItemInspection trackItemInspection = new TrackItemInspection();
        trackItemInspection.setId(itemId);
        trackItemInspection.setRemark(remark);
        trackItemInspection.setFlawDetection(flawDetection);
        trackItemInspection.setTempType(tempType);
        trackItemInspection.setInspectRecordNo(recordNo);
        trackItemInspection.setCheckBy(checkBy);
        trackItemInspection.setAuditBy(auditBy);
        //这里生成报告号

        return trackItemInspectionService.updateById(trackItemInspection);
    }

    /**
     * 报告导出doc
     */
    public void exoprtReport(HttpServletResponse response,String itemId) throws IOException, TemplateException {
        //跟单信息
        TrackHead trackHead = null;
        //探伤记录
        Map<String,Object> recordInfo = new HashMap<>();
        //查询探伤工序
        TrackItemInspection trackItemInspection = trackItemInspectionService.getById(itemId);

        if(!ObjectUtil.isEmpty(trackItemInspection)){
            trackHead = trackHeadService.getById(trackItemInspection.getTrackHeadId());
            List<Map<String, Object>> list = new ArrayList<>();
            if(InspectionRecordTypeEnum.MT.getType().equals(trackItemInspection.getTempType())){
                list = produceInspectionRecordMtService.listMaps(new QueryWrapper<ProduceInspectionRecordMt>().eq("record_no", trackItemInspection.getInspectRecordNo()));
            }else if(InspectionRecordTypeEnum.PT.getType().equals(trackItemInspection.getTempType())){
                list = produceInspectionRecordPtService.listMaps(new QueryWrapper<ProduceInspectionRecordPt>().eq("record_no", trackItemInspection.getInspectRecordNo()));
            }else if(InspectionRecordTypeEnum.RT.getType().equals(trackItemInspection.getTempType())){
                list = produceInspectionRecordRtService.listMaps(new QueryWrapper<ProduceInspectionRecordRt>().eq("record_no", trackItemInspection.getInspectRecordNo()));
            }else if(InspectionRecordTypeEnum.UT.getType().equals(trackItemInspection.getTempType())){
                list = produceInspectionRecordUtService.listMaps(new QueryWrapper<ProduceInspectionRecordUt>().eq("record_no", trackItemInspection.getInspectRecordNo()));
            }
            if(!CollectionUtil.isEmpty(list)){
                recordInfo = list.get(0);
            }
        }
        Map<String, Object> dataMap = new HashMap<>();
        //填充数据
        createDataMap(trackHead,recordInfo,trackItemInspection,dataMap,trackItemInspection.getTempType());

        //根据模板类型获取模板和导出文件名
        Map<String, String> tempNameAndDocNameMap = checkTempNameAndDocName(trackItemInspection.getTempType());
        //导出
        wordUtil.exoprtReport(response,dataMap,tempNameAndDocNameMap.get("tempName"),tempNameAndDocNameMap.get("docName"));
    }

    /**
     * 构造导出填充数据dataMap
     * @param trackHead
     * @param recordInfo
     * @param trackItemInspection
     * @param dataMap
     * @param tempType
     */
    private void createDataMap(TrackHead trackHead,Map<String,Object> recordInfo,TrackItemInspection trackItemInspection,Map<String, Object> dataMap,String tempType)throws IOException{
        if(InspectionRecordTypeEnum.MT.getType().equals(tempType)){
            createMtDataMap(trackHead, recordInfo, trackItemInspection, dataMap);
        }else if(InspectionRecordTypeEnum.PT.getType().equals(tempType)){
            createPtDataMap(trackHead, recordInfo, trackItemInspection, dataMap);
        }else if(InspectionRecordTypeEnum.RT.getType().equals(tempType)){
            createRtDataMap(trackHead, recordInfo, trackItemInspection, dataMap);
        }else if(InspectionRecordTypeEnum.UT.getType().equals(tempType)){
            createUtDataMap(trackHead, recordInfo, trackItemInspection, dataMap);
        }
        dataMap.put("year", String.valueOf(DateUtil.year(DateUtil.date())));
        dataMap.put("month", DateUtil.thisMonth()+1);
        dataMap.put("day", DateUtil.dayOfMonth(DateUtil.date()));
    }

    //mt模板填充
    private void createMtDataMap(TrackHead trackHead, Map<String, Object> recordInfo, TrackItemInspection trackItemInspection, Map<String, Object> dataMap) throws IOException {
        /*//mt探伤记录
        ProduceInspectionRecordMt produceInspectionRecordMt = JSON.parseObject(JSON.toJSONString(recordInfo), ProduceInspectionRecordMt.class);
        //报告号
        dataMap.put("reportNo",produceInspectionRecordMt.getReportNo());
        //图号
        dataMap.put("drawingNo",trackHead.getDrawingNo());
        //零件名称
        dataMap.put("materialName",trackHead.getMaterialName());
        //材质
        dataMap.put("checkMaterial",produceInspectionRecordMt.getCheckMaterial());
        //报告编号
        dataMap.put("reportNo",produceInspectionRecordMt.getReportNo());
        //种类
        dataMap.put("typeList", StringUtils.hasText(produceInspectionRecordMt.getType()) ? produceInspectionRecordMt.getType().split("/") : new String[0]);
        //提升力
        dataMap.put("n",produceInspectionRecordMt.getLiftPower());
        //磁化规范
        dataMap.put("magnetizationSpecificationList",StringUtils.hasText(produceInspectionRecordMt.getMagnetizationSpecification()) ? produceInspectionRecordMt.getMagnetizationSpecification().split("/") : new String[0]);
        //试验规范
        dataMap.put("testSpecification",produceInspectionRecordMt.getTestSpecification());
        //试验标准
        dataMap.put("acceptanceCriteria",produceInspectionRecordMt.getAcceptanceCriteria());
        //灵敏度试片
        dataMap.put("sensitivityTestPiece",produceInspectionRecordMt.getSensitivityTestPiece());
        //检验员
        dataMap.put("checkBy",trackItemInspection.getCheckBy());
        //审核人
        dataMap.put("auditBy",trackItemInspection.getAuditBy());
        //图片base64编码
        dataMap.put("img",systemServiceClient.getBase64Code(produceInspectionRecordMt.getDiagramAttachmentId()).getData());*/
    }

    //rt模板填充
    private void createRtDataMap(TrackHead trackHead, Map<String, Object> recordInfo, TrackItemInspection trackItemInspection, Map<String, Object> dataMap) throws IOException {
        //mt探伤记录
        ProduceInspectionRecordRt produceInspectionRecordRt = JSON.parseObject(JSON.toJSONString(recordInfo), ProduceInspectionRecordRt.class);
        //报告号
        //dataMap.put("reportNo",produceInspectionRecordRt.getReportNo());
        //图号
        dataMap.put("drawingNo",trackHead.getDrawingNo());
        //零件名称
        dataMap.put("materialName",trackHead.getMaterialName());
        //检验员
        dataMap.put("checkBy",trackItemInspection.getCheckBy());
        //审核人
        dataMap.put("auditBy",trackItemInspection.getAuditBy());
        //图片base64编码
        dataMap.put("img",systemServiceClient.getBase64Code(produceInspectionRecordRt.getDiagramAttachmentId()).getData());
    }

    //pt模板填充
    private void createPtDataMap(TrackHead trackHead, Map<String, Object> recordInfo, TrackItemInspection trackItemInspection, Map<String, Object> dataMap) throws IOException {
        //mt探伤记录
        ProduceInspectionRecordPt produceInspectionRecordPt = JSON.parseObject(JSON.toJSONString(recordInfo), ProduceInspectionRecordPt.class);
        //报告号
        dataMap.put("reportNo",produceInspectionRecordPt.getReportNo());
        //图号
        dataMap.put("drawingNo",trackHead.getDrawingNo());
        //零件名称
        dataMap.put("materialName",trackHead.getMaterialName());
        //检验员
        dataMap.put("checkBy",trackItemInspection.getCheckBy());
        //审核人
        dataMap.put("auditBy",trackItemInspection.getAuditBy());
        //图片base64编码
        dataMap.put("img",systemServiceClient.getBase64Code(produceInspectionRecordPt.getDiagramAttachmentId()).getData());
    }

    //ut模板填充
    private void createUtDataMap(TrackHead trackHead, Map<String, Object> recordInfo, TrackItemInspection trackItemInspection, Map<String, Object> dataMap) throws IOException {
        //ut探伤记录
        ProduceInspectionRecordUt produceInspectionRecordUt = JSON.parseObject(JSON.toJSONString(recordInfo), ProduceInspectionRecordUt.class);
        //记录号
        dataMap.put("recordNo",produceInspectionRecordUt.getRecordNo());
        //图号
        dataMap.put("drawingNo",trackHead.getDrawingNo());
        //零件名称
        dataMap.put("materialName",trackHead.getMaterialName());
        //仪器型号
        dataMap.put("instrumentModel",produceInspectionRecordUt.getInstrumentModel());
        //生产单位
        dataMap.put("11","生产单位");
        //材质
        dataMap.put("texture",trackHead.getTexture());
        //种类
        dataMap.put("type",produceInspectionRecordUt.getType());
        //偶合剂
        dataMap.put("couplingAgent",produceInspectionRecordUt.getCouplingAgent());
        //对比试样
        dataMap.put("compareSample",produceInspectionRecordUt.getCompareSample());
        //试验规范
        dataMap.put("testSpecification",produceInspectionRecordUt.getTestSpecification());
        //验收标准
        dataMap.put("acceptanceCriteria",produceInspectionRecordUt.getAcceptanceCriteria());
        //灵敏度
        dataMap.put("sensitivity",produceInspectionRecordUt.getSensitivity());
        //零件顺序号
        dataMap.put("222",produceInspectionRecordUt.getSensitivity());
        //温度
        dataMap.put("tempera",produceInspectionRecordUt.getTempera());
        //室温
        dataMap.put("isRoomTemp",produceInspectionRecordUt.getIsRoomTemp());
        //粗糙度
        dataMap.put("roughness",produceInspectionRecordUt.getRoughness());
        //业主
        dataMap.put("owner",produceInspectionRecordUt.getOwner());
        //见证
        dataMap.put("witnesses",produceInspectionRecordUt.getWitnesses());
        //检验员
        dataMap.put("checkBy",trackItemInspection.getCheckBy());
        //审核人
        dataMap.put("auditBy",trackItemInspection.getAuditBy());
        //检验结果
        dataMap.put("inspectionResults",produceInspectionRecordUt.getInspectionResults());
        //图片base64编码
        if(!StringUtils.isEmpty(produceInspectionRecordUt.getDiagramAttachmentId())){
            dataMap.put("img",systemServiceClient.getBase64Code(produceInspectionRecordUt.getDiagramAttachmentId()).getData());
        }
        //探头列表
        List<ProbeInfo> probeInfoList = probeInfoService.list(new QueryWrapper<ProbeInfo>()
                .eq("record_id", produceInspectionRecordUt.getId())
                .orderByAsc("serial_num"));
        //探头列表
        dataMap.put("probeInfoList",probeInfoList);

    }

    /**
     * 根据模板类型获取模板和导出文件名
     * @param tempType
     * @return
     */
    private Map<String,String> checkTempNameAndDocName(String tempType){
        Map<String, String> returnMap = new HashMap<>();
        if(InspectionRecordTypeEnum.MT.getType().equals(tempType)){
            returnMap.put("tempName","mtTemp.ftl");
            returnMap.put("docName","磁粉探伤报告");
        }else if(InspectionRecordTypeEnum.PT.getType().equals(tempType)){
            returnMap.put("tempName","ptTemp.ftl");
            returnMap.put("docName","渗透探伤报告");
        }else if(InspectionRecordTypeEnum.RT.getType().equals(tempType)){
            returnMap.put("tempName","rtTemp.ftl");
            returnMap.put("docName","射线探伤报告");
        }else if(InspectionRecordTypeEnum.UT.getType().equals(tempType)){
            returnMap.put("tempName","utTemp.ftl");
            returnMap.put("docName","超声探伤报告");
        }else{
            throw new GlobalException(ResultCode.ITEM_NOT_FOUND.getMessage(), ResultCode.FAILED);
        }
        return returnMap;
    }

    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveComplete(List<CompleteDto> completeDtoList) {
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
                BeanUtil.copyProperties(trackItem,trackItemInspection);
                //修改探伤工序表
                trackItemInspectionService.updateById(trackItemInspection);
                trackCompleteCacheService.remove(queryWrapper);
            }
            log.error(completeDto.getTrackCompleteList().toString());
            trackCompleteService.saveBatch(completeDto.getTrackCompleteList());
        }
        return CommonResult.success(true);
    }


}
