package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.CodeRuleItem;
import com.richfit.mes.produce.dao.CodeRuleItemMapper;
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
public class CodeRuleItemServiceImpl extends ServiceImpl<CodeRuleItemMapper, CodeRuleItem> implements CodeRuleItemService {

    @Autowired
    CodeRuleItemMapper codeRuleItemMapper;
}
