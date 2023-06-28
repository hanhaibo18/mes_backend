package com.richfit.mes.produce.service.heat;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.model.util.OptNameUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.*;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.*;
import com.richfit.mes.produce.utils.BeanUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

/**
 * @Author: zhiqiang.lu
 * @Date: 2023.1.4
 */
@Slf4j
@Service
@Transactional(rollbackFor = Exception.class)
public class PrechargeFurnaceServiceImpl extends ServiceImpl<PrechargeFurnaceMapper, PrechargeFurnace> implements PrechargeFurnaceService {

    @Autowired
    private TrackHeadMapper trackHeadMapper;

    @Autowired
    private PrechargeFurnaceAssignMapper prechargeFurnaceAssignMapper;

    @Autowired
    private TrackItemService trackItemService;

    @Autowired
    private SystemServiceClient systemServiceClient;

    @Autowired
    private TrackHeadFlowService trackHeadFlowService;

    @Autowired
    private TrackFlowMapper trackFlowMapper;

    @Autowired
    private TrackHeadService trackHeadService;

    @Autowired
    private TrackItemMapper trackItemMapper;

    @Autowired
    public TrackAssignMapper trackAssignMapper;

    @Autowired
    private BaseServiceClient baseServiceClient;

    @Autowired
    private PrechargeFurnaceMapper prechargeFurnaceMapper;

    @Autowired
    private TrackCompleteService trackCompleteService;

    @Autowired
    private TrackCompleteMapper trackCompleteMapper;

    @Autowired
    private TrackAssignService trackAssignService;

    @Autowired
    private TrackAssignPersonMapper assignPersonMapper;

    @Override
    public void furnaceCharging(List<Assign> assignList, String tempWork) {
        if (assignList.isEmpty()) {
            throw new GlobalException("必须要有装炉的工序", ResultCode.FAILED);
        }
        PrechargeFurnace prechargeFurnace = new PrechargeFurnace();
        prechargeFurnace.setTempWork(tempWork);
        prechargeFurnace.setOptName(optNames(assignList));
        prechargeFurnace.setSiteId(assignList.get(0).getSiteId());
        prechargeFurnace.setTypeCode(assignList.get(0).getTypeCode());
        this.save(prechargeFurnace);
        for (Assign assign : assignList) {
            //跟单工序添加装炉id
            TrackItem trackItem = trackItemService.getById(assign.getTiId());
            trackItem.setPrechargeFurnaceId(prechargeFurnace.getId());
            trackItemService.updateById(trackItem);
        }
    }

    /**
     * 冶炼配炉 根据材质分类合计钢水重量列表
     * @return
     */
    @Override
    public List totalWeightMolten(String branchCode){
        List<Map> returnMap = new ArrayList<>();
        QueryWrapper<TrackItem> trackItemQueryWrapper = new QueryWrapper<>();
        trackItemQueryWrapper.eq("branch_code",branchCode);
        trackItemQueryWrapper.eq("tenant_id",SecurityUtils.getCurrentUser().getTenantId());
        List<TrackItem> pageAssignsHot = trackAssignMapper.getPageAssignsHot(new QueryWrapper<>());
        Map<String, List<TrackItem>> map = pageAssignsHot.stream().collect(Collectors.groupingBy(item -> item.getTexture()));
        for (String s : map.keySet()) {
            Double weightMolten = map.get(s).stream().collect(Collectors.summingDouble(item -> Double.parseDouble(item.getWeightMolten())));
            Map<String, Object> addMap = new HashMap<>();
            addMap.put("weightMolten",weightMolten);
            addMap.put("texture",s);
            returnMap.add(addMap);
        }
        return returnMap;
    }

    /**
     * 冶炼配炉 根据材质查询派工列表
     * @return
     */
    @Override
    public List<TrackItem> queryAssignByTexture(String texture,String branchCode){
        QueryWrapper<TrackItem> trackItemQueryWrapper = new QueryWrapper<>();
        trackItemQueryWrapper.eq("texture",texture);
        trackItemQueryWrapper.eq(!StringUtils.isNotEmpty(branchCode),"branch_code",branchCode);
        trackItemQueryWrapper.eq("tenant_id",SecurityUtils.getCurrentUser().getTenantId());
        return trackAssignMapper.getPageAssignsHot(trackItemQueryWrapper);
    }

