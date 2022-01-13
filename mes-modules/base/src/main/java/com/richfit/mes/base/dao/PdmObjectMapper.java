package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.base.PdmObject;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author renzewen
 * @Description 图纸审核管理
 */
@Mapper
public interface PdmObjectMapper extends BaseMapper<PdmObject> {
}
