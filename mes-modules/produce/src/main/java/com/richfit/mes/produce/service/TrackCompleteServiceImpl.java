package com.richfit.mes.produce.service;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Device;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.model.sys.Tenant;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.TrackAssignMapper;
import com.richfit.mes.produce.dao.TrackAssignPersonMapper;
import com.richfit.mes.produce.dao.TrackCompleteMapper;
import com.richfit.mes.produce.enmus.IdEnum;
import com.richfit.mes.produce.enmus.PublicCodeEnum;
import com.richfit.mes.produce.entity.CompleteDto;
import com.richfit.mes.produce.entity.QueryWorkingTimeVo;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author sun
 * @Description 跟单服务
 */
@Service
public class TrackCompleteServiceImpl extends ServiceImpl<TrackCompleteMapper, TrackComplete> implements TrackCompleteService {

    @Autowired
    private TrackCompleteMapper trackCompleteMapper;

    @Resource
    private TrackAssignService trackAssignService;
    @Resource
    public PublicService publicService;
    @Resource
    private TrackItemService trackItemService;
    @Resource
    private TrackAssignMapper trackAssignMapper;
    @Resource
    private TrackAssignPersonMapper trackAssignPersonMapper;
    @Resource
    private TrackCompleteCacheService trackCompleteCacheService;
    @Resource
    private TrackCompleteService trackCompleteService;
    @Resource
    private TrackHeadService trackHeadService;
    @Resource
    private TrackHeadFlowService trackFlowService;
    @Resource
    private SystemServiceClient systemServiceClient;
    @Resource
    private BaseServiceClient baseServiceClient;

    @Override
    public IPage<TrackComplete> queryPage(Page page, QueryWrapper<TrackComplete> query) {
        return trackCompleteMapper.queryPage(page, query);
    }

    @Override
    public Map<String, Object> queryTrackCompleteList(String trackNo, String startTime, String endTime, String branchCode, String workNo) {
        QueryWrapper<TrackComplete> queryWrapper = new QueryWrapper<TrackComplete>();
        if (!StringUtils.isNullOrEmpty(workNo)) {
            queryWrapper.eq("work_no", workNo);
        }
        if (!StringUtils.isNullOrEmpty(trackNo)) {
            trackNo = trackNo.replaceAll(" ", "");
            queryWrapper.apply("replace(replace(replace(track_no, char(13), ''), char(10), ''),' ', '') like '%" + trackNo + "%'");
        }
        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryWrapper.apply("UNIX_TIMESTAMP(modify_time) >= UNIX_TIMESTAMP('" + startTime + "')");
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            Calendar calendar = new GregorianCalendar();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                calendar.setTime(sdf.parse(endTime));
            } catch (ParseException e) {
                throw new GlobalException("时间格式错误", ResultCode.FAILED);
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            queryWrapper.apply("UNIX_TIMESTAMP(modify_time) <= UNIX_TIMESTAMP('" + sdf.format(calendar.getTime()) + "')");

        }
        //获取当前登录用户角色列表
        List<Role> roleList = systemServiceClient.queryRolesByUserId(SecurityUtils.getCurrentUser().getUserId());
        List<String> roleCodeList = roleList.stream().map(x -> x.getRoleCode()).collect(Collectors.toList());
//            BOMCO_ZF_JMAQ_LDGL;//领导
//            role_tenant_admin;//租户管理员
        //查询权限控制
        if (roleCodeList.contains("BOMCO_ZF_JMAQ_LDGL") || roleCodeList.contains("role_tenant_admin")) {
            if (!StringUtils.isNullOrEmpty(branchCode)) {
                queryWrapper.eq("branch_code", branchCode);
            }
        } else {
            queryWrapper.eq("user_id", SecurityUtils.getCurrentUser().getUsername());
        }
        List<TrackComplete> completes = trackCompleteService.list(queryWrapper);
        List<TrackComplete> emptyTrackComplete = new ArrayList<>();
        List<TrackComplete> dbRecords = completes;

