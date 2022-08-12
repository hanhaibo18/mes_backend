package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.Plan;
import com.richfit.mes.produce.entity.PlanDto;
import com.richfit.mes.produce.entity.PlanSplitDto;
import com.richfit.mes.produce.entity.PlanTrackItemViewDto;
import com.richfit.mes.produce.entity.extend.ProjectBomComplete;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/14 9:54
 */
public interface PlanService extends IService<Plan> {
    IPage<Plan> queryPage(Page<Plan> planPage, PlanDto planDto);

    boolean updatePlanStatus(String code, String tenantId);

    List<PlanTrackItemViewDto> queryPlanTrackItem(String planId);

    void findBranchName(Plan plan);

    /*
        计划下新增跟单，计划状态变化 1
     */
    boolean setPlanStatusStart(String projCode, String tenantId);

    /*
        计划下删除跟单，如果跟单数量为0，则计划状态恢复为 0
     */
    boolean setPlanStatusNew(String projCode, String tenantId);

    /*
        计划下全部任务完成，则计划状态变为 2
     */
    boolean setPlanStatusClose(String projCode, String tenantId);

//    boolean updatePlanStatus(String projCode,String tenantId);


    CommonResult<Object> savePlan(Plan plan);

    CommonResult<Object> updatePlan(Plan plan);

    boolean delPlan(Plan plan);

    CommonResult<Object> addPlan(Plan plan);

    Map computePlanNeedHour(Plan plan);


    /**
     * 功能描述: 根据时间区间 和 图号批量获取计划
     *
     * @param startTime  开始时间
     * @param endTime    结束时间
     * @param drawingNo  图号
     * @param tenantId
     * @param branchCode
     * @Author: xinYu.hou
     * @Date: 2022/4/20 10:59
     * @return: List<Map < String, String>>
     **/
    List<Map<String, String>> getPlanList(Date startTime, Date endTime, String drawingNo, String tenantId, String branchCode);


    /**
     * 功能描述: 物料齐套性检查
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/6/7 11:37
     **/
    List<ProjectBomComplete> completeness(String planId);


    /**
     * 功能描述: 物料齐套性检查
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/7/11 11:37
     **/
    List<ProjectBomComplete> completenessList(List<Plan> planList);

    /**
     * 功能描述: 计划数据自动计算
     *
     * @param planId 计划id
     * @Author: zhiqiang.lu
     * @Date: 2022/7/8 11:37
     **/
    void planData(String planId);

    /**
     * 拆分计划
     *
     * @param planSplitDto
     * @return
     */
    CommonResult<Object> splitPlan(PlanSplitDto planSplitDto);

    /**
     * 撤销拆分计划
     *
     * @param id
     * @return
     */
    CommonResult<Object> backoutPlan(String id);

    void exportPlan(MultipartFile file);


    /**
     * 计划列表动态数据封装（工艺状态）
     *
     * @param planList 计划列表
     * @Author: zhiqiang.lu
     * @Date: 2022/8/11 15:06
     */
    void planPackageRouter(List<Plan> planList);

    /**
     * 计划列表动态数据封装（库存数量）
     *
     * @param planList 计划列表
     * @Author: zhiqiang.lu
     * @Date: 2022/8/11 15:06
     */
    void planPackageStore(List<Plan> planList);
}
