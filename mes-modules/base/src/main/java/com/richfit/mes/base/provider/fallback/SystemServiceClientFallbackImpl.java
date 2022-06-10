package com.richfit.mes.base.provider.fallback;

import com.richfit.mes.base.provider.SystemServiceClient;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.Attachment;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.model.sys.Tenant;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/31 16:51
 */
@Component
public class SystemServiceClientFallbackImpl implements SystemServiceClient {

    @Override
    public CommonResult<TenantUserVo> getUserById(String id) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<List<ItemParam>> selectItemClass(String name, String code, String header) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<Attachment> attachment(@PathVariable String id) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<byte[]> getAttachmentInputStream(@PathVariable String id) {
        return null;
    }

    @Override
    public CommonResult<Tenant> getTenant(String id) {
        return null;
    }
}
