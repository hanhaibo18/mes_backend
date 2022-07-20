package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.OperationDeviceService;
import com.richfit.mes.base.service.OperatiponService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.OperationDevice;
import com.richfit.mes.common.model.base.Operatipon;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * @author 马峰
 * @Description 工序字典Controller
 */
@Slf4j
@Api("工序字典管理")
@RestController
@RequestMapping("/api/base/opt")
public class OperatiponController extends BaseController {

    @Autowired
    private OperatiponService operatiponService;

    @Autowired
    private OperationDeviceService operationDeviceService;

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
            @ApiImplicitParam(name = "routerId", value = "工艺ID", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "optCode", value = "工序字典编码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "optName", value = "工序字典名称", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/page")
    public CommonResult<IPage<Operatipon>> page(int page, int limit, String routerId, String optCode, String optName, String optType, String branchCode, String tenantId) {
        try {

            QueryWrapper<Operatipon> queryWrapper = new QueryWrapper<Operatipon>();

            if (!StringUtils.isNullOrEmpty(tenantId)) {
                queryWrapper.eq("tenant_id", tenantId);
            }
            if (!StringUtils.isNullOrEmpty(branchCode)) {
                queryWrapper.eq("branch_code", branchCode);
            }
            if (!StringUtils.isNullOrEmpty(optCode)) {
                queryWrapper.like("opt_code", optCode);
            }
            if (!StringUtils.isNullOrEmpty(optName)) {
                queryWrapper.like("opt_name", optName);
            }
            if (!StringUtils.isNullOrEmpty(optType)) {
                queryWrapper.eq("opt_type", Integer.parseInt(optType));
            }
            queryWrapper.orderByDesc("modify_time");
            IPage<Operatipon> routers = operatiponService.page(new Page<Operatipon>(page, limit), queryWrapper);
            return CommonResult.success(routers);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "新增工序字典", notes = "新增工序字典")
    @ApiImplicitParam(name = "operatipon", value = "工序字典", required = true, dataType = "Operatipon", paramType = "path")
    @PostMapping("/add")
    public CommonResult<Operatipon> addOperatipon(@RequestBody Operatipon operatipon) {
        if (StringUtils.isNullOrEmpty(operatipon.getOptCode())) {
            return CommonResult.failed("编码不能为空！");
        } else {

            QueryWrapper<Operatipon> queryWrapper = new QueryWrapper<Operatipon>();
            if (!StringUtils.isNullOrEmpty(operatipon.getBranchCode())) {
                queryWrapper.eq("branch_code", operatipon.getBranchCode());
            }
            if (!StringUtils.isNullOrEmpty(operatipon.getOptCode())) {
                queryWrapper.eq("opt_code", operatipon.getOptCode());
            }
            List<Operatipon> list = operatiponService.list(queryWrapper);
            if (list.size() > 0) {
                return CommonResult.failed("操作失败，工序编码不能重复！");
            }
            QueryWrapper<Operatipon> queryWrapper2 = new QueryWrapper<Operatipon>();
            if (!StringUtils.isNullOrEmpty(operatipon.getBranchCode())) {
                queryWrapper2.eq("branch_code", operatipon.getBranchCode());
            }
            if (!StringUtils.isNullOrEmpty(operatipon.getOptName())) {
                queryWrapper2.eq("opt_name", operatipon.getOptName());
            }
            List<Operatipon> list2 = operatiponService.list(queryWrapper2);
            if (list2.size() > 0) {
                return CommonResult.failed("操作失败，工序名称不能重复！");
            }


            TenantUserDetails user = SecurityUtils.getCurrentUser();
            operatipon.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            operatipon.setModifyBy(user.getUsername());
            operatipon.setModifyTime(new Date());
            operatipon.setCreateBy(user.getUsername());
            operatipon.setCreateTime(new Date());
            operatipon.setTenantId(user.getTenantId());
            boolean bool = operatiponService.save(operatipon);
            if (bool) {
                return CommonResult.success(operatipon, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

    @ApiOperation(value = "修改工序字典", notes = "修改工序字典")
    @ApiImplicitParam(name = "operatipon", value = "工序字典", required = true, dataType = "Operatipon", paramType = "path")
    @PostMapping("/update")
    public CommonResult<Operatipon> updateOperatipon(@RequestBody Operatipon operatipon) {
        if (StringUtils.isNullOrEmpty(operatipon.getOptCode())) {
            return CommonResult.failed("机构编码不能为空！");
        } else {
            TenantUserDetails user = SecurityUtils.getCurrentUser();
            operatipon.setModifyBy(user.getUsername());
            operatipon.setModifyTime(new Date());
            operatipon.setTenantId(user.getTenantId());
            boolean bool = operatiponService.updateById(operatipon);
            if (bool) {
                return CommonResult.success(operatipon, "操作成功！");
            } else {
                return CommonResult.failed("操作失败，请重试！");
            }
        }
    }

    @ApiOperation(value = "查询工序字典", notes = "根据编码获得工序字典")
    @ApiImplicitParam(name = "operatiponCode", value = "编码", required = true, dataType = "String", paramType = "path")
    @GetMapping("/find")
    public CommonResult<List<Operatipon>> find(String id, String optCode, String optName, String routerId, String branchCode, String tenantId) {
        QueryWrapper<Operatipon> queryWrapper = new QueryWrapper<Operatipon>();
        if (!StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("id", id);
        }
        if (!StringUtils.isNullOrEmpty(optCode)) {
            queryWrapper.like("opt_code", optCode);
        }

        if (!StringUtils.isNullOrEmpty(optName)) {
            queryWrapper.like("opt_name", optName);
        }
        if (!StringUtils.isNullOrEmpty(routerId)) {
            queryWrapper.eq("router_id", routerId);
        }
        if (!StringUtils.isNullOrEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        queryWrapper.orderByAsc("opt_order");
        List<Operatipon> result = operatiponService.list(queryWrapper);
        return CommonResult.success(result, "操作成功！");
    }

    @ApiOperation(value = "查询工序关联设备", notes = "根据工序ID获得查询工序关联设备")
    @GetMapping("/findDeviceRelation")
    public CommonResult<List<OperationDevice>> findDeviceRelation(String optId, String branchCode, String tenantId) {
        QueryWrapper<OperationDevice> queryWrapper = new QueryWrapper<OperationDevice>();
        if (!StringUtils.isNullOrEmpty(optId)) {
            queryWrapper.eq("operation_id", optId);
        }

        if (!StringUtils.isNullOrEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }

        List<OperationDevice> result = operationDeviceService.list(queryWrapper);
        return CommonResult.success(result, "操作成功！");
    }

    @ApiOperation(value = "保存工序关联设备", notes = "根据工序ID和设备ID保存关联关系")
    @PostMapping("/saveDeviceRelation")
    public CommonResult<Boolean> saveDeviceRelation(@RequestParam String optId, @RequestBody String[] deviceIds, String branchCode, String tenantId) {
        if (StringUtils.isNullOrEmpty(optId)) {
            return CommonResult.failed("必须选择工序！");
        }

        operationDeviceService.remove(new QueryWrapper<OperationDevice>().eq("operation_id", optId));

        List<OperationDevice> data = new ArrayList<>();

        for (String deviceId : deviceIds) {
            OperationDevice operationDevice = new OperationDevice();
            operationDevice.setOperationId(optId);
            operationDevice.setDeviceId(deviceId);
            operationDevice.setTenantId(tenantId);
            operationDevice.setBranchCode(branchCode);
            operationDevice.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
            data.add(operationDevice);
        }

        return CommonResult.success(operationDeviceService.saveBatch(data), "操作成功！");
    }

    @ApiOperation(value = "查询工序字典", notes = "根据工艺ID获得工序字典")
    @ApiImplicitParam(name = "routerId", value = "工艺ID", required = true, dataType = "String", paramType = "path")
    @GetMapping("/findByRouterId")
    public CommonResult<List<Operatipon>> findByRouterId(String routerId) {
        QueryWrapper<Operatipon> queryWrapper = new QueryWrapper<Operatipon>();
        queryWrapper.eq("router_id", routerId);
        List<Operatipon> result = operatiponService.list(queryWrapper);
        return CommonResult.success(result, "操作成功！");
    }

    @ApiOperation(value = "删除工序字典", notes = "根据id删除工序字典")
    @ApiImplicitParam(name = "id", value = "ids", required = true, dataType = "String", paramType = "path")
    @PostMapping("/delete")
    public CommonResult<Operatipon> deleteById(@RequestBody String[] ids) {

        boolean bool = operatiponService.removeByIds(java.util.Arrays.asList(ids));
        if (bool) {
            return CommonResult.success(null, "删除成功！");
        } else {
            return CommonResult.failed("操作失败，请重试！");
        }

    }
}
