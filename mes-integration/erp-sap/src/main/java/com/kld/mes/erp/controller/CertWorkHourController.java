package com.kld.mes.erp.controller;

import com.kld.mes.erp.service.CertWorkHourService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.TrackItem;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/21 14:29
 */
@Slf4j
@Api("ERP工时接口")
@RestController
@RequestMapping("/api/integration/work-hour")
public class CertWorkHourController {

    @Autowired
    CertWorkHourService certWorkHourService;

    @ApiOperation(value = "工时推送", notes = "根据跟单工序推送工时数据到ERP")
    @PostMapping("/push")
    public CommonResult<Boolean> certWorkHourPush(List<TrackItem> trackItemList, String erpCode, String orderNo, int qty, String unit) {

        boolean b = certWorkHourService.sendWorkHour(trackItemList, erpCode, orderNo, qty, unit);

        return new CommonResult(b);

    }

}
