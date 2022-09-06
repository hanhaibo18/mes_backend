package com.richfit.mes.produce.service;


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
import com.richfit.mes.produce.enmus.InspectionRecordTypeEnum;
import com.richfit.mes.produce.entity.ProduceInspectionRecordDto;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.utils.WordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


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
    private TrackHeadService trackHeadService;
    @Autowired
    private ProbeInfoService probeInfoService;

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
    public IPage<TrackItemInspection> page(int page, int limit, String startTime, String endTime, String trackNo, String productName, String branchCode, String tenantId, Boolean isAudit) {
        QueryWrapper<TrackItemInspection> queryWrapper = new QueryWrapper<TrackItemInspection>();
        if (!StringUtils.isEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        if (!StringUtils.isEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }
        //已审核
        if (Boolean.TRUE.equals(isAudit)) {
            queryWrapper.isNotNull("audit_by");
        } else if (Boolean.FALSE.equals(isAudit)) {
            //未审核
            queryWrapper.isNull("audit_by");
        }

        if (!StringUtils.isEmpty(trackNo)) {
            queryWrapper.inSql("id", "select id from  produce_track_item_inspection where track_head_id in ( select id from produce_track_head where track_no LIKE '" + trackNo + '%' + "')");
        }
        if (!StringUtils.isEmpty(productName)) {
            queryWrapper.inSql("id", "select id from  produce_track_item_inspection where track_head_id in ( select id from produce_track_head where product_name LIKE '" + productName + '%' + "')");
        }
        if (!StringUtils.isEmpty(startTime)) {
            queryWrapper.apply("UNIX_TIMESTAMP(modify_time) >= UNIX_TIMESTAMP('" + startTime + "')");

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
            TrackHead trackHead = trackHeadService.getById(trackItemInspection.getTrackHeadId());
            trackItemInspection.setTrackNo(trackHead.getTrackNo());
            trackItemInspection.setDrawingNo(trackHead.getDrawingNo());
            trackItemInspection.setQty(trackHead.getNumber());
            trackItemInspection.setProductName(trackHead.getProductName());
            trackItemInspection.setWorkNo(trackHead.getWorkNo());
            trackItemInspection.setTrackType(trackHead.getTrackType());
            trackItemInspection.setTexture(trackHead.getTexture());
            trackItemInspection.setPartsName(trackHead.getMaterialName());
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
        //缺陷记录
        List<ProduceDefectsInfo> produceDefectsInfos = produceInspectionRecordDto.getProduceDefectsInfos();
        //探头信息
        List<ProbeInfo> probeInfoList = produceInspectionRecordDto.getProbeInfoList();
        //工序ids
        List<String> itemIds = produceInspectionRecordDto.getItemIds();
        //探伤记录id
        String recordId = null;

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

        //保存缺陷记录
        for (ProduceDefectsInfo produceDefectsInfo : produceDefectsInfos) {
            produceDefectsInfo.setRecordId(recordId);
        }
        produceDefectsInfoService.saveBatch(produceDefectsInfos);
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
     * @param trackItemInspection
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean auditSubmitRecord(TrackItemInspection trackItemInspection){
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
        //mt探伤记录
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
        dataMap.put("img",systemServiceClient.getBase64Code(produceInspectionRecordMt.getDiagramAttachmentId()).getData());
    }

    //rt模板填充
    private void createRtDataMap(TrackHead trackHead, Map<String, Object> recordInfo, TrackItemInspection trackItemInspection, Map<String, Object> dataMap) throws IOException {
        //mt探伤记录
        ProduceInspectionRecordRt produceInspectionRecordRt = JSON.parseObject(JSON.toJSONString(recordInfo), ProduceInspectionRecordRt.class);
        //报告号
        dataMap.put("reportNo",produceInspectionRecordRt.getReportNo());
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
        //报告号
        //dataMap.put("reportNo",produceInspectionRecordUt.getReportNo());
        //图号
        /*dataMap.put("drawingNo",trackHead.getDrawingNo());
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
        //偶合剂
        dataMap.put("compareSample",produceInspectionRecordUt.getCompareSample());
        //试验规范
        dataMap.put("testSpecification",produceInspectionRecordUt.getTestSpecification());
        //验收标准
        dataMap.put("acceptanceCriteria",produceInspectionRecordUt.getAcceptanceCriteria());
        //灵敏度
        dataMap.put("sensitivity",produceInspectionRecordUt.getSensitivity());
        //零件顺序号
        dataMap.put("222",produceInspectionRecordUt.getSensitivity());
        //业主
        dataMap.put("owner",produceInspectionRecordUt.getOwner());
        //见证
        dataMap.put("witnesses",produceInspectionRecordUt.getWitnesses());
        //检验员
        dataMap.put("checkBy",trackItemInspection.getCheckBy());
        //审核人
        dataMap.put("auditBy",trackItemInspection.getAuditBy());*/
        //图片base64编码
        if(!StringUtils.isEmpty(produceInspectionRecordUt.getDiagramAttachmentId())){
            dataMap.put("img",systemServiceClient.getBase64Code(produceInspectionRecordUt.getDiagramAttachmentId()).getData());
        }
        //缺陷列表
        List<ProduceDefectsInfo> produceDefectsInfoList = produceDefectsInfoService.list(new QueryWrapper<ProduceDefectsInfo>()
                .eq("record_id", produceInspectionRecordUt.getId())
                .orderByAsc("serial_num"));
        //缺陷记录
        dataMap.put("defectsInfoList",produceDefectsInfoList);
        ProduceDefectsInfo produceDefectsInfo = new ProduceDefectsInfo();
        produceDefectsInfo.setTestResults("1");
        ProduceDefectsInfo produceDefectsInfo2 = new ProduceDefectsInfo();
        produceDefectsInfo2.setTestResults("2");
        produceDefectsInfoList.add(produceDefectsInfo);
        produceDefectsInfoList.add(produceDefectsInfo2);
        //探头列表
        List<ProbeInfo> probeInfoList = probeInfoService.list(new QueryWrapper<ProbeInfo>()
                .eq("record_id", produceInspectionRecordUt.getId())
                .orderByAsc("serial_num"));
        ProbeInfo probeInfo1 = new ProbeInfo();
        probeInfo1.setAngle("q");
        probeInfo1.setFrequency("1");
        probeInfo1.setLeadingEdge("1");
        probeInfoList.add(probeInfo1);
        ProbeInfo probeInfo2 = new ProbeInfo();
        probeInfo2.setAngle("2");
        probeInfo2.setFrequency("2");
        probeInfo2.setLeadingEdge("2");
        probeInfoList.add(probeInfo2);
        ProbeInfo probeInfo3 = new ProbeInfo();
        probeInfo3.setAngle("3");
        probeInfo3.setFrequency("3");
        probeInfo3.setLeadingEdge("3");
        probeInfoList.add(probeInfo3);
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


}
