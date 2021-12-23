package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.Plan;
import com.richfit.mes.produce.entity.PlanDto;
import com.richfit.mes.produce.entity.PlanTrackItemViewDto;

import java.util.List;
import java.util.Map;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/14 9:54
 */
public interface PlanService extends IService<Plan> {
    IPage<Plan> queryPage(Page<Plan> planPage, PlanDto planDto);

    boolean updatePlanStatus(String code,String tenantId);

    List<PlanTrackItemViewDto> queryPlanTrackItem(String planId);

    void findBranchName(Plan plan);

    /*
        计划下新增跟单，计划状态变化 1
     */
    boolean setPlanStatusStart(String projCode,String tenantId);

    /*
        计划下删除跟单，如果跟单数量为0，则计划状态恢复为 0
     */
    boolean setPlanStatusNew(String projCode,String tenantId);

    /*
        计划下全部任务完成，则计划状态变为 2
     */
    boolean setPlanStatusClose(String projCode,String tenantId);

//    boolean updatePlanStatus(String projCode,String tenantId);


    CommonResult<Object> savePlan(Plan plan);
    CommonResult<Object> updatePlan(Plan plan);
    boolean delPlan(Plan plan);

    CommonResult<Object> addPlan(Plan plan);
    Map computePlanNeedHour(Plan plan);
}
