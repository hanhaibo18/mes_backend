package com.richfit.mes.sys.provider;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.sys.provider.fallback.BaseServiceClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2020/6/30 15:28
 */
@FeignClient(name = "base-service", fallback = BaseServiceClientFallbackImpl.class)
public interface BaseServiceClient {

    @GetMapping(value = "/api/base/branch/select_branch_children_by_code")
    public CommonResult<List<Branch>> selectBranchChildByCode(@RequestParam("branchCode") String branchCode);

    @GetMapping(value = "/api/base/branch/branch/one")
    public CommonResult<Branch> selectBranchByCodeAndTenantId(@RequestParam("branchCode") String branchCode, @RequestParam("tenantId") String tenantId);

    @GetMapping(value = "/api/base/branch/queryCodeList")
    public CommonResult<List<Branch>> queryCode(@RequestParam("branchCode") String branchCode);
}
