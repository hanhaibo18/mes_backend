package com.richfit.mes.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.CodeRuleItem;
import com.richfit.mes.common.model.sys.CodeRuleItemTemp;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author mafeng
 * @Description 编码规则项Mapper
 */
@Mapper
public interface CodeRuleItemTempMapper extends BaseMapper<CodeRuleItemTemp> {
}
