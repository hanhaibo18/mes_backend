package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.ProductionProcessService;
import com.richfit.mes.base.service.ProductionRouteService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.ProductionProcess;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * @author HanHaiBo
 * @date 2023/2/21 8:43
 */
@Slf4j
@Api(value = "生产路线工序管理", tags = {"生产路线工序管理"})
@RestController
@RequestMapping("/api/base/process")
public class ProductionProcessController {

    @Autowired
    private ProductionProcessService productionProcessService;
    @Autowired
    private ProductionRouteService productionRouteService;

    @ApiOperation(value = "根据productionRouteId查询工序列表", notes = "根据productionRouteId查询工序列表")
    @GetMapping("/list")
    public CommonResult<List<ProductionProcess>> list(@ApiParam(value = "工艺路线ID") @RequestParam String routeId,
                                                      @ApiParam(value = "工序名称，可不填") @RequestParam(required = false) String processName) {
        QueryWrapper<ProductionProcess> queryWrapper = new QueryWrapper<ProductionProcess>();
        if (processName != null) {
            queryWrapper.like("process_name", processName);
        }
        queryWrapper.eq("production_route_id", routeId);
        queryWrapper.orderByAsc("process_sequence");
        return CommonResult.success(productionProcessService.list(queryWrapper));
    }

    @ApiOperation(value = "根据productionRouteId新增工序", notes = "根据productionRouteId新增工序")
    @PostMapping("/add/{routeId}")
    public CommonResult<ProductionProcess> addProductionProcess(@ApiParam(value = "工艺路线ID") @PathVariable String routeId,
                                                                @ApiParam(value = "热工路线工序") @RequestBody ProductionProcess productionProcess) {
        if (StringUtils.isNullOrEmpty(productionProcess.getProcessName())) {
            return CommonResult.failed("工艺名称不能为空");
        }
        productionProcess.setProductionRouteId(routeId);

        QueryWrapper<ProductionProcess> queryWrapper = new QueryWrapper<ProductionProcess>();
        queryWrapper.eq("production_route_id", routeId);
        queryWrapper.orderByDesc("process_sequence");
        List<ProductionProcess> processList = productionProcessService.list(queryWrapper);
        if (processList.isEmpty()) {
            productionProcess.setProcessSequence(1);
        } else {
            productionProcess.setProcessSequence(processList.get(0).getProcessSequence() + 1);
        }
        boolean saveResult = productionProcessService.save(productionProcess);
        if (saveResult) {
            return CommonResult.success(productionProcess, "新增成功");
        } else {
            return CommonResult.failed("新增失败");
        }
    }

    @ApiOperation(value = "根据productionRouteId批量新增工序", notes = "根据productionRouteId批量新增工序")
    @PostMapping("/addList/{routeId}")
    public CommonResult<String> addProductionProcesses(@ApiParam(value = "工艺路线ID") @PathVariable String routeId,
                                                       @ApiParam(value = "热工路线工序") @RequestBody ProductionProcess[] productionProcesses) {
        for (ProductionProcess process : productionProcesses) {
            if (StringUtils.isNullOrEmpty(process.getProcessName())) {
                return CommonResult.failed("工序名称不能为空");
            }
        }
        QueryWrapper<ProductionProcess> queryWrapper = new QueryWrapper<ProductionProcess>();
        queryWrapper.eq("production_route_id", routeId);
        queryWrapper.orderByDesc("process_sequence");
        List<ProductionProcess> processList = productionProcessService.list(queryWrapper);
        int processSequence;
        if (processList.isEmpty()) {
            processSequence = 1;
        } else {
            processSequence = processList.get(0).getProcessSequence() + 1;
        }

        for (ProductionProcess process : productionProcesses) {
            process.setProductionRouteId(routeId);
            process.setProcessSequence(processSequence++);
            productionProcessService.save(process);
        }
        return CommonResult.success("批量修改成功！");
    }

    @ApiOperation(value = "修改工序", notes = "修改工序")
    @PutMapping("/update")
    public CommonResult<ProductionProcess> updateProductionProcess(@ApiParam(value = "热工路线工序") @RequestBody ProductionProcess productionProcess) {
        if (StringUtils.isNullOrEmpty(productionProcess.getProcessName())) {
            return CommonResult.failed("工序名称不能为空！");
        }
        if (StringUtils.isNullOrEmpty(productionProcess.getId())) {
            return CommonResult.failed("无法获取id！");
        }
        boolean result = productionProcessService.updateById(productionProcess);
        if (result) {
            return CommonResult.success(productionProcess, "修改成功");
        } else {
            return CommonResult.failed("修改失败");
        }
    }

    @ApiOperation(value = "批量修改工序", notes = "批量修改工序")
    @PutMapping("/updateBatch")
    public CommonResult<String> updateProductionProcesses(@ApiParam(value = "热工路线工序") @RequestBody ProductionProcess[] productionProcesses) {
        boolean result = productionProcessService.updateBatch(productionProcesses);
        if (!result) {
            return CommonResult.failed("批量修改失败！");
        }
        return CommonResult.success("批量修改成功！");
    }

    @ApiOperation(value = "删除工序", notes = "删除工序")
    @DeleteMapping("/delete")
    public CommonResult<String> deleteProductionRoute(@ApiParam(value = "工艺路线ID") @RequestBody List<String> ids) {
        if (ids.isEmpty()) {
            return CommonResult.failed("传入ID为空");
        }
        boolean result = productionProcessService.removeByIds(ids);
        if (!result) {
            return CommonResult.failed("删除失败");
        }
        return CommonResult.success("删除成功");
    }


}
