package com.richfit.mes.produce.service.heat;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.TrackCompleteMapper;
import com.richfit.mes.produce.enmus.IdEnum;
import com.richfit.mes.produce.entity.heat.HeatCompleteDto;
import com.richfit.mes.produce.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
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
    public PublicService publicService;
    @Resource
    private TrackItemService trackItemService;
    @Resource
    private PrechargeFurnaceService prechargeFurnaceService;
    @Autowired
    private HeatTrackAssignService heatTrackAssignService;
    @Autowired
    private TrackHeadService trackHeadService;


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
                trackComplete.setTrackNo(trackItem.getTrackNo());
                trackComplete.setProdNo(trackItem.getProductNo());
                trackComplete.setCompleteBy(SecurityUtils.getCurrentUser().getUsername());
                trackComplete.setCompleteTime(new Date());
                trackComplete.setDetectionResult("-");
                trackComplete.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
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
            }
            //保存报工信息
            this.saveBatch(heatCompleteDto.getTrackCompleteList());
        }
        //更新设置预装炉信息
        completeUpdatePreChargeFurnaceInfo(heatCompleteDto, isFinal);
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
        QueryWrapper<TrackComplete> trackCompleteQueryWrapper = new QueryWrapper<>();
        trackCompleteQueryWrapper.eq("precharge_furnace_id",prechargeFurnace.getId())
                .eq("step",prechargeFurnace.getCurrStep());
        List<TrackComplete> list = this.list(trackCompleteQueryWrapper);
        Map<String, List<TrackComplete>> itemCompleteMap = list.stream().collect(Collectors.groupingBy(TrackComplete::getTiId));
        int number = 0;
        for (Map.Entry<String, List<TrackComplete>> itemCompleteEntry : itemCompleteMap.entrySet()) {
            if(!ObjectUtil.isEmpty(itemCompleteEntry)){
                for (TrackComplete trackComplete : itemCompleteEntry.getValue()) {
                    if(trackComplete.getStep()==currStep){
                        number+=1;
                    }
                }
            }
        }
        prechargeFurnace.setNumber(String.valueOf(number));

        prechargeFurnaceService.updateById(prechargeFurnace);
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


}
