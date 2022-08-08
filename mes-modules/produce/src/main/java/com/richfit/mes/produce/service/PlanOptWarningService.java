package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.PlanOptWarning;

import java.util.List;

/**
 * @Author: zhiqiang.lu
 * @Date: 2020/8/8 9:59
 */
public interface PlanOptWarningService extends IService<PlanOptWarning> {

    /**
     * 功能描述: 通过计划id查询预警工序数据
     *
     * @param planId 计划id
     * @Author: zhiqiang.lu
     * @Date: 2022/8/8 15:06
     **/
    List<PlanOptWarning> queryList(String planId) throws Exception;
}
