package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.TrackComplete;
import com.richfit.mes.common.model.produce.TrackCompleteCache;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.TrackCompleteCacheMapper;
import com.richfit.mes.produce.entity.CompleteDto;
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

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveCompleteCache(List<CompleteDto> completeDtoList) {
        for (CompleteDto completeDto : completeDtoList) {
            TrackItem trackItem = trackItemService.getById(completeDto.getTiId());
            //检验人
            trackItem.setQualityCheckBy(completeDto.getQcPersonId());
            trackItem.setQualityCheckBranch(completeDto.getQualityCheckBranch());
            //根据工序Id先删除,在重新新增数据
            QueryWrapper<TrackCompleteCache> removeCache = new QueryWrapper<>();
            removeCache.eq("ti_id", completeDto.getTiId());
            this.remove(removeCache);

            List<TrackCompleteCache> trackCompleteCacheList = new ArrayList<>();
            for (TrackComplete trackComplete : completeDto.getTrackCompleteList()) {
                if (trackComplete.getReportHours() > trackItem.getSinglePieceHours()) {
                    return CommonResult.failed("报工工时不能大于额定工时");
                }
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
                trackCompleteCacheList.add(trackCompleteCache);
            }
            trackItemService.updateById(trackItem);
            this.saveBatch(trackCompleteCacheList);
        }
        return CommonResult.success(true);
    }
}
