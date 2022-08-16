package com.richfit.mes.sys.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.SystemLog;
import com.richfit.mes.common.security.annotation.Inner;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.sys.entity.param.SystemLogPageParam;
import com.richfit.mes.sys.entity.param.SystemLogParam;
import com.richfit.mes.sys.service.LogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * @author sun
 * @Description Log Controller
 */
@RequestMapping("/api/sys/log")
@Api("log")
@RestController
public class LogController {

    @Autowired
    private LogService logService;

    @ApiOperation(value = "分页查询日志", notes = "分页查询日志")
    @GetMapping("/page")
    public CommonResult getLogPage(@Valid SystemLogPageParam systemLogPageParam) {
        SystemLogParam systemLogParam = systemLogPageParam.toParam(SystemLogParam.class);
        //TODO 统一处理tenantId
        QueryWrapper<SystemLog> queryWrapper = systemLogParam.build().eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.eq(StringUtils.isNotBlank(systemLogParam.getType()),"type",systemLogParam.getType());
        queryWrapper.eq(StringUtils.isNotBlank(systemLogParam.getResult()),"result",systemLogParam.getResult());
        queryWrapper.orderByDesc("modify_time");
        return CommonResult.success(logService.page(systemLogPageParam.toPage(), queryWrapper));
    }

    @ApiOperation(value = "删除日志", notes = "根据url的id来指定删除日志")
    @ApiImplicitParam(paramType = "path", name = "id", value = "日志ID", required = true, dataType = "String")
    @DeleteMapping("/{id}")
    public CommonResult delete(@PathVariable String id) {
        return CommonResult.success(logService.removeById(id));
    }

    @ApiOperation(value = "新增日志", notes = "新增日志")
    @ApiImplicitParam(name = "systemLog", value = "日志", required = true, dataType = "SystemLog", paramType = "body")
    @PostMapping("/save")
    @Inner
    public CommonResult save(@Valid @RequestBody SystemLog systemLog) {
        return CommonResult.success(logService.save(systemLog));
    }

    @ApiOperation(value = "查询日志详情", notes = "")
    @GetMapping(value = "/user/{logId}")
    public CommonResult query(@PathVariable String logId) {
        return CommonResult.success(logService.getById(logId));
    }

    @ApiOperation(value = "批量插入日志", notes = "批量插入日志")
    @ApiImplicitParam(name = "logs", value = "日志列表", required = true, dataType = "SystemLog", allowMultiple = true, paramType = "body")
    @PostMapping("/logs")
    @Inner
    public CommonResult saveBatchRequestBody(List<SystemLog> logs) {
        //TODO 统一处理tenantId
        logs.forEach(systemLog -> {
            systemLog.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        });
        return CommonResult.success(logService.saveBatch(logs));
    }
}
