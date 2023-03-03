package com.richfit.mes.produce.service;

import cn.hutool.core.date.DateUnit;
import cn.hutool.core.date.DateUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.model.base.Operatipon;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.produce.dao.HotDemandMapper;
import com.richfit.mes.produce.dao.PlanOptWarningMapper;
import com.richfit.mes.produce.dao.TrackItemMapper;
import com.richfit.mes.produce.provider.BaseServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/14 9:57
 */
@Slf4j
@Service
public class PlanOptWarningServiceImpl extends ServiceImpl<PlanOptWarningMapper, PlanOptWarning> implements PlanOptWarningService {


    @Autowired
    private TrackHeadService trackHeadService;

    @Autowired
    private TrackItemMapper trackItemMapper;

    @Autowired
    private PlanOptWarningMapper planOptWarningMapper;
    @Autowired
    private BaseServiceClient baseServiceClient;
    @Autowired
    private HotPlanNodeService hotPlanNodeService;
    @Resource
    HotDemandMapper hotDemandMapper;

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
    /**
     * 功能描述: 热工通过计划id查询预警工序数据
     * @param planId 计划id
     * @Author: hujia
     **/
    @Override
    public List<HotPlanNode> queryListHot(String planId) throws Exception {
        return queryPlanOptWarningListHot(planId);
    }

    @Override
    public void warning(Plan plan) throws Exception {
        QueryWrapper<PlanOptWarning> queryWrapperPlanOptWarning = new QueryWrapper<>();
        queryWrapperPlanOptWarning.eq("plan_id", plan.getId());
        List<PlanOptWarning> planOptWarnings = planOptWarningMapper.selectList(queryWrapperPlanOptWarning);
        if (planOptWarnings == null || planOptWarnings.size() == 0) {
            //不进行预警
            plan.setAlarmStatus(-1);
            return;
        }
        //查询计划预警的数据
        List<PlanOptWarning> planOptWarningList = this.queryPlanOptWarningList(plan.getId());
        if (planOptWarningList.size() < 1) {
            //不进行预警
            plan.setAlarmStatus(0);
            return;
        }
        long betweenDay = 101;
        for (PlanOptWarning planOptWarning : planOptWarningList) {
            if (!StringUtils.isNullOrEmpty(planOptWarning.getDateWarning())) {
                if (betweenDay > planOptWarning.getDays()) {
                    betweenDay = planOptWarning.getDays();
                }
                if (betweenDay >= 3) {
                    plan.setAlarmStatus(1);
                    return;
                } else if (betweenDay >= 0 && betweenDay < 3) {
                    plan.setAlarmStatus(2);
                    return;
                } else {
                    plan.setAlarmStatus(3);
                    return;
                }
            }
        }
    }

