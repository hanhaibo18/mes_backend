package com.kld.mes.erp.controller;

import com.kld.mes.erp.entity.order.creat.Zc80Ppif032;
import com.kld.mes.erp.entity.order.creat.Zc80Ppif032SO;
import com.kld.mes.erp.service.OrderService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.common.security.annotation.Inner;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @program: mes-backend
 * @description: ERP采购订单接口
 * @author: 王瑞
 * @create: 2022-08-01 15:02
 */
@Slf4j
@Api(value = "ERP接口封装", tags = {"采购订单接口"})
@RestController
@RequestMapping("/api/integration/erp/purchase/order")
public class PurchaseOrderController {

}
