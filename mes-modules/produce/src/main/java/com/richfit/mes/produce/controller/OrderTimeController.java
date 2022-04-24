package com.richfit.mes.produce.controller;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.base.Device;
import com.richfit.mes.common.model.base.WorkingHours;
import com.richfit.mes.common.model.produce.OrderTime;
import com.richfit.mes.common.security.constant.SecurityConstants;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.service.OrderTimeService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Api("订单工时统计")
@RestController
@RequestMapping("/api/produce/ordertime")
public class OrderTimeController extends BaseController {
    @Autowired
    private OrderTimeService orderTimeService;

    @Resource
    private BaseServiceClient baseServiceClient;


    @GetMapping("/page")
    public CommonResult<List<OrderTime>> pageAbnormal(int page, int limit, String branchCode, String productionOrder, String startTime, String endTime) {
        QueryWrapper<List<OrderTime>> wrapper = new QueryWrapper<>();
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            wrapper.eq("head.branch_code", branchCode);
        }
        if (!StringUtils.isNullOrEmpty(productionOrder)) {
            wrapper.eq("production_order", productionOrder);
        }
        if (!StringUtils.isNullOrEmpty(startTime) && !StringUtils.isNullOrEmpty(endTime)) {
            wrapper.between("ordere.start_time",startTime,endTime);
        }
        System.out.println(baseServiceClient);
        CommonResult<List<WorkingHours>> hours = baseServiceClient.pageWorkingHours();
        System.out.println(JSON.toJSON(hours));


        return CommonResult.success(orderTimeService.select(new Page<OrderTime>(page,limit),wrapper));
    }



}