package com.richfit.mes.produce.service.heat;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.TrackCompleteMapper;
import com.richfit.mes.produce.enmus.IdEnum;
import com.richfit.mes.produce.entity.heat.HeatCompleteDto;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.*;
import org.springframework.beans.BeanUtils;
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
 * @author renzewen
 * @Description
 *
 */
@Service
public class HeatTrackCompleteServiceImpl extends ServiceImpl<TrackCompleteMapper, TrackComplete> implements HeatTrackCompleteService {

    @Resource
    private TrackAssignService trackAssignService;
    @Resource
    private TrackItemService trackItemService;
    @Resource
    private PrechargeFurnaceService prechargeFurnaceService;
    @Autowired
    private HeatTrackAssignService heatTrackAssignService;
    @Autowired
    private TrackHeadService trackHeadService;
    @Autowired
    private TrackCompleteMapper trackCompleteMapper;
    @Autowired
    private RgDeviceService rgDeviceService;
    @Autowired
    private StepHourService stepHourService;
    @Autowired
    private StepHourVerService stepHourVerService;
    @Autowired
    private SystemServiceClient systemServiceClient;
    @Resource
    private TrackHeadFlowService trackFlowService;


    @Override
    public List<TrackComplete> queryList(QueryWrapper queryWrapper) {
        return trackCompleteMapper.queryList(queryWrapper);
    }

