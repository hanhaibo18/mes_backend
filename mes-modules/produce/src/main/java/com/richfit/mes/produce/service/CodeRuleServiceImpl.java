package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.CodeRule;
import com.richfit.mes.common.model.produce.CodeRuleItem;
import com.richfit.mes.common.model.produce.CodeRuleValue;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.CodeRuleMapper;
import java.util.List;
import com.mysql.cj.util.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}
