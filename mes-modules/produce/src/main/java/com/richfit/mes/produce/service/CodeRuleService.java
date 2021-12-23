package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.CodeRule;

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
   

}
