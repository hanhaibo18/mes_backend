package com.richfit.mes.produce.utils;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.produce.TrackComplete;
import com.richfit.mes.common.model.sys.QualityInspectionRules;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.model.util.TimeUtil;
import com.richfit.mes.common.security.constant.SecurityConstants;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.TrackCompleteMapper;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import lombok.Data;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * @author zhiqiang.lu
 */
@Data
public class WorkHoursUtil {
    public List<QualityInspectionRules> rulesList = new ArrayList<>();
    public Map<String, TenantUserVo> stringTenantUserVoMap = new HashMap<>();
    public Map<String, Branch> branchMap = new HashMap<>();
    public final CountDownLatch cdl = new CountDownLatch(2);

    public void workHoursThread(SystemServiceClient systemServiceClient, List<TrackComplete> completes) throws InterruptedException {
        //1、通过报工的数据进行数据采集
        Map<String, List<TrackComplete>> completesMap = completes.stream().filter(complete -> StrUtil.isNotBlank(complete.getUserId())).collect(Collectors.groupingBy(TrackComplete::getUserId));

        //2、手动多线程方式查询数据
        new Thread(() -> {
            //1、查询当前车间下所有质检规则
            rulesList = systemServiceClient.allQualityInspectionRulesListInner(SecurityConstants.FROM_INNER);
            cdl.countDown();
        }).start();
        new Thread(() -> {
            //2、人员信息
            ArrayList<String> userIdList = new ArrayList<>(completesMap.keySet());
            stringTenantUserVoMap = systemServiceClient.queryByUserAccountListInner(userIdList, SecurityConstants.FROM_INNER);
            cdl.countDown();
        }).start();

        //3、等待线程计数器归0
        cdl.await();
    }


    public Map<String, Object> workHoursCompletes(BaseServiceClient baseServiceClient, List<TrackComplete> completes, String type) {
        Map<String, QualityInspectionRules> rulesMap = rulesList.stream().collect(Collectors.toMap(BaseEntity::getId, x -> x));

        List<TenantUserVo> tenantUserVoList = new ArrayList<>();
        stringTenantUserVoMap.forEach((key, value) -> tenantUserVoList.add(value));

        //班组人员map
        Map<String, List<TenantUserVo>> belongOrgIdMap = tenantUserVoList.stream().filter(tenantUserVo -> StrUtil.isNotBlank(tenantUserVo.getBelongOrgId()))
                .collect(Collectors.groupingBy(TenantUserVo::getBelongOrgId));

        //1、根据type做数据的整合返回可通用循环执行数据
        List<String> idList = getKeyByType(baseServiceClient, completes, type);
        Map<String, List<TrackComplete>> completeMap = getCompleteMapByType(completes, belongOrgIdMap, type);

        if (CollectionUtils.isNotEmpty(idList)) {
            //2、返回封装后的数据
            return buildComplete(idList, completeMap, stringTenantUserVoMap, rulesMap, type);
        } else {
            return null;
        }
    }

