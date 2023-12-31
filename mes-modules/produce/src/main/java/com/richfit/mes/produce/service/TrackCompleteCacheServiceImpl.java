package com.richfit.mes.produce.service;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.model.produce.RawMaterialRecord;
import com.richfit.mes.common.model.produce.RawMaterialRecordCache;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.TrackCompleteCacheMapper;
import com.richfit.mes.produce.entity.CompleteDto;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: TrackCompleteCacheService.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年07月14日 14:03:00
 */
@Service
public class TrackCompleteCacheServiceImpl extends ServiceImpl<TrackCompleteCacheMapper, TrackCompleteCache> implements TrackCompleteCacheService {

    @Resource
    private TrackItemService trackItemService;
    @Autowired
    private TrackCompleteService trackCompleteService;
    @Autowired
    private LayingOffCacheService layingOffCacheService;
    @Autowired
    private ForgControlRecordCacheService forgControlRecordCacheService;
    @Autowired
    private RawMaterialRecordCacheService rawMaterialRecordCacheService;
    @Autowired
    private ModelingCoreCacheService modelingCoreCacheService;
    @Autowired
    private KnockoutCacheService knockoutCacheService;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveCompleteCache(List<CompleteDto> completeDtoList) {
        //获取用户所属公司
        String companyCode = SecurityUtils.getCurrentUser().getCompanyCode();
        for (CompleteDto completeDto : completeDtoList) {
            TrackItem trackItem = trackItemService.getById(completeDto.getTiId());
            //检验人
            trackItem.setQualityCheckBy(completeDto.getQcPersonId());
            //根据工序Id先删除,在重新新增数据
            QueryWrapper<TrackCompleteCache> removeCache = new QueryWrapper<>();
            removeCache.eq("ti_id", completeDto.getTiId());
            this.remove(removeCache);
            List<TrackCompleteCache> trackCompleteCacheList = new ArrayList<>();
            for (TrackComplete trackComplete : completeDto.getTrackCompleteList()) {
                //验证输入值是否合法
                /*String s = trackCompleteService.verifyTrackComplete(trackComplete, trackItem, companyCode);
                //如果返回值不等于空则代表验证不通过，将提示信息返回
                if (org.apache.commons.lang3.StringUtils.isNotBlank(s)) {
                    return CommonResult.failed(s);
                }*/
                TrackCompleteCache trackCompleteCache = new TrackCompleteCache();
                trackCompleteCache.setAssignId(completeDto.getAssignId());
                trackCompleteCache.setTiId(completeDto.getTiId());
                trackCompleteCache.setTrackId(completeDto.getTrackId());
                trackCompleteCache.setTrackNo(completeDto.getTrackNo());
                trackCompleteCache.setProdNo(completeDto.getProdNo());
                trackCompleteCache.setCompleteBy(SecurityUtils.getCurrentUser().getUsername());
                trackCompleteCache.setCompleteTime(new Date());
                trackCompleteCache.setUserId(trackComplete.getUserId());
                trackCompleteCache.setDeviceId(trackComplete.getDeviceId());
                trackCompleteCache.setCompletedHours(trackComplete.getCompletedHours());
                trackCompleteCache.setActualHours(trackComplete.getActualHours());
                trackCompleteCache.setReportHours(trackComplete.getReportHours());
                trackCompleteCache.setStaticHours(trackComplete.getStaticHours());
                trackCompleteCache.setCompletedQty(trackComplete.getCompletedQty());
                trackCompleteCache.setRejectQty(trackComplete.getRejectQty());
                trackCompleteCache.setDetectionResult(trackComplete.getDetectionResult());
                //北石报工字段
                trackCompleteCache.setActualFixHours(trackComplete.getActualFixHours());
                trackCompleteCache.setActualNomalHours(trackComplete.getActualNomalHours());
                trackCompleteCache.setActualOverHours(trackComplete.getActualOverHours());
                trackCompleteCache.setCompletedChangeHours(trackComplete.getCompletedChangeHours());
                trackCompleteCache.setCompletedFixHours(trackComplete.getCompletedFixHours());
                trackCompleteCache.setSingleAddHours(trackComplete.getSingleAddHours());
                trackCompleteCache.setAuxiliaryHours(trackComplete.getAuxiliaryHours());
                trackCompleteCache.setWeightMolten(trackComplete.getWeightMolten());
                trackCompleteCache.setPourTemperature(completeDto.getPourTemperature());

                trackCompleteCacheList.add(trackCompleteCache);
            }
            trackItemService.updateById(trackItem);
            this.saveBatch(trackCompleteCacheList);
            //保存下料信息
            if (completeDto.getLayingOff() != null) {
                //先删除该已保存过的
                QueryWrapper<LayingOffCache> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("item_id", completeDto.getTiId());
                layingOffCacheService.remove(queryWrapper);

                LayingOffCache layingOffCache = new LayingOffCache();
                BeanUtils.copyProperties(completeDto.getLayingOff(), layingOffCache);
                layingOffCache.setItemId(completeDto.getTiId());
                layingOffCacheService.save(layingOffCache);
            }
            //保存锻造信息
            if (completeDto.getForgControlRecordList() != null) {
                //现根据item_id删除原有记录
                QueryWrapper<ForgControlRecordCache> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("item_id", completeDto.getTiId());
                forgControlRecordCacheService.remove(queryWrapper);

                ForgControlRecord forgControlRecordBarInfo = new ForgControlRecord();
                forgControlRecordBarInfo.setItemId(completeDto.getTiId());
                forgControlRecordBarInfo.setType("2");
                forgControlRecordBarInfo.setBarForge(completeDto.getBarForge());
                ForgControlRecord forgControlRecordRemark = new ForgControlRecord();
                forgControlRecordRemark.setItemId(completeDto.getTiId());
                forgControlRecordRemark.setType("3");
                forgControlRecordRemark.setRemark(completeDto.getForgeRemark());
                for (ForgControlRecord forgControlRecord : completeDto.getForgControlRecordList()) {
                    forgControlRecord.setType("1");
                    forgControlRecord.setItemId(completeDto.getTiId());
                }
                completeDto.getForgControlRecordList().add(forgControlRecordRemark);
                completeDto.getForgControlRecordList().add(forgControlRecordBarInfo);
                String jsonString = JSONObject.toJSONString(completeDto.getForgControlRecordList());
                List<ForgControlRecordCache> forgControlRecordList = JSONArray.parseArray(jsonString, ForgControlRecordCache.class);
                forgControlRecordCacheService.saveBatch(forgControlRecordList);
            }
            //保存原材料消耗信息
            if (completeDto.getRawMaterialRecordList() != null) {
                //现根据item_id删除原有记录
                QueryWrapper<RawMaterialRecordCache> queryWrapperRawMaterialRecord = new QueryWrapper<>();
                queryWrapperRawMaterialRecord.eq("item_id", completeDto.getTiId());
                rawMaterialRecordCacheService.remove(queryWrapperRawMaterialRecord);
                for (RawMaterialRecord rawMaterialRecord : completeDto.getRawMaterialRecordList()) {
                    rawMaterialRecord.setItemId(completeDto.getTiId());
                }
                rawMaterialRecordCacheService.saveBatch(JSONArray.parseArray(JSONObject.toJSONString(completeDto.getRawMaterialRecordList()), RawMaterialRecordCache.class));
            }
            //保存造型/制芯工序报工缓存信息
            if (!ObjectUtil.isEmpty(completeDto.getModelingCore())) {
                //先删除该已保存过的
                QueryWrapper<ModelingCoreCache> queryWrapperModelingCoreCache = new QueryWrapper<>();
                queryWrapperModelingCoreCache.eq("item_id", completeDto.getTiId());
                modelingCoreCacheService.remove(queryWrapperModelingCoreCache);
                completeDto.getModelingCore().setItemId(completeDto.getTiId());
                ModelingCoreCache modelingCoreCache = new ModelingCoreCache();
                BeanUtils.copyProperties(completeDto.getModelingCore(), modelingCoreCache);
                modelingCoreCacheService.saveOrUpdate(modelingCoreCache);
            }
            //保存打箱工序报工缓存信息
            if (!ObjectUtil.isEmpty(completeDto.getKnockout())) {
                //先删除该已保存过的
                QueryWrapper<KnockoutCache> queryWrapperKnockoutCache = new QueryWrapper<>();
                queryWrapperKnockoutCache.eq("item_id", completeDto.getTiId());
                knockoutCacheService.remove(queryWrapperKnockoutCache);
                completeDto.getKnockout().setItemId(completeDto.getTiId());
                KnockoutCache knockoutCache = new KnockoutCache();
                BeanUtils.copyProperties(completeDto.getKnockout(), knockoutCache);
                knockoutCacheService.saveOrUpdate(knockoutCache);
            }
        }
        return CommonResult.success(true);
    }
}
