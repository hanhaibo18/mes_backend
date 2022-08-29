package com.richfit.mes.produce.service;


import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import freemarker.template.*;

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
    private TrackItemService trackItemService;
    @Autowired
    private WordUtil wordUtil;
    @Autowired
    private SystemServiceClient systemServiceClient;

    /**
     * 保存探伤记录
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public CommonResult saveRecord(ProduceInspectionRecordDto produceInspectionRecordDto){
        //获取模板类型
        String tempType = (String)produceInspectionRecordDto.getInspectionRecord().get("tempType");
        //要保存的记录实体
        JSONObject jsonObject = produceInspectionRecordDto.getInspectionRecord();
        //缺陷记录
        List<ProduceDefectsInfo> produceDefectsInfos = produceInspectionRecordDto.getProduceDefectsInfos();
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
        }
        //新增操作才去执行工序and探伤记录绑定操作
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
     * @param trackItem
     */
    @Transactional(rollbackFor = Exception.class)
    public Boolean auditSubmitRecord(TrackItem trackItem){
        return trackItemService.save(trackItem);
    }

    /**
     * 报告导出doc
     */
    public void exoprtReport(HttpServletResponse response) throws IOException, TemplateException {
        Map<String, Object> dataMap = new HashMap<>();
        //跟单信息

        //探伤记录

        //构造填充数据

        List<String> list = new ArrayList<>();
        list.add("1");
        dataMap.put("materialName", "测试赋值");
        dataMap.put("list", list);
        dataMap.put("adress", "昆仑数智");
        //图片base64编码
        dataMap.put("img",systemServiceClient.getBase64Code("1a1ed0cae2e4bf12f2205fac13b5fdd4").getData());
        //根据模板类型获取模板和导出文件名
        Map<String, String> tempNameAndDocNameMap = checkTempNameAndDocName("mt");
        //导出
        wordUtil.exoprtReport(response,dataMap,tempNameAndDocNameMap.get("tempName"),tempNameAndDocNameMap.get("docName"));
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
            returnMap.put("docName","超声波探伤报告");
        }else{
            throw new GlobalException(ResultCode.ITEM_NOT_FOUND.getMessage(), ResultCode.FAILED);
        }
        return returnMap;
    }



}