        if (!CollectionUtils.isEmpty(dbRecords)) {
            //获取设备信息
            Set<String> deviceIds = dbRecords.stream().map(x -> x.getDeviceId()).collect(Collectors.toSet());
            List<Device> deviceByIdList = baseServiceClient.getDeviceByIdList(new ArrayList<>(deviceIds));
            Map<String, Device> deviceMap = deviceByIdList.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));
            //根据跟单id获取跟单数据
            Set<String> trackIdList = dbRecords.stream().map(x -> x.getTrackId()).collect(Collectors.toSet());
            List<TrackHead> trackHeads = trackHeadService.listByIds(new ArrayList<>(trackIdList));
            Map<String, TrackHead> trackHeadMap = trackHeads.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));
            //根据跟单工序id获取跟单工序
            Set<String> tiIdList = dbRecords.stream().map(x -> x.getTiId()).collect(Collectors.toSet());
            List<TrackItem> trackItems = trackItemService.listByIds(new ArrayList<>(tiIdList));
            Map<String, TrackItem> trackMap = trackItems.stream().collect(Collectors.toMap(x -> x.getId(), x -> x, (k, v) -> k));
            List<String> flowIdList = trackItems.stream().map(x -> x.getFlowId()).collect(Collectors.toList());
            List<TrackFlow> trackFlows = trackFlowService.listByIds(flowIdList);
            Map<String, TrackFlow> trackFlowMap = trackFlows.stream().collect(Collectors.toMap(x -> x.getId(), x -> x, (k, v) -> k));
            //根据员工分组
            Map<String, List<TrackComplete>> completesMap = completes.stream().collect(Collectors.groupingBy(TrackComplete::getUserId));
            ArrayList<String> userIdList = new ArrayList<>(completesMap.keySet());
            Map<String, TenantUserVo> stringTenantUserVoMap = systemServiceClient.queryByUserAccountList(userIdList);
            for (String id : userIdList) {
                List<TrackComplete> trackCompletes = completesMap.get(id);
                //统计每个员工
                if (!CollectionUtils.isEmpty(trackCompletes)) {
                    //总工时累计额值
                    Double sumTotalHours = 0.00;
                    //准结工时累计值
                    Double sumPrepareEndHours = 0.00;
                    //额定工时累计值
                    Double sumSinglePieceHours = 0.00;
                    TrackComplete track0 = new TrackComplete();
                    TenantUserVo tenantUserVo = stringTenantUserVoMap.get(id);
                    for (TrackComplete track : trackCompletes) {
                        //空校验
                        if (track.getPrepareEndHours() == null) {
                            track.setPrepareEndHours(0.00);
                        }
                        if (track.getSinglePieceHours() == null) {
                            track.setSinglePieceHours(0.00);
                        }
                        if (track.getCompletedQty() == null) {
                            track.setCompletedQty(0.00);
                        }
                        //计算总工时
                        sumPrepareEndHours = sumPrepareEndHours + track.getPrepareEndHours();
                        sumSinglePieceHours = sumSinglePieceHours + track.getSinglePieceHours();
                        sumTotalHours = sumTotalHours + track.getCompletedQty() * track.getSinglePieceHours() + track.getPrepareEndHours();
                        track.setTotalHours(new BigDecimal(track.getCompletedQty() * track.getSinglePieceHours() + track.getPrepareEndHours()).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());//总工时
                        track.setUserName(tenantUserVo.getEmplName());
                        track0.setUserName(tenantUserVo.getEmplName());
                        track.setDeviceName(deviceMap.get(track.getDeviceId()) == null ? "" : deviceMap.get(track.getDeviceId()).getName());
                        TrackItem trackItem = trackMap.get(track.getTiId());
                        //查询产品编号
                        TrackFlow trackFlow = trackFlowMap.get(trackItem == null ? "" : trackItem.getFlowId());
                        track.setProdNo(trackFlow == null ? "" : trackFlow.getProductNo());
                        track.setProductName(trackHeadMap.get(track.getTrackId()) == null ? "" : trackHeadMap.get(track.getTrackId()).getProductName());
                    }
                    track0.setId(id);
                    track0.setPrepareEndHours(new BigDecimal(sumPrepareEndHours).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());//准备工时
                    track0.setSinglePieceHours(new BigDecimal(sumSinglePieceHours).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());//额定工时
                    track0.setTotalHours(new BigDecimal(sumTotalHours).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());//总工时
                    track0.setUserName(tenantUserVo.getEmplName());
                    track0.setTrackCompleteList(trackCompletes);
                    //判断是否包含叶子结点
                    track0.setIsLeafNodes(trackCompletes != null && !CollectionUtils.isEmpty(trackCompletes));
                    emptyTrackComplete.add(track0);
                }
            }
        }
        List<TrackComplete> records = completes;
//            completes.setRecords(records);
        Map<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("records", records);
        stringObjectHashMap.put("TrackComplete", emptyTrackComplete);
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
            //检验车间
            trackItem.setQualityCheckBranch(completeDto.getQualityCheckBranch());
            //根据工序Id删除缓存表数据
            QueryWrapper<TrackCompleteCache> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("ti_id", completeDto.getTiId());
            double numDouble = 0.00;
            for (TrackComplete trackComplete : completeDto.getTrackCompleteList()) {
                //验证输入值是否合法
                String s = this.verifyTrackComplete(trackComplete, trackItem, companyCode);
                //如果返回值不等于空则代表验证不通过，将提示信息返回
                if (org.apache.commons.lang3.StringUtils.isNotBlank(s)) {
                    return CommonResult.failed(s);
                }

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
            trackItem.setCompleteQty(trackItem.getCompleteQty() + numDouble);
            double intervalNumber = assign.getQty() + 0.0;
            if (numDouble > assign.getQty()) {
                return CommonResult.failed("报工数量:" + numDouble + ",派工数量:" + assign.getQty() + "完工数量不得大于" + assign.getQty());
            }
            if (numDouble < intervalNumber - 0.1) {
                return CommonResult.failed("报工数量:" + numDouble + ",派工数量:" + assign.getQty() + "完工数量不得少于" + (intervalNumber - 0.1));
            }
            if (assign.getQty() >= numDouble && intervalNumber - 0.1 <= numDouble) {
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
        assign.setAssignPersons(trackAssignPersonMapper.selectList(new QueryWrapper<AssignPerson>().eq("assign_id", assign.getId())));

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
        trackItem.setQualityCheckBranch(completeDto.getQualityCheckBranch());
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
        if (numDouble < intervalNumber - 0.1) {
            return CommonResult.failed("报工数量:" + numDouble + ",派工数量:" + assign.getQty() + ",完工数量不得少于" + +(intervalNumber - 0.1));
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


    private Boolean removeComplete(String tiId) {
        QueryWrapper<TrackComplete> removeComplete = new QueryWrapper<>();
        removeComplete.eq("ti_id", tiId);
        return this.remove(removeComplete);
    }
}
