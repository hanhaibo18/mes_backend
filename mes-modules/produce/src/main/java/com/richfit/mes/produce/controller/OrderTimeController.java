package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.WorkingHours;
import com.richfit.mes.common.model.produce.OrderTime;
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
    public CommonResult<List> pageAbnormal(int page, int limit, String branchCode, String orderSn, String startTime, String endTime) {
        QueryWrapper<List> wrapper = new QueryWrapper<List>();
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            wrapper.eq("head.branch_code", branchCode);
        }
        if (!StringUtils.isNullOrEmpty(orderSn)) {
            wrapper.eq("ordere.order_sn", orderSn);
        }
        if (!StringUtils.isNullOrEmpty(startTime) && !StringUtils.isNullOrEmpty(endTime)) {
            wrapper.between("ordere.start_time", startTime, endTime);
        }
        List<OrderTime> select = orderTimeService.select(new Page(page, limit), wrapper);
        List<WorkingHours> workingHours = baseServiceClient.pageWorkingHours().getData();
        //遍历集合将准结工时，额定工时，总工时插入到OrderTime中
        for (OrderTime orderTime : select) {
            for (WorkingHours workingHour : workingHours) {
                if (workingHour.getDrawNo()!=null && orderTime.getDrawNo().equals(workingHour.getDrawNo())) {
                    orderTime.setPrepareEndHours(workingHour.getPrepareEndHours());
                    orderTime.setSinglePieceHours(workingHour.getSinglePieceHours());
                    orderTime.setTotalProductiveHours(workingHour.getTotalProductiveHours());
                }
            }
        }
        return CommonResult.success(select);
    }


}