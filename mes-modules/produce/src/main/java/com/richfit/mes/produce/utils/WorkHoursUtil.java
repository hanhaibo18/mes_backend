package com.richfit.mes.produce.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.produce.TrackComplete;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.model.sys.QualityInspectionRules;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.model.util.TimeUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.TrackCompleteMapper;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

public class WorkHoursUtil {


    public Map<String, Object> workHoursCompletes(BaseServiceClient baseServiceClient, List<TrackComplete> completes, Future<List<QualityInspectionRules>> qualityInspectionRulesFuture, Future<Map<String, TenantUserVo>> userMapFuture, Future<List<TrackHead>> trackHeadListFuture, Future<List<TrackItem>> trackItemListFuture, String type) {
        //并行执行
        List<QualityInspectionRules> rulesList = ConcurrentUtil.futureGet(qualityInspectionRulesFuture);
        Map<String, TenantUserVo> stringTenantUserVoMap = ConcurrentUtil.futureGet(userMapFuture);
        List<TrackHead> trackHeads = ConcurrentUtil.futureGet(trackHeadListFuture);
        List<TrackItem> trackItems = ConcurrentUtil.futureGet(trackItemListFuture);
        Map<String, QualityInspectionRules> rulesMap = rulesList.stream().collect(Collectors.toMap(BaseEntity::getId, x -> x));
        Map<String, TrackHead> trackHeadMap = trackHeads.stream().collect(Collectors.toMap(BaseEntity::getId, x -> x));
        //过滤跟单工序只计算最终完成工序
        Map<String, TrackItem> trackItemMap = trackItems.stream().filter(item -> item.getIsOperationComplete() == 1).collect(Collectors.toMap(TrackItem::getId, x -> x, (k, v) -> k));
//        //只获取已完工数据计算工时
//        List<String> flowIdList = trackItems.stream().map(TrackItem::getFlowId).collect(Collectors.toList());
//        List<TrackFlow> trackFlows = trackFlowService.listByIds(flowIdList);
//        Map<String, TrackFlow> trackFlowMap = trackFlows.stream().collect(Collectors.toMap(BaseEntity::getId, x -> x, (k, v) -> k));
        //根据人员信息获取机构信息
        Map<String, Branch> branchMap = getBranchInfoByUserInfo(baseServiceClient, stringTenantUserVoMap);
        List<TrackHead> trackHeadList = new ArrayList<>();
        List<TenantUserVo> tenantUserVoList = new ArrayList<>();
        trackHeadMap.forEach((key, value) -> trackHeadList.add(value));
        stringTenantUserVoMap.forEach((key, value) -> tenantUserVoList.add(value));
        //跟单工作号map
        Map<String, List<TrackHead>> workNoMap = trackHeadList.stream().filter((trackHead -> StrUtil.isNotBlank(trackHead.getWorkNo())))
                .collect(Collectors.groupingBy(TrackHead::getWorkNo));
        //跟单订单map
        Map<String, List<TrackHead>> orderNoMap = trackHeadList.stream().filter(trackHead -> StrUtil.isNotBlank(trackHead.getProductionOrder()))
                .collect(Collectors.groupingBy(TrackHead::getProductionOrder));
        //班组人员map
        Map<String, List<TenantUserVo>> belongOrgIdMap = tenantUserVoList.stream().filter(tenantUserVo -> StrUtil.isNotBlank(tenantUserVo.getBelongOrgId()))
                .collect(Collectors.groupingBy(TenantUserVo::getBelongOrgId));

        //1、根据type做数据的整合返回可通用循环执行数据
        List<String> idList = getKeyByType(completes, stringTenantUserVoMap, trackHeadMap, trackItemMap, branchMap, workNoMap, orderNoMap, belongOrgIdMap, type);
        Map<String, List<TrackComplete>> completeMap = getCompleteMapByType(completes, stringTenantUserVoMap, trackHeadMap, trackItemMap, branchMap, workNoMap, orderNoMap, belongOrgIdMap, type);
        //2.封装数据
        Map<String, Object> resule = buildComplete(idList, completeMap, stringTenantUserVoMap, rulesMap, trackHeadMap, trackItemMap, branchMap, workNoMap, orderNoMap, belongOrgIdMap, type);
        //3、返回封装后的数据
        return resule;
    }

