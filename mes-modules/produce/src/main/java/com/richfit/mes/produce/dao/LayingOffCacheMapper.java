package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.LayingOffCache;
import org.apache.ibatis.annotations.Mapper;

/**
 * 下料表(ProduceLayingOff)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-23 14:25:09
 */
@Mapper
public interface LayingOffCacheMapper extends BaseMapper<LayingOffCache> {

}

