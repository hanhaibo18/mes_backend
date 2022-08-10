package com.kld.mes.wms.provider;

import com.kld.mes.wms.provider.fallback.SystemServiceClientFallbackImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.MaterialReceive;
import com.richfit.mes.common.model.produce.MaterialReceiveDetail;
import com.richfit.mes.common.model.sys.ItemParam;
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

    @GetMapping(value = "/api/sys/item/param/find_by_code")
    public CommonResult<ItemParam> findItemParamByCode(@RequestParam("code") String code);

    @GetMapping(value = "/api/sys/item/item/param/list")
    public CommonResult<List<ItemParam>> selectItemClass(@RequestParam("code") String code, @RequestParam("name") String name, @RequestHeader(value = SecurityConstants.FROM) String header);

    @GetMapping(value = "/api/produce/material_receive/getlastTime")
    public String getlastTime();

    @GetMapping(value = "/api/produce/material_receive/materialReceive/saveBatch")
    public Boolean materialReceiveSaveBatch(List<MaterialReceive> materialReceiveList);

    @GetMapping(value = "/api/produce/material_receive/detail/saveBatch")
    public Boolean detailSaveBatch(List<MaterialReceiveDetail> detailList);

}