    private Map<String, Object> buildComplete(List<String> idList, Map<String, List<TrackComplete>> completeMap, Map<String, TenantUserVo> stringTenantUserVoMap,
                                              Map<String, QualityInspectionRules> rulesMap, Map<String, TrackHead> trackHeadMap,
                                              Map<String, TrackItem> trackItemMap, Map<String, Branch> branchMap, Map<String, List<TrackHead>> workNoMap,
                                              Map<String, List<TrackHead>> orderNoMap, Map<String, List<TenantUserVo>> belongOrgIdMap, String type) {
        List<TrackComplete> summary = new ArrayList<>();
        List<TrackComplete> details = new ArrayList<>();
        for (String id : idList) {
            //用来展示数据列表
            List<TrackComplete> trackCompleteShowList = new ArrayList<>();
            //总工报工数量
            BigDecimal sumNumber = new BigDecimal(0);
            //总工时累计额值
            BigDecimal sumTotalHours = new BigDecimal(0);
            //准结工时累计值
            BigDecimal sumPrepareEndHours = new BigDecimal(0);
            //额定工时累计值
            BigDecimal sumReportHours = new BigDecimal(0);
            //实际准结工时累计值
            BigDecimal sumRealityPrepareEndHours = new BigDecimal(0);
            //实际额定工时累计值
            BigDecimal sumRealityReportHours = new BigDecimal(0);
            TrackComplete temp = new TrackComplete();
            for (TrackComplete trackComplete : completeMap.get(id)) {
                //获取当前用户信息
                TenantUserVo tenantUserVo = stringTenantUserVoMap.get(trackComplete.getUserId());
                trackComplete.setBranchName(branchMap.get(tenantUserVo.getBelongOrgId()).getBranchName());
                //根据跟单工序id获取跟单工序
                TrackItem trackItem = trackItemMap.get(trackComplete.getTiId());
                if (null == trackItem) {
                    continue;
                }
                //加入校验 需要质检未质检 不记录 需要调度审核 未审核 不计入
                //需要质检,质检未完成 不计入审核
                boolean quality = trackItem.getIsExistQualityCheck() == 1 && trackItem.getIsQualityComplete() == 0;
                //需要调度审核,调度未完成不计入审核
                boolean schedule = trackItem.getIsExistScheduleCheck() == 1 && trackItem.getIsScheduleComplete() == 0;
                if (quality || schedule) {
                    continue;
                }
                //查询产品编号
                TrackHead trackHead = trackHeadMap.get(trackComplete.getTrackId());
                trackComplete.setProdNo(trackHead == null ? "" : trackHead.getProductNo());
                trackComplete.setProductName(trackHead == null ? "" : trackHead.getProductName());
                trackComplete.setDrawingNo(trackHead == null ? "" : trackHead.getDrawingNo());
                trackComplete.setMaterialName(trackHead == null ? "" : trackHead.getMaterialName());
                //空校验
                if (trackItem.getPrepareEndHours() == null) {
                    trackItem.setPrepareEndHours(0.00);
                    trackComplete.setPrepareEndHours(0.00);
                } else {
                    trackComplete.setPrepareEndHours(trackItem.getPrepareEndHours());
                }
                if (trackComplete.getReportHours() == null) {
                    trackComplete.setReportHours(0.00);
                }
                //额定工时
                if (trackItem.getSinglePieceHours() == null) {
                    trackComplete.setSinglePieceHours(0.00);
                } else {
                    trackComplete.setSinglePieceHours(trackItem.getSinglePieceHours());
                }
                if (trackComplete.getCompletedQty() == null) {
                    trackComplete.setCompletedQty(0.00);
                }
                //数量
                BigDecimal number = new BigDecimal(trackComplete.getCompletedQty());
                //额定工时
                BigDecimal reportHours = new BigDecimal(trackComplete.getReportHours());
                //准结工时
                BigDecimal prepareEndHours = new BigDecimal(trackComplete.getPrepareEndHours());
                //实际额定工时
                BigDecimal realityReportHours = new BigDecimal(trackComplete.getReportHours());
                if (0 == trackComplete.getCompletePersonQty()) {
                    trackComplete.setCompletePersonQty(1);
                }
                //实际准结工时
                BigDecimal realityPrepareEndHours = new BigDecimal(trackComplete.getPrepareEndHours() / trackComplete.getCompletePersonQty());

                sumNumber = sumNumber.add(number);
                //累计准结工时
                sumPrepareEndHours = sumPrepareEndHours.add(prepareEndHours);
                //累计额定工时
                sumReportHours = sumReportHours.add(reportHours);
                //已质检 校验不合格是否给工时(单件工时/额定工时)
                if (trackItem.getIsExistQualityCheck() == 1) {
                    if (StrUtil.isNotBlank(trackItem.getRuleId())) {
                        QualityInspectionRules rules = rulesMap.get(trackItem.getRuleId());
                        if (rules != null) {
                            if (rules.getIsGiveTime() == 1) {
                                //累计实际额定工时
                                sumRealityReportHours = sumRealityReportHours.add(realityReportHours);
                            } else {
                                realityReportHours = new BigDecimal(0);
                            }
                            trackComplete.setQualityResult(rules.getStateName());
                        } else {
//                                    realityReportHours = new BigDecimal(0);
                            realityPrepareEndHours = new BigDecimal(0);
                            sumRealityReportHours = sumRealityReportHours.add(realityReportHours);
                            trackComplete.setQualityResult("没有质检内容");
                        }
                    } else {
//                                realityReportHours = new BigDecimal(0);
                        realityPrepareEndHours = new BigDecimal(0);
                        sumRealityReportHours = sumRealityReportHours.add(realityReportHours);
                        trackComplete.setQualityResult("没有质检内容");
                    }
                } else {
                    //不质检也计算工时
                    //累计实际额定工时
                    sumRealityReportHours = sumRealityReportHours.add(realityReportHours);
                    trackComplete.setQualityResult("合格（非质检）");
                }
                //没有调度审核或者 调度已审核并且给予准结工时进入
                if (trackItem.getIsScheduleComplete() == null || trackItem.getIsScheduleComplete() == 0 || (trackItem.getIsScheduleComplete() == 1 && trackItem.getIsPrepare() != null && trackItem.getIsPrepare() == 1)) {
                    //累计实际准结工时
                } else {
                    realityPrepareEndHours = new BigDecimal(0);
                }
                sumRealityPrepareEndHours = sumRealityPrepareEndHours.add(realityPrepareEndHours);
                //总工时
                BigDecimal totalHours = number.multiply(realityReportHours).add(realityPrepareEndHours);
                trackComplete.setTotalHours(totalHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                sumTotalHours = sumTotalHours.add(totalHours);
                buildDetails(trackComplete, tenantUserVo, realityReportHours, realityPrepareEndHours, trackHeadMap, trackItem, id);
                details.add(trackComplete);
                temp = trackComplete;
            }
            TrackComplete track0 = new TrackComplete(completeMap, id, trackCompleteShowList, sumNumber, sumTotalHours, sumPrepareEndHours, sumReportHours, sumRealityPrepareEndHours, sumRealityReportHours, temp, type);
            summary.add(track0);
        }
        Map<String, Object> stringObjectHashMap = new HashMap<>();
        Collections.sort(details);
        stringObjectHashMap.put("details", details);
        stringObjectHashMap.put("summary", summary);
        return stringObjectHashMap;
    }


    private void buildDetails(TrackComplete trackComplete, TenantUserVo tenantUserVo, BigDecimal realityReportHours, BigDecimal realityPrepareEndHours, Map<String, TrackHead> trackHeadMap, TrackItem trackItem, String id) {
        trackComplete.setUserName(tenantUserVo.getEmplName());
        trackComplete.setRealityReportHours(realityReportHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
        trackComplete.setRealityPrepareEndHours(realityPrepareEndHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
        trackComplete.setWorkNo(trackHeadMap.get(trackComplete.getTrackId()) == null ? "" : trackHeadMap.get(trackComplete.getTrackId()).getWorkNo());
        trackComplete.setTrackNo(trackHeadMap.get(trackComplete.getTrackId()) == null ? "" : trackHeadMap.get(trackComplete.getTrackId()).getTrackNo());
        trackComplete.setOptSequence(trackItem.getOptSequence());
        trackComplete.setOptName(trackItem.getOptName());
        trackComplete.setProductionOrder(trackHeadMap.get(trackComplete.getTrackId()) == null ? "" : trackHeadMap.get(trackComplete.getTrackId()).getProductionOrder());
        trackComplete.setOptNo(trackItem.getOptNo());
        trackComplete.setParentId(id);
        trackComplete.setCompleteTimeStr(DateUtil.format(trackComplete.getCompleteTime(), "yyyy-MM-dd HH:mm:ss"));
    }

    private Map<String, List<TrackComplete>> getCompleteMapByType(List<TrackComplete> completes,
                                                                  Map<String, TenantUserVo> stringTenantUserVoMap,
                                                                  Map<String, TrackHead> trackHeadMap,
                                                                  Map<String, TrackItem> trackItemMap,
                                                                  Map<String, Branch> branchMap,
                                                                  Map<String, List<TrackHead>> workNoMap,
                                                                  Map<String, List<TrackHead>> orderNoMap,
                                                                  Map<String, List<TenantUserVo>> belongOrgIdMap, String type) {
        switch (type) {
            case "person":
                return completes.stream().collect(Collectors.groupingBy(TrackComplete::getCompleteBy));
            case "workNo":
                Map<String, List<TrackComplete>> completeMapByWorkNo = new HashMap<>();
                buildTrackHeadResultMap(workNoMap, completes, completeMapByWorkNo);
                return completeMapByWorkNo;
            case "order":
                Map<String, List<TrackComplete>> completeMapByOrderNo = new HashMap<>();
                buildTrackHeadResultMap(orderNoMap, completes, completeMapByOrderNo);
                return completeMapByOrderNo;
            case "branch":
                Map<String, List<TrackComplete>> completeMapByBranch = new HashMap<>();
                buildUserResultMap(completes, belongOrgIdMap, completeMapByBranch);
                return completeMapByBranch;
        }
        return null;
    }

    private void buildTrackHeadResultMap(Map<String, List<TrackHead>> trackHeadMap, List<TrackComplete> completes, Map<String, List<TrackComplete>> trackCompleteMap) {
        Set<String> keySet = trackHeadMap.keySet();
        for (String key : keySet) {
            //先找有哪些跟单
            Set<String> trackHeadIds = trackHeadMap.get(key).stream().map(TrackHead::getId).collect(Collectors.toSet());
            List<TrackComplete> completeList = completes.stream().filter(x -> trackHeadIds.contains(x.getTrackId())).collect(Collectors.toList());
            trackCompleteMap.put(key, completeList);
        }
    }

    private void buildUserResultMap(List<TrackComplete> completes, Map<String, List<TenantUserVo>> belongOrgIdMap, Map<String, List<TrackComplete>> completeMapByBranch) {
        Set<String> branchCodeSet = belongOrgIdMap.keySet();
        for (String branchCode : branchCodeSet) {
            //先找到该班组下的人员
            Set<String> userIdSet = belongOrgIdMap.get(branchCode).stream().map(TenantUserVo::getUserAccount).collect(Collectors.toSet());
            List<TrackComplete> completeList = completes.stream().filter(x -> userIdSet.contains(x.getCompleteBy())).collect(Collectors.toList());
            completeMapByBranch.put(branchCode, completeList);
        }
    }

    private Map<String, Branch> getBranchInfoByUserInfo(BaseServiceClient baseServiceClient, Map<String, TenantUserVo> stringTenantUserVoMap) {
        List<TenantUserVo> tenantUserVoList = new ArrayList<>();
        stringTenantUserVoMap.forEach((key, value) -> tenantUserVoList.add(value));
        Map<String, List<TenantUserVo>> belongOrgIdMap = tenantUserVoList.stream().filter(tenantUserVo -> StrUtil.isNotBlank(tenantUserVo.getBelongOrgId()))
                .collect(Collectors.groupingBy(TenantUserVo::getBelongOrgId));
        return baseServiceClient.getBranchInfoMapByBranchCodeList(new ArrayList<>(belongOrgIdMap.keySet()));
    }

    private List<String> getKeyByType(List<TrackComplete> completes, Map<String, TenantUserVo> stringTenantUserVoMap,
                                      Map<String, TrackHead> trackHeadMap, Map<String, TrackItem> trackItemMap,
                                      Map<String, Branch> branchMap, Map<String, List<TrackHead>> workNoMap,
                                      Map<String, List<TrackHead>> orderNoMap, Map<String, List<TenantUserVo>> belongOrgIdMap,
                                      String type) {
        switch (type) {
            case "person":
                return new ArrayList<>(completes.stream().map(TrackComplete::getUserId).collect(Collectors.toSet()));
            case "workNo":
                return new ArrayList<>(workNoMap.keySet());
            case "order":
                return new ArrayList<>(orderNoMap.keySet());
            case "branch":
                return new ArrayList<>(branchMap.keySet());
        }
        return null;
    }

    public List<TrackComplete> getCompleteByFilter(SystemServiceClient systemServiceClient, TrackCompleteMapper trackCompleteMapper, String trackNo, String startTime, String endTime, String branchCode, String workNo, String userId, String orderNo) {
        QueryWrapper<TrackComplete> queryWrapper = new QueryWrapper<TrackComplete>();
        if (!StringUtils.isNullOrEmpty(trackNo)) {
            trackNo = trackNo.replaceAll(" ", "");
            queryWrapper.apply("replace(replace(replace(track_no, char(13), ''), char(10), ''),' ', '') like '%" + trackNo + "%'");
        }
        if (!StringUtils.isNullOrEmpty(workNo)) {
            queryWrapper.eq("work_no", workNo);
        }
        if (!StringUtils.isNullOrEmpty(orderNo)) {
            queryWrapper.eq("production_order", orderNo);
        }
        //泵业新版使用工序完工时间
        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryWrapper.ge("final_complete_time", TimeUtil.startTime(startTime));
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryWrapper.le("final_complete_time", TimeUtil.endTime(endTime));
        }
        queryWrapper.eq("is_final_complete", "1");
        //获取当前登录用户角色列表
        List<Role> roleList = systemServiceClient.queryRolesByUserId(SecurityUtils.getCurrentUser().getUserId());
        List<String> roleCodeList = roleList.stream().map(x -> x.getRoleCode()).collect(Collectors.toList());
        //查询权限控制
        if (roleCodeList.toString().contains("_LDGL") || roleCodeList.toString().contains("_TJ") || roleCodeList.contains("role_tenant_admin")) {
            if (!StringUtils.isNullOrEmpty(branchCode)) {
                queryWrapper.eq("branch_code", branchCode);
            }
            queryWrapper.eq(StrUtil.isNotBlank(userId), "user_id", userId);
        } else {
            queryWrapper.eq("user_id", SecurityUtils.getCurrentUser().getUsername());
        }
        List<TrackComplete> completes = trackCompleteMapper.queryList(queryWrapper);
        return completes;
    }
}
