package com.richfit.mes.produce.utils;

import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.CodeRule;
import com.richfit.mes.produce.service.CodeRuleService;


/**
 * 编码取值与更新通用工具类
 *
 * @author zhiqiang.lu
 * @date 2022.9.20
 */
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
    public static String value(String code, String tenantId, String branchCode, CodeRuleService codeRuleService) throws Exception {
        CodeRule codeRule = codeRuleService.gerCode(code, null, null, tenantId, branchCode);
        if (codeRule == null || StringUtils.isNullOrEmpty(codeRule.getCurValue())) {
            throw new GlobalException("获取跟单号出现异常", ResultCode.FAILED);
        }
        return codeRule.getCurValue().replace("BRANCHCODE", branchCode);
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
    public static void update(String code, String value, String tenantId, String branchCode, CodeRuleService codeRuleService) throws Exception {
        codeRuleService.updateCode(code, null, value, null, tenantId, branchCode);
    }

    /**
     * 功能描述: 实时获取code，并更新流水码
     *
     * @param code            编号
     * @param tenantId        租户id
     * @param branchCode      工厂代码
     * @param codeRuleService 查询方法类
     * @Author: zhiqiang.lu
     * @Date: 2022.9.20
     */
    public static String valueOnUpdate(String code, String tenantId, String branchCode, CodeRuleService codeRuleService) throws Exception {
        String value = value(code, tenantId, branchCode, codeRuleService);
        update(code, value, tenantId, branchCode, codeRuleService);
        return value;
    }
}
