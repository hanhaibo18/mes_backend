package com.richfit.mes.produce.service;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.CodeRule;
import com.richfit.mes.common.model.produce.CodeRuleItem;
import com.richfit.mes.common.model.produce.CodeRuleValue;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.CodeRuleItemMapper;
import com.richfit.mes.produce.dao.CodeRuleMapper;
import com.richfit.mes.produce.provider.SystemServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * <p>
 * 编码规则 服务实现类
 * </p>
 *
 * @author 马峰
 * @since 2020-09-07
 */
@Service
public class CodeRuleServiceImpl extends ServiceImpl<CodeRuleMapper, CodeRule> implements CodeRuleService {

    @Autowired
    CodeRuleMapper codeRuleMapper;
    @Autowired
    private CodeRuleService codeRuleService;
    @Autowired
    private CodeRuleItemService codeRuleItemService;
    @Autowired
    private CodeRuleValueService codeRuleValueService;
    @Autowired
    CodeRuleItemMapper codeRuleItemMapper;

    public static String ID_NULL_MESSAGE = "ID不能为空!";
    public static String CLASS_NAME_NULL_MESSAGE = "名称不能为空!";
    public static String SUCCESS_MESSAGE = "操作成功！";
    public static String RULE_ID_NULL_MESSAGE = "编码规则ID不能为空!";
    public static final String IS_COMPANY_RULE = "0";
    public static final String IS_SITE_RULE = "1";
    public static final String IS_ROLE_RULE = "2";

