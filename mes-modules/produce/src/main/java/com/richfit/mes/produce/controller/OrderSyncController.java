package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BasePageDto;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.produce.entity.OrdersSynchronizationDto;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.OrderService;
import com.richfit.mes.produce.service.OrderSyncService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: OrderSyncController.java
 * @Author: Hou XinYu
 * @Description: TODO
 * @CreateTime: 2022年01月19日 10:21:00
 */
@Slf4j
@RestController
@RequestMapping("/api/produce/orderSync")
public class OrderSyncController {

    @Autowired
    private OrderSyncService orderSyncService;

    @Autowired
    private ObjectMapper objectMapper;



    @ApiOperation(value = "查询采购同步订单信息", notes = "根据查询条件返回订单信息")
    @GetMapping("/query/synchronization_page")
    public CommonResult<List<Order>> queryByPurchaseOrderSynchronization(BasePageDto<String> queryDto) {
        OrdersSynchronizationDto ordersSynchronization = new OrdersSynchronizationDto();
        try {
            ordersSynchronization = objectMapper.readValue(queryDto.getParam(), OrdersSynchronizationDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        if (ordersSynchronization.getDate() == null || ordersSynchronization.getCode() == null) {
            return CommonResult.success(null);
        }
        return CommonResult.success(orderSyncService.queryOrderSynchronization(ordersSynchronization));
    }

    /**
     * 功能描述: 保存同步信息
     * @Author: xinYu.hou
     * @Date: 2022年1月18日14:19:44
     * @param orderList
     * @return: CommonResult<Boolean>
     **/
    @ApiOperation(value = "保存采购订单", notes = "保存采购订单")
    @PostMapping("/synchronization_save")
    public CommonResult<Boolean> saveOrderSynchronization(@RequestBody List<Order> orderList){
        return orderSyncService.saveOrderSync(orderList);
    }

}
