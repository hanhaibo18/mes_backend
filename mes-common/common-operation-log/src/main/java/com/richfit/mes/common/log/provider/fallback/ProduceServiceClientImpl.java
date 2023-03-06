package com.richfit.mes.common.log.provider.fallback;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.log.provider.ProduceServiceClient;
import com.richfit.mes.common.model.produce.Action;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * @author HanHaiBo
 * @date 2023/3/3 12:35
 */
@Component("saveAction")
public class ProduceServiceClientImpl implements ProduceServiceClient {
    @PostMapping("/api/produce/action/action")
    @Override
    public CommonResult<Boolean> saveAction(Action action) {
        return null;
    }
}
