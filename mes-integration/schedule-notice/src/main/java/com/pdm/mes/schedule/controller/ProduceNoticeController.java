package com.pdm.mes.schedule.controller;

import com.pdm.mes.schedule.common.Result;
import com.pdm.mes.schedule.entity.request.SaleProductionSchedulingRequest;
import com.pdm.mes.schedule.service.ProduceNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@Api("排产单")
@RestController
@RequestMapping("/api/integration/produce/notice")
public class ProduceNoticeController {

    @Autowired
    private ProduceNoticeService produceNoticeService;

    @ApiOperation(value = "批量新增", notes = "同步排产单")
    @PostMapping("/save_batch_notice")
    public Result<?> saveBatchNotice(@RequestBody List<SaleProductionSchedulingRequest> schedulingDtoList) {
        if (CollectionUtils.isEmpty(schedulingDtoList)) {
            return Result.error("N", "排产单不能为空");
        }
        boolean flag = produceNoticeService.saveBatchNotice(schedulingDtoList);
        return flag ? Result.success() : Result.error("N","同步失败,请稍后再试");

    }

}
