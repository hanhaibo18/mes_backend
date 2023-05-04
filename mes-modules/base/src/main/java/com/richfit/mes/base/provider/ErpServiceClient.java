package com.richfit.mes.base.provider;

import com.richfit.mes.base.provider.fallback.ErpServiceClientFallbackImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.security.constant.SecurityConstants;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author: 王瑞
 * @date: 2022/8/3 11:32
 */

@FeignClient(name = "erp-service", decode404 = true, fallback = ErpServiceClientFallbackImpl.class)
public interface ErpServiceClient {

    @PostMapping("/api/integration/erp/router/push")
    public CommonResult<Boolean> pushRouter(@RequestBody List<Router> routers, @RequestHeader(value = SecurityConstants.FROM) String header);


    @PostMapping("/api/integration/erp/router/push")
    public CommonResult erpSync(@RequestBody List<Router> routers);

    @GetMapping("/api/integration/erp/material/getMaterial")
    public CommonResult<List<Product>> getMaterial(@ApiParam(value = "日期") @RequestParam String date,
                                                   @ApiParam(value = "erp代号") @RequestParam String erpCode,
                                                   @RequestHeader(value = SecurityConstants.FROM) String header);

}
