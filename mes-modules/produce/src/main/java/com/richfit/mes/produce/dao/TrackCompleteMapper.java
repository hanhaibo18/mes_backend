package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.TrackComplete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author 马峰
 * @Description 报工Mapper
 */
@Mapper
public interface TrackCompleteMapper extends BaseMapper<TrackComplete> {

    @Select("select * from v_produce_complete a ${ew.customSqlSegment}")
    IPage<TrackComplete> queryPage(Page page, @Param(Constants.WRAPPER) Wrapper<TrackComplete> wrapper);


    @Select("select * from v_produce_complete a ${ew.customSqlSegment}")
    List<TrackComplete> queryList(@Param(Constants.WRAPPER) Wrapper<TrackComplete> wrapper);

    /**
     * 功能描述: 查询报工缓存表
     *
     * @param wrapper
     * @Author: xinYu.hou
     * @Date: 2022/7/14 11:31
     * @return: List<TrackComplete>
     **/
    @Select("select * from produce_track_complete_cache ${ew.customSqlSegment}")
    List<TrackComplete> queryCompleteCache(@Param(Constants.WRAPPER) Wrapper<TrackComplete> wrapper);
}
