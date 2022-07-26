package com.kld.mes.erp.controller;

import com.kld.mes.erp.service.CertWorkHourService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.TrackItem;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/21 14:29
 */
@Slf4j
@Api(value = "ERP接口封装", tags = {"跟单工时推送接口"})
@RestController
@RequestMapping("/api/integration/erp/work-hour")
public class CertWorkHourController {

    @Autowired
    CertWorkHourService certWorkHourService;

    @ApiOperation(value = "工时推送", notes = "根据跟单工序推送工时数据到ERP")
    @PostMapping("/push")
    public CommonResult<Boolean> certWorkHourPush(@ApiParam(value = "工序列表") @RequestBody List<TrackItem> trackItemList,
                                                  @ApiParam(value = "erp代号") @RequestParam String erpCode,
                                                  @ApiParam(value = "订单号") @RequestParam String orderNo,
                                                  @ApiParam(value = "数量") @RequestParam int qty,
                                                  @ApiParam(value = "单位") @RequestParam String unit) {

        boolean b = certWorkHourService.sendWorkHour(trackItemList, erpCode, orderNo, qty, unit);

        if (b) {
            return CommonResult.success(b);
        } else {
            return CommonResult.failed("报工失败");
        }

    }

}
