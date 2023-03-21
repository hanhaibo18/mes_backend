package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.CodeRuleItem;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 编码规则 服务类
 * </p>
 *
 * @author 马峰
 * @since 2020-09-07
 */
public interface CodeRuleItemService extends IService<CodeRuleItem> {


    CommonResult<String> importCodeRuleByExcel(MultipartFile file);
}