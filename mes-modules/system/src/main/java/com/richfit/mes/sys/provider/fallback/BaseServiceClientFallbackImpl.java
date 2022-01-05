package com.richfit.mes.sys.provider.fallback;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.sys.provider.BaseServiceClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2020/6/30 15:32
 */
@Component
@Slf4j
public class BaseServiceClientFallbackImpl implements BaseServiceClient {
    @Override
    public CommonResult<List<Branch>> selectBranchChildByCode(String branchCode) {
        log.error("feign 查询机构信息失败:{}", branchCode);
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<Branch> selectBranchByCodeAndTenantId(String branchCode, String tenantId) {
        return CommonResult.success(null);
    }
}
