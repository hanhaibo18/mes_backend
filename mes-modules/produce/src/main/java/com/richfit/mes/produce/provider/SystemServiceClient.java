package com.richfit.mes.produce.provider;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.TenantUser;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.produce.provider.fallback.SystemServiceClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.richfit.mes.common.model.sys.ItemClass;
import com.richfit.mes.common.model.sys.ItemParam;
import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/31 16:28
 */
@FeignClient(name = "system-service", decode404 = true, fallback = SystemServiceClientFallbackImpl.class)
public interface SystemServiceClient {


    @GetMapping(value = "/api/sys/user/find_one")
    public CommonResult<TenantUserVo> getUserById(@RequestParam("id") String id);

    @GetMapping(value = "/api/sys/item/item/param/list")
    public CommonResult<List<ItemParam>> selectItemClass(@RequestParam("code") String code,@RequestParam("name") String name);
    
}
