package com.richfit.mes.produce.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.RecordsOfPourOperations;
import com.richfit.mes.common.model.produce.RecordsOfSteelmakingOperations;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.service.RecordsOfPourOperationsService;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @ApiOperation(value = "通过预装炉id查询浇注记录信息", notes = "通过预装炉id查询浇注记录信息")
    @GetMapping("/{prechargeFurnaceId}")
    public CommonResult<RecordsOfPourOperations> getByPrechargeFurnaceId(@PathVariable Long prechargeFurnaceId) {
        return CommonResult.success(recordsOfPourOperationsService.getByPrechargeFurnaceId(prechargeFurnaceId));
    }

    @ApiOperation(value = "根据预装炉id初始化浇注作业记录", notes = "根据预装炉id初始化炼钢作业记录")
    @GetMapping("/init")
    public CommonResult<Boolean> init(@RequestParam Long prechargeFurnaceId, @RequestParam String recordNo) {
        return CommonResult.success(recordsOfPourOperationsService.init(prechargeFurnaceId, recordNo));
    }

    @ApiOperation(value = "修改浇注记录信息", notes = "修改炼钢记录信息")
    @PutMapping()
    public CommonResult<Boolean> update(@RequestBody RecordsOfPourOperations recordsOfPourOperations) {
        return CommonResult.success(recordsOfPourOperationsService.update(recordsOfPourOperations));
    }

    @ApiOperation(value = "批量删除浇注记录信息", notes = "批量删除炼钢信息")
    @DeleteMapping
    public CommonResult<Boolean> delete(@RequestBody List<String> ids) {
        return CommonResult.success(recordsOfPourOperationsService.removeByIds(ids));
    }

    @ApiOperation(value = "记录审核", notes = "记录审核")
    @PostMapping("/check")
    public CommonResult<Boolean> check(@RequestBody List<String> ids, @RequestParam int state) {
        return CommonResult.success(recordsOfPourOperationsService.check(ids, state));
    }

//    @ApiOperation(value = "浇注记录管理列表", notes = "浇注记录管理列表")
//    @GetMapping("/record_page")
//    public CommonResult<IPage<RecordsOfSteelmakingOperations>> record(String recordNo, Long prechargeFurnaceId, String furnaceNo, String typeOfSteel, String smeltingEquipment, String startTime, String endTime, Integer status, int page, int limit) {
//        //获取登录用户权限
//        List<String> roles = SecurityUtils.getRoles();
//        String companyCode = SecurityUtils.getCurrentUser().getCompanyCode();
//        companyCode = companyCode + "_JMAQ_BZZZ";
//        //班组长查询
//        if (roles.contains(companyCode)) {
//            return CommonResult.success(recordsOfPourOperationsService.bzzcx(recordNo, prechargeFurnaceId, furnaceNo, typeOfSteel, smeltingEquipment, startTime, endTime, status, page, limit));
//        }
//        //普通操作工查询
//        else {
//            return CommonResult.success(recordsOfPourOperationsService.czgcx(recordNo, prechargeFurnaceId, furnaceNo, typeOfSteel, smeltingEquipment, startTime, endTime, status, page, limit));
//        }
//    }

}