    /********
     *
     * @param code  规则CODE
     * @param input 依赖输入的值，如图号
     * @param value 最终值，如整个跟单号
     * @return
     */
    @Override
    public CodeRule submit(String code, String input, String value) {
        QueryWrapper<CodeRule> queryWrapper = new QueryWrapper<CodeRule>();
        if (!StringUtils.isNullOrEmpty(code)) {
            queryWrapper.eq("code", code);
        }
        queryWrapper.eq("status", 1);
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        List<CodeRule> items = codeRuleService.list(queryWrapper);
        CodeRule item = null;
        if (items.size() > 0) {
            item = items.get(0);

            CodeRuleItem inputTtem = null;
            CodeRuleItem dateRuleItem = null;
            boolean enableInputTtem = false;
            boolean enableDateRuleItem = false;
            List<CodeRuleItem> list2 = codeRuleItemService.list(new QueryWrapper<CodeRuleItem>().eq("code_rule_id", item.getId()));
            for (int i = 0; i < list2.size(); i++) {
                if ("3".equals(list2.get(i).getType())) {
                    inputTtem = list2.get(i);
                }
                if ("1".equals(list2.get(i).getType())) {
                    dateRuleItem = list2.get(i);
                }
                if ("2".equals(list2.get(i).getType()) && StringUtils.isNullOrEmpty(list2.get(i).getSnResetDependency())) {
                    if (!StringUtils.isNullOrEmpty(list2.get(i).getSnCurrentValue())) {
                        list2.get(i).setSnCurrentValue(String.valueOf(Integer.parseInt(list2.get(i).getSnCurrentValue()) + Integer.parseInt(list2.get(i).getSnStep())));
                    } else {
                        list2.get(i).setSnCurrentValue(String.valueOf(Integer.parseInt(list2.get(i).getSnDefault())));

                    }
                    codeRuleItemService.updateById(list2.get(i));
                } else {
                    if ("input".equals(list2.get(i).getSnResetDependency())) {
                        enableInputTtem = true;
                    }
                    if ("year".equals(list2.get(i).getSnResetDependency()) || "month".equals(list2.get(i).getSnResetDependency()) || "date".equals(list2.get(i).getSnResetDependency())) {
                        enableDateRuleItem = true;
                    }
                }
            }


            if (null != inputTtem && enableInputTtem) {
                List<CodeRuleValue> list = codeRuleValueService.list(new QueryWrapper<CodeRuleValue>().eq("input_value", input).apply("item_id  in (select id from produce_code_rule_item where code_rule_id in (select id from produce_code_rule where code = '" + code + "')) "));
                if (list.size() == 0) {
                    CodeRuleValue codeRuleValue = new CodeRuleValue();
                    codeRuleValue.setItemId(inputTtem.getId());
                    codeRuleValue.setInputValue(input);
                    codeRuleValue.setSnValue(inputTtem.getSnDefault());
                    codeRuleValueService.save(codeRuleValue);
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        if (input.equals(list.get(i).getSnValue())) {
                            return null;
                        } else {
                            list.get(i).setSnValue(String.valueOf(Integer.parseInt(list.get(i).getSnValue()) + Integer.parseInt(inputTtem.getSnStep())));
                            codeRuleValueService.updateById(list.get(i));
                        }
                    }
                }
            }
            // 如果流水号重置的依赖条件为日期，且日期输入项不为空
            if (null != dateRuleItem && enableDateRuleItem) {
                List<CodeRuleValue> list = codeRuleValueService.list(new QueryWrapper<CodeRuleValue>().eq("input_value", input).apply("item_id  in (select id from produce_code_rule_item where code_rule_id in (select id from produce_code_rule where code = '" + code + "')) "));
                if (list.size() == 0) {
                    CodeRuleValue codeRuleValue = new CodeRuleValue();
                    codeRuleValue.setItemId(dateRuleItem.getId());
                    codeRuleValue.setInputValue(input);
                    codeRuleValue.setSnValue(dateRuleItem.getSnDefault());
                    codeRuleValueService.save(codeRuleValue);
                } else {
                    for (int i = 0; i < list.size(); i++) {
                        if (input.equals(list.get(i).getSnValue())) {
                            // return CommonResult.failed("该编号已存在！");
                        } else {
                            list.get(i).setSnValue(String.valueOf(Integer.parseInt(list.get(i).getSnValue()) + Integer.parseInt(dateRuleItem.getSnStep())));
                            codeRuleValueService.updateById(list.get(i));
                        }
                    }
                }
            }


            item.setCurValue(value);
            codeRuleService.updateById(item);

            return item;

        } else {
            return null;
        }
    }

    @Autowired
    public SystemServiceClient systemServiceClient;

    /**
     * 获取编码
     *
     * @param
     * @return
     */
    @Override
    public CodeRule gerCode(String code, String name, String[] inputs, String tenantId, String branchCode) {
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
        queryWrapper.orderByAsc("create_time");
        List<CodeRule> items = codeRuleService.list(queryWrapper);

        CodeRule item = null;
        if (items.size() > 0) {
            //权限判断
            if(IS_ROLE_RULE.equals(items.get(0).getLevel())){
                //根据userId获取角色
                List<String> roles = systemServiceClient.queryRolesByUserId(SecurityUtils.getCurrentUser().getUserId()).stream().map(Role::getRoleCode).collect(Collectors.toList());
                for (CodeRule codeRule : items) {
                    List<String> newRoles = new ArrayList<>(roles);
                    List<String> roleIdList = codeRule.getRoleIdList();
                    newRoles.retainAll(roleIdList);
                    if(newRoles.size()>0){
                        item = codeRule;
                        break;
                    }
                }
                if(ObjectUtil.isEmpty(item)){
                    throw new GlobalException("找不到当前登录人编码规则或该编码规则已停用:"+items.get(0).getCode()+"的权限级编码",ResultCode.FAILED);
                }
            }else if(IS_COMPANY_RULE.equals(items.get(0).getLevel())){
                item = items.get(0);
            }else {
                List<CodeRule> siteRules = items.stream().filter(i -> i.getBranchCode().equals(branchCode)).collect(Collectors.toList());
                if(siteRules.size()>0){
                    item = siteRules.get(0);
                }
                if(ObjectUtil.isEmpty(item)){
                    throw new GlobalException("找不到当前登录人编码规则或该编码规则已停用:"+items.get(0).getCode()+"的车间级编码",ResultCode.FAILED);
                }
            }
        } else {
            throw new NullPointerException("找不到该编码规则或该编码规则已停用:" + code);
        }
        item.setCurValue("");
        int index = 0;
        String value = "";
        Date nowDate = new Date();
        List<CodeRuleItem> cris = this.listCodeRuleItem(item.getId());
        for (int i = 0; i < cris.size(); i++) {
            String subvalue = "";
            if (StringUtils.isNullOrEmpty(cris.get(i).getSuffixChar())) {
                cris.get(i).setSuffixChar("");
            }
            if (StringUtils.isNullOrEmpty(cris.get(i).getPrefixChar())) {
                cris.get(i).setPrefixChar("");
            }
            // 常量
            if ("0".equals(cris.get(i).getType())) {
                subvalue = cris.get(i).getPrefixChar() + cris.get(i).getConstant() + cris.get(i).getSuffixChar();
            }
            // 日期
            if ("1".equals(cris.get(i).getType())) {
                Date currentTime = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat(cris.get(i).getDateFormat());
                String dateString = formatter.format(currentTime);

                subvalue = cris.get(i).getPrefixChar() + dateString + cris.get(i).getSuffixChar();
            }
            //流水号
            if ("2".equals(cris.get(i).getType())) {
                //如果规则项 值重置的逻辑处理，根据年，月，日，最大值的变化来重置
                if (StringUtils.isNullOrEmpty(cris.get(i).getSnResetDependency())) {
                    throw new GlobalException("请填写流水号重置条件！", ResultCode.FAILED);
                }
                switch (cris.get(i).getSnResetDependency()) {
                    case "year":
                        //年份重置，当前年份在SnCurrentDate年份之后，重置流水号为默认流水号
                        if (nowDate.getYear() > cris.get(i).getSnCurrentDate().getYear()) {
                            cris.get(i).setSnCurrentValue(String.valueOf
                                    (Integer.parseInt(cris.get(i).getSnDefault()) - Integer.parseInt(cris.get(i).getSnStep())));
                            cris.get(i).setSnCurrentDate(nowDate);
                        }
                        break;
                    case "month":
                        //月份重置 当前月份在SnCurrentDate年份之后，重置流水号为默认流水号
                        if (nowDate.getYear() > cris.get(i).getSnCurrentDate().getYear() ||
                                nowDate.getMonth() > cris.get(i).getSnCurrentDate().getMonth()) {
                            cris.get(i).setSnCurrentValue(String.valueOf
                                    (Integer.parseInt(cris.get(i).getSnDefault()) - Integer.parseInt(cris.get(i).getSnStep())));
                            cris.get(i).setSnCurrentDate(nowDate);
                        }
                        break;
                    case "date":
                        //当前日期在SnCurrentDate年份之后，重置流水号为默认流水号
                        if (nowDate.getYear() > cris.get(i).getSnCurrentDate().getYear() ||
                                nowDate.getMonth() > cris.get(i).getSnCurrentDate().getMonth() ||
                                nowDate.getDay() > cris.get(i).getSnCurrentDate().getDay()) {
                            cris.get(i).setSnCurrentValue(String.valueOf
                                    (Integer.parseInt(cris.get(i).getSnDefault()) - Integer.parseInt(cris.get(i).getSnStep())));
                            cris.get(i).setSnCurrentDate(nowDate);
                        }
                        break;
                    case "input":
                        break;
                    default:
                        if (Integer.parseInt(cris.get(i).getSnCurrentValue()) >= Integer.parseInt(cris.get(i).getSnResetDependency())) {
                            cris.get(i).setSnCurrentValue(String.valueOf(Integer.parseInt(cris.get(i).getSnDefault()) - Integer.parseInt(cris.get(i).getSnStep())));
                        }
                }
                subvalue = cris.get(i).getPrefixChar() + (Integer.parseInt(cris.get(i).getSnCurrentValue()) + Integer.parseInt(cris.get(i).getSnStep())) + cris.get(i).getSuffixChar();
            }
            //用户输入项
            if ("3".equals(cris.get(i).getType())) {
                if ("0".equals(cris.get(i).getCheckType())) {

                    if (!java.util.regex.Pattern.matches("^[0-9]*$", inputs[index])) {
                        throw new NullPointerException("不是数字");
                    }
                } else if ("1".equals(cris.get(i).getCheckType())) {

                    if (!java.util.regex.Pattern.matches("[a-zA-Z]+", inputs[index])) {
                        throw new NullPointerException("不是字母");
                    }
                } else if ("2".equals(cris.get(i).getCheckType()) && !StringUtils.isNullOrEmpty(cris.get(i).getCheckRegex())) {

                    if (!java.util.regex.Pattern.matches(cris.get(i).getCheckRegex(), inputs[index])) {
                        throw new NullPointerException("不满足正则校验");
                    }
                }
                subvalue = cris.get(i).getPrefixChar() + inputs[index] + cris.get(i).getSuffixChar();
                index++;
            }
            //GUID
            if ("4".equals(cris.get(i).getType())) {
                subvalue = cris.get(i).getPrefixChar() + java.util.UUID.randomUUID() + cris.get(i).getSuffixChar();
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
        item.setCurValue(value);
        return item;
    }

    /**
     * 根据编码规则id获取编码样列（不区分启不启用）
     *
     * @param
     * @return
     */
    @Override
    public CodeRule getCodeByRuleId(String id) {
        CodeRule item = codeRuleService.getById(id);
        if(ObjectUtil.isEmpty(item)){
            return item;
        }
        item.setCurValue("");
        int index = 0;
        String value = "";
        Date nowDate = new Date();
        List<CodeRuleItem> cris = this.listCodeRuleItem(item.getId());
        for (int i = 0; i < cris.size(); i++) {
            String subvalue = "";
            if (StringUtils.isNullOrEmpty(cris.get(i).getSuffixChar())) {
                cris.get(i).setSuffixChar("");
            }
            if (StringUtils.isNullOrEmpty(cris.get(i).getPrefixChar())) {
                cris.get(i).setPrefixChar("");
            }
            // 常量
            if ("0".equals(cris.get(i).getType())) {
                subvalue = cris.get(i).getPrefixChar() + cris.get(i).getConstant() + cris.get(i).getSuffixChar();
            }
            // 日期
            if ("1".equals(cris.get(i).getType())) {
                Date currentTime = new Date();
                SimpleDateFormat formatter = new SimpleDateFormat(cris.get(i).getDateFormat());
                String dateString = formatter.format(currentTime);

                subvalue = cris.get(i).getPrefixChar() + dateString + cris.get(i).getSuffixChar();
            }
            //流水号
            if ("2".equals(cris.get(i).getType())) {
                //如果规则项 值重置的逻辑处理，根据年，月，日，最大值的变化来重置
                if (StringUtils.isNullOrEmpty(cris.get(i).getSnResetDependency())) {
                    throw new GlobalException("请填写流水号重置条件！", ResultCode.FAILED);
                }
                switch (cris.get(i).getSnResetDependency()) {
                    case "year":
                        //年份重置，当前年份在SnCurrentDate年份之后，重置流水号为默认流水号
                        if (nowDate.getYear() > cris.get(i).getSnCurrentDate().getYear()) {
                            cris.get(i).setSnCurrentValue(String.valueOf
                                    (Integer.parseInt(cris.get(i).getSnDefault()) - Integer.parseInt(cris.get(i).getSnStep())));
                            cris.get(i).setSnCurrentDate(nowDate);
                        }
                        break;
                    case "month":
                        //月份重置 当前月份在SnCurrentDate年份之后，重置流水号为默认流水号
                        if (nowDate.getYear() > cris.get(i).getSnCurrentDate().getYear() ||
                                nowDate.getMonth() > cris.get(i).getSnCurrentDate().getMonth()) {
                            cris.get(i).setSnCurrentValue(String.valueOf
                                    (Integer.parseInt(cris.get(i).getSnDefault()) - Integer.parseInt(cris.get(i).getSnStep())));
                            cris.get(i).setSnCurrentDate(nowDate);
                        }
                        break;
                    case "date":
                        //当前日期在SnCurrentDate年份之后，重置流水号为默认流水号
                        if (nowDate.getYear() > cris.get(i).getSnCurrentDate().getYear() ||
                                nowDate.getMonth() > cris.get(i).getSnCurrentDate().getMonth() ||
                                nowDate.getDay() > cris.get(i).getSnCurrentDate().getDay()) {
                            cris.get(i).setSnCurrentValue(String.valueOf
                                    (Integer.parseInt(cris.get(i).getSnDefault()) - Integer.parseInt(cris.get(i).getSnStep())));
                            cris.get(i).setSnCurrentDate(nowDate);
                        }
                        break;
                    case "input":
                        break;
                    default:
                        if (Integer.parseInt(cris.get(i).getSnCurrentValue()) >= Integer.parseInt(cris.get(i).getSnResetDependency())) {
                            cris.get(i).setSnCurrentValue(String.valueOf(Integer.parseInt(cris.get(i).getSnDefault()) - Integer.parseInt(cris.get(i).getSnStep())));
                        }
                }
                subvalue = cris.get(i).getPrefixChar() + (Integer.parseInt(cris.get(i).getSnCurrentValue()) + Integer.parseInt(cris.get(i).getSnStep())) + cris.get(i).getSuffixChar();
            }
            //GUID
            if ("4".equals(cris.get(i).getType())) {
                subvalue = cris.get(i).getPrefixChar() + java.util.UUID.randomUUID() + cris.get(i).getSuffixChar();
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
        item.setCurValue(value);
        return item;
    }


    /**
     * 更新编码，以便自增
     *
     * @param
     * @return
     */
    @Override
    public CodeRule updateCodeOld(String code, String name, String value, String input, String tenantId, String branchCode) {
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
        // 获取编码项列表
        List<CodeRule> items = codeRuleService.list(queryWrapper);
        CodeRule item = null;
        if (items.size() > 0) {
            //权限判断
            if(IS_ROLE_RULE.equals(items.get(0).getLevel())){
                //根据userId获取角色
                List<Role> roles = systemServiceClient.queryRolesByUserId(SecurityUtils.getCurrentUser().getUserId());
                for (CodeRule codeRule : items) {
                    List<Role> newRoles = new ArrayList<>(roles);
                    List<String> roleIdList = codeRule.getRoleIdList();
                    newRoles.retainAll(roleIdList);
                    if(newRoles.size()>0){
                        item = codeRule;
                    }
                }
                if(ObjectUtil.isEmpty(item)){
                    throw new GlobalException("找不到当前登录人编码规则或该编码规则已停用:"+items.get(0).getCode()+"的权限级编码",ResultCode.FAILED);
                }
            }else if(IS_COMPANY_RULE.equals(items.get(0).getLevel())){
                item = items.get(0);
            }else {
                List<CodeRule> siteRules = items.stream().filter(i -> i.getBranchCode().equals(branchCode)).collect(Collectors.toList());
                if(siteRules.size()>0){
                    item = siteRules.get(0);
                }
                if(ObjectUtil.isEmpty(item)){
                    throw new GlobalException("找不到当前登录人编码规则或该编码规则已停用:"+items.get(0).getCode()+"的车间级编码",ResultCode.FAILED);
                }
            }
            // 输入依赖项
            CodeRuleItem inputTtem = null;
            // 日期项
            CodeRuleItem dateRuleItem = null;
            //是否流水号按输入依赖项自增
            boolean enableInputTtem = false;
            //是否流水号按日项自增
            boolean enableDateRuleItem = false;
            CodeRuleItem snItem = new CodeRuleItem();
            // 获取编码项
            List<CodeRuleItem> list2 = codeRuleItemService.list(new QueryWrapper<CodeRuleItem>().eq("code_rule_id", item.getId()));
            for (int i = 0; i < list2.size(); i++) {
                if ("3".equals(list2.get(i).getType())) {
                    inputTtem = list2.get(i);
                }
                if ("1".equals(list2.get(i).getType())) {
                    dateRuleItem = list2.get(i);
                }

                //如果当前编码项是流水号，且重置依赖是空，则当前编码项的流水号自增
                if ("2".equals(list2.get(i).getType())) {
                    snItem = list2.get(i);
                    if (!StringUtils.isNullOrEmpty(list2.get(i).getSnCurrentValue())) {
                        //如果当前值不是空，当前编码项的流水号自增
                        list2.get(i).setSnCurrentValue(String.valueOf(Integer.parseInt(list2.get(i).getSnCurrentValue()) + Integer.parseInt(list2.get(i).getSnStep())));
                    } else {
                        //如果当前值是空，则读取重置值，写入
                        list2.get(i).setSnCurrentValue(String.valueOf(Integer.parseInt(list2.get(i).getSnDefault())));
                    }
                    codeRuleItemService.updateById(list2.get(i));
                } else {
                    if ("input".equals(list2.get(i).getSnResetDependency())) {
                        enableInputTtem = true;
                    }
                    if ("year".equals(list2.get(i).getSnResetDependency()) || "month".equals(list2.get(i).getSnResetDependency()) || "date".equals(list2.get(i).getSnResetDependency())) {
                        enableDateRuleItem = true;
                    }
                }
            }

            try {
                //如果当前编码项是流水号，且重置依赖不为空, 将值写入到Code_Rule_Value表中
                if (null != inputTtem && enableInputTtem) {
                    // 获取编码项依赖流水号列表
                    List<CodeRuleValue> list = codeRuleValueService.list(new QueryWrapper<CodeRuleValue>().eq("input_value", input).apply("item_id  in (select id from produce_code_rule_item where code_rule_id in (select id from produce_code_rule where code = '" + code + "' and branch_code='" + branchCode + "')) "));
                    if (list.size() == 0) {
                        //如果是空，则新产生一个编码项依赖流水号自增，如图号
                        CodeRuleValue codeRuleValue = new CodeRuleValue();
                        codeRuleValue.setItemId(inputTtem.getId());
                        codeRuleValue.setInputValue(input);
                        codeRuleValue.setBranchCode(inputTtem.getBranchCode());
                        codeRuleValue.setTenantId(inputTtem.getTenantId());
                        codeRuleValue.setSnValue(inputTtem.getSnDefault());
                        codeRuleValueService.save(codeRuleValue);
                    } else {
                        //如果不是空，则编码项依赖流水号自增，如图号
                        for (int i = 0; i < list.size(); i++) {
                            if (input.equals(list.get(i).getInputValue())) {
                                list.get(i).setSnValue(String.valueOf(Integer.parseInt(snItem.getSnCurrentValue()) + Integer.parseInt(snItem.getSnStep())));
                                codeRuleValueService.updateById(list.get(i));
                            }
                        }
                    }
                }
                // 如果流水号重置的依赖条件为日期，且日期输入项不为空
                if (null != dateRuleItem && enableDateRuleItem) {
                    List<CodeRuleValue> list = codeRuleValueService.list(new QueryWrapper<CodeRuleValue>().eq("input_value", input).apply("item_id  in (select id from produce_code_rule_item where code_rule_id in (select id from produce_code_rule where code = '" + code + "' and branch_code='" + branchCode + "')) "));
                    if (list.size() == 0) {
                        CodeRuleValue codeRuleValue = new CodeRuleValue();
                        codeRuleValue.setItemId(dateRuleItem.getId());
                        codeRuleValue.setInputValue(input);
                        codeRuleValue.setBranchCode(inputTtem.getBranchCode());
                        codeRuleValue.setTenantId(inputTtem.getTenantId());
                        codeRuleValue.setSnValue(dateRuleItem.getSnDefault());
                        codeRuleValueService.save(codeRuleValue);
                    } else {
                        for (int i = 0; i < list.size(); i++) {
                            if (input.equals(list.get(i).getInputValue())) {
                                list.get(i).setSnValue(String.valueOf(Integer.parseInt(snItem.getSnCurrentValue()) + Integer.parseInt(snItem.getSnStep())));
                                codeRuleValueService.updateById(list.get(i));
                            }
                        }
                    }
                }
            } catch (Exception ex) {

            }


            item.setCurValue(value);
            codeRuleService.updateById(item);
            return item;

        } else {
            return null;
        }
    }

    /**
     * 更新编码，以便自增
     *
     * @param
     * @return
     */
    @Override
    public CodeRule updateCode(String code, String name, String value, String input, String tenantId, String branchCode) {
        QueryWrapper<CodeRule> queryWrapper = new QueryWrapper<>();
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
        queryWrapper.orderByAsc("create_time");
        // 获取编码项列表
        List<CodeRule> items = codeRuleService.list(queryWrapper);
        CodeRule item = null;
        if (items.size() > 0) {
            //权限判断
            if(IS_ROLE_RULE.equals(items.get(0).getLevel())){
                //根据userId获取角色
                List<String> roles = systemServiceClient.queryRolesByUserId(SecurityUtils.getCurrentUser().getUserId()).stream().map(Role::getRoleCode).collect(Collectors.toList());
                for (CodeRule codeRule : items) {
                    List<String> newRoles = new ArrayList<>(roles);
                    List<String> roleIdList = codeRule.getRoleIdList();
                    newRoles.retainAll(roleIdList);
                    if(newRoles.size()>0){
                        item = codeRule;
                        break;
                    }
                }
                if(ObjectUtil.isEmpty(item)){
                    throw new GlobalException("找不到当前登录人编码规则或该编码规则已停用:"+items.get(0).getCode()+"的权限级编码",ResultCode.FAILED);
                }
            }else if(IS_COMPANY_RULE.equals(items.get(0).getLevel())){
                item = items.get(0);
            }else {
                List<CodeRule> siteRules = items.stream().filter(i -> i.getBranchCode().equals(branchCode)).collect(Collectors.toList());
                if(siteRules.size()>0){
                    item = siteRules.get(0);
                }
                if(ObjectUtil.isEmpty(item)){
                    throw new GlobalException("找不到当前登录人编码规则或该编码规则已停用:"+items.get(0).getCode()+"的车间级编码",ResultCode.FAILED);
                }
            }
            // 获取编码项
            List<CodeRuleItem> list2 = codeRuleItemService.list(new QueryWrapper<CodeRuleItem>().eq("code_rule_id", item.getId()));
            for (int i = 0; i < list2.size(); i++) {
                //如果当前编码项是流水号，且重置依赖是空，则当前编码项的流水号自增
                if ("2".equals(list2.get(i).getType())) {
                    if (!StringUtils.isNullOrEmpty(list2.get(i).getSnCurrentValue())) {
                        if ("year".equals(list2.get(i).getSnResetDependency())) {
                            if (new Date().getYear() > list2.get(i).getSnCurrentDate().getYear()) {
                                list2.get(i).setSnCurrentValue(String.valueOf(Integer.parseInt(list2.get(i).getSnDefault())));
                            } else {
                                list2.get(i).setSnCurrentValue(String.valueOf(Integer.parseInt(list2.get(i).getSnCurrentValue()) + Integer.parseInt(list2.get(i).getSnStep())));
                            }
                        } else if ("quarter".equals(list2.get(i).getSnResetDependency())) {
                        } else if ("month".equals(list2.get(i).getSnResetDependency())) {
                            if (new Date().getMonth() > list2.get(i).getSnCurrentDate().getMonth()) {
                                list2.get(i).setSnCurrentValue(String.valueOf(Integer.parseInt(list2.get(i).getSnDefault())));
                            } else {
                                list2.get(i).setSnCurrentValue(String.valueOf(Integer.parseInt(list2.get(i).getSnCurrentValue()) + Integer.parseInt(list2.get(i).getSnStep())));
                            }
                        } else if ("date".equals(list2.get(i).getSnResetDependency())) {
                            if (new Date().getDay() > list2.get(i).getSnCurrentDate().getDay()) {
                                list2.get(i).setSnCurrentValue(String.valueOf(Integer.parseInt(list2.get(i).getSnDefault())));
                            } else {
                                list2.get(i).setSnCurrentValue(String.valueOf(Integer.parseInt(list2.get(i).getSnCurrentValue()) + Integer.parseInt(list2.get(i).getSnStep())));
                            }
                        } else if ("input".equals(list2.get(i).getSnResetDependency())) {

                        } else {
                            list2.get(i).setSnCurrentValue(String.valueOf(Integer.parseInt(list2.get(i).getSnCurrentValue()) + Integer.parseInt(list2.get(i).getSnStep())));
                        }
                    } else {
                        //如果当前值是空，则读取重置值，写入
                        list2.get(i).setSnCurrentValue(String.valueOf(Integer.parseInt(list2.get(i).getSnDefault()) + Integer.parseInt(list2.get(i).getSnStep())));
                    }
                    list2.get(i).setSnCurrentDate(new Date());
                    codeRuleItemService.updateById(list2.get(i));
                }
            }
            item.setCurValue(value);
            codeRuleService.updateById(item);
            return item;
        } else {
            return null;
        }
    }

    @Override
    public List<CodeRuleItem> listCodeRuleItem(String codeRuleId) {
        QueryWrapper<CodeRuleItem> queryWrapper = new QueryWrapper<CodeRuleItem>();
        if (!StringUtils.isNullOrEmpty(codeRuleId)) {
            queryWrapper.eq("code_rule_id", codeRuleId);
        }
        queryWrapper.orderByAsc("order_no");
        return codeRuleItemService.list(queryWrapper);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public Boolean editRuleValue(@RequestBody JSONObject jsonObject) throws GlobalException {
        //修改项
        List<CodeRuleItem> ruleItemList = JSON.parseArray(JSONObject.toJSONString(jsonObject.get("ruleItemList")), CodeRuleItem.class);
        //校验
        for (CodeRuleItem codeRuleItem : ruleItemList) {
            if("2".equals(codeRuleItem.getType())){
                if(StringUtils.isNullOrEmpty(codeRuleItem.getSnDefault()) && StringUtils.isNullOrEmpty(codeRuleItem.getSnCurrentValue())){
                    throw new GlobalException("流水号初始值大小和当前值不能同时为空！",ResultCode.FAILED);
                }
                if(StringUtils.isNullOrEmpty(codeRuleItem.getSnStep())){
                    throw new GlobalException("流水号步长不能为空！",ResultCode.FAILED);
                }
            }
        }
        //编码规则id
        String codeRuleId = jsonObject.getString("codeRuleId");

        if (StringUtils.isNullOrEmpty(codeRuleId)) {
            throw new GlobalException(RULE_ID_NULL_MESSAGE,ResultCode.FAILED);
        }
        //先把之前的删除了
        QueryWrapper<CodeRuleItem> deleteWrapper = new QueryWrapper<>();
        deleteWrapper.eq("code_rule_id",codeRuleId);
        boolean remove = codeRuleItemService.remove(deleteWrapper);
        //保存当前的编码项
        for (CodeRuleItem codeRuleItem : ruleItemList) {
            codeRuleItem.setId(null);
            codeRuleItem.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        }
        boolean save = codeRuleItemService.saveBatch(ruleItemList);
        return remove&&save;
    }

}
