package com.richfit.mes.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.sys.DataDictionaryParam;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据字典参数表(SysDataDictionaryParam)表数据库访问层
 *
 * @author makejava
 * @since 2023-04-03 15:19:28
 */
@Mapper
public interface DataDictionaryParamDao extends BaseMapper<DataDictionaryParam> {

}

