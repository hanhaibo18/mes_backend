package com.kld.mes.pdm.provider;

import com.kld.mes.pdm.provider.fallback.SystemServiceClientFallbackImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.model.sys.Tenant;
import com.richfit.mes.common.security.constant.SecurityConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/31 16:28
 */
@FeignClient(name = "system-service", decode404 = true, fallback = SystemServiceClientFallbackImpl.class)
public interface SystemServiceClient {

    @GetMapping(value = "/api/sys/tenant/getTenantById/inner")
    public CommonResult<Tenant> tenantByIdInner(@RequestParam("id") String id, @RequestHeader(value = SecurityConstants.FROM) String header);

    @GetMapping(value = "/api/sys/item/param/find_by_code")
    public CommonResult<ItemParam> findItemParamByCode(@RequestParam("code") String code);

    @GetMapping(value = "/api/sys/item/param/find_by_code/inner")
    public CommonResult<ItemParam> findItemParamByCode(@RequestParam("code") String code, @RequestParam("tenantId") String tenantId, @RequestHeader(value = SecurityConstants.FROM) String header);

    @GetMapping(value = "/api/sys/item/item/param/list/inner")
    public CommonResult<List<ItemParam>> selectItemParamByCodeInner(@RequestParam("code") String code, @RequestParam("label") String label, @RequestParam("tenantId") String tenantId, @RequestHeader(value = SecurityConstants.FROM) String header);

}
