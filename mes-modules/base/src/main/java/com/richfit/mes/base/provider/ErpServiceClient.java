package com.richfit.mes.base.provider;

import com.richfit.mes.base.provider.fallback.ErpServiceClientFallbackImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.security.constant.SecurityConstants;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @author: 王瑞
 * @date: 2022/8/3 11:32
 */

@FeignClient(name = "erp-service2", decode404 = true, fallback = ErpServiceClientFallbackImpl.class)
public interface ErpServiceClient {

    @PostMapping("/api/integration/erp/router/push")
    public CommonResult<Boolean> pushRouter(@RequestBody List<Router> routers, @RequestHeader(value = SecurityConstants.FROM) String header);

}