    /**
     * 功能描述: 通过计划id查询计划节点数据
     *
     * @param plan 计划数据
     * @Author: hujia
     **/
    @Override
    public void warningHot(Plan plan) throws Exception {
        QueryWrapper<PlanOptWarning> queryWrapperPlanOptWarning = new QueryWrapper<>();
        queryWrapperPlanOptWarning.eq("plan_id", plan.getId());
        //查询关键工序计划的数据
        List<HotPlanNode> byPlanNodeByPlanId = hotPlanNodeService.getByPlanNodeByPlanId(plan.getId());
        if (byPlanNodeByPlanId == null || byPlanNodeByPlanId.size() == 0) {
            //不进行预警
            plan.setAlarmStatus(0);
            return;
        }
        //查询计划预警的数据
        List<HotPlanNode> hotPlanNodes = this.queryPlanOptWarningListHot(plan.getId());
        if (hotPlanNodes.size() < 1) {
            //不进行预警
            plan.setAlarmStatus(0);
            return;
        }
        long betweenDay = 101;
        for (HotPlanNode planNode : hotPlanNodes) {
            if (!ObjectUtils.isEmpty(planNode.getFinishTime())) {
                if (betweenDay > planNode.getDays()) {
                    betweenDay = planNode.getDays();
                }
                if (betweenDay >= 3) {
                    plan.setAlarmStatus(1);
                    return;
                } else if (betweenDay >= 0 && betweenDay < 3) {
                    plan.setAlarmStatus(2);
                    return;
                } else {
                    plan.setAlarmStatus(3);
                    return;
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
        List<TrackItem> trackItemList = this.queryOptState(trackHeadList);

        //根据工序列表封装计划工序预警数据
        List<PlanOptWarning> planOptWarningList = new ArrayList<>();
        for (TrackItem trackItem : trackItemList) {
            PlanOptWarning planOptWarning = new PlanOptWarning();
            planOptWarning.setPlanId(planId);
            planOptWarning.setTrackNo(trackItem.getTrackNo());
            planOptWarning.setOptNo(trackItem.getOptNo());
            planOptWarning.setOptName(trackItem.getOptName());
            planOptWarning.setSequenceOrderBy(trackItem.getSequenceOrderBy());
            planOptWarning.setIsOperationComplete(trackItem.getIsOperationComplete());
            planOptWarning.setOperationCompleteTime(trackItem.getOperationCompleteTime());
            planOptWarning.setAssignableQty(trackItem.getNumber());
            planOptWarning.setCompleteQty(trackItem.getCompleteQty());
            planOptWarning.setProductNo(trackItem.getProductNo());
            planOptWarning.setTrackItemId(trackItem.getId());
            List<Operatipon> data = baseServiceClient.find(trackItem.getOperatiponId(), null, null, null, null, null).getData();
            if (data.size() > 0) {
                planOptWarning.setIsKey(data.get(0).getIsKey());
            }
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
                //工序顺序一样并且工序id一样
                if (planOptWarning.getSequenceOrderBy().equals(pow.getSequenceOrderBy()) && planOptWarning.getTrackItemId().equals(pow.getTrackItemId())) {
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
     * 功能描述: 热工通过计划id查询工序预警列表
     *
     * @param planId 计划id
     * @Author: hujia
     **/
    public List<HotPlanNode> queryPlanOptWarningListHot(String planId) throws Exception {
        List<TrackHead> trackHeadList = queryTrackHeadList(planId);
        if (trackHeadList.size() < 1) {
            return new ArrayList<HotPlanNode>();
        }
        //通过跟单列表查询合并后的产品工序
        List<TrackItem> trackItemList = this.queryOptState(trackHeadList);

        QueryWrapper<HotDemand> queryWrapper=new QueryWrapper<>();
        queryWrapper.in("plan_id",planId);
        HotDemand hotDemand = hotDemandMapper.selectOne(queryWrapper);

        //根据工序列表封装计划工序预警数据
        List<HotPlanNode> planNodeList = new ArrayList<>();
        for (TrackItem trackItem : trackItemList) {
            HotPlanNode planNode = new HotPlanNode();
            planNode.setOpNo(trackItem.getSequenceOrderBy()+"");//工序序号
            planNode.setOptName(trackItem.getOptName());//工序名称
            planNode.setTrackItemId(trackItem.getId());
            planNode.setDemandId(hotDemand.getId());//毛坯需求id
            planNode.setDemandNum(trackItem.getNumber());//需求数量
            planNode.setOptStatus(trackItem.getIsDoing()+"");//工序状态 0:未开始,1: 进行中,2:已结束
            planNode.setBranchCode(trackItem.getBranchCode());//车间码
            planNode.setTenantId(trackItem.getTenantId());//租户id
            planNode.setOptId(trackItem.getOperatiponId());//工序字典id
            planNode.setSequenceId(trackItem.getOptId());//工序id

//            List<Operatipon> data = baseServiceClient.find(trackItem.getOperatiponId(), null, null, null, null, null).getData();
//            if (data.size() > 0) {
//                planOptWarning.setIsKey(data.get(0).getIsKey());
//            }
            planNodeList.add(planNode);
        }

        //查询关键计划节点数据
        List<HotPlanNode> byPlanNodeByPlanId = hotPlanNodeService.getByPlanNodeByPlanId(planId);

        //计划预警数据匹配
        Date date = new Date();
        for (HotPlanNode planNode : planNodeList) {
            for (HotPlanNode pn : byPlanNodeByPlanId) {
                //工序顺序一样并且工序id一样
                if (planNode.getOpNo().equals(pn.getOpNo()) && planNode.getTrackItemId().equals(pn.getTrackItemId())) {
                    planNode.setId(pn.getId());
                    planNode.setFinishTime(pn.getFinishTime());
                    if (!ObjectUtils.isEmpty(planNode.getFinishTime())) {
                        Date dateWarnning =planNode.getFinishTime();
                        long d = DateUtil.between(dateWarnning, date, DateUnit.DAY);
                        if (dateWarnning.getTime() < date.getTime()) {
                            planNode.setDays(-d);
                        } else {
                            planNode.setDays(d);
                        }
                    }
                }
            }
        }
        return planNodeList;
    }

    /**
     * 功能描述: 通过计划id查询跟单列表
     *
     * @param planId 计划id
     * @Author: zhiqiang.lu
     * @Date: 2022/8/8 15:06
     **/
    public List<TrackHead> queryTrackHeadList(String planId) throws Exception {
        QueryWrapper<TrackHead> queryWrapperTrackHead = new QueryWrapper<>();
        queryWrapperTrackHead.eq("work_plan_id", planId);
        return trackHeadService.list(queryWrapperTrackHead);
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
            queryWrapperTrackItem.orderByAsc("track_head_id");
            queryWrapperTrackItem.orderByAsc("product_no");
            queryWrapperTrackItem.orderByAsc("sequence_order_by");
            List<TrackItem> trackItems = trackItemMapper.selectList(queryWrapperTrackItem);
            for (TrackItem trackItem : trackItems) {
                trackItem.setTrackNo(trackHead.getTrackNo());
            }
            trackItemList.addAll(trackItems);
        }
        return trackItemList;
    }
}
