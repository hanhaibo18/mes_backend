package com.richfit.mes.produce.provider;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.security.constant.SecurityConstants;
import com.richfit.mes.produce.provider.fallback.ErpServiceClientFallbackImpl;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

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
                                                  @RequestParam String orderNo,
                                                  @RequestParam int qty,
                                                  @RequestParam String unit);

    @PostMapping("/api/integration/erp/work-hour/pushToBs")
    public CommonResult<Boolean> certWorkHourPushToBs(@RequestBody List<TrackItem> trackItemList,
                                                      @RequestParam String erpCode,
                                                      @RequestParam String orderNo,
                                                      @RequestParam String materialNo,
                                                      @RequestParam int qty,
                                                      @RequestParam String unit);


    @GetMapping("/api/integration/erp/order/get")
    public CommonResult<List<Order>> getErpOrder(@RequestParam String erpCode,
                                                 @RequestParam String selectDate,
                                                 @RequestParam String orderNo,
                                                 @RequestParam String controller);

    @GetMapping("/api/integration/erp/order/get/inner")
    public CommonResult<List<Order>> getErpOrderInner(@RequestParam String erpCode,
                                                      @RequestParam String selectDate,
                                                      @RequestParam String orderNo,
                                                      @RequestParam String controller, @RequestHeader(SecurityConstants.FROM) String from);

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