    private Map<String, Object> buildComplete(List<String> idList, Map<String, List<TrackComplete>> completeMap, Map<String, TenantUserVo> stringTenantUserVoMap,
                                              Map<String, QualityInspectionRules> rulesMap, String type) {
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
                if (CollectionUtils.isNotEmpty(branchMap)) {
                    trackComplete.setBranchName(branchMap.get(tenantUserVo.getBelongOrgId()).getBranchName());
                }
                //加入校验 需要质检未质检 不记录 需要调度审核 未审核 不计入
                //需要质检,质检未完成 不计入审核
                boolean quality = trackComplete.getIsExistQualityCheck() == 1 && trackComplete.getIsQualityComplete() == 0;
                //需要调度审核,调度未完成不计入审核
                boolean schedule = trackComplete.getIsExistScheduleCheck() == 1 && trackComplete.getIsScheduleComplete() == 0;
                if (quality || schedule) {
                    continue;
                }
                //空校验
                if (trackComplete.getPrepareEndHours() == null) {
                    trackComplete.setPrepareEndHours(0.00);
                    trackComplete.setPrepareEndHours(0.00);
                } else {
                    trackComplete.setPrepareEndHours(trackComplete.getPrepareEndHours());
                }
                if (trackComplete.getReportHours() == null) {
                    trackComplete.setReportHours(0.00);
                }
                //额定工时
                if (trackComplete.getSinglePieceHours() == null) {
                    trackComplete.setSinglePieceHours(0.00);
                } else {
                    trackComplete.setSinglePieceHours(trackComplete.getSinglePieceHours());
                }
                if (trackComplete.getCompletedQty() == null) {
                    trackComplete.setCompletedQty(0.00);
                }
                //数量
                BigDecimal number = BigDecimal.valueOf(trackComplete.getCompletedQty());
                //额定工时
                BigDecimal reportHours = BigDecimal.valueOf(trackComplete.getReportHours());
                //准结工时
                BigDecimal prepareEndHours = BigDecimal.valueOf(trackComplete.getPrepareEndHours());
                //实际额定工时
                BigDecimal realityReportHours = BigDecimal.valueOf(trackComplete.getReportHours());
                if (0 == trackComplete.getCompletePersonQty()) {
                    trackComplete.setCompletePersonQty(1);
                }
                //实际准结工时
                BigDecimal realityPrepareEndHours = BigDecimal.valueOf(trackComplete.getPrepareEndHours() / trackComplete.getCompletePersonQty());

                sumNumber = sumNumber.add(number);
                //累计准结工时
                sumPrepareEndHours = sumPrepareEndHours.add(prepareEndHours);
                //累计额定工时
                sumReportHours = sumReportHours.add(reportHours);
                //已质检 校验不合格是否给工时(单件工时/额定工时)
                if (trackComplete.getIsExistQualityCheck() == 1) {
                    if (StrUtil.isNotBlank(trackComplete.getRuleId())) {
                        QualityInspectionRules rules = rulesMap.get(trackComplete.getRuleId());
                        if (rules != null) {
                            if (rules.getIsGiveTime() == 1) {
                                //累计实际额定工时
                                sumRealityReportHours = sumRealityReportHours.add(realityReportHours);
                            } else {
                                realityReportHours = new BigDecimal(0);
                            }
                            trackComplete.setQualityResult(rules.getStateName());
                        } else {
                            realityPrepareEndHours = new BigDecimal(0);
                            sumRealityReportHours = sumRealityReportHours.add(realityReportHours);
                            trackComplete.setQualityResult("没有质检内容");
                        }
                    } else {
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
                if (trackComplete.getIsScheduleComplete() == 1 && trackComplete.getIsPrepare() != 1) {
                    realityPrepareEndHours = new BigDecimal(0);
                }
                sumRealityPrepareEndHours = sumRealityPrepareEndHours.add(realityPrepareEndHours);
                //总工时
                BigDecimal totalHours = number.multiply(realityReportHours).add(realityPrepareEndHours);
                trackComplete.setTotalHours(totalHours.setScale(4, RoundingMode.HALF_UP).doubleValue());
                sumTotalHours = sumTotalHours.add(totalHours);
                buildDetails(trackComplete, tenantUserVo, realityReportHours, realityPrepareEndHours, id);
                details.add(trackComplete);
                temp = trackComplete;
            }
            TrackComplete track0 = new TrackComplete(completeMap, id, trackCompleteShowList, sumNumber, sumTotalHours, sumPrepareEndHours, sumReportHours, sumRealityPrepareEndHours, sumRealityReportHours, temp, type);
            summary.add(track0);
        }
        Map<String, Object> stringObjectHashMap = new HashMap<>(2);
        Collections.sort(details);
        stringObjectHashMap.put("details", details);
        stringObjectHashMap.put("summary", summary);
        return stringObjectHashMap;
    }


    private void buildDetails(TrackComplete trackComplete, TenantUserVo tenantUserVo, BigDecimal realityReportHours, BigDecimal realityPrepareEndHours, String id) {
        trackComplete.setUserName(tenantUserVo.getEmplName());
        trackComplete.setRealityReportHours(realityReportHours.setScale(4, RoundingMode.HALF_UP).doubleValue());
        trackComplete.setRealityPrepareEndHours(realityPrepareEndHours.setScale(4, RoundingMode.HALF_UP).doubleValue());
        trackComplete.setParentId(id);
        trackComplete.setCompleteTimeStr(DateUtil.format(trackComplete.getCompleteTime(), "yyyy-MM-dd HH:mm:ss"));
    }

