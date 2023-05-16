package com.richfit.mes.produce.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.github.pagehelper.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.RecordsOfSteelmakingOperations;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.RecordsOfSteelmakingOperationsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    @Autowired
    private SystemServiceClient systemServiceClient;

    @ApiOperation(value = "通过预装炉id查询炼钢记录信息", notes = "通过预装炉id查询炼钢记录信息")
    @GetMapping("/{prechargeFurnaceId}")
    public CommonResult<RecordsOfSteelmakingOperations> getByPrechargeFurnaceId(@PathVariable Long prechargeFurnaceId) {
        return CommonResult.success(recordsOfSteelmakingOperationsService.getByPrechargeFurnaceId(prechargeFurnaceId));
    }

    @ApiOperation(value = "根据预装炉id初始化炼钢作业记录", notes = "根据预装炉id初始化炼钢作业记录")
    @GetMapping("/init")
    public CommonResult<Boolean> init(@RequestParam Long prechargeFurnaceId, @RequestParam String recordNo) {
        return CommonResult.success(recordsOfSteelmakingOperationsService.init(prechargeFurnaceId, recordNo));
    }

    @ApiOperation(value = "修改炼钢记录信息", notes = "修改炼钢记录信息")
    @PutMapping()
    public CommonResult<Boolean> update(@RequestBody RecordsOfSteelmakingOperations recordsOfSteelmakingOperations) {
        return CommonResult.success(recordsOfSteelmakingOperationsService.update(recordsOfSteelmakingOperations));
    }

    @ApiOperation(value = "批量删除炼钢信息", notes = "批量删除炼钢信息")
    @DeleteMapping
    public CommonResult<Boolean> delete(@RequestBody List<String> ids) {
        return CommonResult.success(recordsOfSteelmakingOperationsService.removeByIds(ids));
    }

    @ApiOperation(value = "记录审核", notes = "记录审核")
    @PostMapping("/check")
    public CommonResult<Boolean> check(@RequestBody List<String> ids, @RequestParam int state) {
        return CommonResult.success(recordsOfSteelmakingOperationsService.check(ids, state));
    }

    @ApiOperation(value = "炼钢记录管理", notes = "炼钢记录管理")
    @GetMapping("/record")
    public CommonResult<IPage<RecordsOfSteelmakingOperations>> record(String recordNo, Long prechargeFurnaceId, String furnaceNo, String typeOfSteel, String smeltingEquipment, String startTime, String endTime, Integer status, int page, int limit) {
        //获取登录用户权限
        List<String> roles = SecurityUtils.getRoles();
        String companyCode = SecurityUtils.getCurrentUser().getCompanyCode();
        companyCode = companyCode + "_JMAQ_BZZZ";
        //班组长查询
        if (roles.contains(companyCode)) {
            return CommonResult.success(recordsOfSteelmakingOperationsService.bzzcx(recordNo, prechargeFurnaceId, furnaceNo, typeOfSteel, smeltingEquipment, startTime, endTime, status, page, limit));
        }
        //普通操作工查询
        else {
            return CommonResult.success(recordsOfSteelmakingOperationsService.czgcx(recordNo, prechargeFurnaceId, furnaceNo, typeOfSteel, smeltingEquipment, startTime, endTime, status, page, limit));
        }
    }


}

