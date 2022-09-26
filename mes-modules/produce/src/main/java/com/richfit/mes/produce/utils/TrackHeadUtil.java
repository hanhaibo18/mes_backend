package com.richfit.mes.produce.utils;

import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.service.CodeRuleService;

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
     * @param trackHead       页面传递过来的跟单信息
     * @param codeRuleService 编码 Service
     * @Author: zhiqiang.lu
     * @Date: 2022.9.26
     */
    public static List<TrackHead> saveInfo(TrackHead trackHead, CodeRuleService codeRuleService) throws Exception {
        List<TrackHead> trackHeadList = new ArrayList<>();
        //用于区分是否单件、批量
        if (TrackHead.TRACKHEAD_BATCH_YES.equals(trackHead.getIsBatch())) {
            if (trackHead.getStoreList() == null) {
                //单件装配批量根据数量拆分跟单数量
                int number = trackHead.getNumber();
                for (int i = 0; i < number; i++) {
                    //流水号获取
                    trackNo(trackHead, codeRuleService);
                    trackHead.setNumber(1);
                    trackHeadList.add(trackHead);
                }
            } else {
                //单件机加批量跟单会带入生成编码的物料数据列表，产品编码等信息
                for (Map m : trackHead.getStoreList()) {
                    //流水号获取
                    trackNo(trackHead, codeRuleService);
                    trackHead.setProductNo((String) m.get("workblankNo"));
                    trackHead.setNumber((Integer) m.get("num"));
                    trackHeadList.add(trackHead);
                }
            }
        } else {
            //其他类型的跟单
            trackHeadList.add(trackHead);
        }
        return trackHeadList;
    }

    /**
     * 功能描述: 根据保存的信息进行跟单生产线的数据封装，根据是否单件批量进行数据重组
     *
     * @param trackHead 页面传递过来的跟单信息
     * @Author: zhiqiang.lu
     * @Date: 2022.9.26
     */
    public static List<TrackHead> flowInfo(TrackHead trackHead) throws Exception {
        List<TrackHead> trackHeadList = new ArrayList<>();
        if (TrackHead.TRACKHEAD_BATCH_NO.equals(trackHead.getIsBatch()) && !trackHead.getStoreList().isEmpty()) {
            //单件多数量（多生产线）
            for (Map m : trackHead.getStoreList()) {
                trackHead.setProductNo((String) m.get("workblankNo"));
                trackHead.setNumber((Integer) m.get("num"));
                trackHeadList.add(trackHead);
            }
        } else {
            //正常生产线
            trackHeadList.add(trackHead);
        }
        return trackHeadList;
    }

    public static void trackNo(TrackHead trackHead, CodeRuleService codeRuleService) throws Exception {
        String code = Code.value("track_no", SecurityUtils.getCurrentUser().getTenantId(), trackHead.getBranchCode(), codeRuleService);
        trackHead.setTrackNo(code);
    }
}
