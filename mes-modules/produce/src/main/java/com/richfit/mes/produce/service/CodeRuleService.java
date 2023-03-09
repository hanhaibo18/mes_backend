package com.richfit.mes.produce.service;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.CodeRule;
import com.richfit.mes.common.model.produce.CodeRuleItem;
import org.springframework.web.bind.annotation.RequestBody;

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
    public CodeRule updateCodeOld(String code, String name, String value, String input, String tenantId, String branchCode);


    /**
     * 重写编码更新功能（主要重写流水号根据日期重置功能）
     *
     * @author zhiqiang.lu
     * @date 2022.10.14
     */
    public CodeRule updateCode(String code, String name, String value, String input, String tenantId, String branchCode);

    /**
     * 更新编码，以便自增
     *
     * @param
     * @return
     */
    public CodeRule gerCode(String code, String name, String[] inputs, String tenantId, String branchCode);

    /**
     * 根据编码规则id获取编码
     * @param id
     * @return
     */
    public CodeRule getCodeByRuleId(String id);


    public List<CodeRuleItem> listCodeRuleItem(String codeRuleId);


    Boolean editRuleValue(@RequestBody JSONObject jsonObject) throws GlobalException;

}
