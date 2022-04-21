package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.richfit.mes.common.model.produce.TrackHead;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author 王瑞
 * @Description 跟单Mapper
 */
@Mapper
public interface TrackHeadMapper extends BaseMapper<TrackHead> {

    IPage<TrackHead> selectTrackHeadRouter(IPage<TrackHead> page, @Param(Constants.WRAPPER) Wrapper<TrackHead> query);

    IPage<TrackHead> selectTrackHeadCurrentRouter(IPage<TrackHead> page, @Param(Constants.WRAPPER) Wrapper<TrackHead> query);

    /**
     * 功能描述: 根据计划Id查询 所做物品数量
     * @Author: xinYu.hou
     * @Date: 2022/4/20 15:06
     * @param workPlanId
     * @return: Integer
     **/
    @Select("SELECT sum(number) FROM produce_track_head WHERE work_plan_id = #{workPlanId}")
    Integer selectTrackHeadNumber(@Param("workPlanId")String workPlanId);
}
