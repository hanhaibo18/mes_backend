package com.richfit.mes.sys.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.sys.DataDictionary;
import org.apache.ibatis.annotations.Mapper;

/**
 * 数据字典表(SysDataDictionary)表数据库访问层
 *
 * @author makejava
 * @since 2023-04-03 15:18:29
 */
@Mapper
public interface DataDictionaryDao extends BaseMapper<DataDictionary> {

}

