package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.OrderSyncLog;
import com.richfit.mes.produce.entity.QueryOrderSyncLogPageDto;
import com.richfit.mes.produce.service.OrderSyncLogService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @ClassName: OrderSyncLogController.java
 * @Author: Hou XinYu
 * @Description: 订单同步日志
 * @CreateTime: 2022年12月01日 10:05:00
 */

@Slf4j
@Api(value = "订单同步日志接口", tags = {"订单同步日志接口"})
@RestController
@RequestMapping("/api/produce/order_sync_log")
public class OrderSyncLogController {
    @Resource
    private OrderSyncLogService orderSyncLogService;

    @PostMapping("/query_log_page")
    public CommonResult<IPage<OrderSyncLog>> queryLogPage(@RequestBody QueryOrderSyncLogPageDto queryOrderSyncLogPageDto) {
        return CommonResult.success(orderSyncLogService.queryLogPage(queryOrderSyncLogPageDto));
    }
}
