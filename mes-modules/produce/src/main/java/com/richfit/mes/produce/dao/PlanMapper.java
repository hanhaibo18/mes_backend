package com.richfit.mes.produce.dao;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/14 9:59
 */

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.Plan;
import com.richfit.mes.produce.entity.PlanDto;
import com.richfit.mes.produce.entity.PlanTrackItemViewDto;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface PlanMapper extends BaseMapper<Plan> {
    IPage<Plan> queryPlan(Page<Plan> planPage, @Param("param")PlanDto planDto);

    List<PlanTrackItemViewDto> queryPlanTrackItem(@Param("planId") String planId);

    Plan findPlan(@Param("code")String code,@Param("tenantId")String tenantId);

    Plan findByPlanId(@Param("id")String id,@Param("tenantId")String tenantId);

}
