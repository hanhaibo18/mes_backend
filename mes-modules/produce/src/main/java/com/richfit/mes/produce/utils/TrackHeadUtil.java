package com.richfit.mes.produce.utils;

import cn.hutool.core.collection.CollectionUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.TrackHeadPublicDto;
import com.richfit.mes.produce.service.CodeRuleService;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 功能描述: 跟单工具类
 *
 * @Author: zhiqiang.lu
 * @Date: 2022.9.26
 */
public class TrackHeadUtil {

    /**
     * 功能描述: 根据保存的信息进行跟单的数据封装，首先根据是否单件批量进行数据重组
     *
     * @param trackHeadPublicDto 页面传递过来的跟单信息
     * @param codeRuleService    编码 Service
     * @Author: zhiqiang.lu
     * @Date: 2022.9.26
     */
    public static List<TrackHeadPublicDto> saveInfo(TrackHeadPublicDto trackHeadPublicDto, CodeRuleService codeRuleService) throws Exception {
        List<TrackHeadPublicDto> trackHeadList = new ArrayList<>();
        //用于区分是否单件、批量
        if (TrackHead.TRACKHEAD_BATCH_YES.equals(trackHeadPublicDto.getIsBatch())) {
            if (CollectionUtils.isEmpty(trackHeadPublicDto.getStoreList())) {
                //单件装配批量根据数量拆分跟单数量
                int number = trackHeadPublicDto.getNumber();
                for (int i = 0; i < number; i++) {
                    //避免引用类型
                    TrackHeadPublicDto tpd = new TrackHeadPublicDto();
                    tpd = JSON.parseObject(JSON.toJSONString(trackHeadPublicDto), TrackHeadPublicDto.class);
                    tpd.setNumber(1);
                    //流水号获取
                    trackNo(tpd, codeRuleService);
                    trackHeadList.add(tpd);
                }
            } else {
                //单件机加批量跟单会带入生成编码的物料数据列表，产品编码等信息
                for (Map m : trackHeadPublicDto.getStoreList()) {
                    //多数据创建新的对象
                    TrackHeadPublicDto newModel = new TrackHeadPublicDto();
                    BeanUtils.copyProperties(trackHeadPublicDto, newModel);
                    //流水号获取
                    trackNo(newModel, codeRuleService);
                    newModel.setProductNo((String) m.get("workblankNo"));
                    newModel.setNumber((Integer) m.get("num"));
                    newModel.setProductSource((String) m.get("materialSource"));
                    newModel.setProductSourceName((String) m.get("materialSourceName"));
                    trackHeadList.add(newModel);
                }
            }
        } else {
            //其他类型的跟单
            trackNo(trackHeadPublicDto, codeRuleService);
            trackHeadList.add(trackHeadPublicDto);
        }
        return trackHeadList;
    }

    /**
     * 功能描述: 根据保存的信息进行跟单生产线的数据封装，根据是否单件批量进行数据重组
     *
     * @param trackHeadPublicDto 页面传递过来的跟单信息
     * @Author: zhiqiang.lu
     * @Date: 2022.9.26
     */
    public static List<TrackHead> flowInfo(TrackHeadPublicDto trackHeadPublicDto) throws Exception {
        List<TrackHead> trackHeadList = new ArrayList<>();
        //IsBatch类型为N(是否批次：Y是 N否), StoreList 不为空 进行创建
        if (TrackHead.TRACKHEAD_BATCH_NO.equals(trackHeadPublicDto.getIsBatch()) && CollectionUtils.isNotEmpty(trackHeadPublicDto.getStoreList())) {
            //单件多数量（多生产线）
            for (Map m : trackHeadPublicDto.getStoreList()) {
                TrackHead trackHead = new TrackHead();
                BeanUtils.copyProperties(trackHeadPublicDto, trackHead);
                trackHead.setProductNo((String) m.get("workblankNo"));
                trackHead.setProductSource((String) m.get("materialSource"));
                trackHead.setProductSourceName((String) m.get("materialSourceName"));
                trackHead.setNumber((Integer) m.get("num"));
                trackHeadList.add(trackHead);
            }
        } else {
            //正常生产线
            TrackHead trackHead = new TrackHead();
            BeanUtils.copyProperties(trackHeadPublicDto, trackHead);
            trackHeadList.add(trackHead);
        }
        return trackHeadList;
    }

    public static void trackNo(TrackHeadPublicDto trackHeadPublicDto, CodeRuleService codeRuleService) throws Exception {
        String code = Code.valueOnUpdate("track_no", SecurityUtils.getCurrentUser().getTenantId(), trackHeadPublicDto.getBranchCode(), codeRuleService);
        trackHeadPublicDto.setTrackNo(code);
    }

    /**
     * 根据第一个产品号，批量生成产品号
     */
    public static void productNoAutogeneration(TrackHeadPublicDto trackHead){
        List<Map> returnStoreList = new ArrayList<>();
        List<Map> storeList = trackHead.getStoreList();
        Integer number = trackHead.getNumber();
        if(CollectionUtil.isEmpty(storeList)){
            throw new GlobalException("产品号不能为空！！", ResultCode.FAILED);
        }
        if(number<1){
            return;
        }
        String beginStr = (String) storeList.get(0).get("beginStr");
        int serialNumber = (int) storeList.get(0).get("serialNumber");
        String endStr = (String) storeList.get(0).get("endStr");
        for (int i= 0;i<=number;i++) {
            String newWorkblankNo = beginStr+String.valueOf(serialNumber+i)+endStr;
            Map<String, String> store = storeList.get(0);
            store.put("workblankNo",newWorkblankNo);
            store.put("serialNumber",String.valueOf(serialNumber+1));
            returnStoreList.add(store);
        }
        trackHead.setStoreList(returnStoreList);
    }
}