    @Override
    public List<TrackItem> getItemsByPrechargeFurnace(Long id) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_current", 1)
                .eq("precharge_furnace_id", id);
        return trackItemService.ylItemListSetRouterInfo(trackItemService.getTrackItemList(queryWrapper));
    }

    /**
     *
     * @param assignList
     * @param texture   材质
     */
    @Override
    public void furnaceChargingHot(List<Assign> assignList, String texture, String branchCode, String workblankType,String classes) {
        if (assignList.isEmpty()) {
            throw new GlobalException("必须要有装炉的工序", ResultCode.FAILED);
        }
        PrechargeFurnace prechargeFurnace = new PrechargeFurnace();
        prechargeFurnace.setOptName(optNames(assignList));
        prechargeFurnace.setSiteId(assignList.get(0).getSiteId());
        prechargeFurnace.setTypeCode(assignList.get(0).getTypeCode());
        prechargeFurnace.setTexture(texture);
        prechargeFurnace.setRecordStatus("0");
        prechargeFurnace.setBranchCode(Optional.ofNullable(branchCode).orElse(""));
        prechargeFurnace.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        prechargeFurnace.setWorkblankType(Optional.ofNullable(workblankType).orElse(""));
        //冶炼车间需要进行调度一致
        if ("7".equals(classes)) {
            prechargeFurnace.setIsExistScheduleCheck(checkExistSchedule(assignList));
        }
        //数量赋值
        int num = 0;
        double weightMolten = 0;
        for (Assign assign : assignList) {
            num += ObjectUtil.isAllEmpty(assign.getNumber())?0:assign.getNumber();
            weightMolten+= ObjectUtil.isAllEmpty(assign.getWeightMolten())?0:Double.parseDouble(assign.getWeightMolten());
        }
        prechargeFurnace.setNum(num);
        //钢水总计
        prechargeFurnace.setTotalMoltenSteel(weightMolten);

        this.save(prechargeFurnace);
        for (Assign assign : assignList) {
            //跟单工序添加装炉id
            //跟单工序添加装炉id
            TrackItem trackItem = trackItemService.getById(assign.getTiId());
            trackItem.setPrechargeFurnaceId(prechargeFurnace.getId());
            prechargeFurnace.setOptType(trackItem.getOptType());
            trackItemService.updateById(trackItem);
        }
        this.updateById(prechargeFurnace);
    }

    private Integer checkExistSchedule(List<Assign> assignList) {
        if (assignList == null || assignList.size() == 0) {
            return null;
        }
        Set<Integer> isExistSchedule = new HashSet<>();
        Integer name = null;
        for (Assign assign : assignList) {
            //拼接工序名称
            name = trackItemMapper.selectOne(new LambdaQueryWrapper<TrackItem>().eq(TrackItem::getId, assign.getTiId())).getIsExistScheduleCheck();
            isExistSchedule.add(name);
        }
        if (isExistSchedule.size() != 1) {
            throw new GlobalException("同一炉，存在调度确认状态不一致，请在跟单管理更改调度确认状态！", ResultCode.FAILED);
        }
        return name;
    }


    @Override
    public List<Assign> queryTrackItem(Long id) {
        QueryWrapper<Assign> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("u.precharge_furnace_id", id);
        List<Assign> assigns = trackAssignMapper.queryListAssignTrackStore(queryWrapper);
        //下料规格赋值
        for (Assign assign : assigns) {
            Router router = baseServiceClient.getByRouterId(assign.getRouterId(), assign.getBranchCode()).getData();
            if (!ObjectUtil.isEmpty(router)){
                //下料规格
                assign.setBlankSpecifi(router.getBlankSpecifi());
            }
            TrackItem trackItem = trackItemService.getById(assign.getTiId());
            //条件一 需要质检 并且已质检
            if (1 == trackItem.getIsExistQualityCheck() && 1 == trackItem.getIsQualityComplete()) {
                assign.setIsUpdate(1);
                continue;
            }
            //条件二 需要调度 并且以调度
            if (1 == trackItem.getIsExistScheduleCheck() && 1 == trackItem.getIsScheduleComplete()) {
                assign.setIsUpdate(1);
                continue;
            }
            //条件三 不质检 不调度
            if (0 == trackItem.getIsExistQualityCheck() && 0 == trackItem.getIsExistScheduleCheck()) {
                assign.setIsUpdate(1);
                continue;
            }
            //条件四 当前操作人不是开工人
            if (!SecurityUtils.getCurrentUser().getUsername().equals(trackItem.getStartDoingUser())) {
                assign.setIsUpdate(1);
                continue;
            }
            if (null == assign.getIsUpdate()) {
                assign.setIsUpdate(0);
            }
        }
        //设置工艺数据
        if (CollectionUtils.isNotEmpty(assigns)){
            setRouter(assigns);
        }
        return assigns;
    }

    @Override
    public void updateItemInfo(Long id) {
        QueryWrapper<TrackItem> trackItemQueryWrapper = new QueryWrapper<>();
        trackItemQueryWrapper.eq("precharge_furnace_id", id);
        List<TrackItem> list = trackItemService.list(trackItemQueryWrapper);
        for (TrackItem trackItem : list) {
            LambdaUpdateWrapper<TrackItem> trackItemLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
            trackItemLambdaUpdateWrapper.eq(TrackItem::getId, trackItem.getId());
            trackItemLambdaUpdateWrapper.set(TrackItem::getPrechargeFurnaceId, null);
            trackItemService.update(trackItemLambdaUpdateWrapper);
        }
    }

    /**
     * 设置工艺数据
     * @param assigns
     */
    private void setRouter(List<Assign> assigns) {
        List<String> routerIdList = assigns.stream().map(x -> x.getRouterId()).collect(Collectors.toList());
        //根据需求图号查询工艺库
        List<String> routerIdAndBranchCodeList =new ArrayList<>(assigns.stream().map(x -> x.getRouterId() + "_" + x.getBranchCode()).collect(Collectors.toSet()));
        CommonResult<List<Router>> byDrawNo = baseServiceClient.getRouterByIdAndBranchCode(routerIdAndBranchCodeList);
        //工艺库数据
        Map<String, Router> routerMap = byDrawNo.getData().stream().collect(Collectors.toMap(x -> x.getId(), x -> x));
        for (Assign assign : assigns) {
            Router router = routerMap.get(assign.getRouterId());
            if (ObjectUtils.isNotEmpty(router)) {
                //设置一系列重量
                assign.setPieceWeight(String.valueOf(router.getWeight()));
                assign.setWeightMolten(router.getWeightMolten());
                assign.setForgWeight(router.getForgWeight());
            }
        }
    }

    @Override
    public PrechargeFurnace addTrackItem(List<Assign> assignList) {
        //预装炉未开工状态
        if (assignList.isEmpty()) {
            throw new GlobalException("必须要选择添加预装炉的工序", ResultCode.FAILED);
        }
        PrechargeFurnace prechargeFurnace = this.getById(assignList.get(0).getPrechargeFurnaceId());
        if (!PrechargeFurnace.STATE_WKG.equals(prechargeFurnace.getStatus())) {
            throw new GlobalException("只能添加未开工的预装炉的工序", ResultCode.FAILED);
        }
        for (Assign assign : assignList) {
            UpdateWrapper<TrackItem> updateWrapper = new UpdateWrapper();
            updateWrapper.eq("id", assign.getTiId());
            updateWrapper.set("precharge_furnace_id", assign.getPrechargeFurnaceId());
            trackItemService.update(updateWrapper);
        }
        prechargeFurnace.setOptName(optNames(this.queryTrackItem(prechargeFurnace.getId())));

        this.updateById(prechargeFurnace);
        return prechargeFurnace;
    }

    /**
     *
     * @param assignList
     * @return
     */
    @Override
    public PrechargeFurnace addTrackItemHot(List<Assign> assignList) {
        //预装炉未开工状态
        if (assignList.isEmpty()) {
            throw new GlobalException("必须要选择添加预装炉的工序", ResultCode.FAILED);
        }
        PrechargeFurnace prechargeFurnace = this.getById(assignList.get(0).getPrechargeFurnaceId());
        if (!PrechargeFurnace.STATE_WKG.equals(prechargeFurnace.getStatus())) {
            throw new GlobalException("只能添加未开工的预装炉的工序", ResultCode.FAILED);
        }
        for (Assign assign : assignList) {
            UpdateWrapper<TrackItem> updateWrapper = new UpdateWrapper();
            updateWrapper.eq("id", assign.getTiId());
            updateWrapper.set("precharge_furnace_id", assign.getPrechargeFurnaceId());
            trackItemService.update(updateWrapper);
        }
        prechargeFurnace.setOptName(optNames(this.queryTrackItem(prechargeFurnace.getId())));
        //数量和钢水重量赋值
        int num = 0;
        double totalMoltenSteel = 0.0;
        for (Assign assign : assignList) {
            num+=ObjectUtil.isEmpty(assign.getNumber())?0:assign.getNumber();
            totalMoltenSteel+=StringUtils.isEmpty(assign.getWeightMolten())?0.0:Double.parseDouble(assign.getWeightMolten());
        }
        if(!ObjectUtil.isEmpty(prechargeFurnace.getNum()) && num>0){
            prechargeFurnace.setNum(prechargeFurnace.getNum()+num);
        }
        if(!ObjectUtil.isEmpty(prechargeFurnace.getTotalMoltenSteel()) && totalMoltenSteel>0){
            prechargeFurnace.setTotalMoltenSteel(prechargeFurnace.getTotalMoltenSteel()+totalMoltenSteel);
        }
        this.updateById(prechargeFurnace);
        return prechargeFurnace;
    }

    @Override
    public PrechargeFurnace addTrackItemHotYl(List<Assign> assignList) {
        //预装炉未开工状态
        if (assignList.isEmpty()) {
            throw new GlobalException("必须要选择添加预装炉的工序", ResultCode.FAILED);
        }
        PrechargeFurnace prechargeFurnace = this.getById(assignList.get(0).getPrechargeFurnaceId());
        //未派工的配炉，只更改工序的配炉信息；
        for (Assign assign : assignList) {
            UpdateWrapper<TrackItem> updateWrapper = new UpdateWrapper();
            updateWrapper.eq("id", assign.getTiId());
            updateWrapper.set("precharge_furnace_id", assign.getPrechargeFurnaceId());
            trackItemService.update(updateWrapper);
        }
        //工序信息过滤
        prechargeFurnace.setOptName(optNames(assignList));
        //设置工艺信息
        this.setRouter(assignList);
        //根据毛坯类型执行相应流程
        if ("1".equals(prechargeFurnace.getWorkblankType())) {
            if (PrechargeFurnace.END_START_WORK.equals(prechargeFurnace.getStatus()) ||
                    !"炼钢".equals(prechargeFurnace.getOptName())) {
                throw new GlobalException("只能在炼钢工序切未报工才可进行添加", ResultCode.FAILED);
            }
            //铸件配炉
            this.executeFoundryAddZj(assignList, prechargeFurnace);
        }
        if ("2".equals(prechargeFurnace.getWorkblankType())) {
            //钢锭的配炉
            this.executeFoundryAddGd(assignList, prechargeFurnace);
        }
        //数量和钢水重量赋值
        int num = 0;
        double totalMoltenSteel = 0.0;
        for (Assign assign : assignList) {
            num += ObjectUtil.isEmpty(assign.getNumber()) ? 0 : assign.getNumber();
            totalMoltenSteel += StringUtils.isEmpty(assign.getWeightMolten()) ? 0.0 : Double.parseDouble(assign.getWeightMolten());
        }
        if (!ObjectUtil.isEmpty(prechargeFurnace.getNum()) && num > 0) {
            prechargeFurnace.setNum(prechargeFurnace.getNum() + num);
        }
        if (!ObjectUtil.isEmpty(prechargeFurnace.getTotalMoltenSteel()) && totalMoltenSteel > 0) {
            prechargeFurnace.setTotalMoltenSteel(prechargeFurnace.getTotalMoltenSteel() + totalMoltenSteel);
        }
        this.updateById(prechargeFurnace);
        return prechargeFurnace;
    }

    @Override
    public PrechargeFurnace deleteTrackItem(List<Assign> assignList) {
        //预装炉未开工状态
        if (assignList.isEmpty()) {
            throw new GlobalException("必须要选择删除预装炉的工序", ResultCode.FAILED);
        }
        PrechargeFurnace prechargeFurnace = this.getById(assignList.get(0).getPrechargeFurnaceId());
        if (!PrechargeFurnace.STATE_WKG.equals(prechargeFurnace.getStatus())) {
            throw new GlobalException("只能删除未开工的预装炉的工序", ResultCode.FAILED);
        }
        for (Assign assign : assignList) {
            UpdateWrapper<TrackItem> updateWrapper = new UpdateWrapper();
            updateWrapper.eq("id", assign.getTiId());
            updateWrapper.set("precharge_furnace_id", null);
            trackItemService.update(updateWrapper);
        }
        prechargeFurnace.setOptName(optNames(this.queryTrackItem(prechargeFurnace.getId())));
        //设备类型赋值
        prechargeFurnace.setTypeCode(assignList.get(0).getTypeCode());
        //数量和钢水重量赋值
        int num = 0;
        double totalMoltenSteel = 0.0;
        for (Assign assign : assignList) {
            num+=ObjectUtil.isEmpty(assign.getNumber())?0:assign.getNumber();
            totalMoltenSteel+=StringUtils.isEmpty(assign.getWeightMolten())?0.0:Double.parseDouble(assign.getWeightMolten());
        }
        if(!ObjectUtil.isEmpty(prechargeFurnace.getNum()) && num>0){
            prechargeFurnace.setNum(prechargeFurnace.getNum()-num);
        }
        if(!ObjectUtil.isEmpty(prechargeFurnace.getTotalMoltenSteel()) && totalMoltenSteel>0){
            prechargeFurnace.setTotalMoltenSteel(prechargeFurnace.getTotalMoltenSteel()-totalMoltenSteel);
        }
        this.updateById(prechargeFurnace);
        return prechargeFurnace;
    }

    @Override
    public PrechargeFurnace deleteTrackItemYl(List<Assign> assignList) {
        //预装炉未开工状态
        if (assignList.isEmpty()) {
            throw new GlobalException("必须要选择删除预装炉的工序", ResultCode.FAILED);
        }
        PrechargeFurnace prechargeFurnace = this.getById(assignList.get(0).getPrechargeFurnaceId());

        for (Assign assign : assignList) {
            LambdaQueryWrapper<TrackItem> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TrackItem::getId, assign.getTiId());
            TrackItem trackItem = trackItemService.getOne(queryWrapper);
            if (trackItem.getIsCurrent() != 1) {
                throw new GlobalException("只能删除当前工序", ResultCode.FAILED);
            }
            //移除跟单配炉状态；
            UpdateWrapper<TrackItem> updateWrapper = new UpdateWrapper();
            updateWrapper.eq("id", assign.getTiId());
            updateWrapper.set("precharge_furnace_id", null);
            trackItemService.update(updateWrapper);
        }

        //设置工艺数据
        this.setRouter(assignList);
        //设备类型赋值；
        prechargeFurnace.setTypeCode(assignList.get(0).getTypeCode());

        //获取毛坯类型
        if ("1".equals(prechargeFurnace.getWorkblankType())) {
            if (PrechargeFurnace.END_START_WORK.equals(prechargeFurnace.getStatus()) ||
                    !"炼钢".equals(prechargeFurnace.getOptName())) {
                throw new GlobalException("只能在炼钢工序切未报工才可进行移除", ResultCode.FAILED);
            }
            //铸件移除
            this.executeFoundryRemoveZj(assignList, prechargeFurnace);
        }
        if ("2".equals(prechargeFurnace.getWorkblankType())) {
            //钢锭移除
            this.executeFoundryRemoveGd(assignList, prechargeFurnace);
            //如果全部移除，配炉工序名置空；
            LambdaQueryWrapper<TrackItem> trackItemLambdaQueryWrapper = new LambdaQueryWrapper<>();
            trackItemLambdaQueryWrapper.eq(TrackItem::getPrechargeFurnaceId, assignList.get(0).getPrechargeFurnaceId());
            List<TrackItem> trackItemList = trackItemMapper.selectList(trackItemLambdaQueryWrapper);
            if (CollectionUtils.isEmpty(trackItemList)) {
                LambdaUpdateWrapper<PrechargeFurnace> prechargeFurnaceLambdaUpdateWrapper = new LambdaUpdateWrapper<>();
                prechargeFurnaceLambdaUpdateWrapper.set(PrechargeFurnace::getOptName, null);
                prechargeFurnaceLambdaUpdateWrapper.eq(PrechargeFurnace::getId, assignList.get(0).getPrechargeFurnaceId());
            }
        }
        //数量和钢水重量赋值
        int num = 0;
        double totalMoltenSteel = 0.0;
        for (Assign assign : assignList) {
            num += ObjectUtil.isEmpty(assign.getNumber()) ? 0 : assign.getNumber();
            totalMoltenSteel += StringUtils.isEmpty(assign.getWeightMolten()) ? 0.0 : Double.parseDouble(assign.getWeightMolten());
        }
        if (!ObjectUtil.isEmpty(prechargeFurnace.getNum()) && num > 0) {
            prechargeFurnace.setNum(prechargeFurnace.getNum() - num);
        }
        if (!ObjectUtil.isEmpty(prechargeFurnace.getTotalMoltenSteel()) && totalMoltenSteel > 0) {
            prechargeFurnace.setTotalMoltenSteel(prechargeFurnace.getTotalMoltenSteel() - totalMoltenSteel);
        }
        this.updateById(prechargeFurnace);
        return prechargeFurnace;
    }

    private String optNames(List<Assign> assignList) {
        if (assignList == null || assignList.size() == 0) {
            return "";
        }
        Set<String> optNames = new HashSet<>();
        String name = "";
        for (Assign assign : assignList) {
            //拼接工序名称
            name = OptNameUtil.optName(assign.getOptName());
            optNames.add(name);
        }
        if (optNames.size() != 1) {
            throw new GlobalException("只有工序名称相同的工序才能放到一个预装炉内！", ResultCode.FAILED);
        }
        return name;
    }

    /**
     * 更新记录状态
     * @param id
     * @param recordStatus
     * @return
     */
    @Override
    public Boolean updateRecordStatus(Long id, String recordStatus) {
        PrechargeFurnace prechargeFurnace= new PrechargeFurnace();
        prechargeFurnace.setId(id);
        prechargeFurnace.setRecordStatus(recordStatus);
        int i = prechargeFurnaceMapper.updateById(prechargeFurnace);
        if(i>0){
            return true;
        }else {
            return false;
        }
    }

    /**
     * 预装炉报工回滚接口(锻造)
     * @param id
     * @return
     */
    @Override
    public boolean furnaceRollBack(Long id) {
        QueryWrapper<TrackComplete> trackCompleteQueryWrapper = new QueryWrapper<>();
        trackCompleteQueryWrapper.eq("precharge_furnace_id",id);
        List<TrackComplete> trackCompletes = trackCompleteService.list(trackCompleteQueryWrapper);
        //回滚报工信息
        for (TrackComplete trackComplete : trackCompletes) {
            trackCompleteService.rollBack(trackComplete.getId());
        }
        //预装炉信息回滚
        PrechargeFurnace prechargeFurnace = this.getById(id);
        prechargeFurnace.setStatus("1");
        prechargeFurnace.setAssignStatus(0);
        return this.updateById(prechargeFurnace);
    }

    /**
     * 铸件派工后配炉添加工序；
     *
     * @param assignList
     * @param prechargeFurnace
     */
    private void executeFoundryAddZj(List<Assign> assignList, PrechargeFurnace prechargeFurnace) {
        //派工或已开工之后添加工序,该工序需要集成派工及开工信息
        //已经派工，工序继承当前的派工信息；
        if (prechargeFurnace.getAssignStatus() == 1) {
            //派工信息全局变量，查询出派炉中一个工序的派工信息，后续更新需要更新的字段；
            Assign assign = new Assign();
            //派工信息继承；
            //查询出当前配炉工序信息；
            QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("precharge_furnace_id", assignList.get(0).getPrechargeFurnaceId()).eq("is_current", 1).isNotNull("precharge_furnace_assign_id");
            List<TrackItem> trackItems = trackItemMapper.selectList(queryWrapper);
            if (CollectionUtils.isNotEmpty(trackItems)) {
                TrackItem trackItem = trackItems.get(0);
                //查询出之前配炉中的工序的派工信息，给后续添加的工序的派工信息赋值；
                LambdaQueryWrapper<Assign> assignLambdaQueryWrapper = new LambdaQueryWrapper<>();
                assignLambdaQueryWrapper.eq(Assign::getTrackId, trackItem.getTrackHeadId());
                assignLambdaQueryWrapper.eq(Assign::getTiId, trackItem.getId());
                assign = trackAssignMapper.selectOne(assignLambdaQueryWrapper);
            }
            //获取所有派工人员的集合
            LambdaQueryWrapper<AssignPerson> assignPersonLambdaQueryWrapper = new LambdaQueryWrapper<>();
            assignPersonLambdaQueryWrapper.eq(AssignPerson::getAssignId, assign.getId());
            List<AssignPerson> assignPeople = assignPersonMapper.selectList(assignPersonLambdaQueryWrapper);
            //处理工序信息；
            for (Assign assignExt : assignList) {
                LambdaQueryWrapper<TrackItem> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(TrackItem::getId, assignExt.getTiId());
                //查询出当前添加工序的工序信息和跟单信息；
                TrackItem trackItem = trackItemMapper.selectOne(queryWrapper1);
                TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
                assign.setQty(trackItem.getAssignableQty());
                trackItem.setAssignableQty(0);
                trackItem.setIsSchedule(1);
                //设置派工设备
                trackItem.setDeviceId(assign.getDeviceId());
                if (!com.mysql.cj.util.StringUtils.isNullOrEmpty(trackHead.getStatus()) || "0".equals(trackHead.getStatus())) {
                    //将跟单状态改为在制
                    trackHead.setStatus("1");
                    trackHeadService.updateById(trackHead);
                    UpdateWrapper<TrackFlow> update = new UpdateWrapper<>();
                    update.set("status", "1");
                    update.eq("id", trackItem.getFlowId());
                    trackHeadFlowService.update(update);
                }
                //构造派工信息
                constructAssignInfo(assign, trackItem, trackHead);
                //处理预装炉派工信息
                LambdaQueryWrapper<PrechargeFurnaceAssign> prechargeFurnaceAssignLambdaQueryWrapper = new LambdaQueryWrapper<>();
                prechargeFurnaceAssignLambdaQueryWrapper.eq(PrechargeFurnaceAssign::getFurnaceId, assignList.get(0).getPrechargeFurnaceId())
                        .ne(PrechargeFurnaceAssign::getIsDoing, PrechargeFurnace.END_START_WORK);
                PrechargeFurnaceAssign prechargeFurnaceAssign = prechargeFurnaceAssignMapper.selectOne(prechargeFurnaceAssignLambdaQueryWrapper);
                //工序预装炉派工id赋值
                trackItem.setPrechargeFurnaceAssignId(prechargeFurnaceAssign.getId());
                trackAssignService.save(assign);
                //保存工序信息
                trackItemService.updateById(trackItem);
                //派工人员信息修改；
                constructAssignPersonInfo(assignPeople, assign);
            }
            //已经开工需要将工序的开工信息进行更新；
            if (PrechargeFurnace.YES_START_WORK.equals(prechargeFurnace.getStatus())) {
                //获取当前所有添加的工序信息
                List<String> collect = assignList.stream().map(e -> e.getTiId()).collect(Collectors.toList());
                LambdaQueryWrapper<TrackItem> queryWrapperItem = new LambdaQueryWrapper<>();
                queryWrapperItem.in(TrackItem::getId, collect);
                List<TrackItem> trackItemList = trackItemMapper.selectList(queryWrapperItem);
                if (CollectionUtils.isNotEmpty(trackItemList)) {
                    constructStartWorkingInfo(trackItemList);
                }
            }
        }
    }

    /**
     * 钢锭派工后配炉添加工序；
     *
     * @param assignList
     * @param prechargeFurnace
     */
    private void executeFoundryAddGd(List<Assign> assignList, PrechargeFurnace prechargeFurnace) {
        for (Assign assign : assignList) {
            //查询跟单信息；
            TrackHead trackHead = trackHeadService.getById(assign.getTrackHeadId());
            //处理前三道序
            for (int i = 1; i <= 3; i++) {
                //查询出配炉中的第一/二/三道工序
                LambdaQueryWrapper<TrackItem> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(TrackItem::getPrechargeFurnaceId, prechargeFurnace.getId()).eq(TrackItem::getSequenceOrderBy, i);
                TrackItem trackItem = trackItemMapper.selectList(queryWrapper).get(0);
                //查询出之前配炉中的工序的派工信息，给后续添加的工序的派工信息赋值；
                LambdaQueryWrapper<Assign> assignLambdaQueryWrapper = new LambdaQueryWrapper<>();
                assignLambdaQueryWrapper.eq(Assign::getTrackId, trackItem.getTrackHeadId());
                assignLambdaQueryWrapper.eq(Assign::getTiId, trackItem.getId());
                Assign assignItem = trackAssignMapper.selectOne(assignLambdaQueryWrapper);
                //获取所有派工人员的集合
                LambdaQueryWrapper<AssignPerson> assignPersonLambdaQueryWrapper = new LambdaQueryWrapper<>();
                assignPersonLambdaQueryWrapper.eq(AssignPerson::getAssignId, assignItem.getId());
                List<AssignPerson> assignPeople = assignPersonMapper.selectList(assignPersonLambdaQueryWrapper);
                //获取报工信息
                LambdaQueryWrapper<TrackComplete> trackCompleteLambdaQueryWrapper = new LambdaQueryWrapper<>();
                trackCompleteLambdaQueryWrapper.eq(TrackComplete::getTiId, trackItem.getId());
                trackCompleteLambdaQueryWrapper.eq(TrackComplete::getTrackId, trackItem.getTrackHeadId());
                TrackComplete trackComplete = trackCompleteMapper.selectList(trackCompleteLambdaQueryWrapper).get(0);
                //获取当前要添加工序(第一/二/三道序)；
                LambdaQueryWrapper<TrackItem> queryWrapper1 = new LambdaQueryWrapper<>();
                queryWrapper1.eq(TrackItem::getSequenceOrderBy, i);
                queryWrapper1.eq(TrackItem::getTrackHeadId, trackHead.getTrackHeadId());
                TrackItem trackItem1 = trackItemMapper.selectOne(queryWrapper1);
                //工序信息修改
                trackItem1.setAssignableQty(0);
                trackItem1.setIsSchedule(1);
                trackItem1.setIsDoing(1);
                trackItem1.setDeviceId(assignItem.getDeviceId());
                //将该工序中对应工序的派工id赋值到当前需要添加的工序上
                trackItem1.setPrechargeFurnaceAssignId(trackItem.getPrechargeFurnaceAssignId());
                assignItem.setQty(trackItem.getAssignableQty());
                //构造派工信息
                constructAssignInfo(assignItem, trackItem1, trackHead);
                //保存派工信息
                trackAssignService.save(assignItem);
                //保存工序信息
                trackItemService.updateById(trackItem);
                //派工人员信息修改；
                constructAssignPersonInfo(assignPeople, assignItem);
                //报工信息添加
                TrackComplete complete = new TrackComplete();
                BeanUtils.copyBeanProp(complete, trackComplete);
                complete.setId(UUID.randomUUID().toString().replace("-", ""));
                complete.setTiId(trackItem1.getId());
                complete.setAssignId(assignItem.getId());
                complete.setTrackId(assignItem.getTrackId());
                complete.setTrackNo(assignItem.getTrackNo());
                trackCompleteMapper.insert(complete);
            }
            //跟单信息修改
            LambdaUpdateWrapper<TrackHead> trackHeadLambdaQueryWrapper = new LambdaUpdateWrapper<>();
            trackHeadLambdaQueryWrapper.eq(TrackHead::getId, trackHead.getId());
            trackHeadLambdaQueryWrapper.set(TrackHead::getStatus, "1");
            trackHeadMapper.update(trackHead, trackHeadLambdaQueryWrapper);
            //跟单分流信息修改
            LambdaUpdateWrapper<TrackFlow> trackFlowLambdaQueryWrapper = new LambdaUpdateWrapper<>();
            trackFlowLambdaQueryWrapper.eq(TrackFlow::getTrackHeadId, trackHead.getId());
            trackFlowLambdaQueryWrapper.set(TrackFlow::getStatus, "1");
            //如果已经派工，炼钢工序直接走铸钢添加流程
            if (prechargeFurnace.getAssignStatus() == 1) {
                this.executeFoundryAddZj(assignList, prechargeFurnace);
            }
        }
    }

    /**
     * 钢锭派工后配炉移除工序；
     *
     * @param assignList
     * @param prechargeFurnace
     */
    private void executeFoundryRemoveGd(List<Assign> assignList, PrechargeFurnace prechargeFurnace) {
        for (Assign assign : assignList) {
            //查询跟单信息；
            TrackHead trackHead = trackHeadService.getById(assign.getTrackHeadId());
            //处理前三道工序
            for (int i = 1; i <= 3; i++) {
                //根据工序号查询出对应的第几道序
                LambdaQueryWrapper<TrackItem> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(TrackItem::getTrackHeadId, assign.getTrackHeadId());
                queryWrapper.eq(TrackItem::getSequenceOrderBy, i);
                TrackItem trackItem = trackItemMapper.selectOne(queryWrapper);
                //查询出派工信息
                LambdaQueryWrapper<Assign> assignLambdaQueryWrapper = new LambdaQueryWrapper<>();
                assignLambdaQueryWrapper.eq(Assign::getTrackId, trackItem.getTrackHeadId());
                assignLambdaQueryWrapper.eq(Assign::getTiId, trackItem.getId());
                assign = trackAssignMapper.selectOne(assignLambdaQueryWrapper);
                //工序信息修改
                trackItem.setAssignableQty(assign.getQty());
                trackItem.setIsDoing(0);
                trackItem.setIsSchedule(0);
                //解除预装炉派工信息
                trackItem.setPrechargeFurnaceAssignId(null);
                //解除预装炉信息
                trackItem.setPrechargeFurnaceId(null);
                trackItemService.updateById(trackItem);
                //移除跟单配炉状态；
                UpdateWrapper<TrackItem> updateWrapper = new UpdateWrapper();
                updateWrapper.eq("id", trackItem.getId());
                updateWrapper.set("precharge_furnace_id", null);
                trackItemService.update(updateWrapper);
                //报工信息删除
                LambdaQueryWrapper<TrackComplete> trackCompleteLambdaQueryWrapper = new LambdaQueryWrapper<>();
                trackCompleteLambdaQueryWrapper.eq(TrackComplete::getAssignId, assign.getId());
                trackCompleteMapper.delete(trackCompleteLambdaQueryWrapper);
                //派工人信息删除
                LambdaQueryWrapper<AssignPerson> assignPersonLambdaQueryWrapper = new LambdaQueryWrapper<>();
                assignPersonLambdaQueryWrapper.eq(AssignPerson::getAssignId, assign.getId());
                assignPersonMapper.delete(assignPersonLambdaQueryWrapper);
                //派工信息删除
                trackAssignMapper.delete(assignLambdaQueryWrapper);
            }
            //TrackFlow信息修改
            LambdaUpdateWrapper<TrackFlow> update = new LambdaUpdateWrapper<>();
            update.set(TrackFlow::getStatus, "0");
            update.eq(TrackFlow::getTrackHeadId, trackHead.getId());
            trackFlowMapper.update(null, update);
            //跟单信息修改
            trackHead.setStatus("0");
            trackHeadService.updateById(trackHead);
        }

        //处理第四道 “炼钢”回滚逻辑；
        //如果配炉已经派工，回滚逻辑同铸件，走铸件回滚逻辑；
        if (prechargeFurnace.getAssignStatus() == 1) {
            this.executeFoundryRemoveZj(assignList, prechargeFurnace);
        }
    }

    /**
     * 铸件派工后配炉移除工序；
     *
     * @param assignList
     * @param prechargeFurnace
     */
    private void executeFoundryRemoveZj(List<Assign> assignList, PrechargeFurnace prechargeFurnace) {
        //配炉已经派工走以下处理逻辑
        if (prechargeFurnace.getAssignStatus() == 1) {
            for (Assign assign : assignList) {
                //查询工序信息；
                LambdaQueryWrapper<TrackItem> queryWrapper = new LambdaQueryWrapper<>();
                queryWrapper.eq(TrackItem::getId, assign.getTiId());
                TrackItem trackItem = trackItemMapper.selectOne(queryWrapper);
                //查询跟单信息；
                TrackHead trackHead = trackHeadService.getById(trackItem.getTrackHeadId());
                //查询派工信息；
                LambdaQueryWrapper<Assign> assignLambdaQueryWrapper = new LambdaQueryWrapper<>();
                assignLambdaQueryWrapper.eq(Assign::getTrackId, trackItem.getTrackHeadId());
                assignLambdaQueryWrapper.eq(Assign::getTiId, trackItem.getId());
                assign = trackAssignMapper.selectOne(assignLambdaQueryWrapper);
                //工序信息修改
                trackItem.setAssignableQty(assign.getQty());
                trackItem.setIsDoing(0);
                trackItem.setIsSchedule(0);
                trackItem.setPrechargeFurnaceAssignId(null);
                trackItemService.updateById(trackItem);
                //TrackFlow信息修改
                UpdateWrapper<TrackFlow> update = new UpdateWrapper<>();
                update.set("status", "0");
                update.eq("id", trackItem.getFlowId());
                trackHeadFlowService.update(update);
                //跟单信息修改
                trackHead.setStatus("0");
                trackHeadService.updateById(trackHead);
                //派工人信息删除
                LambdaQueryWrapper<AssignPerson> assignPersonLambdaQueryWrapper = new LambdaQueryWrapper<>();
                assignPersonLambdaQueryWrapper.eq(AssignPerson::getAssignId, assign.getId());
                assignPersonMapper.delete(assignPersonLambdaQueryWrapper);
                //派工信息删除
                trackAssignMapper.delete(assignLambdaQueryWrapper);
            }
        }
    }

    /**
     * 派工构造派工信息
     *
     * @param assign
     * @param trackItem
     * @param trackHead
     */
    private void constructAssignInfo(Assign assign, TrackItem trackItem, TrackHead trackHead) {
        assign.setId(UUID.randomUUID().toString().replaceAll("-", ""));
        if (null != SecurityUtils.getCurrentUser()) {
            assign.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
            assign.setAssignBy(SecurityUtils.getCurrentUser().getUsername());
        }
        CommonResult<TenantUserVo> user = systemServiceClient.queryByUserId(assign.getAssignBy());
        assign.setAssignName(user.getData().getEmplName());
        assign.setAssignTime(new Date());
        assign.setModifyTime(new Date());
        assign.setCreateTime(new Date());
        assign.setAvailQty(assign.getQty());
        assign.setFlowId(trackItem.getFlowId());
        assign.setTiId(trackItem.getId());
        assign.setClasses(trackHead.getClasses());
        assign.setBranchCode(trackItem.getBranchCode());
        assign.setTenantId(trackItem.getTenantId());
        assign.setState(0);
        assign.setTrackNo(trackHead.getTrackNo());
        assign.setTrackId(trackHead.getId());
        if (com.mysql.cj.util.StringUtils.isNullOrEmpty(assign.getTenantId())) {
            assign.setTenantId(trackHead.getTenantId());
        }
    }

    /**
     * 构造开工信息
     *
     * @param trackItemList
     */
    private void constructStartWorkingInfo(List<TrackItem> trackItemList) {
        List<String> itemIds = trackItemList.stream().map(TrackItem::getId).collect(Collectors.toList());
        List<String> headIds = trackItemList.stream().map(TrackItem::getTrackHeadId).collect(Collectors.toList());
        List<String> flowIds = trackItemList.stream().map(TrackItem::getFlowId).collect(Collectors.toList());
        //将跟单状态改为在制
        UpdateWrapper<TrackHead> trackHeadUpdateWrapper = new UpdateWrapper<>();
        trackHeadUpdateWrapper.set("status", "1")
                .eq("status", "0")
                .in("id", headIds);
        trackHeadService.update(trackHeadUpdateWrapper);
        UpdateWrapper<TrackFlow> update = new UpdateWrapper<>();
        update.set("status", "1")
                .eq("status", "0")
                .in("id", flowIds);
        trackHeadFlowService.update(update);
        UpdateWrapper<Assign> assignUpdate;
        assignUpdate = new UpdateWrapper<>();
        assignUpdate.set("state", "1")
                .eq("state", "0")
                .in("ti_id", itemIds);
        trackAssignService.update(assignUpdate);
        UpdateWrapper<TrackItem> trackItemUpdateWrapper = new UpdateWrapper<>();
        trackItemUpdateWrapper.set("is_doing", 1)
                .set("start_doing_time", new Date())
                .set("start_doing_user", SecurityUtils.getCurrentUser().getUsername())
                .eq("is_doing", 0)
                .in("id", itemIds);
        trackItemService.update(trackItemUpdateWrapper);
    }

    /**
     * 构造派工人员信息
     *
     * @param assignPeople
     * @param assign
     */
    private void constructAssignPersonInfo(List<AssignPerson> assignPeople, Assign assign) {
        if (CollectionUtils.isNotEmpty(assignPeople)) {
            for (AssignPerson assignPerson : assignPeople) {
                AssignPerson assignPersonItem = new AssignPerson();
                assignPersonItem.setAssignId(assign.getId());
                assignPersonItem.setModifyTime(new Date());
                assignPersonItem.setUserId(assignPerson.getUserId());
                assignPersonItem.setUserName(assignPerson.getUserName());
                assignPersonMapper.insert(assignPersonItem);
            }
        }
    }

    /**
     * 校验炉子里边跟单工序的状态是否一致，如果不一致去提示无法操作
     * @param furnaceId
     * @param furnaceAssignId
     */
    @Override
    public void checkFurnaceItemStatus(String furnaceId, String furnaceAssignId){
        //同一炉子里的跟单工序
        List<TrackItem> trackItems = new ArrayList<>();
        if(!StringUtils.isEmpty(furnaceAssignId)){
            trackItems.addAll(trackItemService.list(new QueryWrapper<TrackItem>().eq("precharge_furnace_assignId", furnaceAssignId)));
        }
        if(CollectionUtil.isEmpty(trackItems) && !StringUtils.isEmpty(furnaceId)){
            trackItems.addAll(trackItemService.list(new QueryWrapper<TrackItem>().eq("precharge_furnace_id", furnaceId).eq("is_current",1)));
        }
        if(!CollectionUtil.isEmpty(trackItems)){
            //校验同一种工序
            int sameOPtNum = trackItems.stream().filter(item ->
                    item.getSequenceOrderBy().equals(trackItems.get(0).getSequenceOrderBy())
                    && item.getOptName().equals(trackItems.get(0).getOptName())).collect(Collectors.toList()).size();
            if(sameOPtNum != trackItems.size()){
                throw new GlobalException("配炉中各个跟单工序不一致，无法进行操作！",ResultCode.FAILED);
            }
            //校验调度状态
            int sameScheduleNum = trackItems.stream().filter(item ->
                    item.getIsScheduleComplete().equals(trackItems.get(0).getIsScheduleComplete())).collect(Collectors.toList()).size();
            if(sameScheduleNum != trackItems.size()){
                throw new GlobalException("配炉中各个跟单工序调度状态不一致，无法进行操作！",ResultCode.FAILED);
            }
            //校验质检状态
            int sameQualityNum = trackItems.stream().filter(item ->
                    item.getIsQualityComplete().equals(trackItems.get(0).getIsQualityComplete())).collect(Collectors.toList()).size();
            if(sameQualityNum != trackItems.size()){
                throw new GlobalException("配炉中各个跟单工序质检状态不一致，无法进行操作！",ResultCode.FAILED);
            }
            //校验报工状态
            int sameOperationNum = trackItems.stream().filter(item ->
                    item.getIsOperationComplete().equals(trackItems.get(0).getIsOperationComplete())).collect(Collectors.toList()).size();
            if(sameOperationNum != trackItems.size()){
                throw new GlobalException("配炉中各个跟单工序质检状态不一致，无法进行操作！",ResultCode.FAILED);
            }
            //校验派工状态
            int sameAssignNum = trackItems.stream().filter(item ->
                    item.getIsSchedule().equals(trackItems.get(0).getIsSchedule())).collect(Collectors.toList()).size();
            if(sameAssignNum != trackItems.size()){
                throw new GlobalException("配炉中各个跟单工序派工状态不一致，无法进行操作！",ResultCode.FAILED);
            }
        }
    }

}
