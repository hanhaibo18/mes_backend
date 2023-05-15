package com.richfit.mes.produce.controller;


import com.baomidou.mybatisplus.extension.api.ApiController;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.RecordsOfSteelmakingOperations;
import com.richfit.mes.produce.service.RecordsOfSteelmakingOperationsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 炼钢作业记录表(RecordsOfSteelmakingOperations)表控制层
 *
 * @author makejava
 * @since 2023-05-15 10:18:52
 */
@RestController
@RequestMapping("api/produce/records_of_steelmaking_operations")
public class RecordsOfSteelmakingOperationsController extends ApiController {
    /**
     * 服务对象
     */
    @Autowired
    private RecordsOfSteelmakingOperationsService recordsOfSteelmakingOperationsService;

    @ApiOperation(value = "通过预装炉id查询炼钢记录信息", notes = "通过预装炉id查询炼钢记录信息")
    @GetMapping("/{prechargeFurnaceId}")
    public CommonResult<RecordsOfSteelmakingOperations> getByPrechargeFurnaceId(@PathVariable Long prechargeFurnaceId) {
        return CommonResult.success(recordsOfSteelmakingOperationsService.getByPrechargeFurnaceId(prechargeFurnaceId));
    }

}

