package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.ProductionProcessService;
import com.richfit.mes.base.service.ProductionRouteService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.ProductionProcess;
import com.richfit.mes.common.model.base.ProductionRoute;
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
@Api("生产路线工序管理")
@RestController
@RequestMapping("/api/base/process")
public class ProductionProcessController {

    @Autowired
    private ProductionProcessService productionProcessService;
    @Autowired
    private ProductionRouteService productionRouteService;

    @ApiOperation(value = "根据productionRouteId查询工序列表", notes = "根据productionRouteId查询工序列表")
    @GetMapping("/page")
    public CommonResult<IPage<ProductionProcess>> list(@ApiParam(value = "工艺id", required = true) @RequestParam String routeId,
                                                       Integer page, Integer limit, String processName) {
        if (page == null) {
            page = 1;
        }
        if (limit == null) {
            limit = 10;
        }
        try {
            QueryWrapper<ProductionProcess> queryWrapper = new QueryWrapper<ProductionProcess>();
            if (processName != null) {
                queryWrapper.like("process_name", "%" + processName + "%");
            }
            queryWrapper.eq("production_route_id", routeId);
            queryWrapper.orderByAsc("process_sequence");
            return CommonResult.success(productionProcessService.page(new Page<ProductionProcess>(page, limit), queryWrapper));
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "根据productionRouteId新增工序", notes = "根据productionRouteId新增工序")
    @PostMapping("/add/{routeId}")
    public CommonResult<ProductionProcess> addProductionProcess(@PathVariable String routeId, @RequestBody
            ProductionProcess productionProcess) {
        if (StringUtils.isNullOrEmpty(productionProcess.getProcessName())) {
            return CommonResult.failed("工艺名称不能为空");
        }
        if (null != SecurityUtils.getCurrentUser()) {
            productionProcess.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
            productionProcess.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
        }
        productionProcess.setCreateTime(new Date());
        productionProcess.setModifyTime(new Date());
        productionProcess.setProductionRouteId(routeId);

        QueryWrapper queryWrapper = new QueryWrapper<ProductionProcess>();
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
    public CommonResult addProductionProcesses(@PathVariable String routeId, @RequestBody
            ProductionProcess[] productionProcesses) {
        for (ProductionProcess process : productionProcesses) {
            if (StringUtils.isNullOrEmpty(process.getProcessName())){
                return CommonResult.failed("工序名称不能为空");
            }
        }
        String currentUser = "unknownUser";
        Date nowTime = new Date();
        if (null != SecurityUtils.getCurrentUser()) {
            currentUser = SecurityUtils.getCurrentUser().getUsername();
        }
        QueryWrapper queryWrapper = new QueryWrapper<ProductionProcess>();
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
            process.setCreateBy(currentUser);
            process.setModifyBy(currentUser);
            process.setCreateTime(nowTime);
            process.setModifyTime(nowTime);
            productionProcessService.save(process);
        }
        return CommonResult.success("批量修改成功！");
    }

    @ApiOperation(value = "修改工序", notes = "修改工序")
    @PutMapping("/update")
    public CommonResult<ProductionProcess> updateProductionProcess(@RequestBody ProductionProcess productionProcess) {
        if (StringUtils.isNullOrEmpty(productionProcess.getProcessName())) {
            return CommonResult.failed("工序名称不能为空！");
        }
        if (StringUtils.isNullOrEmpty(productionProcess.getId())) {
            return CommonResult.failed("无法获取id！");
        }
        if (null != SecurityUtils.getCurrentUser()) {
            productionProcess.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
        }
        productionProcess.setModifyTime(new Date());
        boolean result = productionProcessService.updateById(productionProcess);
        if (result) {
            return CommonResult.success(productionProcess, "修改成功");
        } else {
            return CommonResult.failed("修改失败");
        }
    }

    @ApiOperation(value = "批量修改工序", notes = "批量修改工序")
    @PutMapping("/updates")
    public CommonResult<String> updateProductionProcesses(@RequestBody ProductionProcess[] productionProcesses) {
        for (ProductionProcess process : productionProcesses) {
            if (StringUtils.isNullOrEmpty(process.getProcessName())){
                return CommonResult.failed("工序名称不能为空");
            }
            if (StringUtils.isNullOrEmpty(process.getId())) {
                return CommonResult.failed("无法获取id！");
            }
        }
        String currentUser = "unknownUser";
        Date nowTime = new Date();
        if (null != SecurityUtils.getCurrentUser()) {
            currentUser = SecurityUtils.getCurrentUser().getUsername();
        }
        for (ProductionProcess process : productionProcesses) {
            process.setModifyBy(currentUser);
            process.setModifyTime(nowTime);
            productionProcessService.updateById(process);
        }
       return CommonResult.success("批量修改成功！");
    }

    @ApiOperation(value = "删除工序", notes = "删除工序")
    @DeleteMapping("/delete/{processId}")
    public CommonResult<String> deleteProductionRoute(@PathVariable String processId) {
        if (processId != null) {
            boolean result = productionProcessService.removeById(processId);
            if (result) {
                return CommonResult.success("删除成功 ID:" + processId);
            } else {
                return CommonResult.failed("删除失败");
            }
        } else {
            return CommonResult.failed("删除失败");
        }
    }



}
