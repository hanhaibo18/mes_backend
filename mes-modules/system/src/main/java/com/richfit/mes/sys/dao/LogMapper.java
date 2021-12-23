package com.richfit.mes.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.sys.SystemLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author sun
 * @Description Log Mapper
 */
@Mapper
public interface LogMapper  extends BaseMapper<SystemLog> {
}
