package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.common.model.produce.ProcessTrack;
import com.richfit.mes.produce.entity.KanbanDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Author: GaoLiang
 * @Date: 2020/10/16 10:23
 */
@Mapper
public interface KanbanMapper extends BaseMapper<ProcessTrack> {

    IPage<ProcessTrack> queryProcessTrack(IPage<ProcessTrack> page,@Param("param") KanbanDto kanbanDto);

}