    /**
     * 功能描述: 热工报工接口
     * @Author: renzewen
     * @param heatCompleteDto
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean saveComplete(HeatCompleteDto heatCompleteDto) throws Exception {
        PrechargeFurnace furnace = prechargeFurnaceService.getById(heatCompleteDto.getPrechargeFurnaceId());
        if(furnace.getStepStatus().equals("0")){
            throw new GlobalException("请先开工后报工！",ResultCode.FAILED);
        }
        //校验报工信息
        if (null == heatCompleteDto.getTrackCompleteList() && heatCompleteDto.getTrackCompleteList().isEmpty()) {
            throw new GlobalException("报工人员不能为空",ResultCode.FAILED);
        }
        if(ObjectUtil.isEmpty(heatCompleteDto.getTiIds()) || heatCompleteDto.getTiIds().size()==0){
            throw new GlobalException("报工工序Id不能为空",ResultCode.FAILED);
        }
        if(ObjectUtil.isEmpty(heatCompleteDto.getPrechargeFurnaceId())){
            throw new GlobalException("预装炉Id不能为空",ResultCode.FAILED);
        }
        //是否最后一道工序
        boolean isFinal = TrackComplete.IS_FINAL_STEP.equals(heatCompleteDto.getTrackCompleteList().get(0).getIsFinalStep());
        //报工信息
        TrackComplete complete = heatCompleteDto.getTrackCompleteList().get(0);
        //处理炉设备转换
        String deviceName = null;
        if(!StringUtils.isNullOrEmpty(complete.getDeviceId())){
            RgDevice rgDevice = rgDeviceService.getById(complete.getDeviceId());
            deviceName = ObjectUtil.isEmpty(rgDevice) ? null : rgDevice.getDeviceName();
        }
        //保存报工信息(给步骤分组id赋值)
        String stepGroupId = UUID.randomUUID().toString().replaceAll("-", "");
        //保存每一个工序的报工信息
        for (String tiId : heatCompleteDto.getTiIds()) {
            if(heatCompleteDto.getIsUpdate().equals(HeatCompleteDto.IS_UPDATE)){
                //修改操作
                //删除当前报工数据
                QueryWrapper<TrackComplete> completeQueryWrapper = new QueryWrapper<TrackComplete>();
                completeQueryWrapper.eq("ti_id",tiId).eq("is_current",TrackComplete.YES_IS_CURRENT);
                this.remove(completeQueryWrapper);
            }else{
                //新增操作
                //把之前的当前报工数据修改为非当前报工数据
                UpdateWrapper<TrackComplete> trackCompleteUpdateWrapper = new UpdateWrapper<>();
                trackCompleteUpdateWrapper.eq("ti_id",tiId).set("is_current",TrackComplete.NO_IS_CURRENT);
                this.update(trackCompleteUpdateWrapper);
            }
            TrackItem trackItem = trackItemService.getById(tiId);
            //派工信息
            Assign assign = trackAssignService.list(new QueryWrapper<Assign>().eq("ti_id", tiId)).get(0);

            for (TrackComplete trackComplete : heatCompleteDto.getTrackCompleteList()) {
                trackComplete.setId(null);
                trackComplete.setAssignId(assign.getId());
                trackComplete.setTiId(tiId);
                trackComplete.setTrackId(trackItem.getTrackHeadId());
                trackComplete.setTrackNo(assign.getTrackNo());
                trackComplete.setProdNo(trackItem.getProductNo());
                trackComplete.setCompleteBy(SecurityUtils.getCurrentUser().getUsername());
                trackComplete.setCompletedQty(Double.parseDouble(String.valueOf(assign.getQty())));
                trackComplete.setCompleteTime(new Date());
                trackComplete.setDetectionResult("-");
                trackComplete.setDeviceName(deviceName);
                trackComplete.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                trackComplete.setBranchCode(heatCompleteDto.getBranchCode());
                //设置为当前工序
                trackComplete.setIsCurrent(TrackComplete.YES_IS_CURRENT);
                //预装炉id
                trackComplete.setPrechargeFurnaceId(heatCompleteDto.getPrechargeFurnaceId());
                //步骤分组id
                trackComplete.setStepGroupId(stepGroupId);
            }
            //最后一道工序需要激活下工序
            if(isFinal){
                //检验人
                trackItem.setQualityCheckBy(complete.getQualityCheckBy());
                //检验车间
                trackItem.setQualityCheckBranch(complete.getQualityCheckBranch());
                nextItemActivate(tiId, trackItem, assign);
            }else{
                trackItem.setIsDoing(1);
                trackItemService.updateById(trackItem);
                //派工状态设置在制
                assign.setState(1);
                trackAssignService.updateById(assign);
            }
            //保存报工信息
            this.saveBatch(heatCompleteDto.getTrackCompleteList());
        }
        //计算工时
        calculationHour(stepGroupId);
        //更新设置预装炉信息
        completeUpdatePreChargeFurnaceInfo(heatCompleteDto, isFinal);
        return true;
    }

    /**
     * 编辑报工
     * @param trackCompleteList
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateComplete(List<TrackComplete> trackCompleteList){
        //校验报工信息
        if (null == trackCompleteList && trackCompleteList.isEmpty()) {
            throw new GlobalException("报工人员不能为空",ResultCode.FAILED);
        }
        //报工工序ids
        TrackComplete complete = trackCompleteList.get(0);
        String stepGroupId = complete.getStepGroupId();
        QueryWrapper<TrackComplete> wrapper = new QueryWrapper<>();
        wrapper.eq("step_group_id",stepGroupId);
        List<TrackComplete> completes = trackCompleteMapper.queryList(wrapper);
        Set<String> tiIds = completes.stream().map(TrackComplete::getTiId).collect(Collectors.toSet());
        //删除旧的
        trackCompleteMapper.delete(wrapper);
        //保存新的数据
        for (String tiId : tiIds) {
            TrackItem trackItem = trackItemService.getById(tiId);
            //派工信息
            Assign assign = trackAssignService.list(new QueryWrapper<Assign>().eq("ti_id", tiId)).get(0);

            for (TrackComplete trackComplete : trackCompleteList) {
                trackComplete.setId(null);
                trackComplete.setAssignId(assign.getId());
                trackComplete.setTiId(tiId);
                trackComplete.setTrackId(trackItem.getTrackHeadId());
                trackComplete.setTrackNo(trackItem.getTrackNo());
                trackComplete.setProdNo(trackItem.getProductNo());
                trackComplete.setCompleteBy(SecurityUtils.getCurrentUser().getUsername());
                trackComplete.setCompleteTime(new Date());
                trackComplete.setDetectionResult("-");
                trackComplete.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            }
            this.saveBatch(trackCompleteList);
        }
        //计算工时
        this.calculationHour(stepGroupId);
        return true;
    }

    /**
     * 功能描述: 最后一步需要激活下工序，并且对下工序派工
     * @Author: renzewen
     * @param tiId
     * @param trackItem
     * @param assign
     * @throws Exception
     */
    private void nextItemActivate(String tiId, TrackItem trackItem, Assign assign) throws Exception {
        //跟新工序完成数量(热工不涉及拆分默认全部派工和报工  派工全部派  报工也是全部报工)
        trackItem.setCompleteQty(Double.parseDouble(String.valueOf(assign.getQty())));
        //最后一次报工进行下工序装炉
        //更改状态 标识当前工序完成
        trackItem.setIsDoing(2);
        trackItem.setIsOperationComplete(1);
        trackItem.setOperationCompleteTime(new Date());
        //此工序完工
        trackItemService.updateById(trackItem);
        //派工状态设置为完成
        assign.setState(2);
        trackAssignService.updateById(assign);
        //调用工序激活方法
        Map<String, String> map = new HashMap<>(3);
        map.put(IdEnum.FLOW_ID.getMessage(), trackItem.getFlowId());
        map.put(IdEnum.TRACK_HEAD_ID.getMessage(), trackItem.getTrackHeadId());
        map.put(IdEnum.TRACK_ITEM_ID.getMessage(), tiId);
        map.put(IdEnum.ASSIGN_ID.getMessage(), assign.getId());
        this.activationProcess(map,assign);
    }

