package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.base.PdmTask;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author renzewen
 * @Description 图纸同步队列
 */
@Mapper
public interface PdmTaskMapper extends BaseMapper<PdmTask> {
}
