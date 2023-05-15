package com.richfit.mes.produce.controller;


import com.baomidou.mybatisplus.extension.api.ApiController;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.RecordsOfPourOperations;
import com.richfit.mes.produce.service.RecordsOfPourOperationsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * (RecordsOfPourOperations)表控制层
 *
 * @author makejava
 * @since 2023-05-15 10:18:41
 */
@RestController
@RequestMapping("/api/produce/records_of_pour_operations")
public class RecordsOfPourOperationsController extends ApiController {
    /**
     * 服务对象
     */
    @Autowired
    private RecordsOfPourOperationsService recordsOfPourOperationsService;

    @GetMapping("/{prechargeFurnaceId}")
    public CommonResult<RecordsOfPourOperations> getByPrechargeFurnaceId(@PathVariable Long prechargeFurnaceId) {
        return CommonResult.success(recordsOfPourOperationsService.getByPrechargeFurnaceId(prechargeFurnaceId));
    }

}

