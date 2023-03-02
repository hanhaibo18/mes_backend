package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.IErrorCode;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.CodeRule;
import com.richfit.mes.common.model.produce.CodeRuleItem;
import com.richfit.mes.common.model.produce.CodeRuleValue;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.service.CodeRuleItemService;
import com.richfit.mes.produce.service.CodeRuleService;
import com.richfit.mes.produce.service.CodeRuleValueService;
import com.richfit.mes.produce.utils.Code;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * <p>
 * 编码规则
 * </p>
 *
 * @author 马峰
 * @since 2020-09-07
 */
@Slf4j
@Api(value = "编码规则", tags = {"编码规则"})
@RestController
@RequestMapping("/api/produce/coderule")
public class CodeRuleController extends BaseController {

    @Autowired
    private CodeRuleService codeRuleService;
    @Autowired
    private CodeRuleItemService codeRuleItemService;
    @Autowired
    private CodeRuleValueService codeRuleValueService;
    public static String ID_NULL_MESSAGE = "ID不能为空!";
    public static String CLASS_NAME_NULL_MESSAGE = "名称不能为空!";
    public static String SUCCESS_MESSAGE = "操作成功！";
    public static String CODE_IS_EXIST = "编码已存在！";
    public static String DEFAULT_LENGTH = "10";
    public static String DEFAULT_WIDTH = "4";


