package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.base.Sequence;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 马峰
 * @Description 工艺Mapper
 */
@Mapper
public interface SequenceMapper extends BaseMapper<Sequence> {
}
