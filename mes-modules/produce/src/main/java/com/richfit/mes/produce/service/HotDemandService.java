package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.HotDemand;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HotDemandService extends IService<HotDemand> {
    CommonResult importDemand(MultipartFile file, String branchCode);

    List<String> checkModel(List<String> idList, String branchCode);

    CommonResult<?> ratify(List<String> idList, Integer ratifyState, String branchCode);

    CommonResult modelProductionScheduling(List<String> idList, String branchCode);

    CommonResult<?> initPlanNode(List<String> idList, String branchCode);
}
