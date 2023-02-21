package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.ProductionRouteService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.ProductionBom;
import com.richfit.mes.common.model.base.ProductionRoute;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
    public CommonResult<IPage<ProductionRoute>> page(int page, int limit, String branchCode, String orderCol, String order) {
        QueryWrapper<ProductionRoute> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        OrderUtil.query(queryWrapper, orderCol, order);
        return CommonResult.success(productionRouteService.page(new Page<>(page, limit), queryWrapper));
    }

    @ApiOperation(value = "按名称获取生产路线", notes = "按名称获取生产路线")
    @GetMapping("/get/{routeName}")
    public CommonResult<List<ProductionRoute>> getByName(@PathVariable String routeName, String branchCode, String orderCol, String order) {
        QueryWrapper<ProductionRoute> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.like("production_route_name", "%" + routeName + "%");
        OrderUtil.query(queryWrapper, orderCol, order);
        return CommonResult.success(productionRouteService.list(queryWrapper));
    }

    @ApiOperation(value = "新增生产路线", notes = "新增生产路线")
    @PostMapping("/add")
    public CommonResult<ProductionRoute> addProductionRoute(@RequestBody ProductionRoute productionRoute) {
        if (StringUtils.isNullOrEmpty(productionRoute.getProductionRouteName())) {
            return CommonResult.failed("生产路线名称不能为空");
        }
        if (null != SecurityUtils.getCurrentUser()) {
            productionRoute.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
            productionRoute.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            productionRoute.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        }
        productionRoute.setCreateTime(new Date());
        productionRoute.setModifyTime(new Date());
        boolean saveResult = productionRouteService.save(productionRoute);
        if (saveResult) {
            return CommonResult.success(productionRoute, "新增成功");
        } else {
            return CommonResult.failed("新增失败");
        }
    }

    @ApiOperation(value = "修改生产路线", notes = "修改生产路线")
    @PutMapping("/update")
    public CommonResult<ProductionRoute> updateProductionRoute(@RequestBody ProductionRoute productionRoute) {
        if (StringUtils.isNullOrEmpty(productionRoute.getProductionRouteName())) {
            return CommonResult.failed("生产路线名称不能为空！");
        }
        if (StringUtils.isNullOrEmpty(productionRoute.getId())) {
            return CommonResult.failed("id不能为空！");
        }
        if (null != SecurityUtils.getCurrentUser()) {
            productionRoute.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
        }
        productionRoute.setModifyTime(new Date());
        boolean result = productionRouteService.updateById(productionRoute);
        if (result) {
            return CommonResult.success(productionRoute, "修改成功");
        } else {
            return CommonResult.failed("修改失败");
        }
    }

    @ApiOperation(value = "批量修改生产路线", notes = "批量修改生产路线")
    @PutMapping("/updates")
    public CommonResult<String> updateProductionRoutes(@RequestBody ProductionRoute[] ProductionRoutes) {
        for (ProductionRoute route : ProductionRoutes) {
            if (StringUtils.isNullOrEmpty(route.getProductionRouteName())) {
                return CommonResult.failed("名称不能为空！");
            }
        }
        for (ProductionRoute ProductionRoute : ProductionRoutes) {
            ProductionRoute.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            ProductionRoute.setModifyTime(new Date());
            productionRouteService.updateById(ProductionRoute);
        }
        return CommonResult.success("批量修改成功！");
    }

    @ApiOperation(value = "删除生产路线", notes = "删除生产路线")
    @DeleteMapping("/delete/{routeId}")
    public CommonResult<String> deleteProductionRoute(@PathVariable String routeId) {
        if (routeId != null) {
            boolean result = productionRouteService.removeById(routeId);
            if (result) {
                return CommonResult.success("删除成功 ID:" + routeId);
            } else {
                return CommonResult.failed("删除失败");
            }
        } else {
            return CommonResult.failed("删除失败");
        }
    }


}
