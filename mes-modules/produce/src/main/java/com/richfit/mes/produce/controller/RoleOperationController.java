package com.richfit.mes.produce.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.ProduceRoleOperation;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.service.ProduceRoleOperationService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * @Author: zhiqiang.lu
 * @Date: 2022/9/2
 */
@Slf4j
@Api(tags = "角色工序关联表")
@RestController
@RequestMapping("/api/produce/role_operation")
public class RoleOperationController extends BaseController {

    @Autowired
    private ProduceRoleOperationService produceRoleOperationService;

    @ApiOperation(value = "角色工序配置表保存", notes = "角色工序配置表保存")
    @PostMapping("/save")
    public void save(@ApiParam(value = "开始时间", required = true) @RequestBody List<ProduceRoleOperation> produceRoleOperationList,
                     @ApiParam(value = "角色id", required = true) @RequestParam String roleId,
                     @ApiParam(value = "工厂代码", required = true) @RequestParam String branchCode) {
        QueryWrapper<ProduceRoleOperation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        queryWrapper.eq("branch_code", branchCode);
        produceRoleOperationService.remove(queryWrapper);
        for (ProduceRoleOperation produceRoleOperation : produceRoleOperationList) {
            produceRoleOperation.setId(UUID.randomUUID().toString().replaceAll("-", ""));
            produceRoleOperation.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            produceRoleOperationService.save(produceRoleOperation);
        }
        log.debug("inspection_record_card save is params produceRoleOperationList [{}]", produceRoleOperationList);
        log.debug("inspection_record_card save is params roleId [{}]", roleId);
        log.debug("inspection_record_card save is params branchCode [{}]", branchCode);
    }

    @ApiOperation(value = "角色工序配置表查询", notes = "角色工序配置表查询")
    @GetMapping("/select")
    public CommonResult<List<ProduceRoleOperation>> select(@ApiParam(value = "角色id", required = true) @RequestParam String roleId,
                                                           @ApiParam(value = "工厂代码", required = true) @RequestParam String branchCode) {
        QueryWrapper<ProduceRoleOperation> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(!StrUtil.isBlank(roleId), "role_id", roleId);
        queryWrapper.eq(!StrUtil.isBlank(branchCode), "branch_code", branchCode);
        List<ProduceRoleOperation> produceRoleOperationList = produceRoleOperationService.list(queryWrapper);
        log.debug("inspection_record_card trackFlowPage return is [{}]", produceRoleOperationList);
        return CommonResult.success(produceRoleOperationList);
    }
}
