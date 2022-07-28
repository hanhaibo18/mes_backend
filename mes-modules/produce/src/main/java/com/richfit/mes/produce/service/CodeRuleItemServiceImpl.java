package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.IErrorCode;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.model.produce.CodeRule;
import com.richfit.mes.common.model.produce.CodeRuleItem;
import com.richfit.mes.common.model.produce.CodeRuleValue;
import com.richfit.mes.produce.dao.CodeRuleItemMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

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

    
}
