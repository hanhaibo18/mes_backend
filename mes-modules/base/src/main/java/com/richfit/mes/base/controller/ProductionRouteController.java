package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.ProductionRouteService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.ProductionRoute;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author HanHaiBo
 * @date 2023/2/20 15:43
 */

@Slf4j
@Api(value = "生产路线管理", tags = {"生产路线管理"})
@RestController
@RequestMapping("/api/base/production")
public class ProductionRouteController extends BaseController {
    @Autowired
    private ProductionRouteService productionRouteService;

    @ApiOperation(value = "分页获取生产路线", notes = "分页获取生产路线")
    @GetMapping("/page")
    public CommonResult<IPage<ProductionRoute>> page(@ApiParam(value = "页数") @RequestParam(defaultValue = "1") int page, @ApiParam(value = "每页个数") @RequestParam(defaultValue = "10") int limit, @ApiParam(value = "机构ID") @RequestParam String branchCode, @ApiParam(value = "排序列") @RequestParam(required = false) String orderCol, @ApiParam(value = "asc/desc") @RequestParam(required = false) String order, @ApiParam(value = "生产路线名称") @RequestParam(required = false) String productionRouteName) {
        QueryWrapper<ProductionRoute> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        if (productionRouteName != null) {
            queryWrapper.like("production_route_name", productionRouteName);
        }
        OrderUtil.query(queryWrapper, orderCol, order);
        return CommonResult.success(productionRouteService.page(new Page<>(page, limit), queryWrapper));
    }

    @ApiOperation(value = "新增生产路线", notes = "新增生产路线")
    @PostMapping("/add")
    public CommonResult<ProductionRoute> addProductionRoute(@ApiParam(value = "热工路线") @RequestBody ProductionRoute productionRoute) {
        if (StringUtils.isNullOrEmpty(productionRoute.getProductionRouteName())) {
            return CommonResult.failed("生产路线名称不能为空");
        }
        if (null != SecurityUtils.getCurrentUser()) {
            productionRoute.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        }
        boolean saveResult = productionRouteService.save(productionRoute);
        if (saveResult) {
            return CommonResult.success(productionRoute, "新增成功");
        } else {
            return CommonResult.failed("新增失败");
        }
    }

    @ApiOperation(value = "修改生产路线", notes = "修改生产路线")
    @PutMapping("/update")
    public CommonResult<ProductionRoute> updateProductionRoute(@ApiParam(value = "热工路线") @RequestBody ProductionRoute productionRoute) {
        if (StringUtils.isNullOrEmpty(productionRoute.getProductionRouteName())) {
            return CommonResult.failed("生产路线名称不能为空！");
        }
        if (StringUtils.isNullOrEmpty(productionRoute.getId())) {
            return CommonResult.failed("id不能为空！");
        }
        boolean result = productionRouteService.updateById(productionRoute);
        if (!result) {
            return CommonResult.failed("修改失败");
        }
        return CommonResult.success(productionRoute, "修改成功");
    }

    @ApiOperation(value = "批量修改生产路线", notes = "批量修改生产路线")
    @PutMapping("/updateBatch")
    public CommonResult<String> updateProductionRoutes(@ApiParam(value = "热工路线") @RequestBody List<ProductionRoute> productionRoutes) {
        for (ProductionRoute route : productionRoutes) {
            if (StringUtils.isNullOrEmpty(route.getProductionRouteName())) {
                return CommonResult.failed("名称不能为空！");
            }
        }
        boolean result = productionRouteService.updateBatchById(productionRoutes);
        if (!result) {
            return CommonResult.failed("修改失败");
        }
        return CommonResult.success("批量修改成功！");
    }

    @ApiOperation(value = "删除生产路线", notes = "删除生产路线")
    @DeleteMapping("/delete")
    public CommonResult<String> deleteProductionRoute(@ApiParam(value = "要删除的路线ID") @RequestBody List<String> ids) {
        if (ids.isEmpty()) {
            return CommonResult.failed("传入ID为空");
        }
        boolean result = productionRouteService.removeByIds(ids);
        if (!result) {
            return CommonResult.failed("删除失败");
        }
        return CommonResult.success("删除成功");
    }


}
