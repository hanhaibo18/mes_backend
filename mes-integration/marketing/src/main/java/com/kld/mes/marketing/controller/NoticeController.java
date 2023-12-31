package com.kld.mes.marketing.controller;

import com.kld.mes.marketing.service.ProduceNoticeService;
import com.kld.mes.marketing.common.Result;
import com.kld.mes.marketing.entity.request.SaleProductionSchedulingRequest;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@Api("排产单")
@RestController
@RequestMapping("/api/integration/marketing/notice")
public class NoticeController {

    @Autowired
    private ProduceNoticeService produceNoticeService;

    @ApiOperation(value = "批量新增", notes = "同步排产单")
    @PostMapping("/save_batch_notice")
    public Result<?> saveBatchNotice(@RequestBody List<SaleProductionSchedulingRequest> schedulingDtoList) {
        if (CollectionUtils.isEmpty(schedulingDtoList)) {
            return Result.error("N", "排产单不能为空");
        }
        boolean flag = produceNoticeService.saveBatchNotice(schedulingDtoList);
        return flag ? Result.success() : Result.error("N", "同步失败,请稍后再试");

    }

    @ApiOperation(value = "测试", notes = "测试")
    @GetMapping("/test")
    public Result<?> test() {
        return Result.success("测试成功");
    }
}
