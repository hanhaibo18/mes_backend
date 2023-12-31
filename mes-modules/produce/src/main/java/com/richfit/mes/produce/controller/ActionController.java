package com.richfit.mes.produce.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.Action;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.service.ActionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * @Author: 王瑞
 */
@Slf4j
@Api(value = "操作信息", tags = {"操作信息"})
@RestController
@RequestMapping("/api/produce/action")
public class ActionController {

    @Autowired
    private ActionService actionService;

    /**
     * 分页查询
     */
    @ApiOperation(value = "查询操作信息", notes = "根据查询条件返回操作信息")
    @GetMapping("/action")
    public CommonResult queryByCondition(String startTime, String endTime, String user, String actionType, String actionItem, String remark, String branchId, int page, int limit) throws GlobalException {

        QueryWrapper<Action> queryWrapper = new QueryWrapper<Action>();
        if (!StringUtils.isNullOrEmpty(startTime)) {
            queryWrapper.ge("action_time", startTime + " 00:00:00");
        }
        if (!StringUtils.isNullOrEmpty(endTime)) {
            queryWrapper.le("action_time", endTime + " 23:59:59");
        }
        if (!StringUtils.isNullOrEmpty(user)) {
            queryWrapper.like("user", user);
        }
        if (!StringUtils.isNullOrEmpty(actionType)) {
            queryWrapper.eq("action_type", actionType);
        }
        if (!StringUtils.isNullOrEmpty(actionItem)) {
            queryWrapper.eq("action_item", actionItem);
        }
        queryWrapper.like(StrUtil.isNotBlank(remark), "remark", remark);
        queryWrapper.eq(StrUtil.isNotBlank(branchId), "branch_id", branchId);
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.orderByDesc("action_time");
        return CommonResult.success(actionService.page(new Page<Action>(page, limit), queryWrapper));

    }

    /**
     * 新增操作信息
     */
    @ApiOperation(value = "新增操作信息", notes = "新增操作信息")
    @PostMapping("/action")
    public CommonResult<Boolean> saveAction(@RequestBody Action action) {
        return CommonResult.success(actionService.saveAction(action));
    }

    /**
     * 根据ID获取操作信息
     */
    @ApiOperation(value = "获取操作信息", notes = "根据id获取操作信息")
    @GetMapping("/action/{id}")
    public CommonResult<Action> getAction(@PathVariable String id) throws GlobalException {
        return CommonResult.success(actionService.getById(id));
    }

    /**
     * 删除操作信息
     */
    @ApiOperation(value = "删除操作信息", notes = "根据id删除操作信息")
    @DeleteMapping("/action/{id}")
    public CommonResult<Boolean> delById(@PathVariable String id) throws GlobalException {
        return CommonResult.success(actionService.removeById(id));
    }
}
