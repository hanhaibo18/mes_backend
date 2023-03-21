package com.richfit.mes.produce.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Device;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.sys.QualityInspectionRules;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.model.sys.Tenant;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.model.util.TimeUtil;
import com.richfit.mes.common.security.constant.SecurityConstants;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.TrackAssignMapper;
import com.richfit.mes.produce.dao.TrackAssignPersonMapper;
import com.richfit.mes.produce.dao.TrackCompleteMapper;
import com.richfit.mes.produce.enmus.IdEnum;
import com.richfit.mes.produce.enmus.PublicCodeEnum;
import com.richfit.mes.produce.entity.CompleteDto;
import com.richfit.mes.produce.entity.OutsourceCompleteDto;
import com.richfit.mes.produce.entity.OutsourceDto;
import com.richfit.mes.produce.entity.QueryWorkingTimeVo;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.utils.ConcurrentUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/**
 * @author sun
 * @Description 跟单服务
 */
@Service
public class TrackCompleteServiceImpl extends ServiceImpl<TrackCompleteMapper, TrackComplete> implements TrackCompleteService {

    @Autowired
    private TrackCompleteMapper trackCompleteMapper;

    @Autowired
    private TrackAssignService trackAssignService;
    @Autowired
    public PublicService publicService;
    @Autowired
    private TrackItemService trackItemService;
    @Autowired
    private TrackAssignMapper trackAssignMapper;
    @Autowired
    private TrackAssignPersonMapper trackAssignPersonMapper;
    @Autowired
    private TrackCompleteCacheService trackCompleteCacheService;
    @Autowired
    private TrackCompleteService trackCompleteService;
    @Autowired
    private TrackHeadService trackHeadService;
    @Autowired
    private TrackHeadFlowService trackFlowService;
    @Autowired
    private SystemServiceClient systemServiceClient;
    @Autowired
    private BaseServiceClient baseServiceClient;
    @Autowired
    public TrackCheckService trackCheckService;


