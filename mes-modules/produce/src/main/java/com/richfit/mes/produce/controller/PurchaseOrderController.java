package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BasePageDto;
import com.richfit.mes.common.model.produce.ProducePurchaseOrder;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.produce.entity.PurchaseOrderDto;
import com.richfit.mes.produce.entity.PurchaseOrderSynchronizationDto;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.ProducePurchaseOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
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
@Api("计划管理")
@RestController
@RequestMapping("/api/produce/PurchaseOrder")
public class PurchaseOrderController {

    @Resource
    private com.richfit.mes.produce.provider.SystemServiceClient systemServiceClient;
//    private SystemServiceClient systemServiceClient;

    @Resource
    private ProducePurchaseOrderService producePurchaseOrderService;

    @Resource
    private ObjectMapper objectMapper;

    /**
     * 功能描述: 分页查询
     * @Author: xinYu.hou
     * @Date: 2022/1/10 11:15
     * @param queryDto
     * @return: CommonResult<IPage<ProducePurchaseOrder>>
     **/
    @GetMapping("/query/page")
    public CommonResult<IPage<ProducePurchaseOrder>> queryByOrder(BasePageDto<String> queryDto) throws ParseException {
        PurchaseOrderDto purchaseOrderDto = null;
        try {
            purchaseOrderDto = objectMapper.readValue(queryDto.getParam(), PurchaseOrderDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        QueryWrapper<ProducePurchaseOrder> wrapper = new QueryWrapper<>();
        boolean isEmpty = null != purchaseOrderDto;
        //处理传入时间类型
        if (isEmpty && purchaseOrderDto.getEndTime() != null && purchaseOrderDto.getStartTime() != null) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date createDate = dateFormat.parse(purchaseOrderDto.getStartTime());
            Date endDate = dateFormat.parse(purchaseOrderDto.getEndTime());
            wrapper.between("delivery_date",createDate,endDate);
        }
        if (isEmpty && !StringUtils.isNullOrEmpty(purchaseOrderDto.getOrderNo())){
            wrapper.eq("order_no",purchaseOrderDto.getOrderNo());
        }
        if (!StringUtils.isNullOrEmpty(purchaseOrderDto.getBranchCode())) {
            wrapper.like("branch_code", "%" + purchaseOrderDto.getBranchCode() + "%");
        }
        if (!StringUtils.isNullOrEmpty(purchaseOrderDto.getTenantId())) {
            wrapper.like("tenant_id", "%" + purchaseOrderDto.getTenantId() + "%");
        }
        IPage<ProducePurchaseOrder> productPage = producePurchaseOrderService.page(
                new Page<>(queryDto.getPage(), queryDto.getLimit()),wrapper);
        return CommonResult.success(productPage);
    }


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
            e.printStackTrace();
        }
        purchaseOrderSynchronizationDto.setCode("X092");
        if (purchaseOrderSynchronizationDto.getStartTime() == null || purchaseOrderSynchronizationDto.getEndTime() == null) {
            return CommonResult.success(null);
        }
        return CommonResult.success(producePurchaseOrderService.queryPurchaseSynchronization(purchaseOrderSynchronizationDto));
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
    @PostMapping("/save")
    public CommonResult<Boolean> saveProducePurchaseSynchronization(@RequestBody List<ProducePurchaseOrder> producePurchase){
        for (ProducePurchaseOrder producePurchaseOrder : producePurchase) {
            producePurchaseOrderService.save(producePurchaseOrder);
        }
        return CommonResult.success(true,"操作成功!");
    }

    /**
     * 功能描述: 定时保存同步信息
     * @Author: xinYu.hou
     * @Date: 2022/1/13 14:27
     * @return: CommonResult<Boolean>
     **/
    //    @Scheduled(cron = "0 30 23 * * ? ") @Scheduled(cron = "*/10 * * * * ?")
    @ApiOperation(value = "保存采购订单", notes = "定时保存采购订单")
    @PostMapping("/timing_save")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Boolean> saveTimingProducePurchaseSynchronization(){
        //拿到今天的同步数据
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        PurchaseOrderSynchronizationDto synchronizationDto = new PurchaseOrderSynchronizationDto();
        Date date = new Date();
        synchronizationDto.setStartTime(format.format(date));
        synchronizationDto.setEndTime(format.format(date));
        //获取工厂列表
        Boolean saveData = false;
        try {
            CommonResult<List<ItemParam>> listCommonResult = systemServiceClient.selectItemClass("", "");
            for (ItemParam itemParam : listCommonResult.getData()){
                log.info(itemParam.getCode());
                log.info(itemParam.getLabel());
                synchronizationDto.setCode(itemParam.getCode());
                List<ProducePurchaseOrder> producePurchaseOrders = producePurchaseOrderService.queryPurchaseSynchronization(synchronizationDto);
                for (ProducePurchaseOrder producePurchaseOrder : producePurchaseOrders){
                    saveData = producePurchaseOrderService.save(producePurchaseOrder);
                }
            }
        }catch (Exception e) {
            saveData = false;
            log.error(e.getMessage());
            e.printStackTrace();
        }
        return CommonResult.success(saveData);
    }
}
