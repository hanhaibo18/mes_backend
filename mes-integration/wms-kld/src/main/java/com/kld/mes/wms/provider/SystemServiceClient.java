package com.kld.mes.wms.provider;

import com.kld.mes.wms.provider.fallback.SystemServiceClientFallbackImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.ItemParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/31 16:28
 */
@FeignClient(name = "system-service", decode404 = true, fallback = SystemServiceClientFallbackImpl.class)
public interface SystemServiceClient {

    @GetMapping(value = "/api/sys/item/param/find_by_code")
    public CommonResult<ItemParam> findItemParamByCode(@RequestParam("code") String code);

}
