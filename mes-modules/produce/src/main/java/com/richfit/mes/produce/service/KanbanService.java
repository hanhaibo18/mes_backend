package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.ProcessTrack;
import com.richfit.mes.produce.entity.KanbanDto;

/**
 * @Author: GaoLiang
 * @Date: 2020/10/16 10:40
 */
public interface KanbanService extends IService<ProcessTrack> {
    IPage<ProcessTrack> queryProgress(Page<ProcessTrack> kanbanPage, KanbanDto kanbanDto);
}
