package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.HotDemand;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.List;

public interface HotDemandService extends IService<HotDemand> {
    CommonResult importDemand(MultipartFile file, String branchCode);

    @Transactional(rollbackFor = Exception.class)
    CommonResult importDemandYL(MultipartFile file, String branchCode);

    List<String> checkModel(List<String> idList, String branchCode);

    CommonResult<?> ratify(List<String> idList, Integer ratifyState, String branchCode);

    CommonResult<?> ratifyYL(List<String> idList, Integer ratifyState, String branchCode);

    CommonResult revocation(List<String> idList);

    CommonResult modelProductionScheduling(List<String> idList, String branchCode);

    String workblankTypeToBranchCode(String workblankType);

    CommonResult<?> initPlanNode(List<String> idList, String branchCode);

    HashMap<String, List<String>> getStringListHashMap(List<HotDemand> hotDemands);

    String getSubmitOrderOrg(String branchCode, TenantUserDetails currentUser);

    //请合并相同项目产品
    void checkDemand(String workNo, String drawNo, String versionNum);
}
