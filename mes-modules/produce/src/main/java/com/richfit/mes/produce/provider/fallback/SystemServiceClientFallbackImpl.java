package com.richfit.mes.produce.provider.fallback;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.*;
import com.richfit.mes.common.model.sys.TenantUser;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;
import com.richfit.mes.common.model.sys.ItemClass;
import com.richfit.mes.common.model.sys.ItemParam;
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