    @ApiOperation(value = "分页查询编码规则", notes = "根据编码、名称、分类分页查询编码规则")
    @GetMapping("/page")
    public CommonResult<IPage<CodeRule>> pageCodeRule(String code, String name, int page, int limit, String tenantId, String branchCode) {
        QueryWrapper<CodeRule> queryWrapper = new QueryWrapper<CodeRule>();
        if (!StringUtils.isNullOrEmpty(code)) {
            queryWrapper.like("code", code);
        }
        if (!StringUtils.isNullOrEmpty(name)) {
            queryWrapper.like("name", name);
        }
        if (!StringUtils.isNullOrEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }

        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        if (SecurityUtils.getCurrentUser() != null && SecurityUtils.getCurrentUser().getTenantId() != null) {
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        }
        return CommonResult.success(codeRuleService.page(new Page<CodeRule>(page, limit), queryWrapper), SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "获取默认值", notes = "根据编码、名称、输入项获取默认值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "编码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "name", value = "编码名称", paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "inputs", value = "输入项值，如图号", paramType = "query", dataType = "string")
    })
    @GetMapping("/gerCode")
    public CommonResult<CodeRule> gerCode(String code, String name, String[] inputs, String tenantId, String branchCode) {
        try {
            return CommonResult.success(codeRuleService.gerCode(code, name, inputs, tenantId, branchCode), SUCCESS_MESSAGE);
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "更新编码值，流水号自增", notes = "更新编码值，流水号自增")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "编码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "value", value = "值，如整个产品号", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "inputs", value = "输入项值，如图号", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/updateCode")
    public CommonResult<CodeRule> updateCode(String code, String name, String value, String input, String tenantId, String branchCode) {
        try {
            return CommonResult.success(codeRuleService.updateCode(code, name, value, input, tenantId, branchCode));
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }

    @ApiOperation(value = "新增编码规则", notes = "新增编码规则")
    @PostMapping("/save")
    public CommonResult<Boolean> saveCodeRule(@RequestBody CodeRule entity) throws GlobalException {
        if (StringUtils.isNullOrEmpty(entity.getMaxLength())){
            entity.setMaxLength(DEFAULT_LENGTH);
        }
            if (StringUtils.isNullOrEmpty(entity.getName())) {
                return CommonResult.failed(CLASS_NAME_NULL_MESSAGE);
            }
        //新增编码已存在校验
        if (CheckCodeExist(entity)) return CommonResult.failed(CODE_IS_EXIST);
        return CommonResult.success(codeRuleService.save(entity));
    }

    @ApiOperation(value = "修改编码规则", notes = "修改编码规则")
    @PostMapping("/update")
    public CommonResult<Boolean> updateCodeRule(@RequestBody CodeRule entity) throws GlobalException {
        if (StringUtils.isNullOrEmpty(entity.getId())) {
            return CommonResult.failed(ID_NULL_MESSAGE);
        }
        if (StringUtils.isNullOrEmpty(entity.getName())) {
            return CommonResult.failed(CLASS_NAME_NULL_MESSAGE);
        }
        //新增编码已存在校验
        if (CheckCodeExist(entity)) {
            QueryWrapper<CodeRule> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("code", entity.getCode());
            queryWrapper.eq("branch_code", entity.getBranchCode());
            queryWrapper.eq("tenant_id", entity.getTenantId());
            List<CodeRule> codeRules = codeRuleService.list(queryWrapper);
            if (codeRules.size() > 1) {
                return CommonResult.failed(CODE_IS_EXIST);
            } else if (!codeRules.get(0).getId().equals(entity.getId())) {
                return CommonResult.failed(CODE_IS_EXIST);
            }
        }
        return CommonResult.success(codeRuleService.updateById(entity));
    }

    private boolean CheckCodeExist(@RequestBody CodeRule entity) {
        QueryWrapper<CodeRule> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("code", entity.getCode());
        queryWrapper.eq("branch_code", entity.getBranchCode());
        queryWrapper.eq("tenant_id", entity.getTenantId());
        List<CodeRule> codeRules = codeRuleService.list(queryWrapper);
        if (!codeRules.isEmpty()) {
            return true;
        }
        return false;
    }

    @ApiOperation(value = "删除编码规则", notes = "删除编码规则")
    @PostMapping("/delete/{id}")
    public CommonResult<Boolean> deleteCodeRule(@PathVariable String id) throws GlobalException {
        if (StringUtils.isNullOrEmpty(id)) {
            return CommonResult.failed(ID_NULL_MESSAGE);
        }
        List<CodeRuleItem> items = codeRuleItemService.list(new QueryWrapper<CodeRuleItem>().eq("code_rule_id", id));
        for (int i = 0; i < items.size(); i++) {
            codeRuleItemService.removeById(items.get(i));
        }
        return CommonResult.success(codeRuleService.removeById(id));
    }

    @ApiOperation(value = "查询编码规则项列表", notes = "查询编码规则项列表")
    @GetMapping("/item/list")
    public CommonResult<List<CodeRuleItem>> listCodeRuleItem(String codeRuleId, String code, String type, String tenantId, String branchCode) {
        QueryWrapper<CodeRuleItem> queryWrapper = new QueryWrapper<CodeRuleItem>();
        if (!StringUtils.isNullOrEmpty(code)) {

            List<CodeRule> list = codeRuleService.list(new QueryWrapper<CodeRule>().eq("code", code).eq("branch_code", branchCode).eq("tenant_id", tenantId));
            if (!list.isEmpty()) {
                queryWrapper.eq("code_rule_id", list.get(0).getId());
            } else {
                IErrorCode iErrorCode = new IErrorCode() {
                    @Override
                    public long getCode() {
                        return ResultCode.FAILED.getCode();
                    }

                    @Override
                    public String getMessage() {
                        return code + ":暂无编码规则";
                    }
                };
                return CommonResult.failed(iErrorCode);
            }

        }
        if (!StringUtils.isNullOrEmpty(codeRuleId)) {
            queryWrapper.eq("code_rule_id", codeRuleId);
        }
        if (!StringUtils.isNullOrEmpty(type)) {
            queryWrapper.eq("type", type);
        }
        queryWrapper.orderByAsc("order_no");
        if (!StringUtils.isNullOrEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }

        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        return CommonResult.success(codeRuleItemService.list(queryWrapper), SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询编码流水号值列表", notes = "查询编码流水号值列表")
    @GetMapping("/value/list")
    public CommonResult<List<CodeRuleValue>> listCodeRuleValue(String id, String input, String value, String tenantId, String branchCode) {
        QueryWrapper<CodeRuleValue> queryWrapper = new QueryWrapper<CodeRuleValue>();
        if (!StringUtils.isNullOrEmpty(input)) {
            queryWrapper.eq("input_value", input);
        }
        if (!StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("item_id", id);
        }
        if (!StringUtils.isNullOrEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }

        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        List<CodeRuleValue> list = codeRuleValueService.list(queryWrapper);
        if (list.size() == 0) {
            CodeRuleValue codeRuleValue = new CodeRuleValue();
            codeRuleValue.setItemId(id);
            codeRuleValue.setInputValue(input);
            codeRuleValue.setSnValue(value);
            list.add(codeRuleValue);
        }

        return CommonResult.success(list, SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "新增编码规则项", notes = "新增编码规则项")
    @PostMapping("/item/save")
    public CommonResult<Boolean> saveCodeRuleItem(@RequestBody CodeRuleItem entity) throws GlobalException {
        if (StringUtils.isNullOrEmpty(entity.getMaxLength())) {
            entity.setMaxLength(DEFAULT_LENGTH);
        }
        if (StringUtils.isNullOrEmpty(entity.getWidth())) {
            entity.setWidth(DEFAULT_WIDTH);
        }
        return CommonResult.success(codeRuleItemService.save(entity));
    }

    @ApiOperation(value = "修改编码规则项", notes = "修改编码规则项")
    @PostMapping("/item/update")
    public CommonResult<Boolean> updateCodeRuleItem(@RequestBody CodeRuleItem entity) throws GlobalException {
        if (StringUtils.isNullOrEmpty(entity.getId())) {
            return CommonResult.failed(ID_NULL_MESSAGE);
        }
        if (StringUtils.isNullOrEmpty(entity.getMaxLength())) {
            entity.setMaxLength(DEFAULT_LENGTH);
        }
        if (StringUtils.isNullOrEmpty(entity.getWidth())) {
            entity.setWidth(DEFAULT_WIDTH);
        }
        return CommonResult.success(codeRuleItemService.updateById(entity));
    }

    @ApiOperation(value = "删除编码规则项", notes = "删除编码规则项")
    @PostMapping("/item/delete/{id}")
    public CommonResult<Boolean> deleteCodeRuleItem(@PathVariable String id) throws GlobalException {
        if (StringUtils.isNullOrEmpty(id)) {
            return CommonResult.failed(ID_NULL_MESSAGE);
        }


        return CommonResult.success(codeRuleItemService.removeById(id));
    }


    @ApiOperation(value = "新增编码规则项流水号", notes = "新增编码规则项流水号")
    @PostMapping("/value/save")
    public CommonResult<Boolean> saveCodeRuleValue(@RequestBody CodeRuleValue entity) throws GlobalException {

        entity.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
        entity.setCreateTime(new Date());

        return CommonResult.success(codeRuleValueService.save(entity));
    }

    @ApiOperation(value = "修改编码规则项流水号", notes = "修改编码规则项流水号")
    @PostMapping("/value/update")
    public CommonResult<Boolean> updateCodeRuleValue(@RequestBody CodeRuleValue entity) throws GlobalException {
        if (StringUtils.isNullOrEmpty(entity.getId())) {
            return CommonResult.failed(ID_NULL_MESSAGE);
        }

        entity.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
        entity.setModifyTime(new Date());
        return CommonResult.success(codeRuleValueService.updateById(entity));
    }

    @ApiOperation(value = "删除编码规则项流水号", notes = "删除编码规则项流水号")
    @PostMapping("/value/delete/{id}")
    public CommonResult<Boolean> deleteCodeRuleValue(@PathVariable String id) throws GlobalException {
        if (StringUtils.isNullOrEmpty(id)) {
            return CommonResult.failed(ID_NULL_MESSAGE);
        }


        return CommonResult.success(codeRuleValueService.removeById(id));
    }

    @ApiOperation(value = "获取编码默认值", notes = "获取编码默认值")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "code", value = "编码", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "tenantId", value = "输入项值，如图号", required = true, paramType = "query", dataType = "string"),
            @ApiImplicitParam(name = "branchCode", value = "编码名称", required = true, paramType = "query", dataType = "string"),
    })
    @GetMapping("/get_code")
    public CommonResult<String> getCode(String code, String tenantId, String branchCode) {
        try {
            return CommonResult.success(Code.valueOnUpdate(code, tenantId, branchCode, codeRuleService));
        } catch (Exception e) {
            return CommonResult.failed(e.getMessage());
        }
    }
}
