package com.richfit.mes.produce.service;


import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.produce.enmus.InspectionRecordTypeEnum;
import com.richfit.mes.produce.entity.ProduceInspectionRecordDto;
import com.richfit.mes.produce.utils.WordUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import javax.servlet.http.HttpServletRequest;
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
    private ProduceItemInspectInfoService produceItemInspectInfoServicel;
    @Autowired
    private TrackItemService trackItemService;
    @Autowired
    private WordUtil wordUtil;

    /**
     * 保存探伤记录
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
    public CommonResult saveOrUpdateRecord(ProduceInspectionRecordDto produceInspectionRecordDto){
        //判断新增or修改
        boolean insert = !ObjectUtil.isEmpty(produceInspectionRecordDto.getInspectionRecord().get("id"))?false:true;
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
            produceInspectionRecordMtService.saveOrUpdate(produceInspectionRecordMt);
            recordId = produceInspectionRecordMt.getId();
        }else if(InspectionRecordTypeEnum.PT.getType().equals(tempType)){
            ProduceInspectionRecordPt produceInspectionRecordPt = jsonObject.toJavaObject(ProduceInspectionRecordPt.class);
            produceInspectionRecordPtService.saveOrUpdate(produceInspectionRecordPt);
            recordId = produceInspectionRecordPt.getId();
        }else if(InspectionRecordTypeEnum.RT.getType().equals(tempType)){
            ProduceInspectionRecordRt produceInspectionRecordRt = jsonObject.toJavaObject(ProduceInspectionRecordRt.class);
            produceInspectionRecordRtService.saveOrUpdate(produceInspectionRecordRt);
            recordId = produceInspectionRecordRt.getId();
        }else if(InspectionRecordTypeEnum.UT.getType().equals(tempType)){
            ProduceInspectionRecordUt produceInspectionRecordUt = jsonObject.toJavaObject(ProduceInspectionRecordUt.class);
            produceInspectionRecordUtService.saveOrUpdate(produceInspectionRecordUt);
            recordId = produceInspectionRecordUt.getId();
        }
        //新增操作才去执行工序and探伤记录绑定操作
        if(insert){
            List<ProduceItemInspectInfo> produceItemInspectInfos = new ArrayList<>();
            for (String itemId : itemIds) {
                ProduceItemInspectInfo produceItemInspectInfo = new ProduceItemInspectInfo();
                produceItemInspectInfo.setTrackItemId(itemId);
                produceItemInspectInfo.setInspectRecordId(recordId);
                produceItemInspectInfo.setTempType(tempType);
                produceItemInspectInfos.add(produceItemInspectInfo);
            }
            produceItemInspectInfoService.saveBatch(produceItemInspectInfos);
        }

        //保存缺陷记录
        for (ProduceDefectsInfo produceDefectsInfo : produceDefectsInfos) {
            produceDefectsInfo.setRecordId(recordId);
        }
        produceDefectsInfoService.saveOrUpdateBatch(produceDefectsInfos);
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
        List<ProduceItemInspectInfo> list = produceItemInspectInfoServicel.list(itemInspectInfoQueryWrapper);
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
    public Boolean auditSubmitRecord(TrackItem trackItem){
        return trackItemService.save(trackItem);
    }

    /**
     * 报告导出doc
     */
    public void exoprtReport(HttpServletResponse response) throws IOException, TemplateException {
        //跟单信息

        //探伤记录

        //构造填充数据
        Map<String, Object> dataMap = new HashMap<>();
        List<String> list = new ArrayList<>();
        list.add("1");
        dataMap.put("materialName", "测试赋值");
        dataMap.put("list", list);
        dataMap.put("cc", 1);
        dataMap.put("date", "2020-07-27");
        //模版名称
        String tempName = "mtTemp.ftl";
        //导出
        wordUtil.exoprtReport(response,dataMap,tempName);

    }





}
