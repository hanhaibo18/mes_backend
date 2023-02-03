package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.RouterCheckService;
import com.richfit.mes.base.service.SequenceService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.RouterCheck;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author 马峰
 * @Description 技术要求Controller
 */
@Slf4j
@Api("工序技术要求")
@RestController
@RequestMapping("/api/base/routerCheck")
public class RouterCheckController extends BaseController {

    @Autowired
    private RouterCheckService routerCheckService;

    @Autowired
    private SequenceService sequenceService;

    /**
     * 功能描述: id查询
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/8/31 11:37
     **/
    @ApiOperation(value = "id查询", notes = "id查询")
    @GetMapping("/select_by_id")
    public CommonResult<RouterCheck> selectById(@ApiParam(value = "id", required = true) @RequestParam String id) {
        try {
            return CommonResult.success(routerCheckService.getById(id));
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 功能描述: 列表查询
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/7/14 11:37
     **/
    @ApiOperation(value = "技术要求分页查询", notes = "技术要求分页查询")
    @GetMapping("/list")
    public CommonResult<List<RouterCheck>> list(@ApiParam(value = "工序id", required = true) @RequestParam String sequenceId,
                                                @ApiParam(value = "类型") @RequestParam(required = false) String type,
                                                @ApiParam(value = "状态") @RequestParam(required = false) String status) {
        try {
            QueryWrapper<RouterCheck> queryWrapper = new QueryWrapper<RouterCheck>();
            queryWrapper.eq("sequence_id", sequenceId);
            if (!StringUtils.isNullOrEmpty(type)) {
                queryWrapper.eq("type", type);
            } else {
                queryWrapper.in("type", "检查内容".split(","));

            }
            if (!StringUtils.isNullOrEmpty(status)) {
                queryWrapper.eq("status", status);
            }
            queryWrapper.orderByAsc("check_order");
            return CommonResult.success(routerCheckService.list(queryWrapper));
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    /**
     * 功能描述: 更新质量资料
     *
     * @Author: zhiqiang.lu
     * @Date: 2022/7/19 11:37
     **/
    @ApiOperation(value = "更新质量资料", notes = "更新质量资料")
    @PostMapping("/update_zlzl")
    public void updateZlzl(@ApiParam(value = "质量资料列表", required = true) @RequestBody List<RouterCheck> routerChecks, String branchCode, String tenantId, String sequenceId) throws Exception {
        try {
            TenantUserDetails user = SecurityUtils.getCurrentUser();

            QueryWrapper<RouterCheck> queryWrapperRouterCheck = new QueryWrapper<>();
            queryWrapperRouterCheck.eq("sequence_id", sequenceId);
            queryWrapperRouterCheck.eq("type", "质量资料");
            queryWrapperRouterCheck.eq("branch_code", branchCode);
            queryWrapperRouterCheck.eq("tenant_id", tenantId);
            routerCheckService.remove(queryWrapperRouterCheck);

            for (RouterCheck routerCheck : routerChecks) {
                routerCheck.setId(UUID.randomUUID().toString().replaceAll("-", ""));
                routerCheck.setType("质量资料");
                routerCheck.setStatus("1");
                routerCheck.setTenantId(user.getTenantId());
                routerCheck.setCreateTime(new Date());
                routerCheck.setCreateBy(user.getUsername());
                routerCheck.setModifyTime(new Date());
                routerCheck.setModifyBy(user.getUsername());
                routerCheckService.save(routerCheck);
            }

        } catch (Exception e) {
            e.printStackTrace();
            throw new Exception("更新质量资料异常");
        }
    }


    /**
     * ***
     * 分页查询
     *
     * @param page
     * @param limit
     * @return
     */
    @ApiOperation(value = "技术要求分页查询", notes = "技术要求分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sId", value = "工序ID", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "name", value = "名称", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "drawingNo", value = "图号", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/page")
    public CommonResult<IPage<RouterCheck>> page(int page, int limit, String sequenceId, String name, String drawingNo, String id, String type) {
        try {
            QueryWrapper<RouterCheck> queryWrapper = new QueryWrapper<RouterCheck>();
            if (!StringUtils.isNullOrEmpty(sequenceId)) {
                queryWrapper.eq("sequence_id", sequenceId);
            } else {
                queryWrapper.eq("sequence_id", "-1");
            }
            if (!StringUtils.isNullOrEmpty(type)) {
                queryWrapper.eq("type", type);
            } else {
                queryWrapper.notIn("type", "质量资料,技术要求,注意事项".split(","));
            }
            if (!StringUtils.isNullOrEmpty(name)) {
                queryWrapper.like("name", name);
            }
            if (!StringUtils.isNullOrEmpty(drawingNo)) {
                DrawingNoUtil.queryLike(queryWrapper, "drawing_no", drawingNo);
            }
            if (!StringUtils.isNullOrEmpty(id)) {
                queryWrapper.eq("id", id);
            }
            queryWrapper.orderByAsc("check_order");
            IPage<RouterCheck> routerChecks = routerCheckService.page(new Page<RouterCheck>(page, limit), queryWrapper);
            return CommonResult.success(routerChecks);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }


    /**
     * ***
     * 分页查询
     */
    @ApiOperation(value = "技术要求分页查询", notes = "技术要求分页查询")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "limit", value = "每页条数", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "page", value = "页码", required = true, paramType = "query", dataType = "int"),
            @ApiImplicitParam(name = "sId", value = "工序ID", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "name", value = "名称", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "drawingNo", value = "图号", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/find")
    public CommonResult<List<RouterCheck>> find(String drawingNo, String optId, String type) {
        try {
            QueryWrapper<RouterCheck> queryWrapper = new QueryWrapper<RouterCheck>();
            queryWrapper.apply("sequence_id in (select opt_id from base_sequence where id = '" + optId + "') and router_id in (select id from base_router where router_no = '" + drawingNo + "' and status ='1')");

            if (!StringUtils.isNullOrEmpty(type)) {
                queryWrapper.eq("type", type);
            } else {
                queryWrapper.notIn("type", "质量资料,技术要求,注意事项".split(","));
            }
            queryWrapper.orderByAsc("check_order");
            List<RouterCheck> routerChecks = routerCheckService.list(queryWrapper);
            return CommonResult.success(routerChecks);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "新增技术要求", notes = "新增技术要求")
    @ApiImplicitParam(name = "routerCheck", value = "技术要求", required = true, dataType = "RouterCheck", paramType = "query")
    @PostMapping("/add")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<RouterCheck> addRouterCheck(@RequestBody RouterCheck routerCheck) {

        if (StringUtils.isNullOrEmpty(routerCheck.getName())) {
            return CommonResult.failed("名称不能为空！");
        } else {
            TenantUserDetails user = SecurityUtils.getCurrentUser();
            routerCheck.setCreateBy(user.getUsername());
            routerCheck.setCreateTime(new Date());
            routerCheck.setModifyBy(user.getUsername());
            routerCheck.setModifyTime(new Date());
            routerCheck.setTenantId(user.getTenantId());
            boolean bool = routerCheckService.save(routerCheck);
            if (bool) {
                return CommonResult.success(routerCheck, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

    @ApiOperation(value = "修改技术要求", notes = "修改技术要求")
    @ApiImplicitParam(name = "routerCheck", value = "技术要求", required = true, dataType = "RouterCheck", paramType = "path")
    @PostMapping("/update")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<RouterCheck> updateRouterCheck(@RequestBody RouterCheck routerCheck) {
        if (StringUtils.isNullOrEmpty(routerCheck.getId())) {
            return CommonResult.failed("ID不能为空！");
        } else {
            TenantUserDetails user = SecurityUtils.getCurrentUser();
            routerCheck.setModifyBy(user.getUsername());
            routerCheck.setModifyTime(new Date());
            routerCheck.setTenantId(user.getTenantId());
            boolean bool = routerCheckService.updateById(routerCheck);
            if (bool) {
                return CommonResult.success(routerCheck, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }


    @ApiOperation(value = "删除技术要求", notes = "根据id删除技术要求")
    @ApiImplicitParam(name = "ids", value = "编码", required = true, dataType = "String[]", paramType = "query")
    @PostMapping("/delete")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult<RouterCheck> delete(@RequestBody String[] ids) {

        for (int i = 0; i < ids.length; i++) {

            routerCheckService.removeById(ids[i]);
        }
        return CommonResult.success(null, "删除成功！");

    }

    @ApiOperation(value = "导入工艺质量资料", notes = "根据Excel文档导入工艺质量资料")
    @ApiImplicitParam(name = "file", value = "Excel文件流", required = true, dataType = "MultipartFile", paramType = "query")
    @PostMapping("/import_excel_check")
    @Transactional(rollbackFor = Exception.class)
    public CommonResult importExcelCheck(@RequestParam("file") MultipartFile file, String tenantId, String branchCode) {
        return routerCheckService.importExcelCheck(file, tenantId, branchCode);
    }

    @ApiOperation(value = "查询质量资料列表", notes = "查询质量资料列表")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "optId", value = "optId", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "type", value = "类型", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "branchCode", value = "车间", required = true, dataType = "String", paramType = "query"),
            @ApiImplicitParam(name = "tenantId", value = "租户", required = true, dataType = "String", paramType = "query"),
    })
    @GetMapping("/queryRouterList")
    public List<RouterCheck> queryRouterList(String optId, String type, String branchCode, String tenantId) {
        return routerCheckService.queryRouterList(optId, type, branchCode, tenantId);
    }


}
