package com.richfit.mes.base.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.RouterService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author 马峰
 * @Description 工艺Controller
 */
@Slf4j
@Api("工艺管理")
@RestController
@RequestMapping("/api/base/router")
public class RouterController extends BaseController {

    @Autowired
    public RouterService routerService;

    public RouterController(RouterService routerService) {
        this.routerService = routerService;
    }

    /**
     * ***
     * 分页查询
     *
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "工艺", notes = "工艺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "routerNo", value = "图号", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "routerName", value = "名称", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "branchCode", value = "机构编码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "status", value = "状态", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/page")
    public CommonResult<IPage<Router>> page(int page, int limit, String routerNo, String routerName, String branchCode, String tenantId, String status, String order, String orderCol) {
        try {

            QueryWrapper<Router> queryWrapper = new QueryWrapper<Router>();
            if (!StringUtils.isNullOrEmpty(routerNo)) {
                queryWrapper.like("router_no", "%" + routerNo + "%");
            }
            if (!StringUtils.isNullOrEmpty(routerName)) {
                queryWrapper.like("router_name", "%" + routerName + "%");
            }
            if (!StringUtils.isNullOrEmpty(branchCode)) {
                queryWrapper.eq("branch_code", branchCode);
            }
            if (!StringUtils.isNullOrEmpty(status)) {
                queryWrapper.in("status", status.split(","));
            } else {
                queryWrapper.in("is_active", "0,1".split(","));
            }

            if (!StringUtils.isNullOrEmpty(tenantId)) {
                queryWrapper.eq("tenant_id", tenantId);
            }
            if (!StringUtils.isNullOrEmpty(orderCol)) {
                if (!StringUtils.isNullOrEmpty(order)) {
                    if (order.equals("desc")) {
                        queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
                    } else if (order.equals("asc")) {
                        queryWrapper.orderByAsc(StrUtil.toUnderlineCase(orderCol));
                    }
                } else {
                    queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
                }
            } else {
                queryWrapper.orderByDesc("modify_time");
            }
            IPage<Router> routers = routerService.selectPageAndChild(new Page<Router>(page, limit), queryWrapper);
            return CommonResult.success(routers);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "新增工艺", notes = "新增工艺")
    @ApiImplicitParam(name = "router", value = "工艺", required = true, dataType = "Router", paramType = "query")
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Router> addRouter(@RequestBody Router router) {
        // 如果新增的不是历史状态，则将其他的状态设置为历史状态
        // 2为历史 1为启用 0为未启用

        //如果不是历史版本，需要将历史版本先删除

        if (null != router && !router.getStatus().equals("2")) {
            CommonResult<List<Router>> result = this.find("", router.getRouterNo(), "", "", router.getBranchCode(), "0,1", router.getTenantId());
            if (result.getData().size() > 0) {
                //将当前激活的工艺状态设置为历史状态
                for (int i = 0; i < result.getData().size(); i++) {
                    Router oldRouter = result.getData().get(i);
                    oldRouter.setStatus("2");
                    routerService.updateById(oldRouter);
                }
            }
        }
        if (StringUtils.isNullOrEmpty(router.getRouterNo())) {
            return CommonResult.failed("工艺不能为空！");
        } else {
            if (null != SecurityUtils.getCurrentUser()) {
                String tenantId = SecurityUtils.getCurrentUser().getTenantId();
                router.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
                router.setCreateBy(SecurityUtils.getCurrentUser().getUsername());

                router.setModifyBy(SecurityUtils.getCurrentUser().getUsername());

            }
            router.setCreateTime(new Date());
            router.setModifyTime(new Date());
            boolean bool = true;
            if (!StringUtils.isNullOrEmpty(router.getId())) {
                Router r = routerService.getById(router);
                if (null == r) {
                    bool = routerService.save(router);
                }
            } else {
                bool = routerService.save(router);
            }
            if (bool) {
                return CommonResult.success(router, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

    @ApiOperation(value = "修改工艺", notes = "修改工艺")
    @ApiImplicitParam(name = "router", value = "工艺", required = true, dataType = "Router", paramType = "path")
    @PostMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Router> updateRouter(@RequestBody Router router) {
        if (StringUtils.isNullOrEmpty(router.getRouterNo())) {
            return CommonResult.failed("编码不能为空！");
        } else {
            if (null != SecurityUtils.getCurrentUser()) {
                router.setTenantId(SecurityUtils.getCurrentUser().getTenantId());

                router.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            }
            router.setModifyTime(new Date());
            //禁止或激活状态无法直接修改为历史状态;历史状态修改为禁止或启动状态，要把其他的工艺状态设置为历史
            if (router.getStatus().equals("2")) {
                CommonResult<List<Router>> result = this.find("", router.getRouterNo(), "", "", router.getBranchCode(), "0,1", router.getTenantId());
                if (result.getData().size() == 0) {
                    return CommonResult.failed("启用状态不能直接修改为历史状态！");
                }
            } else {
                CommonResult<List<Router>> result = this.find("", router.getRouterNo(), "", "", router.getBranchCode(), "0,1", router.getTenantId());
                if (result.getData().size() > 0) {
                    //将当前激活的工艺状态设置为历史状态
                    for (int i = 0; i < result.getData().size(); i++) {
                        Router oldRouter = result.getData().get(i);
                        oldRouter.setStatus("2");
                        oldRouter.setIsActive("2");
                        routerService.updateById(oldRouter);
                    }
                }
            }
            boolean bool = routerService.updateById(router);
            if (bool) {
                return CommonResult.success(router, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

    @ApiOperation(value = "查询工艺", notes = "根据编码获得工艺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "编码", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "routerNo", value = "图号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "routerName", value = "名称", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "机构", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "status", value = "状态", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping("/find")
    public CommonResult<List<Router>> find(String id, String routerNo, String routerName, String version, String branchCode, String tenantId, String status) {
        QueryWrapper<Router> queryWrapper = new QueryWrapper<Router>();
        if (!StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (!StringUtils.isNullOrEmpty(routerNo)) {
            queryWrapper.eq("router_no", routerNo);
        }
        if (!StringUtils.isNullOrEmpty(routerName)) {
            queryWrapper.like("router_name", "%" + routerName + "%");
        }
        if (!StringUtils.isNullOrEmpty(version)) {
            queryWrapper.eq("version", version);
        }
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        if (!StringUtils.isNullOrEmpty(status)) {
            queryWrapper.in("status", status);
        } else {
            queryWrapper.in("is_active", "0,1".split(","));
        }
        if (!StringUtils.isNullOrEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }
        List<Router> result = routerService.list(queryWrapper);
        return CommonResult.success(result, "操作成功！");
    }

    @ApiOperation(value = "查询工艺", notes = "根据ID获得工艺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "id", value = "ID", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping("/getById")
    public CommonResult<Router> getByRouterId(String id) {
        QueryWrapper<Router> queryWrapper = new QueryWrapper<Router>();
        if (!StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        List<Router> routers = routerService.list(queryWrapper);
        if (routers.size() > 0) {
            return CommonResult.success(routers.get(0), "操作成功！");
        } else {
            return CommonResult.success(null, "操作成功！");
        }

    }

    @ApiOperation(value = "查询工艺", notes = "根据ID获得工艺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "routerNo", value = "图号", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "机构", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping("/getByRouterNo")
    public CommonResult<Router> getByRouterNo(String routerNo, String branchCode, String tenantId) {
        QueryWrapper<Router> queryWrapper = new QueryWrapper<Router>();

        if (!StringUtils.isNullOrEmpty(routerNo)) {
            queryWrapper.eq("router_no", routerNo);
        }
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        queryWrapper.in("is_active", "0,1".split(","));

        if (!StringUtils.isNullOrEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }
        List<Router> routers = routerService.list(queryWrapper);
        if (routers.size() > 0) {
            return CommonResult.success(routers.get(0), "操作成功！");
        } else {
            return CommonResult.success(null, "操作成功！");
        }

    }

    @ApiOperation(value = "删除工艺", notes = "根据id删除工艺")
    @ApiImplicitParam(name = "ids", value = "编码", required = true, dataType = "String[]", paramType = "query")
    @PostMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Router> delete(@RequestBody String[] ids) {

        for (int i = 0; i < ids.length; i++) {
            Router r = this.getByRouterId(ids[i]).getData();
            //如果不是历史版本，需要将历史版本先删除
            if (null != r && !r.getStatus().equals("2")) {
                List<Router> routers = this.find(null, r.getRouterNo(), null, null, r.getBranchCode(), "2", r.getTenantId()).getData();
                for (int j = 0; j < routers.size(); j++) {
                    routerService.removeById(routers.get(j).getId());
                }
            }
            routerService.removeById(ids[i]);
        }
        return CommonResult.success(null, "删除成功！");

    }

    @ApiOperation(value = "导入工艺", notes = "根据Excel文档导入工艺")
    @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "query")
    @PostMapping("/import_excel")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult importExcel(@RequestParam("file") MultipartFile file, String tenantId, String branchCode) {
        CommonResult result = null;
        //封装证件信息实体类
        java.lang.reflect.Field[] fields = Router.class.getDeclaredFields();
        //封装证件信息实体类
        String[] fieldNames = new String[fields.length];
        for (int i = 0; i < fields.length; i++) {
            fieldNames[i] = fields[i].getName();
        }
        File excelFile = null;
        //给导入的excel一个临时的文件名
        StringBuilder tempName = new StringBuilder(UUID.randomUUID().toString());
        tempName.append(".").append(FileUtils.getFilenameExtension(file.getOriginalFilename()));
        try {
            excelFile = new File(System.getProperty("java.io.tmpdir"), tempName.toString());
            file.transferTo(excelFile);
            //将导入的excel数据生成证件实体类list
            List<Router> list = ExcelUtils.importExcel(excelFile, Router.class, fieldNames, 1, 0, 0, tempName.toString());
            FileUtils.delete(excelFile);
            for (int i = 0; i < list.size(); i++) {
                list.get(i).setTenantId(tenantId);
                list.get(i).setBranchCode(branchCode);
                if (null != SecurityUtils.getCurrentUser()) {
                    list.get(i).setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                }
            }
            //list = list.stream().filter(item -> item.getMaterialNo() != null).collect(Collectors.toList());
            boolean bool = routerService.saveBatch(list);
            if (bool) {
                return CommonResult.success(null, "成功");
            } else {
                return CommonResult.failed("失败");
            }
        } catch (Exception e) {
            return CommonResult.failed("失败:" + e.getMessage());
        }
    }
}
