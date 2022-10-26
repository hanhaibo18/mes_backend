package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BasePageDto;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.common.model.produce.ProducePurchaseOrder;
import com.richfit.mes.produce.entity.OrdersSynchronizationDto;
import com.richfit.mes.produce.entity.PurchaseOrderDto;
import com.richfit.mes.produce.entity.PurchaseOrderSynchronizationDto;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.PurchaseOrderService;
import com.richfit.mes.produce.service.PurchaseOrderSyncService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @ClassName: ProducePurchaseOrderController.java
 * @Author: Hou XinYu
 * @Description: 采购订单
 * @CreateTime: 2022年01月07日 15:46:00
 */
@Slf4j
@Api(value = "采购订单", tags = {"采购订单"})
@RestController
@RequestMapping("/api/produce/PurchaseOrderSync")
public class PurchaseOrderSyncController {

    @Resource
    private PurchaseOrderSyncService producePurchaseOrderSyncService;

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 功能描述: 同步分页查询
     * @Author: xinYu.hou
     * @Date: 2022/1/10 11:15
     * @param queryDto
     * @return: CommonResult<IPage<ProducePurchaseOrder>>
     **/
    @ApiOperation(value = "查询采购同步订单信息", notes = "根据查询条件返回订单信息")
    @GetMapping("/query/synchronization_page")
    public CommonResult<List<ProducePurchaseOrder>> queryByPurchaseOrderSynchronization(BasePageDto<String> queryDto) {
        PurchaseOrderSynchronizationDto purchaseOrderSynchronizationDto = new PurchaseOrderSynchronizationDto();
        try {
            purchaseOrderSynchronizationDto = objectMapper.readValue(queryDto.getParam(), PurchaseOrderSynchronizationDto.class);
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
        if (purchaseOrderSynchronizationDto.getStartTime() == null || purchaseOrderSynchronizationDto.getEndTime() == null) {
            return CommonResult.success(null);
        }
        return CommonResult.success(producePurchaseOrderSyncService.queryPurchaseSynchronization(purchaseOrderSynchronizationDto));
    }

    /**
     * 功能描述: 保存同步信息
     * @Author: xinYu.hou
     * @Date: 2022/1/13 14:27
     * @param producePurchase
     * @return: CommonResult<Boolean>
     **/
    @ApiOperation(value = "保存采购订单", notes = "保存采购订单")
    @Transactional(rollbackFor = Exception.class)
    @PostMapping("/synchronization_save")
    public CommonResult<Boolean> saveProducePurchaseSynchronization(@RequestBody List<ProducePurchaseOrder> producePurchase){
        return producePurchaseOrderSyncService.saveProducePurchaseSynchronization(producePurchase);
    }

    @ApiOperation(value = "导出订单信息", notes = "通过Excel文档导出订单信息")
    @GetMapping("/export_excel")
    public void exportExcel(BasePageDto<String> queryDto, HttpServletResponse rsp) {
        PurchaseOrderSynchronizationDto purchaseOrderSynchronizationDto = new PurchaseOrderSynchronizationDto();
        try {
            purchaseOrderSynchronizationDto = objectMapper.readValue(queryDto.getParam(), PurchaseOrderSynchronizationDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        List<ProducePurchaseOrder> purchaseOrderList = producePurchaseOrderSyncService.queryPurchaseSynchronization(purchaseOrderSynchronizationDto);

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");

        String fileName =   "采购同步订单_" + format.format(new Date()) + ".xlsx";

        String[] columnHeaders = {"订单号","订单采购类型","物料号","物料描述","图号","数量","Unit","单位","采购时间","工厂编码","项目编号","八位码","供应商"};
        String[] fieldNames ={"orderNo","orderType","materialNo","materialRemark","drawingNo","number","","","branchCode","materialCode","lifnr"};
        //export
        try {
            ExcelUtils.exportExcel(fileName, purchaseOrderList , columnHeaders, fieldNames, rsp);
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }
}
















