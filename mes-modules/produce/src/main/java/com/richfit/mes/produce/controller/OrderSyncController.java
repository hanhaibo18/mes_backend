package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BasePageDto;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.produce.entity.OrderDto;
import com.richfit.mes.produce.entity.OrdersSynchronizationDto;
import com.richfit.mes.produce.service.OrderSyncService;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
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

    @ApiOperation(value = "导出订单信息", notes = "通过Excel文档导出订单信息")
    @GetMapping("/export_excel")
    public void exportExcel(BasePageDto<String> queryDto, HttpServletResponse rsp) {
        OrdersSynchronizationDto ordersSynchronization = new OrdersSynchronizationDto();
        try {
            ordersSynchronization = objectMapper.readValue(queryDto.getParam(), OrdersSynchronizationDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        List<Order> orderList = orderSyncService.queryOrderSynchronization(ordersSynchronization);

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");

        String fileName =   "订单同步_" + format.format(new Date()) + ".xlsx";

        String[] columnHeaders = {"订单号","物料号","物料描述","工厂编码","控制者","数量","计划开始时间","计划结束时间","KDAUF","BSTKD"};
        String[] fieldNames ={"orderSn","materialCode","materialDesc","branchCode","inChargeOrg","orderNum","startTime","endTime","",""};
        //export
        try {
            ExcelUtils.exportExcel(fileName, orderList , columnHeaders, fieldNames, rsp);
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
