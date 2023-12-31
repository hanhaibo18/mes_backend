package com.kld.mes.plm.controller;

import com.kld.mes.plm.entity.request.SaleProductionSchedulingRequest;
import com.kld.mes.plm.service.ProduceNoticeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Api("排产单")
@RestController
@RequestMapping("/api/produce")
public class ProduceNoticeController {

    @Autowired
    private ProduceNoticeService produceNoticeService;

    @ApiOperation(value = "批量新增", notes = "批量新增排产单")
    @PostMapping("/save_batch_notice")
    public boolean saveBatchNotice(@RequestBody List<SaleProductionSchedulingRequest> schedulingDtoList) {
        return produceNoticeService.saveBatchNotice(schedulingDtoList);
    }


}