    /**
     * 功能描述: 激活工序
     *
     * @Author: renzewen
     * @Date: 2023/1/10 10:08
     **/
    @Transactional(rollbackFor = Exception.class)
    public Boolean activationProcess(Map<String, String> map,Assign assign) throws Exception {
        //倒序获取工序列表
        QueryWrapper<TrackItem> currentTrackItem = new QueryWrapper<>();
        currentTrackItem.eq("flow_id", map.get("flowId"));
        currentTrackItem.eq("is_current", 1);
        currentTrackItem.orderByDesc("sequence_order_by");
        List<TrackItem> currentTrackItemList = trackItemService.list(currentTrackItem);
        //判断还有没有下工序
        if (currentTrackItemList.get(0).getNextOptSequence() == 0) {
            trackHeadService.trackHeadFinish(map.get("flowId"));
            return true;
        }
        //判断所有并行工序是否全部完成
        for (TrackItem trackItem : currentTrackItemList) {
            if (2 != trackItem.getIsDoing()) {
                return false;
            }
        }
        for (TrackItem trackItem : currentTrackItemList) {
            trackItem.setIsCurrent(0);
            trackItemService.updateById(trackItem);
        }
        boolean activation = false;
        if (!currentTrackItemList.isEmpty() && currentTrackItemList.get(0).getNextOptSequence() != 0) {
            //激活下工序
            activation = activation(currentTrackItemList.get(0),assign);
        }
        return activation;
    }

    /**
     * 功能描述: 激活
     *
     * @Author: renzewen
     * @Date: 2023/1/10 15:08
     **/
    private boolean activation(TrackItem trackItem,Assign assign) throws Exception {
        //激活下工序
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("flow_id", trackItem.getFlowId());
        queryWrapper.eq("original_opt_sequence", trackItem.getNextOptSequence());
        List<TrackItem> trackItemList = trackItemService.list(queryWrapper);
        boolean update = false;
        for (TrackItem trackItemEntity : trackItemList) {
            trackItemEntity.setIsCurrent(1);
            trackItemEntity.setModifyTime(new Date());
            update = trackItemService.updateById(trackItemEntity);
            //下工序派工(继承上工序的派工信息)
            Assign nextItemAssign = new Assign();
            BeanUtils.copyProperties(assign,nextItemAssign,new String[]{"id"});
            nextItemAssign.setTiId(trackItemEntity.getId());
            heatTrackAssignService.assignItem(nextItemAssign);
        }
        return update;
    }

