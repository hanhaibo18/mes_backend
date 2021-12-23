package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.utils.DateUtils;
import com.richfit.mes.common.model.produce.ProcessTrack;
import com.richfit.mes.produce.dao.KanbanMapper;
import com.richfit.mes.produce.entity.KanbanDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: GaoLiang
 * @Date: 2020/10/16 10:43
 */
@Service
public class KanbanServiceImpl extends ServiceImpl<KanbanMapper, ProcessTrack> implements KanbanService{

    @Autowired
    KanbanMapper kanbanMapper;

    @Override
    public IPage<ProcessTrack> queryProgress(Page<ProcessTrack> orderPage, KanbanDto kanbanDto) {

        kanbanDto.setYearStart(DateUtils.getYearStart());
        kanbanDto.setYearEnd(DateUtils.getYearEnd());
        kanbanDto.setMonthStart(DateUtils.getMonthStart());
        kanbanDto.setMonthEnd(DateUtils.getMonthEnd());

        return kanbanMapper.queryProcessTrack(orderPage,kanbanDto);
    }
}
