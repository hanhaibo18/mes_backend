package com.richfit.mes.produce.provider;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.provider.fallback.ErpServiceClientFallbackImpl;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/25 11:32
 */

@FeignClient(name = "erp-service", decode404 = true, fallback = ErpServiceClientFallbackImpl.class)
public interface ErpServiceClient {

    @PostMapping("/api/integration/erp/work-hour/push")
    public CommonResult<Boolean> certWorkHourPush(@RequestBody List<TrackItem> trackItemList,
                                                  @RequestParam String erpCode,
                                                  @RequestParam(value = "订单号") String orderNo,
                                                  @RequestParam(value = "数量") int qty,
                                                  @RequestParam(value = "单位") String unit);

    /**
     * 功能描述: ERP库存查询
     *
     * @param materialNos 物料号码
     * @param erpCode     erpCode
     * @Author: zhiqiang.lu
     * @Date: 2022/8/12 11:37
     **/
    @GetMapping("/api/integration/erp/storage/getStorage")
    public CommonResult<List<Product>> getStorage(@ApiParam(value = "物料号") @RequestParam String materialNos,
                                                  @ApiParam(value = "erp代号") @RequestParam String erpCode);

}
