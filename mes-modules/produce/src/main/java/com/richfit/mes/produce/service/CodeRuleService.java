package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.CodeRule;
import com.richfit.mes.common.model.produce.CodeRuleItem;

import java.util.List;

/**
 * <p>
 * 编码规则 服务类
 * </p>
 *
 * @author 马峰
 * @since 2020-09-07
 */
public interface CodeRuleService extends IService<CodeRule> {

    public CodeRule submit(String code, String input, String value);

    /**
     * 更新编码，以便自增
     *
     * @param
     * @return
     */
    public CodeRule updateCode(String code, String name, String value, String input, String tenantId, String branchCode);

    /**
     * 更新编码，以便自增
     *
     * @param
     * @return
     */
    public CodeRule gerCode(String code, String name, String[] inputs, String tenantId, String branchCode);


    public List<CodeRuleItem> listCodeRuleItem(String codeRuleId, String code, String type, String tenantId, String branchCode);


}