    /**
     * 功能描述: 步骤报工后，预装炉信息跟新
     * @Author: renzewen
     * @param heatCompleteDto  报工信息
     * @param isFinal  是否最后一步骤
     */
    private void completeUpdatePreChargeFurnaceInfo(HeatCompleteDto heatCompleteDto, boolean isFinal) {
        PrechargeFurnace prechargeFurnace = prechargeFurnaceService.getById(heatCompleteDto.getPrechargeFurnaceId());
        String currStep = prechargeFurnace.getCurrStep();
        prechargeFurnace.setDealFurnace(heatCompleteDto.getTrackCompleteList().get(0).getDeviceName());
        //上工步
        prechargeFurnace.setUpStep(currStep);
        prechargeFurnace.setCurrStep(heatCompleteDto.getTrackCompleteList().get(0).getStep());
        prechargeFurnace.setFurnaceNo(heatCompleteDto.getTrackCompleteList().get(0).getFurnaceNo());
        prechargeFurnace.setDealFurnace(heatCompleteDto.getTrackCompleteList().get(0).getDeviceName());
        if(!isFinal){
            prechargeFurnace.setStatus(PrechargeFurnace.YES_START_WORK);
            //工步状态设为未开工
            prechargeFurnace.setStepStatus(PrechargeFurnace.NO_START_WORK);
        }else{
            //完工
            prechargeFurnace.setStatus(PrechargeFurnace.END_START_WORK);
            prechargeFurnace.setStepStatus(PrechargeFurnace.END_START_WORK);
        }
        //次数字段赋值
        int number = getNumber(prechargeFurnace.getId(),prechargeFurnace.getCurrStep());
        prechargeFurnace.setNumber(String.valueOf(number));

        prechargeFurnaceService.updateById(prechargeFurnace);
    }

    //同预装炉 相同步骤报工次数计算
    private int getNumber(Long fuId,String step) {
        QueryWrapper<TrackComplete> trackCompleteQueryWrapper = new QueryWrapper<>();
        trackCompleteQueryWrapper.eq("precharge_furnace_id",fuId)
                .eq("step",step);
        List<TrackComplete> list = this.list(trackCompleteQueryWrapper);
        Map<String, List<TrackComplete>> itemCompleteMap = list.stream().collect(Collectors.groupingBy(item->item.getStepGroupId()));

        return itemCompleteMap.keySet().size();
    }


    /**
     * 功能描述: 热工报工开工
     * 预装炉id
     * @param prechargeFurnaceId
     */
    @Override
    public boolean startWork(String prechargeFurnaceId){
        PrechargeFurnace prechargeFurnace= prechargeFurnaceService.getById(prechargeFurnaceId);
        if(prechargeFurnace.getStatus().equals(PrechargeFurnace.END_START_WORK)){
            throw new GlobalException("工序已完工无法再开工",ResultCode.FAILED);
        }
        prechargeFurnace.setStatus(PrechargeFurnace.YES_START_WORK);
        prechargeFurnace.setStepStatus(PrechargeFurnace.YES_START_WORK);
        prechargeFurnace.setStartWorkBy(SecurityUtils.getCurrentUser().getUserId());
        return prechargeFurnaceService.updateById(prechargeFurnace);
    }

    /**
     * 根据预装炉id获取当前步骤的报工信息
     * @param prechargeFurnaceId
     * @return
     */
    @Override
    public Map<String,Object> getCompleteInfoByFuId(String prechargeFurnaceId){
        Map<String, Object> returnMap = new HashMap<>();

        QueryWrapper<TrackComplete> trackCompleteQueryWrapper = new QueryWrapper<>();
        trackCompleteQueryWrapper.eq("precharge_furnace_id",prechargeFurnaceId)
                .eq("is_current","1");
        List<TrackComplete> completeList = this.list(trackCompleteQueryWrapper);

        Map<String, List<TrackComplete>> groups = completeList.stream().collect(Collectors.groupingBy(TrackComplete::getTiId));
        //报工信息
        returnMap.put("completeInfo",completeList.get(0));
        //人员信息
        for (Map.Entry<String, List<TrackComplete>> completePeoples : groups.entrySet()) {
            if(!ObjectUtil.isEmpty(completePeoples)){
                returnMap.put("completePeoples",completePeoples);
                break;
            }
        }

        return returnMap;
    }


