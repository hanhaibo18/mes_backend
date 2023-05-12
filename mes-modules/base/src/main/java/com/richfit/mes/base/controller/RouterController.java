package com.richfit.mes.base.controller;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.enmus.OptTypeEnum;
import com.richfit.mes.base.enmus.RouterStatusEnum;
import com.richfit.mes.base.entity.QueryIsHistory;
import com.richfit.mes.base.entity.QueryProcessRecordsVo;
import com.richfit.mes.base.provider.ErpServiceClient;
import com.richfit.mes.base.service.OperationAssignService;
import com.richfit.mes.base.service.RouterOptAssignService;
import com.richfit.mes.base.service.RouterService;
import com.richfit.mes.base.service.SequenceService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;
import com.richfit.mes.common.model.base.OperationAssign;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.base.RouterOptAssign;
import com.richfit.mes.common.model.base.Sequence;
import com.richfit.mes.common.model.produce.AssignPerson;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import com.richfit.mes.common.security.constant.SecurityConstants;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

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
    public ErpServiceClient erpServiceClient;

    @Autowired
    public RouterService routerService;

    @Autowired
    public SequenceService sequenceService;

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
            @ApiImplicitParam(name = "routerType", value = "工艺类型", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "branchCode", value = "机构编码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "status", value = "状态", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/page")
    public CommonResult<IPage<Router>> page(int page, int limit, String routerNo, String routerName, String branchCode, String tenantId, String status, String order, String orderCol, boolean isPDM, String routerType) {
        try {

            QueryWrapper<Router> queryWrapper = new QueryWrapper<Router>();
            if (!StringUtils.isNullOrEmpty(routerNo)) {
                DrawingNoUtil.queryLike(queryWrapper, "router_no", routerNo);
            }
            if (!StringUtils.isNullOrEmpty(routerName)) {
                queryWrapper.like("router_name", routerName);
            }
            if (!StringUtils.isNullOrEmpty(status)) {
                queryWrapper.in("status", status.split(","));
            }
            if (!StringUtils.isNullOrEmpty(routerType)) {
                queryWrapper.eq("router_type", routerType);
            }
            if (isPDM) {
                queryWrapper.isNull("draw_no");
            }
//            queryWrapper.eq("is_active", "1");
            queryWrapper.eq("branch_code", branchCode);
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
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
                queryWrapper.orderByDesc("router_no").orderByAsc("status");
            }
            IPage<Router> routers = routerService.selectPageAndChild(new Page<Router>(page, limit), queryWrapper);
            return CommonResult.success(routers);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }


    @ApiOperation(value = "查询历史工序列表", notes = "查询历史工序列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "routerNo", value = "图号", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "branchCode", value = "机构编码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "id", value = "id", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/query_history")
    public CommonResult<List<Router>> getHistoryList(String routerNo, String routerType, String branchCode, String id) {
        return CommonResult.success(routerService.getHistoryList(routerNo, routerType, branchCode, id));
    }


    @ApiOperation(value = "新增工艺", notes = "新增工艺")
    @ApiImplicitParam(name = "router", value = "工艺", required = true, dataType = "Router", paramType = "query")
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Router> addRouter(@RequestBody Router router) {
        if (StringUtils.isNullOrEmpty(router.getRouterNo())) {
            return CommonResult.failed("工艺不能为空！");
        } else {
            //校验能否新增
            //校验 如果存在图号+版本号+类型的工艺 跳过发布
            QueryWrapper<Router> routerQueryWrapper = new QueryWrapper<>();
            routerQueryWrapper.eq("version", router.getVersion())
                    .eq("router_type", router.getRouterType())
                    .eq("branch_code", router.getBranchCode());
            DrawingNoUtil.queryEq(routerQueryWrapper, "router_no", router.getRouterNo());
            List<Router> list = routerService.list(routerQueryWrapper);
            //存在的话跳过发布
            if (list.size() > 0) {
                return CommonResult.failed("工艺重复，无法新增！");
            }
            if ("1".equals(router.getStatus())) {
                System.out.println("-------------------");
                //更新老工艺状态模块
                Router routerOlds = new Router();
                routerOlds.setIsActive("0");
                routerOlds.setStatus("2");
                QueryWrapper<Router> queryWrapperRouter = new QueryWrapper<>();
                queryWrapperRouter.eq("status", "1");
                DrawingNoUtil.queryEq(queryWrapperRouter, "router_no", router.getRouterNo());
                queryWrapperRouter.eq("branch_code", router.getBranchCode());
                //工艺唯一  工艺类型+图号（历史数据）
                queryWrapperRouter.eq("router_type", router.getRouterType());
                queryWrapperRouter.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
                routerService.update(routerOlds, queryWrapperRouter);
                //更新新工艺状态模块
                router.setIsActive("1");
            }
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
                CommonResult<List<Router>> result = this.find("", router.getRouterNo(), "", "", router.getBranchCode(), router.getTenantId(), "0,1");
                if (result.getData().size() == 0) {
                    return CommonResult.failed("启用状态不能直接修改为历史状态！");
                }
            } else if (router.getStatus().equals("1")) {
                //更新老工艺状态模块
                Router routerOlds = new Router();
                routerOlds.setIsActive("0");
                routerOlds.setStatus("2");
                QueryWrapper<Router> queryWrapperRouter = new QueryWrapper<>();
                queryWrapperRouter.eq("status", "1");
                DrawingNoUtil.queryEq(queryWrapperRouter, "router_no", router.getRouterNo());
                queryWrapperRouter.eq("branch_code", router.getBranchCode());
                queryWrapperRouter.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
                queryWrapperRouter.eq("router_type", router.getRouterType());
                routerService.update(routerOlds, queryWrapperRouter);
                //更新新工艺状态模块
                router.setIsActive("1");
            }
            QueryWrapper<Router> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("id", router.getId());
            queryWrapper.eq("branch_code", router.getBranchCode());
            boolean bool = routerService.update(router, queryWrapper);
//            boolean bool = routerService.updateById(router);
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
            DrawingNoUtil.queryEq(queryWrapper, "router_no", routerNo);
//            queryWrapper.eq("router_no", routerNo);
        }
        if (!StringUtils.isNullOrEmpty(routerName)) {
            queryWrapper.like("router_name", routerName);
        }
        if (!StringUtils.isNullOrEmpty(version)) {
            queryWrapper.eq("version", version);
        }
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        if (!StringUtils.isNullOrEmpty(status)) {
            queryWrapper.in("status", status.split(","));
        }
        if (!StringUtils.isNullOrEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }
        /**
         * 描述: 加入版本号降序排序
         *
         * @Author: zhiqiang.lu
         * @Date: 2022/6/13 10:25
         **/
        queryWrapper.orderByAsc("status").orderByDesc("version");
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
    public CommonResult<Router> getByRouterNo(String routerNo, String branchCode) {
        QueryWrapper<Router> queryWrapper = new QueryWrapper<Router>();

        if (!StringUtils.isNullOrEmpty(routerNo)) {
            DrawingNoUtil.queryEq(queryWrapper, "router_no", routerNo);
        }
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        queryWrapper.in("is_active", "0,1".split(","));
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.eq("status", "1");
        List<Router> routers = routerService.list(queryWrapper);
        if (routers.size() > 0) {
            return CommonResult.success(routers.get(0), "操作成功！");
        } else {
            return CommonResult.success(null, "操作成功！");
        }
    }

    @ApiOperation(value = "根据ID查询工艺", notes = "根据ID查询工艺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "routerId", value = "工艺id", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "机构", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping("/getByRouterId")
    public CommonResult<Router> getByRouterId(String routerId, String branchCode) {
        QueryWrapper<Router> queryWrapper = new QueryWrapper<Router>();

        if (!StringUtils.isNullOrEmpty(routerId)) {
            DrawingNoUtil.queryEq(queryWrapper, "id", routerId);
        }
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        queryWrapper.in("is_active", "0,1".split(","));
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.eq("status", "1");
        List<Router> routers = routerService.list(queryWrapper);
        if (routers.size() > 0) {
            return CommonResult.success(routers.get(0), "操作成功！");
        } else {
            return CommonResult.success(null, "操作成功！");
        }
    }

    @ApiOperation(value = "根据ID查询工艺", notes = "根据ID查询工艺")
    @ApiImplicitParam(name = "routerId", value = "工艺id", required = true, dataType = "String", paramType = "query")
    @GetMapping("/getRouter")
    public CommonResult<Router> getRouter(String routerId) {
        return CommonResult.success(routerService.getById(routerId), "操作成功！");
    }

    @ApiOperation(value = "批量图号查询工艺", notes = "批量图号获得工艺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "routerNos", value = "批量图号（,隔开）", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "机构", required = true, dataType = "String", paramType = "query")
    })
    @GetMapping("/getByRouterNos")
    public CommonResult<List<Router>> getByRouterNos(String routerNos, String branchCode) {
        QueryWrapper<Router> queryWrapper = new QueryWrapper<Router>();
        if (!StringUtils.isNullOrEmpty(routerNos)) {
            List<String> list = new ArrayList<>();
            Collections.addAll(list, routerNos.split(","));
            DrawingNoUtil.queryIn(queryWrapper, "router_no", list);
        }
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        queryWrapper.in("is_active", "0,1".split(","));
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.eq("status", "1");
        List<Router> routers = routerService.list(queryWrapper);
        if (routers.size() > 0) {
            return CommonResult.success(routers, "操作成功！");
        } else {
            return CommonResult.success(null, "操作成功！");
        }
    }

    @ApiOperation(value = "删除工艺", notes = "根据id删除工艺")
    @ApiImplicitParam(name = "ids", value = "编码", required = true, dataType = "String[]", paramType = "query")
    @PostMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<Router> delete(@RequestBody String[] ids) {
        routerService.delete(ids);
        return CommonResult.success(null, "删除成功！");
    }

    /**
     * @author mafeng
     * @date 2022-08-08 10:31:00
     */
    @ApiOperation(value = "复制工艺", notes = "复制工艺")
    @PostMapping("/copy")
    public CommonResult<Router> copy(String routerId, String tenantId, String branchCode) {


        if (!StringUtils.isNullOrEmpty(routerId)) {
            // 查询工艺和工艺路径
            QueryWrapper<Router> queryWrapper = new QueryWrapper<Router>();
            queryWrapper.eq("id", routerId);
            List<Router> routers = routerService.list(queryWrapper);

            QueryWrapper<Sequence> queryWrapper2 = new QueryWrapper<>();
            queryWrapper2.eq("router_id", routerId);
            List<Sequence> sequences = sequenceService.list(queryWrapper2);

            // 复制工艺，名字加复制
            Router r = routers.get(0);
            String copyRouterId = UUID.randomUUID().toString().replaceAll("-", "");
            r.setId(copyRouterId);
            String copyName = r.getRouterName() + "复制";
            String copyNo = r.getRouterNo() + "复制";
            r.setCreateTime(new Date());
            r.setModifyTime(new Date());
            r.setIsActive("1");
            r.setStatus("1");

            if (null != SecurityUtils.getCurrentUser()) {

                r.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                r.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            }
            r.setRouterName(copyName);
            r.setRouterNo(copyNo);
            routerService.save(r);

            // 复制工艺路径
            for (int i = 0; i < sequences.size(); i++) {
                sequences.get(i).setRouterId(copyRouterId);
                sequences.get(i).setId(UUID.randomUUID().toString().replaceAll("-", ""));
                sequences.get(i).setCreateTime(new Date());
                sequences.get(i).setModifyTime(new Date());
                if (null != SecurityUtils.getCurrentUser()) {
                    sequences.get(i).setCreateBy(SecurityUtils.getCurrentUser().getUsername());
                    sequences.get(i).setModifyBy(SecurityUtils.getCurrentUser().getUsername());
                }
            }
            sequenceService.saveBatch(sequences);
            return CommonResult.success(r);

        } else {
            return CommonResult.failed("失败!找不到要复制的工艺");
        }
    }


    @ApiOperation(value = "导入工艺", notes = "根据Excel文档导入工艺")
    @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "query")
    @PostMapping("/import_excel2")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult importExcel2(@RequestParam("file") MultipartFile file, String tenantId, String branchCode) {
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


    @ApiOperation(value = "导出工艺信息", notes = "通过Excel文档导出工艺信息")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "routerNo", value = "图号", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "routerName", value = "名称", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "branchCode", value = "机构编码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "status", value = "状态", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "isPDM", value = "查询是否有图纸", required = true, paramType = "query", dataType = "boolean"),
            @ApiImplicitParam(name = "tenantId", value = "租户ID", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/export_excel")
    public void exportExcel(String routerNo, String routerName, String branchCode, String tenantId, String status, boolean isPDM, HttpServletResponse rsp) {

        QueryWrapper<Router> queryWrapper = new QueryWrapper<Router>();
        if (!StringUtils.isNullOrEmpty(routerNo)) {
            DrawingNoUtil.queryLike(queryWrapper, "router_no", routerNo);
        }
        if (!StringUtils.isNullOrEmpty(routerName)) {
            queryWrapper.like("router_name", routerName);
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
        queryWrapper.orderByDesc("modify_time");
        List<Router> list = routerService.list(queryWrapper);

        for (Router router : list) {
            router.setType(OptTypeEnum.getMessage(Integer.parseInt(router.getType())));
            router.setStatus(RouterStatusEnum.getMessage(Integer.parseInt(router.getStatus())));
        }

        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddhhmmss");

        String fileName = "工艺数据" + format.format(new Date()) + ".xlsx";

        String[] columnHeaders = {"id", "图号", "工艺名称", "版本号", "状态", "类型", "物料号"};
        String[] fieldNames = {"id", "routerNo", "routerName", "version", "status", "type", "materialNo"};
        //export
        try {
            ExcelUtils.exportExcel(fileName, list, columnHeaders, fieldNames, rsp);
        } catch (IOException e) {
            log.error(e.getMessage());
            e.printStackTrace();
        }
    }


    @ApiOperation(value = "查询工艺是否为历史工艺", notes = "查询工艺是否为历史工艺")
    @ApiImplicitParam(name = "routerId", value = "工艺Id", required = true, dataType = "String", paramType = "query")
    @GetMapping("/queryIsHistory")
    public CommonResult<QueryIsHistory> queryIsHistory(String routerId, String branchCode) {
        return CommonResult.success(routerService.queryIsHistory(routerId, branchCode));
    }

    @ApiOperation(value = "查询新旧工艺", notes = "查询新旧工艺")
    @ApiImplicitParam(name = "routerId", value = "工艺Id", required = true, dataType = "String", paramType = "query")
    @GetMapping("/queryProcessRecords")
    public CommonResult<QueryProcessRecordsVo> queryProcessRecords(String routerId) {
        return CommonResult.success(routerService.queryProcessRecords(routerId));
    }

    @ApiOperation(value = "根据图号查询工艺", notes = "根据图号查询工艺")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "drawNos", value = "图号集合", required = true, paramType = "query", dataType = "list"),
            @ApiImplicitParam(name = "branchCode", value = "车间代码", required = true, paramType = "query", dataType = "string")
    })
    @PostMapping("/get_by_drawNo")
    public CommonResult<List<Router>> getByDrawNo(@RequestBody List<String> drawNos, @RequestParam String branchCode) {
        try {
            QueryWrapper<Router> queryWrapper = new QueryWrapper<Router>();
            DrawingNoUtil.queryIn(queryWrapper, "router_no", drawNos);
//            queryWrapper.in("draw_no", drawNos);
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            queryWrapper.eq("branch_code", branchCode);
            List<Router> routers = routerService.list(queryWrapper);
            return CommonResult.success(routers);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }


    @Autowired
    private RouterOptAssignService routerOptAssignService;
    @Autowired
    private OperationAssignService operationAssignService;


    @ApiOperation(value = "根据图号和工序name查询工艺工序派工", notes = "根据图号和工序name查询工艺工序派工")
    @GetMapping("/router/opt/assign/get")
    public CommonResult<RouterOptAssign> assignGet(String routerNo, String optName, String branchCode) {
        QueryWrapper<RouterOptAssign> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("opt_name", optName);
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.eq("router_no", routerNo);
        if (SecurityUtils.getCurrentUser() != null && SecurityUtils.getCurrentUser().getTenantId() != null) {
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        }
        RouterOptAssign one = routerOptAssignService.getOne(queryWrapper);
        //没有自动派工信息返回工序定义中的自动派工信息
        if (ObjectUtil.isEmpty(one)) {
            OperationAssign operatinoAssign = operationAssignService.getOperatinoByParam(optName, branchCode);
            //将工序定义的自动派工数据插入到工艺工序的自动派工配置里
            RouterOptAssign routerOptAssign = new RouterOptAssign();
            BeanUtil.copyProperties(operatinoAssign, routerOptAssign, new String[]{"id", "createTime", "modifyTime", "modifyBy"});
            routerOptAssign.setRouterNo(routerNo);
            routerOptAssignService.save(routerOptAssign);
            return CommonResult.success(routerOptAssign, "操作成功！");
        } else {
            ArrayList<AssignPerson> assignPeoples = new ArrayList<>();
            if (!ObjectUtil.isEmpty(one) && !StringUtils.isNullOrEmpty(one.getUserId())) {
                List<String> list = Arrays.asList(one.getUserId().split(","));
                for (String userId : list) {
                    AssignPerson assignPerson = new AssignPerson();
                    assignPerson.setUserId(userId);
                    assignPeoples.add(assignPerson);
                }
                one.setAssignPersons(assignPeoples);
            }
            return CommonResult.success(one, "操作成功！");
        }
    }

    @ApiOperation(value = "新增工艺工序派工", notes = "新增工艺工序派工")
    @ApiImplicitParam(name = "assign", value = "工艺工序派工", required = true, dataType = "RouterOptAssign", paramType = "body")
    @PostMapping("/router/opt/save")
    public CommonResult<Boolean> assignSave(@RequestBody RouterOptAssign assign) {
        //热处理派工派到班组  所以没有人员 （assign.getAssignPersons()为空）
        if (StringUtils.isNullOrEmpty(assign.getUserId()) && !ObjectUtil.isEmpty(assign.getAssignPersons())) {
            StringBuilder userId = new StringBuilder();
            StringBuilder userName = new StringBuilder();
            for (AssignPerson assignPerson : assign.getAssignPersons()) {
                if (!StringUtils.isNullOrEmpty(String.valueOf(userId))) {
                    userId.append(",");
                    userName.append(",");
                }
                userId.append(assignPerson.getUserId());
                userName.append(assignPerson.getUserName());
            }
            assign.setUserId(String.valueOf(userId));
            assign.setUserName(String.valueOf(userName));
        }
        assign.setSiteId(assign.getSiteId(assign.getBranchCode()));
        assign.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        return CommonResult.success(routerOptAssignService.save(assign), "操作成功！");
    }

    @ApiOperation(value = "修改工艺工序派工", notes = "修改工艺工序派工")
    @PostMapping("router/opt/update")
    public CommonResult<Boolean> assignUpdate(@RequestBody RouterOptAssign assign) {
        StringBuilder userId = new StringBuilder();
        StringBuilder userName = new StringBuilder();
        for (AssignPerson assignPerson : assign.getAssignPersons()) {
            if (!StringUtils.isNullOrEmpty(String.valueOf(userId))) {
                userId.append(",");
                userName.append(",");
            }
            userId.append(assignPerson.getUserId());
            userName.append(assignPerson.getUserName());
        }
        assign.setUserId(String.valueOf(userId));
        assign.setUserName(String.valueOf(userName));
        assign.setSiteId(assign.getSiteId(assign.getBranchCode()));
        return CommonResult.success(routerOptAssignService.updateById(assign), "操作成功！");
    }


    @ApiOperation(value = "erp同步", notes = "erp同步")
    @PostMapping("/erp/sync")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult erpSync(@ApiParam(value = "跟单号", required = true) @RequestBody List<Router> routers) {
        CommonResult result = erpServiceClient.erpSync(routers);
        if (result.getStatus() == 200) {
            for (Router router : routers) {
                router.setErpSyncTime(new Date());
                routerService.updateById(router);
            }
        }
        return result;
    }
}
