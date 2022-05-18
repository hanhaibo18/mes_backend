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
import com.richfit.mes.common.model.produce.ProducePurchaseOrder;
import com.richfit.mes.produce.entity.PurchaseOrderDto;
import com.richfit.mes.produce.service.PurchaseOrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
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
     *
     * @param queryDto
     * @Author: xinYu.hou
     * @Date: 2022/1/10 11:15
     * @return: CommonResult<IPage < ProducePurchaseOrder>>
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
        if (isEmpty && !StringUtils.isNullOrEmpty(purchaseOrderDto.getEndTime()) && !StringUtils.isNullOrEmpty(purchaseOrderDto.getStartTime())) {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date createDate = dateFormat.parse(purchaseOrderDto.getStartTime());
            Date endDate = dateFormat.parse(purchaseOrderDto.getEndTime());
            wrapper.between("purchase_date", createDate, endDate);
        }
        if (isEmpty && !StringUtils.isNullOrEmpty(purchaseOrderDto.getOrderNo())) {
            wrapper.like("order_no", "%" + purchaseOrderDto.getOrderNo() + "%");
        }
        if (isEmpty && !StringUtils.isNullOrEmpty(purchaseOrderDto.getBranchCode())) {
            wrapper.like("branch_code", "%" + purchaseOrderDto.getBranchCode() + "%");
        }
        if (!StringUtils.isNullOrEmpty(purchaseOrderDto.getTenantId())) {
            wrapper.like("tenant_id", "%" + purchaseOrderDto.getTenantId() + "%");
        }
        if (isEmpty && !StringUtils.isNullOrEmpty(purchaseOrderDto.getMaterialNo())) {
            wrapper.like("material_no", "%" + purchaseOrderDto.getMaterialNo() + "%");
        }
        IPage<ProducePurchaseOrder> productPage = producePurchaseOrderService.page(
                new Page<>(queryDto.getPage(), queryDto.getLimit()), wrapper);
        return CommonResult.success(productPage);
    }

    @ApiOperation(value = "导出采购订单信息", notes = "通过Excel文档导出采购订单信息")
    @GetMapping("/export_excel")
    public void exportExcel(BasePageDto<String> queryDto, HttpServletResponse rsp) {
        PurchaseOrderDto purchaseOrderDto = null;
        try {
            purchaseOrderDto = objectMapper.readValue(queryDto.getParam(), PurchaseOrderDto.class);
            QueryWrapper<ProducePurchaseOrder> wrapper = new QueryWrapper<>();
            boolean isEmpty = null != purchaseOrderDto;
            //处理传入时间类型
            if (isEmpty && purchaseOrderDto.getEndTime() != null && purchaseOrderDto.getStartTime() != null) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Date createDate = dateFormat.parse(purchaseOrderDto.getStartTime());
                Date endDate = dateFormat.parse(purchaseOrderDto.getEndTime());
                wrapper.between("purchase_date", createDate, endDate);
            }
            if (isEmpty && !StringUtils.isNullOrEmpty(purchaseOrderDto.getOrderNo())) {
                wrapper.like("order_no", "%" + purchaseOrderDto.getOrderNo() + "%");
            }
            if (isEmpty && !StringUtils.isNullOrEmpty(purchaseOrderDto.getMaterialNo())) {
                wrapper.like("material_no", "%" + purchaseOrderDto.getMaterialNo() + "%");
            }
            if (isEmpty && !StringUtils.isNullOrEmpty(purchaseOrderDto.getBranchCode())) {
                wrapper.like("branch_code", "%" + purchaseOrderDto.getBranchCode() + "%");
            }
            if (isEmpty && !StringUtils.isNullOrEmpty(purchaseOrderDto.getTenantId())) {
                wrapper.like("tenant_id", "%" + purchaseOrderDto.getTenantId() + "%");
            }
            List<ProducePurchaseOrder> list = producePurchaseOrderService.list(wrapper);

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");

            String fileName = "采购订单_" + format.format(new Date()) + ".xlsx";

            String[] columnHeaders = {"订单号", "物料号", "物料描述", "图号", "数量", "下单日期", "工厂代码", "项目编号", "八位码", "修改时间", "创建人"};

            String[] fieldNames = {"orderNo", "materialNo", "materialRemark", "drawingNo", "number", "purchaseDate", "branchCode", "projectNo", "materialCode", "modifyTime", "createBy"};
            //export
            ExcelUtils.exportExcel(fileName, list, columnHeaders, fieldNames, rsp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}
