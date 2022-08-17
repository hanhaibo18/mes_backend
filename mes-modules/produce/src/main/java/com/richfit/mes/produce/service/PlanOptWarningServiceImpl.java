package com.richfit.mes.produce.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.model.produce.Plan;
import com.richfit.mes.common.model.produce.PlanOptWarning;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.dao.PlanMapper;
import com.richfit.mes.produce.dao.PlanOptWarningMapper;
import com.richfit.mes.produce.dao.TrackItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/14 9:57
 */
@Slf4j
@Service
public class PlanOptWarningServiceImpl extends ServiceImpl<PlanOptWarningMapper, PlanOptWarning> implements PlanOptWarningService {

    @Autowired
    private PlanMapper planMapper;

    @Autowired
    private TrackHeadService trackHeadService;

    @Autowired
    private TrackItemMapper trackItemMapper;

    @Autowired
    private PlanOptWarningMapper planOptWarningMapper;

    /**
     * 功能描述: 通过计划id查询预警工序数据
     *
     * @param planId 计划id
     * @Author: zhiqiang.lu
     * @Date: 2022/8/8 15:06
     **/
    @Override
    public List<PlanOptWarning> queryList(String planId) throws Exception {
        return queryPlanOptWarningList(planId);
    }

    @Override
    public void warning(Plan plan) throws Exception {
        //查询计划预警的数据
        List<PlanOptWarning> planOptWarningList = queryPlanOptWarningList(plan.getId());
        if (planOptWarningList.size() < 1) {
            //不进行预警
            plan.setAlarmStatus(0);
        }
        long betweenDay = 101;
        for (PlanOptWarning planOptWarning : planOptWarningList) {
            if (!StringUtils.isNullOrEmpty(planOptWarning.getDateWarning())) {
                if (betweenDay > planOptWarning.getDays()) {
                    betweenDay = planOptWarning.getDays();
                }
                if (betweenDay >= 3) {
                    plan.setAlarmStatus(1);
                } else if (betweenDay >= 0 && betweenDay < 3) {
                    plan.setAlarmStatus(2);
                } else {
                    plan.setAlarmStatus(3);
                }
            }
        }
    }

    /**
     * 功能描述: 通过计划id查询工序预警列表
     *
     * @param planId 计划id
     * @Author: zhiqiang.lu
     * @Date: 2022/8/8 15:06
     **/
    public List<PlanOptWarning> queryPlanOptWarningList(String planId) throws Exception {
        List<TrackHead> trackHeadList = queryTrackHeadList(planId);
        if (trackHeadList.size() < 1) {
            return new ArrayList<PlanOptWarning>();
        }
        //通过跟单列表查询合并后的产品工序
        List<TrackItem> trackItemList = queryOptState(trackHeadList);

        //根据工序列表封装计划工序预警数据
        List<PlanOptWarning> planOptWarningList = new ArrayList<>();
        for (TrackItem trackItem : trackItemList) {
            PlanOptWarning planOptWarning = new PlanOptWarning();
            planOptWarning.setPlanId(planId);
            planOptWarning.setOptNo(trackItem.getOptNo());
            planOptWarning.setOptName(trackItem.getOptName());
            planOptWarning.setSequenceOrderBy(trackItem.getSequenceOrderBy());
            planOptWarning.setIsOperationComplete(trackItem.getIsOperationComplete());
            planOptWarning.setOperationCompleteTime(trackItem.getOperationCompleteTime());
            planOptWarning.setAssignableQty(trackItem.getAssignableQty());
            planOptWarning.setCompleteQty(trackItem.getCompleteQty());
            planOptWarningList.add(planOptWarning);
        }

        //查询计划预警的数据
        QueryWrapper<PlanOptWarning> queryWrapperPlanOptWarning = new QueryWrapper<>();
        queryWrapperPlanOptWarning.eq("plan_id", planId);
        queryWrapperPlanOptWarning.orderByAsc("sequence_order_by");
        List<PlanOptWarning> planOptWarnings = planOptWarningMapper.selectList(queryWrapperPlanOptWarning);

        //计划预警数据匹配
        Date date = new Date();
        for (PlanOptWarning planOptWarning : planOptWarningList) {
            for (PlanOptWarning pow : planOptWarnings) {
                if (planOptWarning.getSequenceOrderBy() == pow.getSequenceOrderBy()) {
                    planOptWarning.setId(pow.getId());
                    planOptWarning.setDateWarning(pow.getDateWarning());
                    if (!StringUtils.isNullOrEmpty(planOptWarning.getDateWarning())) {
                        Date dateWarnning = DateUtil.parse(planOptWarning.getDateWarning());
                        long d = DateUtil.between(dateWarnning, date, DateUnit.DAY);
                        if (dateWarnning.getTime() < date.getTime()) {
                            planOptWarning.setDays(-d);
                        } else {
                            planOptWarning.setDays(d);
                        }
                    }
                }
            }
        }
        return planOptWarningList;
    }

    /**
     * 功能描述: 通过计划id查询跟单列表
     *
     * @param planId 计划id
     * @Author: zhiqiang.lu
     * @Date: 2022/8/8 15:06
     **/
    public List<TrackHead> queryTrackHeadList(String planId) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("workPlanId", planId);
        return trackHeadService.selectTrackFlowList(map);
    }

    /**
     * 功能描述: 通过跟单列表返回最后一个工序的完工状态
     *
     * @param trackHeadList 跟单列表
     * @Author: zhiqiang.lu
     * @Date: 2022/8/8 15:06
     **/
    public List<TrackItem> queryOptState(List<TrackHead> trackHeadList) throws Exception {
        List<TrackItem> trackItemList = new ArrayList<>();
        for (TrackHead trackHead : trackHeadList) {
            //查询产品工序
            QueryWrapper<TrackItem> queryWrapperTrackItem = new QueryWrapper<>();
            queryWrapperTrackItem.eq("track_head_id", trackHead.getId());
            queryWrapperTrackItem.orderByAsc("sequence_order_by");
            List<TrackItem> trackItems = trackItemMapper.selectList(queryWrapperTrackItem);
            if (trackItemList.size() < trackItems.size()) {
                trackItemList.addAll(trackItems);
            } else {
                //通过最后完成时间删除重复的工序
                for (int i = 0; i < trackItems.size(); i++) {
                    if (trackItems.get(i).getOperationCompleteTime() != null) {
                        if (trackItemList.get(i).getOperationCompleteTime() == null) {
                            trackItemList.set(i, trackItems.get(i));
                        } else {
                            if (trackItemList.get(i).getOperationCompleteTime().getTime() < trackItems.get(i).getOperationCompleteTime().getTime()) {
                                trackItemList.set(i, trackItems.get(i));
                            }
                        }
                    }
                }
            }
        }
        return trackItemList;
    }
}
