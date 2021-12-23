package com.richfit.mes.sys.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.Menu;
import com.richfit.mes.common.model.sys.TenantMenu;
import com.richfit.mes.sys.service.TenantMenuService;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.richfit.mes.common.core.base.BaseController;

import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 租户菜单 前端控制器
 * </p>
 *
 * @author gaoliang
 * @since 2020-05-25
 */
@RestController
@RequestMapping("/api/sys/tenant/menu")
public class TenantMenuController extends BaseController {

    @Autowired
    TenantMenuService tenantMenuService;


    /**
     * 查询某租户某父菜单下的已分配菜单 返回所有菜单，增加是否分配标记
     */
    @ApiOperation(value = "查询某租户某父菜单下的已分配菜单，返回所有菜单，增加是否分配标记", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户Id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "pMenuId", value = "父菜单Id", required = true, dataType = "String", paramType = "path")
    })
    @GetMapping("/query/{tenantId}/{pMenuId}")
    public CommonResult<List<Menu>> queryTenantMenuByPId(@PathVariable String tenantId, @PathVariable String pMenuId) {
        return CommonResult.success(tenantMenuService.queryTenantMenuByPId(tenantId, pMenuId));
    }

    /**
     * 为某租户首次分配菜单
     */
    @ApiOperation(value = "批量保存租户菜单", notes = "批量保存租户菜单")
    @ApiImplicitParam(name = "menus", value = "租户菜单JSON", required = true, dataType = "TenantMenu",allowMultiple = true, paramType = "body")
    @PostMapping("/batch/save")
    public CommonResult<Boolean> batchSaveTenantMenu(@RequestBody List<TenantMenu> menus) {

        return CommonResult.success(tenantMenuService.saveBatch(menus));

    }

    /**
     * 收回某租户的已分配菜单  租户下用户的该菜单也要收回
     */
    @ApiOperation(value = "批量删除租户菜单", notes = "批量删除租户菜单")
    @ApiImplicitParam(name = "menus", value = "租户菜单JSON", required = true, dataType = "TenantMenu",allowMultiple = true, paramType = "body")
    @PostMapping("/batch/del")
    public CommonResult<Boolean> batchDelTenantMenu(@RequestBody List<TenantMenu> menus) {

        List ids = menus.stream().map(TenantMenu::getMenuId).collect(Collectors.toList());

        return CommonResult.success(tenantMenuService.removeByIds(ids));

    }


    @ApiOperation(value = "查询某租户所有已分配菜单", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户Id", required = true, dataType = "String", paramType = "path")
    })
    @GetMapping("/query/all/{tenantId}")
    public CommonResult<List<TenantMenu>> queryTenantMenuAll(@PathVariable String tenantId) {
        List<TenantMenu> menus = tenantMenuService.list(new QueryWrapper<TenantMenu>()
                .eq("tenant_id", tenantId)
        );
        return CommonResult.success(menus);
    }

    @ApiOperation(value = "保存某租户所有已分配菜单", notes = "")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "tenantId", value = "租户Id", required = true, dataType = "String", paramType = "path"),
            @ApiImplicitParam(name = "menus", value = "菜单JSON", required = true, dataType = "TenantMenu", allowMultiple = true, paramType = "body")
    })
    @PostMapping("/save/{tenantId}")
    public CommonResult<Boolean> saveTenantMenu(@RequestBody List<TenantMenu> menus, @PathVariable String tenantId) {

        return CommonResult.success(tenantMenuService.saveTenantMenu(menus, tenantId));
    }
}

