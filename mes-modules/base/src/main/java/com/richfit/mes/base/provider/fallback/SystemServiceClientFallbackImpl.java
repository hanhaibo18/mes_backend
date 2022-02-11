package com.richfit.mes.base.provider.fallback;

import com.richfit.mes.base.provider.SystemServiceClient;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/31 16:51
 */
@Component
public class SystemServiceClientFallbackImpl implements SystemServiceClient {

    @Override
    public CommonResult<TenantUserVo> getUserById(String id)  {
        return CommonResult.success(null);
    }

     @Override
    public CommonResult<List<ItemParam>> selectItemClass(String name,String code,String header)  {
        return CommonResult.success(null);
    }
}
