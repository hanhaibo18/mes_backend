package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.dao.TrackItemMapper;
import com.richfit.mes.produce.entity.QueryDto;
import com.richfit.mes.produce.entity.QueryFlawDetectionDto;
import com.richfit.mes.produce.entity.QueryFlawDetectionListDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;

/**
 * @author 王瑞
 * @Description 跟单工序服务
 */
@Service
public class TrackItemServiceImpl extends ServiceImpl<TrackItemMapper, TrackItem> implements TrackItemService {

    @Autowired
    private TrackItemMapper trackItemMapper;

    @Override
    public List<TrackItem> selectTrackItem(QueryWrapper<TrackItem> query) {
        return trackItemMapper.selectTrackItem(query);
    }

    @Override
    public List<TrackItem> selectTrackItemAssign(QueryWrapper<TrackItem> query) {
        return trackItemMapper.selectTrackItemAssign(query);
    }

    @Override
    public List<TrackItem> queryTrackItemByTrackNo(String trackNo) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("track_head_id", trackNo);
        return this.list(queryWrapper);
    }

    @Override
    public IPage<TrackItem> queryFlawDetectionList(QueryDto<QueryFlawDetectionDto> queryDto) {
        QueryFlawDetectionDto queryFlawDetectionDto = queryDto.getParam();
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        if (null != queryFlawDetectionDto.getEndTime() && null != queryFlawDetectionDto.getStartTime()) {
            queryWrapper.ge("create_time", queryFlawDetectionDto.getStartTime());
            //处理结束时间
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(queryFlawDetectionDto.getEndTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            queryWrapper.le("create_time", calendar.getTime());
        }
        //TODO:复检=再次检查不合格产品
        if (queryFlawDetectionDto.getIsRecheck()) {
            queryWrapper.eq("", "不合格状态码");
        }
        if (!StringUtils.isNullOrEmpty(queryFlawDetectionDto.getProductNo())) {
            queryWrapper.eq("product_no", queryFlawDetectionDto.getProductNo());
        }
        queryWrapper.isNull("flaw_detection");
        queryWrapper.eq("branch_code", queryDto.getBranchCode());
        queryWrapper.eq("tenant_id", queryDto.getTenantId());
        queryWrapper.orderByDesc("create_time");
        return this.page(new Page<>(queryDto.getPage(), queryDto.getSize()), queryWrapper);
    }

    @Override
    public Boolean updateFlawDetection(TrackItem trackItem) {
        return this.updateById(trackItem);
    }

    @Override
    public IPage<TrackItem> queryFlawDetectionPage(QueryDto<QueryFlawDetectionListDto> queryDto) {
        QueryFlawDetectionListDto queryFlawDetectionDto = queryDto.getParam();
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        if (null != queryFlawDetectionDto.getEndTime() && null != queryFlawDetectionDto.getStartTime()) {
            queryWrapper.ge("create_time", queryFlawDetectionDto.getStartTime());
            //处理结束时间
            Calendar calendar = new GregorianCalendar();
            calendar.setTime(queryFlawDetectionDto.getEndTime());
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            queryWrapper.le("create_time", calendar.getTime());
        }
        if (!StringUtils.isNullOrEmpty(queryFlawDetectionDto.getTrackNo())) {
            queryWrapper.eq("track_o", queryFlawDetectionDto.getTrackNo());
        }
        if (!StringUtils.isNullOrEmpty(queryFlawDetectionDto.getProductNo())) {
            queryWrapper.eq("product_no", queryFlawDetectionDto.getProductNo());
        }
        queryWrapper.isNotNull("flaw_detection");
        queryWrapper.eq("branch_code", queryDto.getBranchCode());
        queryWrapper.eq("tenant_id", queryDto.getTenantId());
        queryWrapper.orderByDesc("create_time");
        return this.page(new Page<>(queryDto.getPage(), queryDto.getSize()), queryWrapper);
    }

}
