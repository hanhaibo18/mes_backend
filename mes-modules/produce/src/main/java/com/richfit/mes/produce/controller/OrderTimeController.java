package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.hourSum.WorkingHours;
import com.richfit.mes.common.model.produce.hourSum.OrderTime;
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

    //根据订单号查询工时
    @GetMapping("/queryHour")
    public CommonResult<List> queryHour(int page, int limit, String branchCode, String orderNo, String startTime, String endTime) {
        QueryWrapper<List> wrapper = new QueryWrapper<List>();
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            wrapper.eq("ordere.branch_code", branchCode);
        }
        if (!StringUtils.isNullOrEmpty(orderNo)) {
            wrapper.eq("plan.order_no", orderNo);
        }
        if (!StringUtils.isNullOrEmpty(startTime) && !StringUtils.isNullOrEmpty(endTime)) {
            wrapper.between("ordere.order_date", startTime, endTime);
        }
        wrapper.isNotNull("plan.order_no");
//        wrapper.eq("item.is_schedule_complete", '1');
        List<OrderTime> list = orderTimeService.select(new Page(page, limit), wrapper);
        return CommonResult.success(list);
    }


}