package com.kld.mes.wms.provider.fallback;

import com.kld.mes.wms.provider.SystemServiceClient;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.ItemParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/31 16:51
 */
@Component
public class SystemServiceClientFallbackImpl implements SystemServiceClient {

    @Override
    public CommonResult<ItemParam> findItemParamByCode(String code, String header) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<ItemParam> findItemParamByCode(String code, String tenantId, String header) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<List<ItemParam>> selectItemParamByCodeInner(String code, String label, String tenantId, String header) {
        return null;
    }

}
