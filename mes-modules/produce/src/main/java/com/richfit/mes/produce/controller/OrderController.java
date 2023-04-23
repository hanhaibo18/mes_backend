package com.richfit.mes.produce.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.base.BasePageDto;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.Action;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.common.model.produce.Plan;
import com.richfit.mes.common.model.util.ActionUtil;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.aop.OperationLog;
import com.richfit.mes.produce.aop.OperationLogAspect;
import com.richfit.mes.produce.entity.OrderDto;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.service.ActionService;
import com.richfit.mes.produce.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.richfit.mes.produce.aop.LogConstant.ORDER;
import static com.richfit.mes.produce.aop.LogConstant.ORDER_ID;

/**
 * @Author: GaoLiang
 * @Date: 2020/8/10 18:10
 */
@Slf4j
@Api(tags = "订单管理")
@RestController
@RequestMapping("/api/produce/order")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private ActionService actionService;

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private BaseServiceClient baseServiceClient;

    /**
     * 分页查询plan
     */
    @ApiOperation(value = "查询订单信息", notes = "根据查询条件返回订单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "queryDto", value = "订单属性", paramType = "BasePageDto")
    })
    @GetMapping("/query/page")
    public CommonResult queryByCondition(BasePageDto<String> queryDto) throws GlobalException {

        OrderDto orderDto = null;
        try {
            orderDto = objectMapper.readValue(queryDto.getParam(), OrderDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (null == orderDto) {
            orderDto = new OrderDto();
        }

//        orderDto.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        if (StringUtils.hasText(orderDto.getOrderCol())) {
            orderDto.setOrderCol(StrUtil.toUnderlineCase(orderDto.getOrderCol()));
        } else {
            orderDto.setOrderCol("modify_time");
            orderDto.setOrder("desc");
        }


        IPage<Order> orderList = orderService.queryPage(new Page<Order>(queryDto.getPage(), queryDto.getLimit()), orderDto);
        List<String> materialCodes = orderList.getRecords().stream().map(x -> x.getMaterialCode()).collect(Collectors.toList());
        //根据物料号查询物料信息
        List<Product> productList = baseServiceClient.listByMaterialNoList(materialCodes);
        if (!CollectionUtils.isEmpty(productList)) {
            Map<String, Product> productMap = productList.stream().collect(Collectors.toMap(x -> x.getMaterialNo(), x -> x));
            //把物料名称和图号拼接后存入订单描述字段内
            for (Order record : orderList.getRecords()) {
                Product product = productMap.get(record.getMaterialCode());
                //物料不为空
                if (!ObjectUtils.isEmpty(product)) {
                    record.setProductName(product.getProductName());//产品名称
                    record.setDrawingNo(product.getDrawingNo());//图号
                }
            }
        }

        return CommonResult.success(orderList);
    }

    @GetMapping("/query/pageEqMaterialCode")
    public CommonResult pageEqMaterialCode(BasePageDto<String> queryDto) throws GlobalException {

        OrderDto orderDto = null;
        try {
            orderDto = objectMapper.readValue(queryDto.getParam(), OrderDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (null == orderDto) {
            orderDto = new OrderDto();
        }
//        orderDto.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        if (StringUtils.hasText(orderDto.getOrderCol())) {
            orderDto.setOrderCol(StrUtil.toUnderlineCase(orderDto.getOrderCol()));
        } else {
            orderDto.setOrderCol("modify_time");
            orderDto.setOrder("desc");
        }


        List<Order> orderList = orderService.queryPageEqMaterialCode(orderDto);

        return CommonResult.success(orderList);

    }

    @GetMapping("/query/List")
    public CommonResult queryOrderList(BasePageDto<String> queryDto) throws GlobalException {

        OrderDto orderDto = null;
        try {
            orderDto = objectMapper.readValue(queryDto.getParam(), OrderDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if (null == orderDto) {
            orderDto = new OrderDto();
        }

//        orderDto.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        if (StringUtils.hasText(orderDto.getOrderCol())) {
            orderDto.setOrderCol(StrUtil.toUnderlineCase(orderDto.getOrderCol()));
        } else {
            orderDto.setOrderCol("modify_time");
            orderDto.setOrder("desc");
        }


        List<Order> orderList = orderService.queryList(orderDto);

        return CommonResult.success(orderList);
    }

    /**
     * 新增订单
     */
    @ApiOperation(value = "新增计划信息", notes = "新增计划信息")
    @ApiImplicitParam(name = "order", value = "订单", required = true, dataType = "Order", paramType = "body")
    @PostMapping("/save")
    @OperationLog(actionType = "0", actionItem = "0", argType = ORDER)
    public CommonResult<Boolean> savePlan(@RequestBody Order order) throws GlobalException {
        TenantUserDetails user = SecurityUtils.getCurrentUser();
        order.setStartTime(order.getOrderDate());
        order.setEndTime(order.getDeliveryDate());
        order.setTenantId(user.getTenantId());
        return CommonResult.success(orderService.save(order));
    }

    /**
     * 根据ID获取订单
     */
    @ApiOperation(value = "获取订单信息", notes = "根据id获取订单信息")
    @ApiImplicitParam(name = "id", value = "订单Id", required = true, dataType = "String", paramType = "path")
    @GetMapping("/{id}")
    public CommonResult<Order> getPlan(@PathVariable String id) throws GlobalException {
        Order order = orderService.queryOrder(id);
        orderService.findBranchName(order);
        return CommonResult.success(order);
    }

    /**
     * 更新订单
     */
    @ApiOperation(value = "修改计划信息", notes = "修改计划信息")
    @ApiImplicitParam(name = "order", value = "订单", required = true, dataType = "Order", paramType = "body")
    @PutMapping("/update")
    @OperationLog(actionType = "1", actionItem = "0", argType = ORDER)
    public CommonResult<Boolean> updateOrder(@RequestBody Order order) throws GlobalException {
        order.setStartTime(order.getOrderDate());
        order.setEndTime(order.getDeliveryDate());
        return CommonResult.success(orderService.updateById(order));
    }

    /**
     * 删除订单
     */
    @ApiOperation(value = "删除计划信息", notes = "根据计划id删除计划记录")
    @ApiImplicitParam(name = "id", value = "订单id", required = true, dataType = "String", paramType = "path")
    @OperationLog(actionType = "2", actionItem = "0", argType = ORDER_ID)
    @DeleteMapping("/delete/{id}")
    public void delById(@PathVariable String id) throws GlobalException {
        Order order = orderService.getById(id);
        //记录操作日志
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        actionService.saveAction(ActionUtil.buildAction(order.getBranchCode(), "2", "0", "订单号：" + order.getOrderSn(), OperationLogAspect.getIpAddress(request)));
        orderService.deleteOrder(id);
    }

    @ApiOperation(value = "导出订单信息", notes = "通过Excel文档导出订单信息")
    @GetMapping("/export_excel")
    public void exportExcel(BasePageDto<String> queryDto, HttpServletResponse rsp) {
        OrderDto orderDto = null;
        try {
            orderDto = objectMapper.readValue(queryDto.getParam(), OrderDto.class);
            QueryWrapper<Order> wrapper = new QueryWrapper<>();
            boolean isEmpty = null != orderDto;
            if (isEmpty && StringUtils.hasText(orderDto.getOrderSn())) {
                wrapper.like("order_sn", orderDto.getOrderSn());
            }
            if (isEmpty && StringUtils.hasText(orderDto.getMaterialCode())) {
                wrapper.like("material_code", orderDto.getMaterialCode());
            }
            if (isEmpty && StringUtils.hasText(orderDto.getStartTime())) {
                wrapper.ge("order_date", orderDto.getStartTime());
            }
            if (isEmpty && StringUtils.hasText(orderDto.getEndTime())) {
                wrapper.le("order_date", orderDto.getEndTime());
            }
            if (isEmpty && StringUtils.hasText(orderDto.getStatus())) {
                wrapper.eq("status", orderDto.getStatus());
            }
            if (isEmpty && StringUtils.hasText(orderDto.getBranchCode())) {
                wrapper.like("branch_code", orderDto.getBranchCode());
            }
            if (isEmpty && StringUtils.hasText(orderDto.getTenantId())) {
                wrapper.like("tenant_id", orderDto.getTenantId());
            }
            List<Order> list = orderService.list(wrapper);

            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");

            String fileName = "采购订单_" + format.format(new Date()) + ".xlsx";

            String[] columnHeaders = {"订单号", "物料号", "物料描述", "工厂编号", "控制者", "数量", "计划开始时间", "计划结束时间", "销售订单", "工号", "合同", "交货日期", "最后更新时间", "最后更新人", "订单日期"};

            String[] fieldNames = {"orderSn", "materialCode", "materialDesc", "branchCode", "inChargeOrg", "orderNum", "startTime", "endTime", "", "", "", "", "modifyTime", "modifyBy", "orderDate"};
            //export
            ExcelUtils.exportExcel(fileName, list, columnHeaders, fieldNames, rsp);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }


    @ApiOperation(value = "根据物料编码查询订单信息", notes = "根据物料编码查询订单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "materialCode", value = "物料编码", paramType = "String"),
            @ApiImplicitParam(name = "tenantId", value = "租户id", paramType = "String")
    })
    @PostMapping("/query_by_materialcode")
    public CommonResult<List<Order>> queryByMaterialCode(@RequestBody List<String> materialCodes, @RequestParam("tenantId") String tenantId) {
        QueryWrapper<Order> orderWrapper = new QueryWrapper<>();
        //根据物料号和订单号获取订单
        orderWrapper.in("material_code", materialCodes);
        orderWrapper.eq("tenant_id", tenantId);
        List<Order> orderList = orderService.list(orderWrapper);
        return CommonResult.success(orderList);
    }

    @ApiOperation(value = "根据计划逆向生成订单并且关联",notes = "根据计划逆向生成订单")
    @ApiImplicitParam(name = "PlanList", value = "计划列表", paramType = "Plan")
    @PostMapping("/save_by_plan")
    public CommonResult<Boolean> saveOrderByPlan(@RequestBody List<Plan> plans){
        return CommonResult.success(orderService.saveByPlan(plans));
    }

}
