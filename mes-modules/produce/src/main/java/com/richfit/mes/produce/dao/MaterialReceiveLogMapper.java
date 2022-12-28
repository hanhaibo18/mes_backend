package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.MaterialReceiveLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Description 用于存储物料接收日志记录
 * @Author zhiqiang.lu
 * @Date 2022/12/28 09:25
 */
@Mapper
public interface MaterialReceiveLogMapper extends BaseMapper<MaterialReceiveLog> {
}
