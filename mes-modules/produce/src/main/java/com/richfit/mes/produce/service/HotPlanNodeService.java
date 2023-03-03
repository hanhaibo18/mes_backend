package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.HotPlanNode;

import java.util.List;

public interface HotPlanNodeService extends IService<HotPlanNode> {
    List<HotPlanNode> getByDemandId(String demandId);

    List<HotPlanNode> getByPlanNodeByPlanId(String planId);
}
