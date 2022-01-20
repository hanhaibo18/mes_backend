package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.CodeRule;
import com.richfit.mes.common.model.produce.CodeRuleItem;
import com.richfit.mes.common.model.produce.CodeRuleValue;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.service.CodeRuleService;
import com.richfit.mes.produce.service.CodeRuleItemService;
import com.richfit.mes.produce.service.CodeRuleValueService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import java.text.SimpleDateFormat;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.richfit.mes.common.model.sys.ItemClass;
import com.richfit.mes.common.model.sys.ItemParam;
import java.util.Date;
import java.util.List;
import org.springframework.scheduling.annotation.Scheduled;

/**
 * <p>
 * 编码规则
 * </p>
 *
 * @author 马峰
 * @since 2020-09-07
 */
@Slf4j
@Api("编码规则")
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
    
    @Autowired
    private com.richfit.mes.produce.provider.SystemServiceClient systemServiceClient;
   
    
     

    @ApiOperation(value = "分页查询编码规则", notes = "根据编码、名称、分类分页查询编码规则")
    @GetMapping("/page")
    public CommonResult<IPage<CodeRule>> pageCodeRule(String code, String name, int page, int limit, String tenantId, String branchCode) {
        QueryWrapper<CodeRule> queryWrapper = new QueryWrapper<CodeRule>();
        if (!StringUtils.isNullOrEmpty(code)) {
            queryWrapper.eq("code", code);
        }
        if (!StringUtils.isNullOrEmpty(name)) {
            queryWrapper.eq("name", name);
        }
        if (!StringUtils.isNullOrEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }
        
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        return CommonResult.success(codeRuleService.page(new Page<CodeRule>(page, limit), queryWrapper), SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "获取默认值", notes = "根据编码、名称、输入项获取默认值")
    @ApiImplicitParams({
        @ApiImplicitParam(name = "code", value = "编码", required = true, paramType = "query", dataType = "string"),
        @ApiImplicitParam(name = "name", value = "编码名称", required = true, paramType = "query", dataType = "string"),
        @ApiImplicitParam(name = "inputs", value = "输入项值，如图号", required = true, paramType = "query", dataType = "string")
    })
    @GetMapping("/gerCode")
    public CommonResult<CodeRule> gerCode(String code, String name, String[] inputs, String tenantId, String branchCode) {
        QueryWrapper<CodeRule> queryWrapper = new QueryWrapper<CodeRule>();
        if (!StringUtils.isNullOrEmpty(name)) {
            queryWrapper.eq("name", name);
        }
        if (!StringUtils.isNullOrEmpty(code)) {
            queryWrapper.eq("code", code);
        }
        queryWrapper.eq("status", 1);
        if (!StringUtils.isNullOrEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }
        
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        List<CodeRule> items = codeRuleService.list(queryWrapper);
        CodeRule item = null;
        if (items.size() > 0) {
            item = items.get(0);

        } else {
            return CommonResult.failed("找不到该编码规则");
        }
        item.setCurValue("");
        int index = 0;
        try {
            String value = "";
            List<CodeRuleItem> cris = this.listCodeRuleItem(item.getId(), null, null,tenantId,branchCode).getData();
            for (int i = 0; i < cris.size(); i++) {
                String subvalue = "";
                if (StringUtils.isNullOrEmpty(cris.get(i).getSuffixChar())) {
                    cris.get(i).setSuffixChar("");
                }
                // 常量
                if ("0".equals(cris.get(i).getType())) {
                    subvalue = cris.get(i).getConstant() + cris.get(i).getSuffixChar();
                }
                // 日期
                if ("1".equals(cris.get(i).getType())) {
                    Date currentTime = new Date();
                    SimpleDateFormat formatter = new SimpleDateFormat(cris.get(i).getDateFormat());
                    String dateString = formatter.format(currentTime);

                    subvalue = dateString + cris.get(i).getSuffixChar();
                }
                //流水号
                if ("2".equals(cris.get(i).getType())) {
                    //如果规则项 值重置的逻辑处理，根据年，月，日，最大值的变化来重置
                    if (!StringUtils.isNullOrEmpty(cris.get(i).getSnResetDependency())) {
                        if ("year".equals(cris.get(i).getSnResetDependency())) {
                            if (new Date().getYear() > cris.get(i).getSnCurrentDate().getYear()) {
                                cris.get(i).setSnCurrentValue(String.valueOf(Integer.parseInt(cris.get(i).getSnDefault()) - Integer.parseInt(cris.get(i).getSnStep())));
                            }
                        } else if ("quarter".equals(cris.get(i).getSnResetDependency())) {
                        } else if ("month".equals(cris.get(i).getSnResetDependency())) {
                            if (new Date().getMonth() > cris.get(i).getSnCurrentDate().getMonth()) {
                                cris.get(i).setSnCurrentValue(String.valueOf(Integer.parseInt(cris.get(i).getSnDefault()) - Integer.parseInt(cris.get(i).getSnStep())));
                            }
                        } else if ("date".equals(cris.get(i).getSnResetDependency())) {
                            if (new Date().getDay() > cris.get(i).getSnCurrentDate().getDay()) {
                                cris.get(i).setSnCurrentValue(String.valueOf(Integer.parseInt(cris.get(i).getSnDefault()) - Integer.parseInt(cris.get(i).getSnStep())));
                            }
                        } else if ("input".equals(cris.get(i).getSnResetDependency())) {
                            
                        } else {

                            if (Integer.parseInt(cris.get(i).getSnCurrentValue()) >= Integer.parseInt(cris.get(i).getSnResetDependency())) {
                                cris.get(i).setSnCurrentValue(String.valueOf(Integer.parseInt(cris.get(i).getSnDefault()) - Integer.parseInt(cris.get(i).getSnStep())));
                            }
                        }
                    }
                    subvalue = String.valueOf(Integer.parseInt(cris.get(i).getSnCurrentValue()) + Integer.parseInt(cris.get(i).getSnStep())) + cris.get(i).getSuffixChar();
                }
                //用户输入项
                if ("3".equals(cris.get(i).getType())) {

                    if ("0".equals(cris.get(i).getCheckType())) {

                        if (!java.util.regex.Pattern.matches("^[0-9]*$", inputs[index])) {
                            return CommonResult.failed(i + " 不是数字。");
                        }
                    }
                    if ("1".equals(cris.get(i).getCheckType())) {

                        if (!java.util.regex.Pattern.matches("[a-zA-Z]+", inputs[index])) {
                            return CommonResult.failed(i + " 不是字母。");
                        }
                    }
                    if ("2".equals(cris.get(i).getCheckType()) && !StringUtils.isNullOrEmpty(cris.get(i).getCheckRegex())) {

                        if (!java.util.regex.Pattern.matches(cris.get(i).getCheckRegex(), inputs[index])) {
                            return CommonResult.failed(i + " 不满足正则校验。");
                        }
                    }


                    subvalue = inputs[index] + cris.get(i).getSuffixChar();
                    index++;
                }
                //GUID
                if ("4".equals(cris.get(i).getType())) {


                    subvalue = java.util.UUID.randomUUID() + cris.get(i).getSuffixChar();
                    index++;
                }
                if (!StringUtils.isNullOrEmpty(cris.get(i).getCompChar())) {
                    if ("0".equals(cris.get(i).getCompDirect())) {
                        subvalue = org.apache.commons.lang.StringUtils.leftPad(subvalue, Integer.parseInt(cris.get(i).getMaxLength()), cris.get(i).getCompChar());
                    } else {
                        subvalue = org.apache.commons.lang.StringUtils.rightPad(subvalue, Integer.parseInt(cris.get(i).getMaxLength()), cris.get(i).getCompChar());
                    }
                }
                value = value + subvalue;
            }
            if (!StringUtils.isNullOrEmpty(item.getMaxLength())) {
                if (value.length() > Integer.parseInt(item.getMaxLength())) {
                    return CommonResult.failed("编码长度超出范围，最大为" + item.getMaxLength());
                }
            }
            item.setCurValue(value);
            return CommonResult.success(item, SUCCESS_MESSAGE);
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

        QueryWrapper<CodeRule> queryWrapper = new QueryWrapper<CodeRule>();
        if (!StringUtils.isNullOrEmpty(name)) {
            queryWrapper.eq("name", name);
        }
        if (!StringUtils.isNullOrEmpty(code)) {
            queryWrapper.eq("code", code);
        }
        queryWrapper.eq("status", 1);
        if (!StringUtils.isNullOrEmpty(tenantId)) {
            queryWrapper.eq("tenant_id", tenantId);
        }
        
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.eq("branch_code", branchCode);
        }
        // 获取编码项列表
        List<CodeRule> items = codeRuleService.list(queryWrapper);
        CodeRule item = null;
        if (items.size() > 0) {
            item = items.get(0);
                // 输入依赖项
                CodeRuleItem inputTtem = null;
                // 日期项
                CodeRuleItem dateRuleItem = null;
                //是否流水号按输入依赖项自增
                boolean enableInputTtem =false;
                 //是否流水号按日项自增
                boolean enableDateRuleItem =false;
                
                // 获取编码项
                List<CodeRuleItem> list2 = codeRuleItemService.list(new QueryWrapper<CodeRuleItem>().eq("code_rule_id", item.getId()));
                for (int i = 0; i < list2.size(); i++) {
                     if ("3".equals(list2.get(i).getType())){
                         inputTtem = list2.get(i);
                     }
                     if ("1".equals(list2.get(i).getType())){
                         dateRuleItem = list2.get(i);
                     }
                     //如果当前编码项是流水号，且重置依赖是空，则当前编码项的流水号自增
                    if ("2".equals(list2.get(i).getType()) && StringUtils.isNullOrEmpty(list2.get(i).getSnResetDependency())) {
                        if (!StringUtils.isNullOrEmpty(list2.get(i).getSnCurrentValue())) {
                             //如果当前值不是空，当前编码项的流水号自增
                            list2.get(i).setSnCurrentValue(String.valueOf(Integer.parseInt(list2.get(i).getSnCurrentValue()) + Integer.parseInt(list2.get(i).getSnStep())));
                        } else {
                            //如果当前值是空，则读取重置值，写入
                            list2.get(i).setSnCurrentValue(String.valueOf(Integer.parseInt(list2.get(i).getSnDefault())));

                        }
                        codeRuleItemService.updateById(list2.get(i));
                    }
                    else {
                        if("input".equals(list2.get(i).getSnResetDependency())) {
                            enableInputTtem= true;
                        }
                        if("year".equals(list2.get(i).getSnResetDependency())||"month".equals(list2.get(i).getSnResetDependency())||"date".equals(list2.get(i).getSnResetDependency())) {
                            enableDateRuleItem= true;
                        }
                    }
                 }
                    
                
                //如果当前编码项是流水号，且重置依赖不为空
                if(null!=inputTtem && enableInputTtem) {
                // 获取编码项依赖流水号列表
                List<CodeRuleValue> list = codeRuleValueService.list(new QueryWrapper<CodeRuleValue>().eq("input_value",input).apply("item_id  in (select id from produce_code_rule_item where code_rule_id in (select id from produce_code_rule where code = '" + code + "')) "));
                if(list.size() ==0) {
                    //如果是空，则新产生一个编码项依赖流水号自增，如图号
                    CodeRuleValue codeRuleValue =new CodeRuleValue();
                    codeRuleValue.setItemId(inputTtem.getId());
                    codeRuleValue.setInputValue(input);
                    codeRuleValue.setSnValue(inputTtem.getSnDefault());
                    codeRuleValueService.save(codeRuleValue);
                }
                else {
                     //如果不是空，则编码项依赖流水号自增，如图号
                  for (int i = 0; i < list.size(); i++) {
                    if(input.equals(list.get(i).getSnValue())) {
                        return CommonResult.failed("该编号已存在！");
                    }
                    else {
                    // 流水号自增
                    list.get(i).setSnValue(String.valueOf(Integer.parseInt(list.get(i).getSnValue()) +Integer.parseInt( inputTtem.getSnStep())));
                       codeRuleValueService.updateById(list.get(i));
                    }
                  }
                }
                }
                 // 如果流水号重置的依赖条件为日期，且日期输入项不为空
                 if(null!=dateRuleItem && enableDateRuleItem) {
                List<CodeRuleValue> list = codeRuleValueService.list(new QueryWrapper<CodeRuleValue>().eq("input_value",input).apply("item_id  in (select id from produce_code_rule_item where code_rule_id in (select id from produce_code_rule where code = '" + code + "')) "));
                if(list.size() ==0) {
                    CodeRuleValue codeRuleValue =new CodeRuleValue();
                    codeRuleValue.setItemId(dateRuleItem.getId());
                    codeRuleValue.setInputValue(input);
                    codeRuleValue.setSnValue(dateRuleItem.getSnDefault());
                    codeRuleValueService.save(codeRuleValue);
                }
                else {
                  for (int i = 0; i < list.size(); i++) {
                    if(input.equals(list.get(i).getSnValue())) {
                       // return CommonResult.failed("该编号已存在！");
                    }
                    else {
                    // 流水号自增
                    list.get(i).setSnValue(String.valueOf(Integer.parseInt(list.get(i).getSnValue()) +Integer.parseInt( dateRuleItem.getSnStep())));
                       codeRuleValueService.updateById(list.get(i));
                    }
                  }
                }
                }
                

                item.setCurValue(value);
                codeRuleService.updateById(item);

                return CommonResult.success(item);
           
        } else {
            return CommonResult.failed("找不到该编码规则");
        }

    }

    @ApiOperation(value = "新增编码规则", notes = "新增编码规则")
    @PostMapping("/save")
    public CommonResult<Boolean> saveCodeRule(@RequestBody CodeRule entity) throws GlobalException {
        
        entity.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
        entity.setCreateTime(new Date());
        if (StringUtils.isNullOrEmpty(entity.getName())) {
            return CommonResult.failed(CLASS_NAME_NULL_MESSAGE);
        }
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
        entity.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
        entity.setModifyTime(new Date());
        return CommonResult.success(codeRuleService.updateById(entity));
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
            queryWrapper.eq("code_rule_id", list.get(0).getId());
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
                if(list.size() ==0 ||StringUtils.isNullOrEmpty(input) ) {
                    CodeRuleValue codeRuleValue =new CodeRuleValue();
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
        
        entity.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
        entity.setCreateTime(new Date());

        return CommonResult.success(codeRuleItemService.save(entity));
    }

    @ApiOperation(value = "修改编码规则项", notes = "修改编码规则项")
    @PostMapping("/item/update")
    public CommonResult<Boolean> updateCodeRuleItem(@RequestBody CodeRuleItem entity) throws GlobalException {
        if (StringUtils.isNullOrEmpty(entity.getId())) {
            return CommonResult.failed(ID_NULL_MESSAGE);
        }

        entity.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
        entity.setModifyTime(new Date());
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
    
    
}
