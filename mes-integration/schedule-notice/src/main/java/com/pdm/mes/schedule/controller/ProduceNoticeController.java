package com.pdm.mes.schedule.controller;

import com.pdm.mes.schedule.common.Result;
import com.pdm.mes.schedule.entity.request.SaleProductionSchedulingRequest;
import com.pdm.mes.schedule.service.ProduceNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@Api("排产单")
@RestController
@RequestMapping("/api/produce/notice")
public class ProduceNoticeController {

    @Autowired
    private ProduceNoticeService produceNoticeService;

    @ApiOperation(value = "批量新增", notes = "批量新增排产单")
    @PostMapping("/save_batch_notice")
    public Result<?> saveBatchNotice(@RequestBody List<SaleProductionSchedulingRequest> schedulingDtoList) {
        produceNoticeService.saveBatchNotice(schedulingDtoList);
        return Result.success();
    }

}