    /**
     * 步骤工时计算  工序标准工时* 步骤工时标准比列 / 预装炉步骤次数 / 报工人数
     * @param stepGroupId
     * @return
     */
    public void calculationHour(String stepGroupId){
        QueryWrapper<TrackComplete> wrapper = new QueryWrapper<>();
        wrapper.eq("step_group_id",stepGroupId);
        List<TrackComplete> list = trackCompleteMapper.queryList(wrapper);
        //报工步骤
        if(list.size() == 0){
            throw new GlobalException("当前步骤没有对应的报工信息，无法计算工时！",ResultCode.FAILED);
        }
        String stepName = list.get(0).getStep();

        //判断使用哪个步骤工时标准
        String stepType = "多步骤无保温";
        String fuId = list.get(0).getPrechargeFurnaceId();
        List<TrackComplete> trackCompletes = this.list(new QueryWrapper<TrackComplete>().eq("precharge_furnace_id", fuId));
        List<String> stepNameList = trackCompletes.stream().map(TrackComplete::getStep).collect(Collectors.toList());
        if(stepNameList.contains("保温")){
            stepType = "多步骤有保温";
        }

        PrechargeFurnace fuInfo = prechargeFurnaceService.getById(fuId);
        //该步骤的报工次数
        int number = this.getNumber(fuInfo.getId(), stepName);

        //查询当前激活的步骤工时标准版本
        List<StepHourVer> stepHourVer = stepHourVerService.list(new QueryWrapper<StepHourVer>().eq("is_activate", StepHourVer.YES_ACTIVATE));
        if(stepHourVer.size() == 0){
            throw new GlobalException("当前没有激活的工时标准版本，无法报工时！",ResultCode.FAILED);
        }
        stepName = ("保温".equals(stepName) || "装炉".equals(stepName) || "出炉".equals(stepName) || "校直".equals(stepName))?stepName:"工序";
        List<StepHour> stepHours = stepHourService.list(new QueryWrapper<StepHour>().eq("ver_id", stepHourVer.get(0).getId()).eq("step_type", stepType).eq("step_name",stepName));
        if(stepHours.size()>0){
            QueryWrapper<TrackComplete> trackCompleteQueryWrapper = new QueryWrapper<>();
            trackCompleteQueryWrapper.eq("precharge_furnace_id",fuId)
                    .eq("step",stepName);
            //修改报工信息的工时
            Map<String, List<TrackComplete>> itemMap = this.queryList(trackCompleteQueryWrapper).stream().collect(Collectors.groupingBy(item->item.getStepGroupId()+"_"+item.getTiId()));
            //修改后的报工信息
            List<TrackComplete> updateCompletes = new ArrayList<>();

            itemMap.forEach((key,value)->{
                //该步骤报工人数
                int poepleNumber = value.size();
                BigDecimal stepHour = new BigDecimal(stepHours.get(0).getHourRatio());
                for (TrackComplete complete : value) {
                    //工序标准工时
                    BigDecimal hour = complete.getHeatHour();
                    //报工工时
                    if(hour==null || hour.compareTo(new BigDecimal(0))==0){
                        complete.setCompletedHours(0.0);
                        complete.setActualHours(0.0);
                        complete.setReportHours(0.0);
                        complete.setStaticHours(0.0);
                    }else{
                        BigDecimal completeHour = hour.multiply(stepHour).divide(new BigDecimal(number)).divide(new BigDecimal(poepleNumber)).setScale(2, BigDecimal.ROUND_HALF_UP);
                        complete.setCompletedHours(Double.parseDouble(String.valueOf(completeHour)));
                        complete.setActualHours(Double.parseDouble(String.valueOf(completeHour)));
                        complete.setReportHours(Double.parseDouble(String.valueOf(completeHour)));
                        complete.setStaticHours(Double.parseDouble(String.valueOf(completeHour)));
                    }
                    updateCompletes.add(complete);
                }
            });
            this.updateBatchById(updateCompletes);
            //throw new GlobalException("当前激活的步骤工时版本中没有对应的步骤工时分配比例，无法报工时！",ResultCode.FAILED);
        }


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
                calendar.setTime(sdf.parse(endTime + " 00:00:00"));
            } catch (ParseException e) {
                e.printStackTrace();
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
        if (roleCodeList.toString().contains("_LDGL") || roleCodeList.toString().contains("_TJ") || roleCodeList.contains("role_tenant_admin")) {
            if (!StringUtils.isNullOrEmpty(branchCode)) {
                queryWrapper.eq("branch_code", branchCode);
            }
        } else {
            queryWrapper.eq("user_id", SecurityUtils.getCurrentUser().getUsername());
        }
        List<TrackComplete> completes = this.list(queryWrapper);
        List<TrackComplete> emptyTrackComplete = new ArrayList<>();
        List<TrackComplete> dbRecords = completes;

        if (!CollectionUtils.isEmpty(dbRecords)) {
            //根据跟单id获取跟单数据
            Set<String> trackIdList = dbRecords.stream().map(x -> x.getTrackId()).collect(Collectors.toSet());
            List<TrackHead> trackHeads = trackHeadService.listByIds(new ArrayList<>(trackIdList));
            Map<String, TrackHead> trackHeadMap = trackHeads.stream().collect(Collectors.toMap(x -> x.getId(), x -> x));
            //根据跟单工序id获取跟单工序
            Set<String> tiIdList = dbRecords.stream().map(x -> x.getTiId()).collect(Collectors.toSet());
            List<TrackItem> trackItems = trackItemService.listByIds(new ArrayList<>(tiIdList));
            //只获取数据计算工时
            Map<String, TrackItem> trackMap = trackItems.stream().collect(Collectors.toMap(x -> x.getId(), x -> x, (k, v) -> k));
            List<String> flowIdList = trackItems.stream().map(x -> x.getFlowId()).collect(Collectors.toList());
            List<TrackFlow> trackFlows = trackFlowService.listByIds(flowIdList);
            Map<String, TrackFlow> trackFlowMap = trackFlows.stream().collect(Collectors.toMap(x -> x.getId(), x -> x, (k, v) -> k));
            //根据员工分组
            Map<String, List<TrackComplete>> completesMap = completes.stream().filter(complete -> StrUtil.isNotBlank(complete.getUserId())).collect(Collectors.groupingBy(TrackComplete::getUserId));
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
                        //根据跟单工序id获取跟单工序
                        TrackItem trackItem = trackMap.get(track.getTiId());
                        if (null == trackItem) {
                            continue;
                        }
                        //查询产品编号
                        TrackFlow trackFlow = trackFlowMap.get(trackItem == null ? "" : trackItem.getFlowId());
                        track.setProdNo(trackFlow == null ? "" : trackFlow.getProductNo());
                        track.setProductName(trackHeadMap.get(track.getTrackId()) == null ? "" : trackHeadMap.get(track.getTrackId()).getProductName());
                        //空校验
                        if (trackItem.getPrepareEndHours() == null) {
                            trackItem.setPrepareEndHours(0.00);
                            track.setPrepareEndHours(0.00);
                        } else {
                            track.setPrepareEndHours(track.getCompletedHours());
                        }
                        if (trackItem.getSinglePieceHours() == null) {
                            trackItem.setSinglePieceHours(0.00);
                            track.setSinglePieceHours(0.00);
                        } else {
                            track.setSinglePieceHours(track.getCompletedHours());
                        }
                        if (track.getCompletedQty() == null) {
                            track.setCompletedQty(0.00);
                        }
                        //计算总工时
                        sumPrepareEndHours = sumPrepareEndHours + trackItem.getPrepareEndHours();
                        sumSinglePieceHours = sumSinglePieceHours + trackItem.getSinglePieceHours();
                        sumTotalHours = sumTotalHours + track.getCompletedQty() * (ObjectUtil.isEmpty(track.getCompletedHours())?0.0:track.getCompletedHours());
                        track.setTotalHours(new BigDecimal(track.getCompletedQty() * (ObjectUtil.isEmpty(track.getCompletedHours())?0.0:track.getCompletedHours())).setScale(4, BigDecimal.ROUND_HALF_UP).doubleValue());//总工时
                        track.setUserName(tenantUserVo.getEmplName());
                        track0.setUserName(tenantUserVo.getEmplName());
                        track.setWorkNo(trackHeadMap.get(track.getTrackId()) == null ? "" : trackHeadMap.get(track.getTrackId()).getWorkNo());
                        track.setTrackNo(trackHeadMap.get(track.getTrackId()) == null ? "" : trackHeadMap.get(track.getTrackId()).getTrackNo());
                        track.setOptSequence(trackItem.getOptSequence());
                        track.setOptName(trackItem.getOptName());
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
        Map<String, Object> stringObjectHashMap = new HashMap<>();
        stringObjectHashMap.put("records", records);
        stringObjectHashMap.put("TrackComplete", emptyTrackComplete);
        return stringObjectHashMap;
    }


}
