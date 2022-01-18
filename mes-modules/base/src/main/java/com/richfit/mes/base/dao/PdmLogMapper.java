package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.base.PdmLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author renzewen
 * @Description pdm同步日志
 */
@Mapper
public interface PdmLogMapper extends BaseMapper<PdmLog> {
}
