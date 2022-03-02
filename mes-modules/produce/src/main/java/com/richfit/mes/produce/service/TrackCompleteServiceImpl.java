package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.TrackComplete;
import com.richfit.mes.produce.dao.TrackCompleteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author sun
 * @Description 跟单服务
 */
@Service
public class TrackCompleteServiceImpl extends ServiceImpl<TrackCompleteMapper, TrackComplete> implements TrackCompleteService{

    @Autowired
    private TrackCompleteMapper trackCompleteMapper;

    public IPage<TrackComplete> queryPage(Page page, QueryWrapper<TrackComplete> query)
    {

        return trackCompleteMapper.queryPage(page,query);
    }
}
