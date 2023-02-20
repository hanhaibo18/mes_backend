package com.richfit.mes.produce.utils;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.TrackHeadMoldDto;
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
     * @param trackHeadMoldDto 页面传递过来的跟单信息
     * @param codeRuleService  编码 Service
     * @Author: zhiqiang.lu
     * @Date: 2022.9.26
     */
    public static List<TrackHeadMoldDto> saveInfo(TrackHeadMoldDto trackHeadMoldDto, CodeRuleService codeRuleService) throws Exception {
        List<TrackHeadMoldDto> trackHeadList = new ArrayList<>();
        //用于区分是否单件、批量
        if (TrackHead.TRACKHEAD_BATCH_YES.equals(trackHeadMoldDto.getIsBatch())) {
            if (CollectionUtils.isEmpty(trackHeadMoldDto.getStoreList())) {
                //单件装配批量根据数量拆分跟单数量
                int number = trackHeadMoldDto.getNumber();
                for (int i = 0; i < number; i++) {
                    //流水号获取
                    trackNo(trackHeadMoldDto, codeRuleService);
                    trackHeadMoldDto.setNumber(1);
                    trackHeadList.add(trackHeadMoldDto);
                }
            } else {
                //单件机加批量跟单会带入生成编码的物料数据列表，产品编码等信息
                for (Map m : trackHeadMoldDto.getStoreList()) {
                    //多数据创建新的对象
                    TrackHeadMoldDto newModel = new TrackHeadMoldDto();
                    BeanUtils.copyProperties(trackHeadMoldDto, newModel);
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
            trackNo(trackHeadMoldDto, codeRuleService);
            trackHeadList.add(trackHeadMoldDto);
        }
        return trackHeadList;
    }

    /**
     * 功能描述: 根据保存的信息进行跟单生产线的数据封装，根据是否单件批量进行数据重组
     *
     * @param trackHeadMoldDto 页面传递过来的跟单信息
     * @Author: zhiqiang.lu
     * @Date: 2022.9.26
     */
    public static List<TrackHead> flowInfo(TrackHeadMoldDto trackHeadMoldDto) throws Exception {
        List<TrackHead> trackHeadList = new ArrayList<>();
        //IsBatch类型为N(是否批次：Y是 N否), StoreList 不为空 进行创建
        if (TrackHead.TRACKHEAD_BATCH_NO.equals(trackHeadMoldDto.getIsBatch()) && CollectionUtils.isNotEmpty(trackHeadMoldDto.getStoreList())) {
            //单件多数量（多生产线）
            for (Map m : trackHeadMoldDto.getStoreList()) {
                TrackHead trackHead = new TrackHead();
                BeanUtils.copyProperties(trackHeadMoldDto, trackHead);
                trackHead.setProductNo((String) m.get("workblankNo"));
                trackHead.setProductSource((String) m.get("materialSource"));
                trackHead.setProductSourceName((String) m.get("materialSourceName"));
                trackHead.setNumber((Integer) m.get("num"));
                trackHeadList.add(trackHead);
            }
        } else {
            //正常生产线
            TrackHead trackHead = new TrackHead();
            BeanUtils.copyProperties(trackHeadMoldDto, trackHead);
            trackHeadList.add(trackHead);
        }
        return trackHeadList;
    }

    public static void trackNo(TrackHeadMoldDto trackHeadMoldDto, CodeRuleService codeRuleService) throws Exception {
        String code = Code.valueOnUpdate("track_no", SecurityUtils.getCurrentUser().getTenantId(), trackHeadMoldDto.getBranchCode(), codeRuleService);
        trackHeadMoldDto.setTrackNo(code);
    }
}
