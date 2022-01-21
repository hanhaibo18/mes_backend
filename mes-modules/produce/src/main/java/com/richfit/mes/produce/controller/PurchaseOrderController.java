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
import com.richfit.mes.produce.entity.PurchaseOrderDto;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.PurchaseOrderService;
import com.richfit.mes.produce.service.PurchaseOrderSyncService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

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
    private PurchaseOrderService producePurchaseOrderService;

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
            wrapper.between("purchase_date",createDate,endDate);
        }
        if (isEmpty && !StringUtils.isNullOrEmpty(purchaseOrderDto.getOrderNo())){
            wrapper.like("order_no","%" + purchaseOrderDto.getOrderNo() + "%");
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

}
