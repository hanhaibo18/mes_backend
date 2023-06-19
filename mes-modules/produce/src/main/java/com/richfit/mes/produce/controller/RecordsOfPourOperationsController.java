package com.richfit.mes.produce.controller;


import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.api.ApiController;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.RecordsOfPourOperations;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.RecordsOfPourOperationsService;
import com.richfit.mes.produce.service.TrackItemService;
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
 * (RecordsOfPourOperations)表控制层
 *
 * @author makejava
 * @since 2023-05-15 10:18:41
 */
@Api(value = "浇注记录", tags = {"浇注记录"})
@RestController
@RequestMapping("/api/produce/records_of_pour_operations")
public class RecordsOfPourOperationsController extends ApiController {
    /**
     * 服务对象
     */
    @Autowired
    private RecordsOfPourOperationsService recordsOfPourOperationsService;
    @Autowired
    private SystemServiceClient systemServiceClient;
    @Autowired
    private TrackItemService trackItemService;

    @ApiOperation(value = "通过预装炉id查询浇注记录信息", notes = "通过预装炉id查询浇注记录信息")
    @GetMapping("/{prechargeFurnaceId}")
    public CommonResult<RecordsOfPourOperations> getByPrechargeFurnaceId(@ApiParam(value = "预装炉id") @PathVariable Long prechargeFurnaceId) {
        return CommonResult.success(recordsOfPourOperationsService.getByPrechargeFurnaceId(prechargeFurnaceId));
    }

    @ApiOperation(value = "根据预装炉id初始化浇注作业记录", notes = "根据预装炉id初始化炼钢作业记录")
    @GetMapping("/init")
    public CommonResult<Boolean> init(@ApiParam(value = "预装炉id") @RequestParam Long prechargeFurnaceId, @ApiParam(value = "作业单编号") @RequestParam String branchCode) {
        return CommonResult.success(recordsOfPourOperationsService.init(prechargeFurnaceId, branchCode));
    }

    @ApiOperation(value = "修改浇注记录信息", notes = "修改炼钢记录信息")
    @PutMapping
    public CommonResult<Boolean> update(@RequestBody RecordsOfPourOperations recordsOfPourOperations) {
        return CommonResult.success(recordsOfPourOperationsService.update(recordsOfPourOperations));
    }

    @ApiOperation(value = "批量删除浇注记录信息", notes = "批量删除炼钢信息")
    @DeleteMapping
    public CommonResult<Boolean> delete(@RequestBody List<String> ids) {
        return CommonResult.success(recordsOfPourOperationsService.delete(ids));
    }

    @ApiOperation(value = "记录审核", notes = "记录审核")
    @PostMapping("/check")
    public CommonResult<Boolean> check(@RequestBody List<String> ids, @ApiParam(value = "审核结果0不通过/1通过") @RequestParam int state) {
        return CommonResult.success(recordsOfPourOperationsService.check(ids, state));
    }

    @ApiOperation(value = "浇注记录管理列表", notes = "浇注记录管理列表")
    @GetMapping("/record_page")
    public CommonResult<IPage<RecordsOfPourOperations>> record(@ApiParam(value = "作业单编号") String recordNo,
                                                               @ApiParam(value = "预装炉id") Long prechargeFurnaceId,
                                                               @ApiParam(value = "炉号") String furnaceNo,
                                                               @ApiParam(value = "钢种") String typeOfSteel,
                                                               @ApiParam(value = "锭型") String ingotCase,
                                                               String startTime, String endTime, @ApiParam(value = "审核状态") Integer status,
                                                               @RequestParam(defaultValue = "1") int page,
                                                               @RequestParam(defaultValue = "10") int limit, String orderCol, String order) {
        //获取登录用户权限
        List<Role> roles = systemServiceClient.queryRolesByUserId(SecurityUtils.getCurrentUser().getUserId());
        Set<String> rolesCode = roles.stream().map(Role::getRoleCode).collect(Collectors.toSet());
        //班组长标识
        String bzzBs = "JMAQ_BZZZ";
        boolean isBzz = false;
        for (String code : rolesCode) {
            if (code.endsWith(bzzBs)) {
                isBzz = true;
                break;
            }
        }
        //班组长查询
        if (isBzz) {
            return CommonResult.success(recordsOfPourOperationsService.bzzcx(recordNo, prechargeFurnaceId, furnaceNo, typeOfSteel, ingotCase, startTime, endTime, status, page, limit, orderCol, order));
        }
        //普通操作工查询
        else {
            return CommonResult.success(recordsOfPourOperationsService.czgcx(recordNo, prechargeFurnaceId, furnaceNo, typeOfSteel, ingotCase, startTime, endTime, status, page, limit, orderCol, order));
        }
    }

    @ApiOperation(value = "浇注页面修改工序信息", notes = "浇注页面修改工序信息")
    @PutMapping("/item")
    public CommonResult<Boolean> updateItem(@RequestBody TrackItem item) {
        recordsOfPourOperationsService.countHoldFinishedTime(item, item.getPourTimeDot(), item.getHoldTime());

        return CommonResult.success(trackItemService.updateById(item));
    }

    @ApiOperation(value = "导出浇注记录excel", notes = "导出浇注记录excel")
    @GetMapping("/export")
    public void export(@ApiParam(value = "作业单编号") String recordNo,
                       @ApiParam(value = "预装炉id") Long prechargeFurnaceId,
                       @ApiParam(value = "炉号") String furnaceNo,
                       @ApiParam(value = "钢种") String typeOfSteel,
                       @ApiParam(value = "锭型") String ingotCase,
                       String startTime, String endTime, @ApiParam(value = "审核状态") Integer status, HttpServletResponse response) {
        recordsOfPourOperationsService.export(recordNo, prechargeFurnaceId, furnaceNo, typeOfSteel, ingotCase, startTime, endTime, status, response);
    }

    @ApiOperation(value = "根据预装炉Id获取可以装炉的工序列表", notes = "根据预装炉Id获取可以装炉的工序列表")
    @GetMapping("/item")
    public CommonResult<IPage<TrackItem>> getItemByPrechargeFurnaceId(Long prechargeFurnaceId, @RequestParam(defaultValue = "1") int page, @RequestParam(defaultValue = "10") int limit) {
        return CommonResult.success(recordsOfPourOperationsService.getItemByPrechargeFurnaceId(prechargeFurnaceId, page, limit));
    }

    @ApiOperation(value = "根据工序id列表和预装炉id往炉中新增工序", notes = "根据工序id列表和预装炉id往炉中新增工序")
    @PostMapping("/add_item")
    public CommonResult<Boolean> addItem(@RequestParam Long prechargeFurnaceId, @RequestBody List<String> itemIds) {
        return CommonResult.success(recordsOfPourOperationsService.addItem(prechargeFurnaceId, itemIds));
    }

    @ApiOperation(value = "根据工序id列表移除预装炉", notes = "根据工序id列表移除预装炉")
    @DeleteMapping("delete_item")
    public CommonResult<Boolean> deleteItem(@RequestBody List<String> itemIds, @RequestParam Long prechargeFurnaceId) {
        return CommonResult.success(recordsOfPourOperationsService.deleteItem(itemIds, prechargeFurnaceId));
    }


}

