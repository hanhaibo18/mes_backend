package com.richfit.mes.base.controller;

import com.richfit.mes.base.service.WorkingHoursService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.WorkingHours;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@Api("工时统计")
@RestController
@RequestMapping("/api/base/workinghours")
public class WorkingHoursController extends BaseController {
    @Autowired
    private WorkingHoursService workingHoursService;

    @GetMapping("/page")
    public CommonResult<List<WorkingHours>> pageWorkingHours(int page, int limit, String branchCode, String productionOrder, String startTime, String endTime) {
        return CommonResult.success(workingHoursService.selectOrderTime());
    }
}
