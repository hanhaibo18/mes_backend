package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.TrackComplete;

/**
 * @author 马峰
 * @Description 报工服务
 */
public interface TrackCompleteService extends IService<TrackComplete> {
    IPage<TrackComplete> queryPage(Page page, QueryWrapper<TrackComplete> query);


}
