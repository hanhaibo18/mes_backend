package com.kld.mes.erp.controller;

import com.kld.mes.erp.entity.order.ZC80PPIF009Response;
import com.kld.mes.erp.service.OrderService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.TrackItem;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: mes-backend
 * @description: ERP生产订单接口
 * @author: 王瑞
 * @create: 2022-08-01 15:02
 **/
@Slf4j
@Api(value = "ERP接口封装", tags = {"生产订单接口"})
@RestController
@RequestMapping("/api/integration/erp/order")
public class OrderController {

    @Autowired
    OrderService orderService;

    @ApiOperation(value = "查询订单", notes = "查询ERP生产订单")
    @GetMapping("/get")
    public CommonResult<ZC80PPIF009Response> getErpOrder(@ApiParam(value = "erp代号") @RequestParam String erpCode,
                                                         @ApiParam(value = "订单日期") @RequestParam String selectDate,
                                                         @ApiParam(value = "订单号") @RequestParam(required = false) String orderNo,
                                                         @ApiParam(value = "控制者") @RequestParam(required = false) String controller) {

        return CommonResult.success(orderService.getErpCode(erpCode, selectDate,controller, orderNo ));

    }

}
