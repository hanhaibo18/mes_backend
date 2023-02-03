package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.TrackCheck;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.TrackCheckCountMapper;
import com.richfit.mes.produce.dao.TrackCheckMapper;
import com.richfit.mes.produce.entity.CountDto;
import org.apache.ibatis.annotations.Param;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author mafeng
 * @Description 质检结果
 */
@Service
public class TrackCheckServiceImpl extends ServiceImpl<TrackCheckMapper, TrackCheck> implements TrackCheckService {

    @Autowired
    private TrackCheckMapper trackCheckMapper;

    @Autowired
    private TrackCheckCountMapper trackCheckCountMapper;

    @Resource
    private TrackItemService trackItemService;

    public List<CountDto> count(String dateType, String startTime, @Param("endTime") String endTime) {
        return trackCheckCountMapper.count(dateType, startTime, endTime);
    }

    @Override
    public List<TrackItem> getItemList(String tiId) {
        TrackItem trackItem = trackItemService.getById(tiId);
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("flow_id", trackItem.getFlowId());
        queryWrapper.gt("opt_sequence", trackItem.getOptSequence());
        queryWrapper.orderByAsc("opt_sequence");
        return trackItemService.list(queryWrapper);
    }

    @Override
    public Integer qualityTestingNumber(String branchCode) {
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("is_exist_quality_check", 1);
        queryWrapper.eq("is_quality_complete", 0);
        queryWrapper.eq("quality_check_by", SecurityUtils.getCurrentUser().getUsername());
        queryWrapper.eq("branch_code", branchCode);
        return trackItemService.count(queryWrapper);
    }

    @Override
    public IPage<TrackCheck> queryCheckPage(Page<TrackCheck> page, QueryWrapper<TrackCheck> qw) {
        return trackCheckMapper.queryTrackCheckPage(page, qw);
    }

    @Override
    public Boolean countQueryRules(String rulesId) {
        QueryWrapper<TrackCheck> queryWrapper = new QueryWrapper<TrackCheck>();
        queryWrapper.eq("result", rulesId);
        return this.count(queryWrapper) > 0;
    }

}
