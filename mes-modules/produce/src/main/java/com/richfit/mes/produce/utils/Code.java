package com.richfit.mes.produce.utils;

import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.CodeRule;
import com.richfit.mes.produce.service.CodeRuleService;

public class Code {

    /**
     * 功能描述: 获取code
     *
     * @param code            编号
     * @param tenantId        租户id
     * @param branchCode      工厂代码
     * @param codeRuleService 查询方法类
     * @Author: zhiqiang.lu
     * @Date: 2022.9.20
     */
    public static String code(String code, String tenantId, String branchCode, CodeRuleService codeRuleService) throws Exception {
        CodeRule codeRule = codeRuleService.gerCode(code, null, null, tenantId, branchCode);
        if (codeRule == null || StringUtils.isNullOrEmpty(codeRule.getCurValue())) {
            throw new GlobalException("获取跟单号出现异常", ResultCode.FAILED);
        }
        return codeRule.getCurValue();
    }

    /**
     * 功能描述: 更新code
     *
     * @param code            编号
     * @param value           当前值
     * @param tenantId        租户id
     * @param branchCode      工厂代码
     * @param codeRuleService 查询方法类
     * @Author: zhiqiang.lu
     * @Date: 2022.9.20
     */
    public static void codeUpdate(String code, String value, String tenantId, String branchCode, CodeRuleService codeRuleService) throws Exception {
        codeRuleService.updateCode(code, null, value, null, tenantId, branchCode);
    }
}
