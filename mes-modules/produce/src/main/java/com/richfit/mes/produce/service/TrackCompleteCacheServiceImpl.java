package com.richfit.mes.produce.service;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.*;
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

                trackCompleteCacheList.add(trackCompleteCache);
            }
            trackItemService.updateById(trackItem);
            this.saveBatch(trackCompleteCacheList);
            //保存下料信息
            if (completeDto.getLayingOff() != null) {
                LayingOffCache layingOffCache = new LayingOffCache();
                BeanUtils.copyProperties(completeDto.getLayingOff(), layingOffCache);
                layingOffCacheService.saveOrUpdate(layingOffCache);
            }
            //保存锻造信息
            if (completeDto.getForgControlRecordList() != null) {
                String jsonString = JSONObject.toJSONString(completeDto.getForgControlRecordList());
                List<ForgControlRecordCache> forgControlRecordList = JSONArray.parseArray(jsonString, ForgControlRecordCache.class);
                forgControlRecordCacheService.saveOrUpdateBatch(forgControlRecordList);
            }
        }
        return CommonResult.success(true);
    }
}
