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
 * @Author: fengxy
 * @Date: 2022年9月26日
 */
@Slf4j
@Api(value = "ERP接口封装", tags = {"跟单工时推送接口"})
@RestController
@RequestMapping("/api/integration/erp/work-hour")
public class CertWorkHourController {

    @Autowired
    CertWorkHourService certWorkHourService;

    @ApiOperation(value = "工时推送", notes = "根据跟单工序推送工时数据到ERP")
    @PostMapping("/pushToBs")
    public CommonResult<Boolean> certWorkHourPush(@ApiParam(value = "工序列表") @RequestBody List<TrackItem> trackItemList,
                                                  @ApiParam(value = "erp代号") @RequestParam(required = false) String erpCode,
                                                  @ApiParam(value = "订单号") @RequestParam String orderNo,
                                                  @ApiParam(value = "物料编码") @RequestParam String materialNo,
                                                  @ApiParam(value = "数量") @RequestParam(required = false) Integer qty,
                                                  @ApiParam(value = "单位") @RequestParam(required = false) String unit) {

        boolean b = certWorkHourService.sendWorkHour(trackItemList, erpCode, orderNo, materialNo, qty, unit);

        if (b) {
            return CommonResult.success(b);
        } else {
            return CommonResult.failed("报工失败");
        }

    }

}
