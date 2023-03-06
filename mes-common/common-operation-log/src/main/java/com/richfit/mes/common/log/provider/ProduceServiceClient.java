package com.richfit.mes.common.log.provider;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.log.provider.fallback.ProduceServiceClientImpl;
import com.richfit.mes.common.model.produce.Action;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author HanHaiBo
 * @date 2023/3/3 12:33
 */
@FeignClient(name = "produce-service", decode404 = true, fallback = ProduceServiceClientImpl.class)
public interface ProduceServiceClient {

    @PostMapping(value = "/api/produce/action/action")
    public CommonResult<Boolean> saveAction(@RequestBody Action action);
}
