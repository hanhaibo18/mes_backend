package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.PlanOptWarning;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.dao.PlanMapper;
import com.richfit.mes.produce.dao.PlanOptWarningMapper;
import com.richfit.mes.produce.dao.TrackItemMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        Map<String, String> map = new HashMap<>();
        map.put("workPlanId", planId);
        //查询跟单产品分流视图
        List<TrackHead> trackHeadList = trackHeadService.selectTrackFlowList(map);
        if (trackHeadList.size() < 1) {
            throw new Exception("当前计划没有匹配跟单，不能进行工序的预警查看！");
        }
        //查询产品工序
        QueryWrapper<TrackItem> queryWrapperTrackItem = new QueryWrapper<>();
        queryWrapperTrackItem.eq("flow_id", trackHeadList.get(0).getFlowId());
        queryWrapperTrackItem.orderByAsc("sequence_order_by");
        List<TrackItem> trackItemList = trackItemMapper.selectList(queryWrapperTrackItem);

        //根据工序列表封装计划工序预警数据
        List<PlanOptWarning> planOptWarningList = new ArrayList<>();
        for (TrackItem trackItem : trackItemList) {
            PlanOptWarning planOptWarning = new PlanOptWarning();
            planOptWarning.setPlanId(planId);
            planOptWarning.setOptNo(trackItem.getOptNo());
            planOptWarning.setOptName(trackItem.getOptName());
            planOptWarning.setSequenceOrderBy(trackItem.getSequenceOrderBy());
            planOptWarningList.add(planOptWarning);
        }

        //查询计划预警的数据
        QueryWrapper<PlanOptWarning> queryWrapperPlanOptWarning = new QueryWrapper<>();
        queryWrapperPlanOptWarning.eq("plan_id", planId);
        queryWrapperPlanOptWarning.orderByAsc("sequence_order_by");
        List<PlanOptWarning> planOptWarnings = planOptWarningMapper.selectList(queryWrapperPlanOptWarning);

        //计划预警数据匹配
        for (PlanOptWarning planOptWarning : planOptWarningList) {
            for (PlanOptWarning pow : planOptWarnings) {
                if (planOptWarning.getSequenceOrderBy() == pow.getSequenceOrderBy()) {
                    planOptWarning.setId(pow.getId());
                    planOptWarning.setDateWarning(pow.getDateWarning());
                }
            }
        }
        return planOptWarningList;
    }
}