    private Map<String, List<TrackComplete>> buildUserResultMap(List<TrackComplete> completes, Map<String, List<TenantUserVo>> belongOrgIdMap) {
        Map<String, List<TrackComplete>> completeMapByBranch = new HashMap<>();
        Set<String> branchCodeSet = belongOrgIdMap.keySet();
        for (String branchCode : branchCodeSet) {
            //先找到该班组下的人员
            Set<String> userIdSet = belongOrgIdMap.get(branchCode).stream().map(TenantUserVo::getUserAccount).collect(Collectors.toSet());
            List<TrackComplete> completeList = completes.stream().filter(x -> userIdSet.contains(x.getCompleteBy())).collect(Collectors.toList());
            completeMapByBranch.put(branchCode, completeList);
        }
        return completeMapByBranch;
    }

    private List<String> getBranchInfoByUserInfo(BaseServiceClient baseServiceClient, Map<String, TenantUserVo> stringTenantUserVoMap) {
        List<TenantUserVo> tenantUserVoList = new ArrayList<>();
        stringTenantUserVoMap.forEach((key, value) -> tenantUserVoList.add(value));
        Map<String, List<TenantUserVo>> belongOrgIdMap = tenantUserVoList.stream().filter(tenantUserVo -> StrUtil.isNotBlank(tenantUserVo.getBelongOrgId()))
                .collect(Collectors.groupingBy(TenantUserVo::getBelongOrgId));
        branchMap = baseServiceClient.getBranchInfoMapByBranchCodeList(new ArrayList<>(belongOrgIdMap.keySet()));
        return new ArrayList<>(branchMap.keySet());
    }

    private List<String> getKeyByType(BaseServiceClient baseServiceClient, List<TrackComplete> completes,
                                      String type) {
        switch (type) {
            case "person":
                return completes.stream().map(trackComplete -> StrUtil.isEmpty(trackComplete.getUserId()) ? "/" : trackComplete.getCompleteBy()).distinct().collect(Collectors.toList());
            case "workNo":
                return completes.stream().map(trackComplete -> StrUtil.isEmpty(trackComplete.getWorkNo()) ? "/" : trackComplete.getWorkNo()).distinct().collect(Collectors.toList());
            case "order":
                return completes.stream().map(trackComplete -> StrUtil.isEmpty(trackComplete.getProductionOrder()) ? "/" : trackComplete.getProductionOrder()).distinct().collect(Collectors.toList());
            case "branch":
                return getBranchInfoByUserInfo(baseServiceClient, stringTenantUserVoMap);
            default:
                return null;
        }
    }

    private Map<String, List<TrackComplete>> getCompleteMapByType(List<TrackComplete> completes,
                                                                  Map<String, List<TenantUserVo>> belongOrgIdMap, String type) {
        switch (type) {
            case "person":
                return completes.stream().collect(Collectors.groupingBy(trackComplete -> StrUtil.isEmpty(trackComplete.getUserId()) ? "/" : trackComplete.getCompleteBy()));
            case "workNo":
                return completes.stream().collect(Collectors.groupingBy(trackComplete -> StrUtil.isEmpty(trackComplete.getWorkNo()) ? "/" : trackComplete.getWorkNo()));
            case "order":
                return completes.stream().collect(Collectors.groupingBy(trackComplete -> StrUtil.isEmpty(trackComplete.getProductionOrder()) ? "/" : trackComplete.getProductionOrder()));
            case "branch":
                return buildUserResultMap(completes, belongOrgIdMap);
            default:
                return null;
        }
    }

    public List<TrackComplete> getCompleteByFilter(SystemServiceClient systemServiceClient, TrackCompleteMapper trackCompleteMapper, String trackNo, String startTime, String endTime, String branchCode, String workNo, String userId, String orderNo) {
        QueryWrapper<TrackComplete> queryWrapper = new QueryWrapper<>();
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
        List<Role> roleList = systemServiceClient.queryRolesByUserId(Objects.requireNonNull(SecurityUtils.getCurrentUser()).getUserId());
        List<String> roleCodeList = roleList.stream().map(Role::getRoleCode).collect(Collectors.toList());
        //查询权限控制
        if (roleCodeList.toString().contains("_LDGL") || roleCodeList.toString().contains("_TJ") || roleCodeList.contains("role_tenant_admin")) {
            if (!StringUtils.isNullOrEmpty(branchCode)) {
                queryWrapper.eq("branch_code", branchCode);
            }
            queryWrapper.eq(StrUtil.isNotBlank(userId), "user_id", userId);
        } else {
            queryWrapper.eq("user_id", SecurityUtils.getCurrentUser().getUsername());
        }
        return trackCompleteMapper.queryList(queryWrapper);
    }
}
