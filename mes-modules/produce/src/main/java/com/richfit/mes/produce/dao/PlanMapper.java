package com.richfit.mes.produce.dao;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/14 9:59
 */

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.Plan;
import com.richfit.mes.produce.entity.PlanDto;
import com.richfit.mes.produce.entity.PlanTrackItemViewDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PlanMapper extends BaseMapper<Plan> {
    IPage<Plan> queryPlan(Page<Plan> planPage, @Param("param")PlanDto planDto);

    List<PlanTrackItemViewDto> queryPlanTrackItem(@Param("planId") String planId);

    Plan findPlan(@Param("code")String code,@Param("tenantId")String tenantId);

    Plan findByPlanId(@Param("id")String id,@Param("tenantId")String tenantId);

    @Select("select * from (select plan.*,extend.ingot_case from produce_plan as plan left join produce_plan_extend as extend on plan.id = extend.plan_id) a ${ew.customSqlSegment}")
    List<Plan> selectList(@Param(Constants.WRAPPER) QueryWrapper<Plan> queryWrapper);

}
