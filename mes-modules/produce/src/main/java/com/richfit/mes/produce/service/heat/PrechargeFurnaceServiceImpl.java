package com.richfit.mes.produce.service.heat;

import cn.hutool.core.util.ObjectUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.PrechargeFurnace;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.model.util.OptNameUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.PrechargeFurnaceMapper;
import com.richfit.mes.produce.dao.TrackAssignMapper;
import com.richfit.mes.produce.dao.TrackHeadMapper;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.service.TrackItemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
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
    private TrackItemService trackItemService;

    @Autowired
    public TrackAssignMapper trackAssignMapper;

    @Autowired
    private BaseServiceClient baseServiceClient;

    @Autowired
    private PrechargeFurnaceMapper prechargeFurnaceMapper;

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
        trackItemQueryWrapper.eq("branch_code",branchCode);
        trackItemQueryWrapper.eq("tenant_id",SecurityUtils.getCurrentUser().getTenantId());
        return trackAssignMapper.getPageAssignsHot(trackItemQueryWrapper);
    }

    @Override
    public List<TrackItem> getItemsByPrechargeFurnace(Long id) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_current", 1)
                .eq("precharge_furnace_id", id);
        List<TrackItem> list = trackItemService.list(queryWrapper);
        for (TrackItem trackItem : list) {
            //查询跟单信息
            LambdaQueryWrapper<TrackHead> trackHeadLambdaQueryWrapper = new LambdaQueryWrapper<>();
            trackHeadLambdaQueryWrapper.eq(TrackHead::getId, trackItem.getTrackHeadId());
            TrackHead trackHead = trackHeadMapper.selectOne(trackHeadLambdaQueryWrapper);
            trackItem.setTexture(trackHead.getTexture());
            trackItem.setWorkNo(trackHead.getWorkNo());
            trackItem.setProductName(trackHead.getProductName());
            trackItem.setPriority(trackHead.getPriority());
            //查询工艺信息
            Router data = baseServiceClient.getRouter(trackHead.getRouterId()).getData();
            trackItem.setPieceWeight(Objects.nonNull(data) ? data.getPieceWeight() : "");
            trackItem.setWeightMolten(Objects.nonNull(data) ? data.getWeightMolten() : "");
        }
        return list;
    }

    /**
     *
     * @param assignList
     * @param texture   材质
     */
    @Override
    public void furnaceChargingHot(List<Assign> assignList,String texture) {
        if (assignList.isEmpty()) {
            throw new GlobalException("必须要有装炉的工序", ResultCode.FAILED);
        }
        PrechargeFurnace prechargeFurnace = new PrechargeFurnace();
        prechargeFurnace.setOptName(optNames(assignList));
        prechargeFurnace.setSiteId(assignList.get(0).getSiteId());
        prechargeFurnace.setTypeCode(assignList.get(0).getTypeCode());
        prechargeFurnace.setTexture(texture);
        prechargeFurnace.setRecordStatus("0");
        prechargeFurnace.setBranchCode(SecurityUtils.getCurrentUser().getBelongOrgId());
        this.save(prechargeFurnace);
        for (Assign assign : assignList) {
            //跟单工序添加装炉id
            //跟单工序添加装炉id
            TrackItem trackItem = trackItemService.getById(assign.getTiId());
            trackItem.setPrechargeFurnaceId(prechargeFurnace.getId());
            trackItemService.updateById(trackItem);
        }
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



}
