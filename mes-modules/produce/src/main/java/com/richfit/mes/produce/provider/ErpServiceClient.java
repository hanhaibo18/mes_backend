package com.richfit.mes.produce.provider;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.LineStore;
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

    /**
     * 功能描述: ERP工时推送
     *
     * @param trackItemList 工序列表
     * @param erpCode       erpCode
     * @param orderNo       订单号码
     * @param qty           数量
     * @param unit          单位
     * @return Object
     **/
    @PostMapping("/api/integration/erp/work-hour/push")
    CommonResult<Object> certWorkHourPush(@RequestBody List<TrackItem> trackItemList,
                                          @RequestParam String erpCode,
                                          @RequestParam String orderNo,
                                          @RequestParam int qty,
                                          @RequestParam String unit);

    /**
     * 功能描述: ERP工时推送北石
     *
     * @param trackItemList 工序列表
     * @param erpCode       erpCode
     * @param orderNo       订单号码
     * @param materialNo    物料号码
     * @param qty           数量
     * @param unit          单位
     * @return Boolean
     **/
    @PostMapping("/api/integration/erp/work-hour/pushToBs")
    CommonResult<Boolean> certWorkHourPushToBs(@RequestBody List<TrackItem> trackItemList,
                                               @RequestParam String erpCode,
                                               @RequestParam String orderNo,
                                               @RequestParam String materialNo,
                                               @RequestParam int qty,
                                               @RequestParam String unit);

    /**
     * 功能描述: 获取erp订单
     *
     * @param erpCode    erpCode
     * @param selectDate 日期
     * @param orderNo    订单号码
     * @param controller 控制者
     * @return Boolean
     **/
    @GetMapping("/api/integration/erp/order/get")
    CommonResult<List<Order>> getErpOrder(@RequestParam String erpCode,
                                          @RequestParam String selectDate,
                                          @RequestParam String orderNo,
                                          @RequestParam String controller);

    /**
     * 功能描述: 获取erp订单
     *
     * @param erpCode    erpCode
     * @param selectDate 日期
     * @param orderNo    订单号码
     * @param controller 控制者
     * @param from       验证
     * @return Boolean
     **/
    @GetMapping("/api/integration/erp/order/get/inner")
    CommonResult<List<Order>> getErpOrderInner(@RequestParam String erpCode,
                                               @RequestParam String selectDate,
                                               @RequestParam String orderNo,
                                               @RequestParam String controller, @RequestHeader(SecurityConstants.FROM) String from);

    /**
     * 功能描述: 生产投料
     *
     * @param lineStore 物料信息
     * @return List<LineStore>
     * @Author: zhiqiang.lu
     * @Date: 2022/8/12 11:37
     **/
    @PostMapping("/api/integration/erp/feeding/store/send")
    CommonResult<LineStore> storeSendFeeding(@ApiParam(value = "erp代号") @RequestBody LineStore lineStore);

}
