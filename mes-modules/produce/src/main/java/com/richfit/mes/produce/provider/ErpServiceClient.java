package com.richfit.mes.produce.provider;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.provider.fallback.ErpServiceClientFallbackImpl;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/25 11:32
 */

@FeignClient(name = "erp-service", decode404 = true, fallback = ErpServiceClientFallbackImpl.class)
public interface ErpServiceClient {

    @PostMapping("/api/integration/erp/work-hour/push")
    public CommonResult<Boolean> certWorkHourPush(@RequestBody List<TrackItem> trackItemList, String erpCode,
                                                  @ApiParam(value = "订单号") String orderNo,
                                                  @ApiParam(value = "数量") int qty,
                                                  @ApiParam(value = "单位") String unit);

}
