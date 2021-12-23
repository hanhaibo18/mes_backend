package com.richfit.mes.produce.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.base.BasePageDto;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.core.utils.HumpToLineUtils;
import com.richfit.mes.common.model.produce.Action;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.OrderDto;
import com.richfit.mes.produce.service.ActionService;
import com.richfit.mes.produce.service.OrderService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: GaoLiang
 * @Date: 2020/8/10 18:10
 */
@Slf4j
@Api("订单管理")
@RestController
@RequestMapping("/api/produce/order")
public class OrderController extends BaseController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private ActionService actionService;

    @Autowired
    private ObjectMapper objectMapper;
    /**
     * 分页查询plan
     */
    @ApiOperation(value = "查询订单信息", notes = "根据查询条件返回订单信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name="queryDto",value="订单属性", paramType="BasePageDto")
    })
    @GetMapping("/query/page")
    public CommonResult queryByCondition(BasePageDto<String> queryDto) throws GlobalException {

        OrderDto orderDto = null;
        try {
            orderDto = objectMapper.readValue(queryDto.getParam(), OrderDto.class);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        if(null==orderDto){
            orderDto = new OrderDto();
        }

        orderDto.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        if(StringUtils.hasText(orderDto.getOrderCol())){
            orderDto.setOrderCol(StrUtil.toUnderlineCase(orderDto.getOrderCol()));
        }else{
            orderDto.setOrderCol("status");
            orderDto.setOrder("desc");
        }


        IPage<Order> orderList = orderService.queryPage(new Page<Order>(queryDto.getPage(), queryDto.getLimit()),orderDto);

        return CommonResult.success(orderList);

    }

    /**
     * 新增订单
     */
    @ApiOperation(value = "新增计划信息", notes = "新增计划信息")
    @ApiImplicitParam(name = "order", value = "订单", required = true, dataType = "Order", paramType = "body")
    @PostMapping("/save")
    public CommonResult<Boolean> savePlan(@RequestBody Order order) throws GlobalException{
        TenantUserDetails user = SecurityUtils.getCurrentUser();
        order.setTenantId(user.getTenantId());
        order.setBranchCode(user.getOrgId());
        order.setInchargeOrg(user.getBelongOrgId());

        Action action = new Action();
        action.setActionType("0");
        action.setActionItem("0");
        action.setRemark("订单号：" + order.getOrderSn());
        actionService.saveAction(action);

        return CommonResult.success(orderService.save(order));
    }

    /**
     * 根据ID获取订单
     *
     */
    @ApiOperation(value = "获取订单信息", notes = "根据id获取订单信息")
    @ApiImplicitParam(name = "id", value = "订单Id", required = true, dataType = "String", paramType = "path")
    @GetMapping("/{id}")
    public CommonResult<Order> getPlan(@PathVariable String id) throws GlobalException {
        Order order = orderService.getById(id);
        orderService.findBranchName(order);
        return CommonResult.success(order);
    }

    /**
     * 更新订单
     */
    @ApiOperation(value = "修改计划信息", notes = "修改计划信息")
    @ApiImplicitParam(name = "order", value = "订单", required = true, dataType = "Order", paramType = "body")
    @PutMapping("/update")
    public CommonResult<Boolean> updateOrder(@RequestBody Order order) throws GlobalException{

        Action action = new Action();
        action.setActionType("1");
        action.setActionItem("0");
        action.setRemark("订单号：" + order.getOrderSn());
        actionService.saveAction(action);

        return CommonResult.success(orderService.updateById(order));
    }

    /**
     * 删除订单
     */
    @ApiOperation(value = "删除计划信息", notes = "根据计划id删除计划记录")
    @ApiImplicitParam(name = "id", value = "订单id", required = true, dataType = "String", paramType = "path")
    @DeleteMapping("/delete/{id}")
    public CommonResult<Boolean> delById(@PathVariable String id) throws GlobalException{
        //计划状态为‘0’ 未开始时，才能删除
        Order order = orderService.getById(id);
        if(0!=order.getStatus()){
            return CommonResult.failed("订单已匹配计划，请先删除计划，否则不能删除!");
        }
        Action action = new Action();
        action.setActionType("2");
        action.setActionItem("0");
        action.setRemark("订单号：" + order.getOrderSn());
        actionService.saveAction(action);

        return CommonResult.success(orderService.removeById(id));
    }
}