    @Override
    public IPage<TrackComplete> queryPage(Page page, QueryWrapper<TrackComplete> query) {
        return trackCompleteMapper.queryPage(page, query);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> queryTrackCompleteList(String trackNo, String startTime, String endTime, String branchCode, String workNo, String userId, String orderNo) {
        List<TrackComplete> completes = getCompleteByFilter(trackNo, startTime, endTime, branchCode, workNo, userId, orderNo);
        List<TrackComplete> summary = new ArrayList<>();
        List<TrackComplete> details = new ArrayList<>();
        if (!CollectionUtils.isEmpty(completes)) {
            ExecutorService executorService = Executors.newFixedThreadPool(20);
            //查询当前车间下所有质检规则
            Future<List<QualityInspectionRules>> qualityInspectionRulesFuture = ConcurrentUtil.doJob(executorService, () -> systemServiceClient.queryQualityInspectionRulesListInner(completes.get(0).getBranchCode(), SecurityConstants.FROM_INNER));
            //根据员工分组
            Map<String, List<TrackComplete>> completesMap = completes.stream().filter(complete -> StrUtil.isNotBlank(complete.getUserId())).collect(Collectors.groupingBy(TrackComplete::getUserId));
            ArrayList<String> userIdList = new ArrayList<>(completesMap.keySet());
            Future<Map<String, TenantUserVo>> userMapFuture = ConcurrentUtil.doJob(executorService, () -> systemServiceClient.queryByUserAccountListInner(userIdList, SecurityConstants.FROM_INNER));
            //获取设备信息
//            Set<String> deviceIds = completes.stream().map(TrackComplete::getDeviceId).collect(Collectors.toSet());
//            List<Device> deviceByIdList = baseServiceClient.getDeviceByIdList(new ArrayList<>(deviceIds));
//            Map<String, Device> deviceMap = deviceByIdList.stream().collect(Collectors.toMap(BaseEntity::getId, x -> x));
            //根据跟单id获取跟单数据
            Set<String> trackIdList = completes.stream().map(TrackComplete::getTrackId).collect(Collectors.toSet());
            Future<List<TrackHead>> trackHeadListFuture = ConcurrentUtil.doJob(executorService, () -> trackHeadService.listByIds(new ArrayList<>(trackIdList)));
            //根据跟单工序id获取跟单工序
            Set<String> tiIdList = completes.stream().map(TrackComplete::getTiId).collect(Collectors.toSet());
            Future<List<TrackItem>> trackItemListFuture = ConcurrentUtil.doJob(executorService, () -> trackItemService.listByIds(new ArrayList<>(tiIdList)));
            //根据工序Id查询质检记录 2023/3/7 移至到完工查询，减少查询次数
            QueryWrapper<TrackCheck> queryWrapperCheck = new QueryWrapper<>();
            queryWrapperCheck.in("ti_id", new ArrayList<>(tiIdList));
            queryWrapperCheck.orderByDesc("modify_time");
            Future<List<TrackCheck>> trackCheckListFuture = ConcurrentUtil.doJob(executorService, () -> trackCheckService.list(queryWrapperCheck));
            //并行执行
            List<QualityInspectionRules> rulesList = ConcurrentUtil.futureGet(qualityInspectionRulesFuture);
            Map<String, TenantUserVo> stringTenantUserVoMap = ConcurrentUtil.futureGet(userMapFuture);
            List<TrackHead> trackHeads = ConcurrentUtil.futureGet(trackHeadListFuture);
            List<TrackItem> trackItems = ConcurrentUtil.futureGet(trackItemListFuture);
            List<TrackCheck> trackCheckList = ConcurrentUtil.futureGet(trackCheckListFuture);
            Map<String, QualityInspectionRules> rulesMap = rulesList.stream().collect(Collectors.toMap(BaseEntity::getId, x -> x));
            Map<String, TrackHead> trackHeadMap = trackHeads.stream().collect(Collectors.toMap(BaseEntity::getId, x -> x));
            //过滤跟单工序只计算最终完成工序
            Map<String, TrackItem> trackMap = trackItems.stream().filter(item -> item.getIsOperationComplete() == 1).collect(Collectors.toMap(TrackItem::getId, x -> x, (k, v) -> k));
            //只获取已完工数据计算工时
            List<String> flowIdList = trackItems.stream().map(TrackItem::getFlowId).collect(Collectors.toList());
            List<TrackFlow> trackFlows = trackFlowService.listByIds(flowIdList);
            Map<String, TrackFlow> trackFlowMap = trackFlows.stream().collect(Collectors.toMap(BaseEntity::getId, x -> x, (k, v) -> k));
            Map<String, List<TrackCheck>> trackChecksMap = trackCheckList.stream().filter(complete -> StrUtil.isNotBlank(complete.getTiId())).collect(Collectors.groupingBy(TrackCheck::getTiId));
            for (String id : userIdList) {
                List<TrackComplete> trackCompletes = completesMap.get(id);
                //用来展示数据列表
                List<TrackComplete> trackCompleteShowList = new ArrayList<>();
                TenantUserVo tenantUserVo = stringTenantUserVoMap.get(id);
                //统计每个员工
                if (!CollectionUtils.isEmpty(trackCompletes) && tenantUserVo != null) {
                    //总工时累计额值
                    BigDecimal sumTotalHours = new BigDecimal(0);
                    //准结工时累计值
                    BigDecimal sumPrepareEndHours = new BigDecimal(0);
                    //报告工时累计值
                    BigDecimal sumReportHours = new BigDecimal(0);
                    //实际准结工时累计值
                    BigDecimal sumRealityPrepareEndHours = new BigDecimal(0);
                    //实际报告工时累计值
                    BigDecimal sumRealityReportHours = new BigDecimal(0);
                    TrackComplete track0 = new TrackComplete();
                    //for循环计算时间
                    for (TrackComplete track : trackCompletes) {
                        //根据跟单工序id获取跟单工序
                        TrackItem trackItem = trackMap.get(track.getTiId());
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
                        TrackFlow trackFlow = trackFlowMap.get(trackItem.getFlowId());
                        TrackHead trackHead = trackHeadMap.get(track.getTrackId());
                        track.setProdNo(trackFlow == null ? "" : trackFlow.getProductNo());
                        track.setProductName(trackHead == null ? "" : trackHead.getProductName());
                        track.setDrawingNo(trackHead == null ? "" : trackHead.getDrawingNo());
                        //空校验
                        if (trackItem.getPrepareEndHours() == null) {
                            trackItem.setPrepareEndHours(0.00);
                            track.setPrepareEndHours(0.00);
                        } else {
                            track.setPrepareEndHours(trackItem.getPrepareEndHours());
                        }
                        if (track.getReportHours() == null) {
                            track.setReportHours(0.00);
                        }
                        //额定工时
                        if (trackItem.getSinglePieceHours() == null) {
                            track.setSinglePieceHours(0.00);
                        } else {
                            track.setSinglePieceHours(trackItem.getSinglePieceHours());
                        }
                        //报工数量
                        if (track.getCompletedQty() == null) {
                            track.setCompletedQty(0.00);
                        }
                        //数量
                        BigDecimal number = new BigDecimal(track.getCompletedQty());
                        //报告工时
                        BigDecimal reportHours = new BigDecimal(track.getReportHours());
                        //准结工时
                        BigDecimal prepareEndHours = new BigDecimal(track.getPrepareEndHours());
                        //实际报告工时
                        BigDecimal realityReportHours = new BigDecimal(track.getReportHours());
                        //实际准结工时
                        BigDecimal realityPrepareEndHours = new BigDecimal(track.getPrepareEndHours());
                        //累计准结工时
                        sumPrepareEndHours = sumPrepareEndHours.add(prepareEndHours);
                        //累计额定工时
                        sumReportHours = sumReportHours.add(reportHours);
                        //没有调度审核或者 调度已审核并且给予准结工时进入
                        if (trackItem.getIsScheduleComplete() == null || trackItem.getIsScheduleComplete() == 0 || (trackItem.getIsScheduleComplete() == 1 && trackItem.getIsPrepare() != null && trackItem.getIsPrepare() == 1)) {
                            //累计实际准结工时
                            sumRealityPrepareEndHours = sumRealityPrepareEndHours.add(realityPrepareEndHours);
                        } else {
                            realityPrepareEndHours = new BigDecimal(0);
                        }
                        //已质检 校验不合格是否给工时(单件工时/额定工时)
                        if (trackItem.getIsQualityComplete() == 1) {
                            List<TrackCheck> trackChecks = trackChecksMap.get(trackItem.getId());
                            if (trackChecks != null && trackChecks.size() > 0) {
                                QualityInspectionRules rules = rulesMap.get(trackChecks.get(0).getResult());
                                if (rules != null) {
                                    if (rules.getIsGiveTime() == 1) {
                                        //累计实际额定工时
                                        sumRealityReportHours = sumRealityReportHours.add(realityReportHours);
                                    } else {
                                        realityReportHours = new BigDecimal(0);
                                    }
                                    track.setQualityResult(rules.getStateName());
                                } else {
                                    realityReportHours = new BigDecimal(0);
                                    track.setQualityResult("没有质检内容");
                                }
                            } else {
                                realityReportHours = new BigDecimal(0);
                                track.setQualityResult("没有质检内容");
                            }
                        } else if (trackItem.getIsExistQualityCheck() == 0) {
                            //不质检也计算工时
                            //累计实际额定工时
                            sumRealityReportHours = sumRealityReportHours.add(realityReportHours);
                            track.setQualityResult("合格（非质检）");
                        }
                        //总工时
                        BigDecimal totalHours = realityReportHours.add(realityPrepareEndHours);
                        track.setTotalHours(totalHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                        sumTotalHours = sumTotalHours.add(totalHours);
                        track.setUserName(tenantUserVo.getEmplName());
//                        track.setDeviceName(deviceMap.get(track.getDeviceId()) == null ? "" : deviceMap.get(track.getDeviceId()).getName());
                        track.setRealityReportHours(realityReportHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                        track.setRealityPrepareEndHours(realityPrepareEndHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                        track.setWorkNo(trackHeadMap.get(track.getTrackId()) == null ? "" : trackHeadMap.get(track.getTrackId()).getWorkNo());
                        track.setTrackNo(trackHeadMap.get(track.getTrackId()) == null ? "" : trackHeadMap.get(track.getTrackId()).getTrackNo());
                        track.setOptSequence(trackItem.getOptSequence());
                        track.setOptName(trackItem.getOptName());
                        track.setProductionOrder(trackHeadMap.get(track.getTrackId()) == null ? "" : trackHeadMap.get(track.getTrackId()).getProductionOrder());
                        track.setOptNo(trackItem.getOptNo());
                        track.setParentId(id);
                        track.setCompleteTimeStr(DateUtil.format(track.getCompleteTime(), "yyyy-MM-dd HH:mm:ss"));
                        details.add(track);
                    }
                    track0.setId(id);
                    //实际准备工时
                    track0.setRealityPrepareEndHours(sumRealityPrepareEndHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                    //实际报工工时
                    track0.setRealityReportHours(sumRealityReportHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                    //准备工时
                    track0.setPrepareEndHours(sumPrepareEndHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                    //报工工时
                    track0.setReportHours(sumReportHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                    //总工时
                    track0.setTotalHours(sumTotalHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                    track0.setUserName(tenantUserVo.getEmplName());
                    track0.setTrackCompleteList(trackCompleteShowList);
                    //判断是否包含叶子结点
                    track0.setIsLeafNodes(!CollectionUtils.isEmpty(trackCompletes));
                    summary.add(track0);
                }
            }
        }
        Map<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("details", details);
        stringObjectHashMap.put("summary", summary);
        System.out.println("-------------------------------");
        System.out.println(completes.size());
        return stringObjectHashMap;
    }

    @Override
    public List<TrackComplete> queryList(String tiId, String branchCode, String order, String orderCol) {
        try {
            QueryWrapper<TrackComplete> queryWrapper = new QueryWrapper<TrackComplete>();
            if (!StringUtils.isNullOrEmpty(tiId)) {
                queryWrapper.eq("ti_id", tiId);
            }
            if (!StringUtils.isNullOrEmpty(branchCode)) {
                queryWrapper.eq("branch_code", branchCode);
            }
            if (!StringUtils.isNullOrEmpty(orderCol)) {
                if (!StringUtils.isNullOrEmpty(order)) {
                    if (order.equals("desc")) {
                        queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
                    } else if (order.equals("asc")) {
                        queryWrapper.orderByAsc(StrUtil.toUnderlineCase(orderCol));
                    }
                } else {
                    queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
                }
            } else {
                queryWrapper.orderByDesc("modify_time");
            }
            List<TrackComplete> completes = trackCompleteMapper.queryList(queryWrapper);
            try {
                for (TrackComplete track : completes) {
                    CommonResult<TenantUserVo> tenantUserVo = systemServiceClient.queryByUserAccount(track.getUserId());
                    track.setUserName(tenantUserVo.getData().getEmplName());
                    //外协没有设备
                    if (StrUtil.isNotBlank(track.getDeviceId())) {
                        CommonResult<Device> device = baseServiceClient.getDeviceById(track.getDeviceId());
                        if (null != device.getData()) {
                            track.setDeviceName(device.getData().getName());
                        }
                    }
                    TrackItem trackItem = trackItemService.getById(track.getTiId());
                    //查询产品编号
                    TrackFlow trackFlow = trackFlowService.getById(trackItem.getFlowId());
                    track.setProdNo(trackFlow.getProductNo());
                    //查询产品名称
                    TrackHead trackHead = trackHeadService.getById(track.getTrackId());
                    track.setProductName(trackHead.getProductName());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return completes;
        } catch (Exception e) {
            throw new GlobalException("信息获取异常", ResultCode.FAILED);
        }
    }

    @Autowired
    private TrackCompleteExtraService trackCompleteExtraService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveComplete(List<CompleteDto> completeDtoList) {
        //获取用户所属公司
        String companyCode = SecurityUtils.getCurrentUser().getCompanyCode();
        for (CompleteDto completeDto : completeDtoList) {
            if (StringUtils.isNullOrEmpty(completeDto.getQcPersonId())) {
                return CommonResult.failed("质检人员不能为空");
            }
            if (null == completeDto.getTrackCompleteList() && completeDto.getTrackCompleteList().isEmpty()) {
                return CommonResult.failed("报工人员不能为空");
            }
            if (StringUtils.isNullOrEmpty(completeDto.getTiId())) {
                return CommonResult.failed("工序Id不能为空");
            }
            TrackItem trackItem = trackItemService.getById(completeDto.getTiId());
            //检验人
            trackItem.setQualityCheckBy(completeDto.getQcPersonId());
            //根据工序Id删除缓存表数据
            QueryWrapper<TrackCompleteCache> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("ti_id", completeDto.getTiId());
            double numDouble = 0.00;
            for (TrackComplete trackComplete : completeDto.getTrackCompleteList()) {
                //验证输入值是否合法
//                String s = this.verifyTrackComplete(trackComplete, trackItem, companyCode);
//                //如果返回值不等于空则代表验证不通过，将提示信息返回
//                if (org.apache.commons.lang3.StringUtils.isNotBlank(s)) {
//                    return CommonResult.failed(s);
//                }

                trackComplete.setId(null);
                trackComplete.setAssignId(completeDto.getAssignId());
                trackComplete.setTiId(completeDto.getTiId());
                trackComplete.setTrackId(completeDto.getTrackId());
                trackComplete.setTrackNo(completeDto.getTrackNo());
                trackComplete.setProdNo(completeDto.getProdNo());
                trackComplete.setCompleteBy(SecurityUtils.getCurrentUser().getUsername());
                trackComplete.setCompleteTime(new Date());
                trackComplete.setDetectionResult("-");
                trackComplete.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                numDouble += trackComplete.getCompletedQty();
            }
            Assign assign = trackAssignService.getById(completeDto.getAssignId());
            //跟新工序完成数量
            trackItem.setCompleteQty(!Objects.isNull(trackItem.getCompleteQty()) ? trackItem.getCompleteQty() + numDouble : numDouble);
            double intervalNumber = assign.getQty() + 0.0;
            if (numDouble > assign.getQty()) {
                return CommonResult.failed("报工数量:" + numDouble + ",派工数量:" + assign.getQty() + "完工数量不得大于" + assign.getQty());
            }
            if (numDouble < intervalNumber - 0.01) {
                return CommonResult.failed("报工数量:" + numDouble + ",派工数量:" + assign.getQty() + "完工数量不得少于" + (intervalNumber - 0.01));
            }
            if (assign.getQty() >= numDouble && intervalNumber - 0.01 <= numDouble) {
                //最后一次报工进行下工序激活
                if (queryIsComplete(assign)) {
                    //更改状态 标识当前工序完成
                    trackItem.setIsDoing(2);
                    trackItem.setIsOperationComplete(1);
                    trackItemService.updateById(trackItem);
                    trackCompleteCacheService.remove(queryWrapper);
                    //调用工序激活方法
                    Map<String, String> map = new HashMap<>(3);
                    map.put(IdEnum.FLOW_ID.getMessage(), trackItem.getFlowId());
                    map.put(IdEnum.TRACK_HEAD_ID.getMessage(), completeDto.getTrackId());
                    map.put(IdEnum.TRACK_ITEM_ID.getMessage(), completeDto.getTiId());
                    map.put(IdEnum.ASSIGN_ID.getMessage(), completeDto.getAssignId());
                    publicService.publicUpdateState(map, PublicCodeEnum.COMPLETE.getCode());
                }
                //派工状态设置为完成
                assign.setState(2);
                trackAssignService.updateById(assign);
            }
            log.error(completeDto.getTrackCompleteList().toString());
            this.saveBatch(completeDto.getTrackCompleteList());
            //锻造车间 填写的额外报工信息
            if (!ObjectUtil.isEmpty(completeDto.getTrackCompleteExtraList()) && completeDto.getTrackCompleteExtraList().size() > 0) {
                trackCompleteExtraService.saveBatch(completeDto.getTrackCompleteExtraList());
            }
        }
        return CommonResult.success(true);
    }

    /**
     * 功能描述: 判断当前报工是否是最后一次报工
     *
     * @param assign
     * @Author: xinYu.hou
     * @Date: 2022/10/10 16:01
     * @return: boolean
     **/
    private boolean queryIsComplete(Assign assign) {
        TrackItem trackItem = trackItemService.getById(assign.getTiId());
        //只制作一件物品不进行判断
        if (trackItem.getNumber() == 1) {
            return true;
        }
        QueryWrapper<Assign> query = new QueryWrapper<>();
        query.eq("ti_id", assign.getTiId());
        //state = 2 (已完工)
        query.eq("state", 2);
        List<Assign> assignList = trackAssignService.list(query);
        //获取已完成数量
        int size = 0;
        for (Assign assignEntity : assignList) {
            size += assignEntity.getQty();
        }

        //当前工序制造总数 - 已完成数量 == 这次报工数量
        return trackItem.getNumber() - size == assign.getQty();
    }


    @Override
    public CommonResult<QueryWorkingTimeVo> queryDetails(String assignId, String tiId, Integer state) {
        if (StringUtils.isNullOrEmpty(tiId)) {
            return CommonResult.failed("工序Id不能为空");
        }
        if (StringUtils.isNullOrEmpty(assignId)) {
            return CommonResult.failed("派工Id不能为空");
        }
        QueryWorkingTimeVo queryWorkingTimeVo = new QueryWorkingTimeVo();
        Assign assign = trackAssignMapper.queryAssign(assignId);
        //‘/’全部派工人员查询
        if (assign.getUserId().contains("/")) {
            List<String> branchCodes = Arrays.asList(assign.getSiteId().split(","));
            List<TenantUserVo> data = systemServiceClient.queryUserByBranchCodes(branchCodes).getData();
            List<AssignPerson> assignPeople = new ArrayList<>();
            for (TenantUserVo datum : data) {
                AssignPerson assignPerson = new AssignPerson();
                assignPerson.setAssignId(assignId);
                assignPerson.setUserId(datum.getUserAccount());
                assignPerson.setUserName(datum.getEmplName());
                assignPeople.add(assignPerson);
            }
            assign.setAssignPersons(assignPeople);
        } else {
            assign.setAssignPersons(trackAssignPersonMapper.selectList(new QueryWrapper<AssignPerson>().eq("assign_id", assign.getId())));
        }


        QueryWrapper<TrackComplete> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("ti_id", tiId);

        List<TrackComplete> completeList = new ArrayList<>();
        if (0 == state) {
            completeList = trackCompleteMapper.queryCompleteCache(queryWrapper);
        } else {
            completeList = this.list(queryWrapper);
        }

        TrackItem trackItem = trackItemService.getById(tiId);
        queryWorkingTimeVo.setTrackCompleteList(completeList);
        queryWorkingTimeVo.setAssign(assign);
        queryWorkingTimeVo.setQcPersonId(trackItem.getQualityCheckBy());
        queryWorkingTimeVo.setQualityCheckBranch(trackItem.getQualityCheckBranch());
        return CommonResult.success(queryWorkingTimeVo);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<Boolean> updateComplete(CompleteDto completeDto) {
        //获取用户所属公司
        String companyCode = SecurityUtils.getCurrentUser().getCompanyCode();
        if (StringUtils.isNullOrEmpty(completeDto.getTiId())) {
            return CommonResult.failed("工序Id不能为空");
        }
        if (StringUtils.isNullOrEmpty(completeDto.getQcPersonId())) {
            return CommonResult.failed("质检人员不能为空");
        }
        if (null == completeDto.getTrackCompleteList() && completeDto.getTrackCompleteList().isEmpty()) {
            return CommonResult.failed("报工人员不能为空");
        }
        TrackItem trackItem = trackItemService.getById(completeDto.getTiId());
        //检验人
        trackItem.setQualityCheckBy(completeDto.getQcPersonId());
        //根据工序Id先删除,在重新新增数据
        QueryWrapper<TrackComplete> removeComplete = new QueryWrapper<>();
        removeComplete.eq("ti_id", completeDto.getTiId());
        this.remove(removeComplete);
        double numDouble = 0.00;
        Assign assign = trackAssignService.getById(completeDto.getAssignId());
        double intervalNumber = assign.getQty() + 0.0;
        for (TrackComplete trackComplete : completeDto.getTrackCompleteList()) {
            //验证输入值是否合法
            String s = trackCompleteService.verifyTrackComplete(trackComplete, trackItem, companyCode);
            //如果返回值不等于空则代表验证不通过，将提示信息返回
            if (org.apache.commons.lang3.StringUtils.isNotBlank(s)) {
                return CommonResult.failed(s);
            }
            trackComplete.setAssignId(completeDto.getAssignId());
            trackComplete.setTiId(completeDto.getTiId());
            trackComplete.setTrackId(completeDto.getTrackId());
            trackComplete.setTrackNo(completeDto.getTrackNo());
            trackComplete.setProdNo(completeDto.getProdNo());
            trackComplete.setCompleteBy(SecurityUtils.getCurrentUser().getUsername());
            trackComplete.setCompleteTime(new Date());
            numDouble += trackComplete.getCompletedQty();
        }
        //报工数量判断
        if (numDouble > assign.getQty()) {
            return CommonResult.failed("报工数量:" + numDouble + ",派工数量:" + assign.getQty() + ",完工数量不得大于" + assign.getQty());
        }
        if (numDouble < intervalNumber - 0.01) {
            return CommonResult.failed("报工数量:" + numDouble + ",派工数量:" + assign.getQty() + ",完工数量不得少于" + +(intervalNumber - 0.01));
        }
        //跟新工序完成数量
        trackItem.setCompleteQty(trackItem.getCompleteQty() + numDouble);
        trackItemService.updateById(trackItem);
        return CommonResult.success(this.saveOrUpdateBatch(completeDto.getTrackCompleteList()));
    }

    /**
     * @modifiedby mafeng02 2002-07-28 ,修改没有派工的外协和探伤下的处理
     * @Description 报工回滚
     */
    @Override
    public CommonResult<Boolean> rollBack(String id) {
        String msg = "";
        TrackComplete trackComplete = this.getById(id);
        Assign assign = new Assign();
        if (null != trackComplete.getAssignId()) {
            assign = trackAssignService.getById(trackComplete.getAssignId());
        }
        TrackItem trackItem = new TrackItem();
        if (null == assign) {
            trackItem = trackItemService.getById(trackComplete.getTiId());
        } else {
            trackItem = trackItemService.getById(assign.getTiId());
        }
        if (null == trackItem) {
            removeComplete(trackComplete.getTiId());
        } else {
            QueryWrapper<TrackComplete> queryWrapper = new QueryWrapper<TrackComplete>();
            if (!StringUtils.isNullOrEmpty(id)) {
                queryWrapper.eq("track_id", id);
            } else {
                queryWrapper.eq("track_id", "-1");
            }
            queryWrapper.orderByAsc("modify_time");
            List<TrackComplete> cs = trackCompleteService.list(queryWrapper);
            //判断跟单号已质检完成，报工无法取消
            if (trackItem.getIsExistQualityCheck() == 1 && trackItem.getIsQualityComplete() == 1) {
                msg += "跟单号已质检完成，报工无法取消！";
            }
            //判断跟单号已质检完成，报工无法取消
            if (trackItem.getIsExistScheduleCheck() == 1 && trackItem.getIsScheduleComplete() == 1) {
                msg += "跟单号已调度完成，报工无法取消！";
            }
            //判断后置工序是否已派工，否则不可回滚
            QueryWrapper<Assign> queryWrapperAssign = new QueryWrapper<Assign>();
            queryWrapperAssign.eq("ti_id", trackItem.getId());
            List<Assign> assigns = trackAssignService.list(queryWrapperAssign);
            for (int j = 0; j < assigns.size(); j++) {
                TrackItem cstrackItem = trackItemService.getById(assigns.get(j).getTiId());
                if (cstrackItem.getOptSequence() > trackItem.getOptSequence()) {
                    return CommonResult.failed("无法取消报工，已有后序工序【" + cstrackItem.getOptName() + "】已派工，需要先取消后序工序");
                }
            }
            //判断后置工序是否已报工，否则不可回滚
            for (int j = 0; j < cs.size(); j++) {
                TrackItem cstrackItem = trackItemService.getById(cs.get(j).getTiId());
                if (cstrackItem.getOptSequence() > trackItem.getOptSequence()) {
                    return CommonResult.failed("无法取消报工，已有后序工序【" + cstrackItem.getOptName() + "】已报工，需要先取消后序工序");
                }
            }

            //将后置工序IS_CURRENT设置为否，状态为1
            List<TrackItem> items = trackItemService.list(new QueryWrapper<TrackItem>().eq("track_head_id", trackItem.getTrackHeadId()).orderByAsc("opt_sequence"));
            for (TrackItem trackItems : items) {
                if (trackItems.getOptSequence() > trackItem.getOptSequence() && trackItems.getIsCurrent() == 1) {
                    trackItems.setIsCurrent(0);
                    trackItems.setIsDoing(0);
                    trackItems.setIsFinalComplete("0");
                    trackItemService.updateById(trackItems);
                }
            }
            //将当前工序设置为激活
            if (msg.equals("")) {
                trackItem.setIsDoing(0);
                trackItem.setIsCurrent(1);
                trackItem.setIsFinalComplete("0");
                trackItem.setIsOperationComplete(0);
                trackItemService.updateById(trackItem);
                TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
                trackHead.setStatus("1");
                trackHeadService.updateById(trackHead);
                if (null != assign) {
                    assign.setAvailQty(assign.getQty());
                    assign.setState(0);
                    trackAssignService.updateById(assign);
                }
                removeComplete(trackComplete.getTiId());
            }
        }


        if (msg.equals("")) {
            return CommonResult.success(null, "删除成功！");
        } else {
            return CommonResult.failed("操作失败，请重试！" + msg);
        }
    }

    @Override
    public String verifyTrackComplete(TrackComplete trackComplete, TrackItem trackItem, String companyCode) {
        StringBuffer massage = new StringBuffer();
        //根据数据字段配置，判断走那一套验证逻辑，宝石与北石字段不同
        if (Tenant.COMPANYCODE_BEISHI.equals(companyCode)) {
            //北石验证
            //验证实用固定机时是否填写正常,固定机时不能大于准结工时
            if (trackComplete.getActualFixHours() > trackItem.getPrepareEndHours()) {
                return massage.append("固定机时不能大于准结工时").toString();
            }
            //验证实用变动机时（正常班），实用变动机时（正常班）不能大于准结工时+额定工时
            if (trackComplete.getActualNomalHours() > trackItem.getPrepareEndHours() + trackItem.getSinglePieceHours()) {
                return massage.append("实用变动机时（正常班）不能大于准结工时+额定工时").toString();
            }
            //验证实用变动机时（加班），实用变动机时（加班）不能大于准结工时+额定工时
            if (trackComplete.getActualOverHours() > trackItem.getPrepareEndHours() + trackItem.getSinglePieceHours()) {
                return massage.append("实用变动机时（加班）不能大于准结工时+额定工时").toString();
            }
            //验证完成固定机时,完成固定机时不能大于准结工时
            if (trackComplete.getCompletedFixHours() > trackItem.getPrepareEndHours()) {
                return massage.append("完成固定机时不能大于准结工时").toString();
            }
            //验证完成变动机时，验证完成变动机时不能大于准结工时+额定工时
            if (trackComplete.getCompletedChangeHours() > trackItem.getPrepareEndHours() + trackItem.getSinglePieceHours()) {
                return massage.append("完成变动机时不能大于准结工时+额定工时").toString();
            }
        } else if (Tenant.COMPANYCODE_BAOSHI.equals(companyCode)) {
            //宝石验证
            if (trackComplete.getReportHours() > trackItem.getSinglePieceHours()) {
                return massage.append("报工工时不能大于额定工时").toString();
            }
        }
        return massage.toString();
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public CommonResult<Boolean> saveOutsource(OutsourceCompleteDto outsource) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("track_head_id", outsource.getTrackHeadId());
        queryWrapper.in("product_no", outsource.getProdNoList());
        queryWrapper.eq("branch_code", outsource.getBranchCode());
        List<TrackItem> list = trackItemService.list(queryWrapper);
        List<TrackItem> result = new ArrayList<>();
        //获取正确的工序
        for (OutsourceDto outsourceDto : outsource.getOutsourceDtoList()) {
            List<TrackItem> collect = list.stream().filter(trackItem ->
                    trackItem.getOptNo().equals(outsourceDto.getOptNo()) && trackItem.getOptName().equals(outsourceDto.getOptName()) && trackItem.getIsCurrent() == 1
            ).collect(Collectors.toList());
            result.addAll(collect);
        }
        boolean bool = true;
        for (TrackItem trackItem : result) {
            TrackComplete trackComplete = new TrackComplete();
            BeanUtils.copyProperties(outsource.getTrackComplete(), trackComplete);
            if (StringUtils.isNullOrEmpty(trackItem.getStartDoingUser())) {
                trackItem.setStartDoingTime(new Date());
                trackItem.setStartDoingUser(outsource.getTrackComplete().getUserId());
            }
            TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
            trackItem.setCompleteQty(Double.valueOf(trackItem.getNumber()));
            trackItem.setAssignableQty(0);
            trackComplete.setTiId(trackItem.getId());
            trackComplete.setTrackId(trackItem.getTrackHeadId());
            trackComplete.setProdNo(trackItem.getProductNo());
            trackComplete.setAssignId("");
            trackComplete.setModifyTime(new Date());
            trackComplete.setCreateTime(new Date());
            trackComplete.setCompleteBy(outsource.getTrackComplete().getUserId());
            trackComplete.setCompleteTime(new Date());
            trackComplete.setUserId(SecurityUtils.getCurrentUser().getUsername());
            CommonResult<TenantUserVo> userVoCommonResult = systemServiceClient.queryByUserId(SecurityUtils.getCurrentUser().getUserId());
            trackComplete.setUserName(userVoCommonResult.getData().getEmplName());
            trackComplete.setBranchCode(outsource.getBranchCode());
            trackComplete.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            trackComplete.setCompleteBy(SecurityUtils.getCurrentUser().getUsername());
            trackComplete.setCompletedQty(Double.valueOf(trackItem.getNumber()));
            trackComplete.setTrackNo(trackHead.getId());

            trackItem.setOperationCompleteTime(new Date());
            trackItem.setIsOperationComplete(1);
            trackItem.setIsDoing(2);
            trackItem.setQualityCheckBy(trackComplete.getQualityCheckBy());
            trackItem.setQualityCheckBranch(trackComplete.getQualityCheckBranch());
            bool = trackCompleteService.save(trackComplete);
            trackItemService.updateById(trackItem);

            //判断是否需要质检和调度审核 再激活下工序
            boolean next = trackItem.getIsExistQualityCheck().equals(0) && trackItem.getIsExistScheduleCheck().equals(0);
            if (next) {
                Map<String, String> map = new HashMap<String, String>(1);
                map.put(IdEnum.FLOW_ID.getMessage(), trackItem.getFlowId());
                publicService.activationProcess(map);
            }
        }
        if (bool) {
            return CommonResult.success(bool, "操作成功！");
        } else {
            return CommonResult.failed("操作失败，请重试！");
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> queryTrackCompleteListByOrder(String trackNo, String startTime, String endTime, String branchCode, String workNo, String userId, String orderNo) {
        //获取filter过滤后的报工列表
        List<TrackComplete> allCompletes = getCompleteByFilter(trackNo, startTime, endTime, branchCode, workNo, userId, orderNo);
        List<TrackComplete> summary = new ArrayList<>();
        List<TrackComplete> details = new ArrayList<>();

        if (!allCompletes.isEmpty()) {
            ExecutorService executorService = Executors.newFixedThreadPool(20);
            //查询当前车间下所有质检规则
            Future<List<QualityInspectionRules>> qualityInspectionRulesFuture = ConcurrentUtil.doJob(executorService, () -> systemServiceClient.queryQualityInspectionRulesListInner(allCompletes.get(0).getBranchCode(), SecurityConstants.FROM_INNER));
            //根据员工分组
            Map<String, List<TrackComplete>> completesMap = allCompletes.stream().filter(complete -> StrUtil.isNotBlank(complete.getUserId())).collect(Collectors.groupingBy(TrackComplete::getUserId));
            ArrayList<String> userIdList = new ArrayList<>(completesMap.keySet());
            Future<Map<String, TenantUserVo>> userMapFuture = ConcurrentUtil.doJob(executorService, () -> systemServiceClient.queryByUserAccountListInner(userIdList, SecurityConstants.FROM_INNER));
            //获取设备信息
//            Set<String> deviceIds = completes.stream().map(TrackComplete::getDeviceId).collect(Collectors.toSet());
//            List<Device> deviceByIdList = baseServiceClient.getDeviceByIdList(new ArrayList<>(deviceIds));
//            Map<String, Device> deviceMap = deviceByIdList.stream().collect(Collectors.toMap(BaseEntity::getId, x -> x));
            //根据跟单id获取跟单数据
            Set<String> trackIdList = allCompletes.stream().map(TrackComplete::getTrackId).collect(Collectors.toSet());
            Future<List<TrackHead>> trackHeadListFuture = ConcurrentUtil.doJob(executorService, () -> trackHeadService.listByIds(new ArrayList<>(trackIdList)));
            //根据跟单工序id获取跟单工序
            Set<String> tiIdList = allCompletes.stream().map(TrackComplete::getTiId).collect(Collectors.toSet());
            Future<List<TrackItem>> trackItemListFuture = ConcurrentUtil.doJob(executorService, () -> trackItemService.listByIds(new ArrayList<>(tiIdList)));
            //根据工序Id查询质检记录 2023/3/7 移至到完工查询，减少查询次数
            QueryWrapper<TrackCheck> queryWrapperCheck = new QueryWrapper<>();
            queryWrapperCheck.in("ti_id", new ArrayList<>(tiIdList));
            queryWrapperCheck.orderByDesc("modify_time");
            Future<List<TrackCheck>> trackCheckListFuture = ConcurrentUtil.doJob(executorService, () -> trackCheckService.list(queryWrapperCheck));
            //并行执行
            List<QualityInspectionRules> rulesList = ConcurrentUtil.futureGet(qualityInspectionRulesFuture);
            Map<String, TenantUserVo> stringTenantUserVoMap = ConcurrentUtil.futureGet(userMapFuture);
            List<TrackHead> trackHeads = ConcurrentUtil.futureGet(trackHeadListFuture);
            List<TrackItem> trackItems = ConcurrentUtil.futureGet(trackItemListFuture);
            List<TrackCheck> trackCheckList = ConcurrentUtil.futureGet(trackCheckListFuture);
            Map<String, QualityInspectionRules> rulesMap = rulesList.stream().collect(Collectors.toMap(BaseEntity::getId, x -> x));
            Map<String, TrackHead> trackHeadMap = trackHeads.stream().collect(Collectors.toMap(BaseEntity::getId, x -> x));
            Map<String, TrackItem> trackMap = trackItems.stream().filter(item -> item.getIsOperationComplete() == 1).collect(Collectors.toMap(TrackItem::getId, x -> x, (k, v) -> k));
            //只获取已完工数据计算工时
            List<String> flowIdList = trackItems.stream().map(TrackItem::getFlowId).collect(Collectors.toList());
            List<TrackFlow> trackFlows = trackFlowService.listByIds(flowIdList);
            Map<String, TrackFlow> trackFlowMap = trackFlows.stream().collect(Collectors.toMap(BaseEntity::getId, x -> x, (k, v) -> k));
            Map<String, List<TrackCheck>> trackChecksMap = trackCheckList.stream().filter(complete -> StrUtil.isNotBlank(complete.getTiId())).collect(Collectors.groupingBy(TrackCheck::getTiId));
            //根据报工表获取订单id对应的trackHeadList
            Set<String> trackIds = allCompletes.stream().map(TrackComplete::getTrackId).collect(Collectors.toSet());
            List<TrackHead> trackHeadList = trackHeadService.listByIds(new ArrayList<>(trackIds));
            Map<String, List<TrackHead>> trackHeadMapByOrder = trackHeadList.stream().filter(trackHead -> StrUtil.isNotBlank(trackHead.getProductionOrder())).collect(Collectors.groupingBy(TrackHead::getProductionOrder));
            //获取orderList
            List<String> orderList = new ArrayList<>(trackHeadMapByOrder.keySet());
            for (String orderno : orderList) {
                List<TrackHead> trackHeadListByOrder = trackHeadMapByOrder.get(orderno);
                Set<String> trackHeadIdSet = trackHeadListByOrder.stream().map(TrackHead::getId).collect(Collectors.toSet());
                //获取包含了orderNo的completes
                List<TrackComplete> completes = allCompletes.stream().filter(x -> trackHeadIdSet.contains(x.getTrackId())).collect(Collectors.toList());

                if (!CollectionUtils.isEmpty(completes)) {
                    //用来展示数据列表
                    List<TrackComplete> trackCompleteShowList = new ArrayList<>();
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
                    TrackComplete track0 = new TrackComplete();
                    //for循环计算时间
                    for (TrackComplete track : completes) {
                        //获取当前用户信息
                        TenantUserVo tenantUserVo = stringTenantUserVoMap.get(track.getUserId());
                        //根据跟单工序id获取跟单工序
                        TrackItem trackItem = trackMap.get(track.getTiId());
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
                        TrackFlow trackFlow = trackFlowMap.get(trackItem.getFlowId());
                        TrackHead trackHead = trackHeadMap.get(track.getTrackId());
                        track.setProdNo(trackFlow == null ? "" : trackFlow.getProductNo());
                        track.setProductName(trackHead == null ? "" : trackHead.getProductName());
                        track.setDrawingNo(trackHead == null ? "" : trackHead.getDrawingNo());
                        //空校验
                        if (trackItem.getPrepareEndHours() == null) {
                            trackItem.setPrepareEndHours(0.00);
                            track.setPrepareEndHours(0.00);
                        } else {
                            track.setPrepareEndHours(trackItem.getPrepareEndHours());
                        }
                        if (track.getReportHours() == null) {
                            track.setReportHours(0.00);
                        }
                        //额定工时
                        if (trackItem.getSinglePieceHours() == null) {
                            track.setSinglePieceHours(0.00);
                        } else {
                            track.setSinglePieceHours(trackItem.getSinglePieceHours());
                        }
                        if (track.getCompletedQty() == null) {
                            track.setCompletedQty(0.00);
                        }
                        //数量
                        BigDecimal number = new BigDecimal(track.getCompletedQty());
                        //额定工时
                        BigDecimal reportHours = new BigDecimal(track.getReportHours());
                        //准结工时
                        BigDecimal prepareEndHours = new BigDecimal(track.getPrepareEndHours());
                        //实际额定工时
                        BigDecimal realityReportHours = new BigDecimal(track.getReportHours());
                        //实际准结工时
                        BigDecimal realityPrepareEndHours = new BigDecimal(track.getPrepareEndHours());
                        //累计准结工时
                        sumPrepareEndHours = sumPrepareEndHours.add(prepareEndHours);
                        //累计额定工时
                        sumReportHours = sumReportHours.add(reportHours);
                        //没有调度审核或者 调度已审核并且给予准结工时进入
                        if (trackItem.getIsScheduleComplete() == null || trackItem.getIsScheduleComplete() == 0 || (trackItem.getIsScheduleComplete() == 1 && trackItem.getIsPrepare() != null && trackItem.getIsPrepare() == 1)) {
                            //累计实际准结工时
                            sumRealityPrepareEndHours = sumRealityPrepareEndHours.add(realityPrepareEndHours);
                        } else {
                            realityPrepareEndHours = new BigDecimal(0);
                        }
                        //已质检 校验不合格是否给工时(单件工时/额定工时)
                        if (trackItem.getIsQualityComplete() == 1) {
                            List<TrackCheck> trackChecks = trackChecksMap.get(trackItem.getId());
                            if (trackChecks != null && trackChecks.size() > 0) {
                                QualityInspectionRules rules = rulesMap.get(trackChecks.get(0).getResult());
                                if (rules != null) {
                                    if (rules.getIsGiveTime() == 1) {
                                        //累计实际额定工时
                                        sumRealityReportHours = sumRealityReportHours.add(realityReportHours);
                                    } else {
                                        realityReportHours = new BigDecimal(0);
                                    }
                                    track.setQualityResult(rules.getStateName());
                                } else {
                                    realityReportHours = new BigDecimal(0);
                                    track.setQualityResult("没有质检内容");
                                }
                            } else {
                                realityReportHours = new BigDecimal(0);
                                track.setQualityResult("没有质检内容");
                            }
                        } else if (trackItem.getIsExistQualityCheck() == 0) {
                            //不质检也计算工时
                            //累计实际额定工时
                            sumRealityReportHours = sumRealityReportHours.add(realityReportHours);
                            track.setQualityResult("合格（非质检）");
                        }
                        //总工时
                        BigDecimal totalHours = number.multiply(realityReportHours).add(realityPrepareEndHours);
                        track.setTotalHours(totalHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                        sumTotalHours = sumTotalHours.add(totalHours);
                        track.setUserName(tenantUserVo.getEmplName());
//                        track.setDeviceName(deviceMap.get(track.getDeviceId()) == null ? "" : deviceMap.get(track.getDeviceId()).getName());
                        track.setRealityReportHours(realityReportHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                        track.setRealityPrepareEndHours(realityPrepareEndHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                        track.setWorkNo(trackHeadMap.get(track.getTrackId()) == null ? "" : trackHeadMap.get(track.getTrackId()).getWorkNo());
                        track.setTrackNo(trackHeadMap.get(track.getTrackId()) == null ? "" : trackHeadMap.get(track.getTrackId()).getTrackNo());
                        track.setOptSequence(trackItem.getOptSequence());
                        track.setOptName(trackItem.getOptName());
                        track.setProductionOrder(trackHeadMap.get(track.getTrackId()) == null ? "" : trackHeadMap.get(track.getTrackId()).getProductionOrder());
                        track.setOptNo(trackItem.getOptNo());
                        track.setParentId(orderno);
                        track.setCompleteTimeStr(DateUtil.format(track.getCompleteTime(), "yyyy-MM-dd HH:mm:ss"));
                        details.add(track);
                    }
                    track0.setProductionOrder(orderno);
                    track0.setId(orderno);
                    //实际准备工时
                    track0.setRealityPrepareEndHours(sumRealityPrepareEndHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                    //实际额定工时
                    track0.setRealityReportHours(sumRealityReportHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                    //准备工时
                    track0.setPrepareEndHours(sumPrepareEndHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                    //额定工时
                    track0.setReportHours(sumReportHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                    //总工时
                    track0.setTotalHours(sumTotalHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                    track0.setTrackCompleteList(trackCompleteShowList);
                    //判断是否包含叶子结点
                    track0.setIsLeafNodes(!CollectionUtils.isEmpty(completes));
                    summary.add(track0);

                }
            }
        }
        Map<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("details", details);
        stringObjectHashMap.put("summary", summary);
        return stringObjectHashMap;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> queryTrackCompleteListByWorkNo(String trackNo, String startTime, String endTime, String branchCode, String workNo, String userId, String orderNo) {
        //获取filter过滤后的报工列表
        List<TrackComplete> allCompletes = getCompleteByFilter(trackNo, startTime, endTime, branchCode, workNo, userId, orderNo);
        List<TrackComplete> emptyTrackComplete = new ArrayList<>();
        List<TrackComplete> summary = new ArrayList<>();
        List<TrackComplete> details = new ArrayList<>();

        if (!allCompletes.isEmpty()) {
            ExecutorService executorService = Executors.newFixedThreadPool(20);
            //查询当前车间下所有质检规则
            Future<List<QualityInspectionRules>> qualityInspectionRulesFuture = ConcurrentUtil.doJob(executorService, () -> systemServiceClient.queryQualityInspectionRulesListInner(allCompletes.get(0).getBranchCode(), SecurityConstants.FROM_INNER));
            //根据员工分组
            Map<String, List<TrackComplete>> completesMap = allCompletes.stream().filter(complete -> StrUtil.isNotBlank(complete.getUserId())).collect(Collectors.groupingBy(TrackComplete::getUserId));
            ArrayList<String> userIdList = new ArrayList<>(completesMap.keySet());
            Future<Map<String, TenantUserVo>> userMapFuture = ConcurrentUtil.doJob(executorService, () -> systemServiceClient.queryByUserAccountListInner(userIdList, SecurityConstants.FROM_INNER));
            //获取设备信息
//            Set<String> deviceIds = completes.stream().map(TrackComplete::getDeviceId).collect(Collectors.toSet());
//            List<Device> deviceByIdList = baseServiceClient.getDeviceByIdList(new ArrayList<>(deviceIds));
//            Map<String, Device> deviceMap = deviceByIdList.stream().collect(Collectors.toMap(BaseEntity::getId, x -> x));
            //根据跟单id获取跟单数据
            Set<String> trackIdList = allCompletes.stream().map(TrackComplete::getTrackId).collect(Collectors.toSet());
            Future<List<TrackHead>> trackHeadListFuture = ConcurrentUtil.doJob(executorService, () -> trackHeadService.listByIds(new ArrayList<>(trackIdList)));
            //根据跟单工序id获取跟单工序
            Set<String> tiIdList = allCompletes.stream().map(TrackComplete::getTiId).collect(Collectors.toSet());
            Future<List<TrackItem>> trackItemListFuture = ConcurrentUtil.doJob(executorService, () -> trackItemService.listByIds(new ArrayList<>(tiIdList)));
            //根据工序Id查询质检记录 2023/3/7 移至到完工查询，减少查询次数
            QueryWrapper<TrackCheck> queryWrapperCheck = new QueryWrapper<>();
            queryWrapperCheck.in("ti_id", new ArrayList<>(tiIdList));
            queryWrapperCheck.orderByDesc("modify_time");
            Future<List<TrackCheck>> trackCheckListFuture = ConcurrentUtil.doJob(executorService, () -> trackCheckService.list(queryWrapperCheck));
            //并行执行
            List<QualityInspectionRules> rulesList = ConcurrentUtil.futureGet(qualityInspectionRulesFuture);
            Map<String, TenantUserVo> stringTenantUserVoMap = ConcurrentUtil.futureGet(userMapFuture);
            List<TrackHead> trackHeads = ConcurrentUtil.futureGet(trackHeadListFuture);
            List<TrackItem> trackItems = ConcurrentUtil.futureGet(trackItemListFuture);
            List<TrackCheck> trackCheckList = ConcurrentUtil.futureGet(trackCheckListFuture);
            Map<String, QualityInspectionRules> rulesMap = rulesList.stream().collect(Collectors.toMap(BaseEntity::getId, x -> x));
            Map<String, TrackHead> trackHeadMap = trackHeads.stream().collect(Collectors.toMap(BaseEntity::getId, x -> x));
            Map<String, TrackItem> trackMap = trackItems.stream().filter(item -> item.getIsOperationComplete() == 1).collect(Collectors.toMap(TrackItem::getId, x -> x, (k, v) -> k));
            //只获取已完工数据计算工时
            List<String> flowIdList = trackItems.stream().map(TrackItem::getFlowId).collect(Collectors.toList());
            List<TrackFlow> trackFlows = trackFlowService.listByIds(flowIdList);
            Map<String, TrackFlow> trackFlowMap = trackFlows.stream().collect(Collectors.toMap(BaseEntity::getId, x -> x, (k, v) -> k));
            Map<String, List<TrackCheck>> trackChecksMap = trackCheckList.stream().filter(complete -> StrUtil.isNotBlank(complete.getTiId())).collect(Collectors.groupingBy(TrackCheck::getTiId));
            //根据报工表获取订单id对应的trackHeadList
            Set<String> trackIds = allCompletes.stream().map(TrackComplete::getTrackId).collect(Collectors.toSet());
            List<TrackHead> trackHeadList = trackHeadService.listByIds(new ArrayList<>(trackIds));
            Map<String, List<TrackHead>> trackHeadMapByWorkNo = trackHeadList.stream().filter(trackHead -> StrUtil.isNotBlank(trackHead.getWorkNo())).collect(Collectors.groupingBy(TrackHead::getWorkNo));
            //获取workNoList
            List<String> workNoList = new ArrayList<>(trackHeadMapByWorkNo.keySet());
            for (String workno : workNoList) {
                List<TrackHead> trackHeadListByWorkNo = trackHeadMapByWorkNo.get(workno);
                Set<String> trackHeadIdSet = trackHeadListByWorkNo.stream().map(TrackHead::getId).collect(Collectors.toSet());
                //获取包含了workNo的completes
                List<TrackComplete> completes = allCompletes.stream().filter(x -> trackHeadIdSet.contains(x.getTrackId())).collect(Collectors.toList());

                if (!CollectionUtils.isEmpty(completes)) {
                    //用来展示数据列表
                    List<TrackComplete> trackCompleteShowList = new ArrayList<>();
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
                    TrackComplete track0 = new TrackComplete();
                    //for循环计算时间
                    for (TrackComplete track : completes) {
                        //获取当前用户信息
                        TenantUserVo tenantUserVo = stringTenantUserVoMap.get(track.getUserId());
                        //根据跟单工序id获取跟单工序
                        TrackItem trackItem = trackMap.get(track.getTiId());
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
                        TrackFlow trackFlow = trackFlowMap.get(trackItem.getFlowId());
                        TrackHead trackHead = trackHeadMap.get(track.getTrackId());
                        track.setProdNo(trackFlow == null ? "" : trackFlow.getProductNo());
                        track.setProductName(trackHead == null ? "" : trackHead.getProductName());
                        track.setDrawingNo(trackHead == null ? "" : trackHead.getDrawingNo());
                        //空校验
                        if (trackItem.getPrepareEndHours() == null) {
                            trackItem.setPrepareEndHours(0.00);
                            track.setPrepareEndHours(0.00);
                        } else {
                            track.setPrepareEndHours(trackItem.getPrepareEndHours());
                        }
                        if (track.getReportHours() == null) {
                            track.setReportHours(0.00);
                        }
                        //额定工时
                        if (trackItem.getSinglePieceHours() == null) {
                            track.setSinglePieceHours(0.00);
                        } else {
                            track.setSinglePieceHours(trackItem.getSinglePieceHours());
                        }
                        if (track.getCompletedQty() == null) {
                            track.setCompletedQty(0.00);
                        }
                        //数量
                        BigDecimal number = new BigDecimal(track.getCompletedQty());
                        //额定工时
                        BigDecimal reportHours = new BigDecimal(track.getReportHours());
                        //准结工时
                        BigDecimal prepareEndHours = new BigDecimal(track.getPrepareEndHours());
                        //实际额定工时
                        BigDecimal realityReportHours = new BigDecimal(track.getReportHours());
                        //实际准结工时
                        BigDecimal realityPrepareEndHours = new BigDecimal(track.getPrepareEndHours());
                        //累计准结工时
                        sumPrepareEndHours = sumPrepareEndHours.add(prepareEndHours);
                        //累计额定工时
                        sumReportHours = sumReportHours.add(reportHours);
                        //没有调度审核或者 调度已审核并且给予准结工时进入
                        if (trackItem.getIsScheduleComplete() == null || trackItem.getIsScheduleComplete() == 0 || (trackItem.getIsScheduleComplete() == 1 && trackItem.getIsPrepare() != null && trackItem.getIsPrepare() == 1)) {
                            //累计实际准结工时
                            sumRealityPrepareEndHours = sumRealityPrepareEndHours.add(realityPrepareEndHours);
                        } else {
                            realityPrepareEndHours = new BigDecimal(0);
                        }
                        //已质检 校验不合格是否给工时(单件工时/额定工时)
                        if (trackItem.getIsQualityComplete() == 1) {
                            List<TrackCheck> trackChecks = trackChecksMap.get(trackItem.getId());
                            if (trackChecks != null && trackChecks.size() > 0) {
                                QualityInspectionRules rules = rulesMap.get(trackChecks.get(0).getResult());
                                if (rules != null) {
                                    if (rules.getIsGiveTime() == 1) {
                                        //累计实际额定工时
                                        sumRealityReportHours = sumRealityReportHours.add(realityReportHours);
                                    } else {
                                        realityReportHours = new BigDecimal(0);
                                    }
                                    track.setQualityResult(rules.getStateName());
                                } else {
                                    realityReportHours = new BigDecimal(0);
                                    track.setQualityResult("没有质检内容");
                                }
                            } else {
                                realityReportHours = new BigDecimal(0);
                                track.setQualityResult("没有质检内容");
                            }
                        } else if (trackItem.getIsExistQualityCheck() == 0) {
                            //不质检也计算工时
                            //累计实际额定工时
                            sumRealityReportHours = sumRealityReportHours.add(realityReportHours);
                            track.setQualityResult("合格（非质检）");
                        }
                        //总工时
                        BigDecimal totalHours = number.multiply(realityReportHours).add(realityPrepareEndHours);
                        track.setTotalHours(totalHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                        sumTotalHours = sumTotalHours.add(totalHours);
                        track.setUserName(tenantUserVo.getEmplName());
//                        track.setDeviceName(deviceMap.get(track.getDeviceId()) == null ? "" : deviceMap.get(track.getDeviceId()).getName());
                        track.setRealityReportHours(realityReportHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                        track.setRealityPrepareEndHours(realityPrepareEndHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                        track.setWorkNo(trackHeadMap.get(track.getTrackId()) == null ? "" : trackHeadMap.get(track.getTrackId()).getWorkNo());
                        track.setTrackNo(trackHeadMap.get(track.getTrackId()) == null ? "" : trackHeadMap.get(track.getTrackId()).getTrackNo());
                        track.setOptSequence(trackItem.getOptSequence());
                        track.setOptName(trackItem.getOptName());
                        track.setProductionOrder(trackHeadMap.get(track.getTrackId()) == null ? "" : trackHeadMap.get(track.getTrackId()).getProductionOrder());
                        track.setOptNo(trackItem.getOptNo());
                        track.setParentId(workno);
                        track.setCompleteTimeStr(DateUtil.format(track.getCompleteTime(), "yyyy-MM-dd HH:mm:ss"));
                        details.add(track);
                    }
                    track0.setWorkNo(workno);
                    track0.setId(workno);
                    //实际准备工时
                    track0.setRealityPrepareEndHours(sumRealityPrepareEndHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                    //实际额定工时
                    track0.setRealityReportHours(sumRealityReportHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                    //准备工时
                    track0.setPrepareEndHours(sumPrepareEndHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                    //额定工时
                    track0.setReportHours(sumReportHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                    //总工时
                    track0.setTotalHours(sumTotalHours.setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());
                    track0.setTrackCompleteList(trackCompleteShowList);
                    //判断是否包含叶子结点
                    track0.setIsLeafNodes(!CollectionUtils.isEmpty(completes));
                    summary.add(track0);

                }
            }
        }
        Map<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("details", details);
        stringObjectHashMap.put("summary", summary);
        return stringObjectHashMap;
    }

    private List<TrackComplete> getCompleteByFilter(String trackNo, String startTime, String endTime, String branchCode, String workNo, String userId, String orderNo) {
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
//        PageHelper.startPage(1, 1000);
        List<TrackComplete> completes = trackCompleteMapper.queryList(queryWrapper);
//        PageInfo<TrackComplete> page = new PageInfo(completes);
//        for (int i = 1; i < page.getPages(); i++) {
//            PageHelper.startPage(i, 1000);
//            completes.addAll(trackCompleteMapper.queryList(queryWrapper));
//        }
        return completes;
    }


    private Boolean removeComplete(String tiId) {
        QueryWrapper<TrackComplete> removeComplete = new QueryWrapper<>();
        removeComplete.eq("ti_id", tiId);
        return this.remove(removeComplete);
    }

}
