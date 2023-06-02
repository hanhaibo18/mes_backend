package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.Plan;
import com.richfit.mes.produce.entity.PlanSplitDto;
import com.richfit.mes.produce.entity.PlanTrackItemViewDto;
import com.richfit.mes.produce.entity.extend.ProjectBomComplete;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;

/**
 * @Author: zhiqiang.lu
 * @Date: 2020.9.2 9:54
 */
public interface PlanService extends IService<Plan> {

    void updateDeliveryNum(String planId);

    boolean updatePlanStatus(String code, String tenantId);

    List<PlanTrackItemViewDto> queryPlanTrackItem(String planId);

    CommonResult<Object> savePlan(Plan plan);

    CommonResult<Object> updatePlan(Plan plan);

    boolean delPlan(Plan plan);

    CommonResult<Object> addPlan(Plan plan);

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
     * 功能描述: 计划自动添加项目BOM
     *
     * @param plan 计划信息
     * @Author: zhiqiang.lu
     * @Date: 2022/7/8 11:37
     **/
    void autoProjectBom(Plan plan) throws Exception;

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

    void exportPlan(MultipartFile file, HttpServletRequest request) throws IOException;


    @Transactional(rollbackFor = Exception.class)
    void importPlanMX(MultipartFile file, HttpServletRequest request) throws IOException;

    @Transactional(rollbackFor = Exception.class)
    void importPlanDZ(MultipartFile file, HttpServletRequest request) throws IOException;

    @Transactional(rollbackFor = Exception.class)
    void importPlanZG(MultipartFile file, HttpServletRequest request) throws IOException;

    @Transactional(rollbackFor = Exception.class)
    void importPlanYL(MultipartFile file, HttpServletRequest request) throws IOException;

    /**
     * 计划列表动态数据封装（工艺状态）
     *
     * @param planList 计划列表
     * @Author: zhiqiang.lu
     * @Date: 2022/8/11 15:06
     */
    void planPackageRouter(List<Plan> planList);


    /**
     * 完善扩展字段
     *
     * @param planList
     */
    void planPackageExtend(List<Plan> planList);

    /**
     * 计划列表动态数据封装（库存数量）
     *
     * @param planList 计划列表
     * @Author: zhiqiang.lu
     * @Date: 2022/8/11 15:06
     */
    void planPackageStore(List<Plan> planList);

    CommonResult publish(List<String> planIdList);
}
