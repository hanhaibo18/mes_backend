package com.richfit.mes.produce.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.RecordsOfSteelmakingOperations;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.RecordsOfSteelmakingOperationsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 炼钢作业记录表(RecordsOfSteelmakingOperations)表控制层
 *
 * @author makejava
 * @since 2023-05-15 10:18:52
 */
@Api(value = "炼钢记录", tags = {"炼钢记录"})
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
    public CommonResult<RecordsOfSteelmakingOperations> getByPrechargeFurnaceId(@ApiParam(value = "预装炉id") @PathVariable Long prechargeFurnaceId) {
        return CommonResult.success(recordsOfSteelmakingOperationsService.getByPrechargeFurnaceId(prechargeFurnaceId));
    }

    @ApiOperation(value = "根据预装炉id初始化炼钢作业记录", notes = "根据预装炉id初始化炼钢作业记录")
    @GetMapping("/init")
    public CommonResult<Boolean> init(@ApiParam(value = "预装炉id") @RequestParam Long prechargeFurnaceId, @ApiParam(value = "车间编码") @RequestParam String branchCode) {
        return CommonResult.success(recordsOfSteelmakingOperationsService.init(prechargeFurnaceId, branchCode));
    }

    @ApiOperation(value = "修改炼钢记录信息", notes = "修改炼钢记录信息")
    @PutMapping
    public CommonResult<Boolean> update(@RequestBody RecordsOfSteelmakingOperations recordsOfSteelmakingOperations) {
        return CommonResult.success(recordsOfSteelmakingOperationsService.update(recordsOfSteelmakingOperations));
    }

    @ApiOperation(value = "批量删除炼钢信息", notes = "批量删除炼钢信息")
    @DeleteMapping
    public CommonResult<Boolean> delete(@RequestBody List<String> ids) {
        return CommonResult.success(recordsOfSteelmakingOperationsService.delete(ids));
    }

    @ApiOperation(value = "记录审核", notes = "记录审核")
    @PostMapping("/check")
    public CommonResult<Boolean> check(@RequestBody List<String> ids, @ApiParam(value = "审核结果0不通过/1通过") @RequestParam int state) {
        return CommonResult.success(recordsOfSteelmakingOperationsService.check(ids, state));
    }

    @ApiOperation(value = "炼钢记录管理", notes = "炼钢记录管理")
    @GetMapping("/record_page")
    public CommonResult<IPage<RecordsOfSteelmakingOperations>> record(@ApiParam(value = "作业单编号") String recordNo,
                                                                      @ApiParam(value = "预装炉id") Long prechargeFurnaceId,
                                                                      @ApiParam(value = "炉号") String furnaceNo,
                                                                      @ApiParam(value = "钢种") String typeOfSteel,
                                                                      @ApiParam(value = "设备") String smeltingEquipment,
                                                                      String startTime, String endTime, Integer status,
                                                                      @RequestParam(defaultValue = "1") int page,
                                                                      @RequestParam(defaultValue = "10") int limit) {
        //获取登录用户权限
        List<Role> roles = systemServiceClient.queryRolesByUserId(SecurityUtils.getCurrentUser().getUserId());
        Set<String> rolesCode = roles.stream().map(Role::getRoleCode).collect(Collectors.toSet());
        //班组长标识
        String bzzbs = "JMAQ_BZZZ";
        boolean isBzz = false;
        for (String code : rolesCode) {
            if (code.endsWith(bzzbs)) {
                isBzz = true;
                break;
            }
        }
        //班组长查询
        if (isBzz) {
            return CommonResult.success(recordsOfSteelmakingOperationsService.bzzcx(recordNo, prechargeFurnaceId, furnaceNo, typeOfSteel, smeltingEquipment, startTime, endTime, status, page, limit));
        }
        //普通操作工查询
        else {
            return CommonResult.success(recordsOfSteelmakingOperationsService.czgcx(recordNo, prechargeFurnaceId, furnaceNo, typeOfSteel, smeltingEquipment, startTime, endTime, status, page, limit));
        }
    }

    @ApiOperation(value = "导出炼钢记录excel", notes = "导出炼钢记录excel")
    @GetMapping("/export")
    public void export(@ApiParam(value = "作业单编号") String recordNo,
                       @ApiParam(value = "预装炉id") Long prechargeFurnaceId,
                       @ApiParam(value = "炉号") String furnaceNo,
                       @ApiParam(value = "钢种") String typeOfSteel,
                       @ApiParam(value = "冶炼设备") String smeltingEquipment,
                       String startTime, String endTime, @ApiParam(value = "审核状态") Integer status, HttpServletResponse response) {
        recordsOfSteelmakingOperationsService.export(recordNo, prechargeFurnaceId, furnaceNo, typeOfSteel, smeltingEquipment, startTime, endTime, status, response);
    }

}

