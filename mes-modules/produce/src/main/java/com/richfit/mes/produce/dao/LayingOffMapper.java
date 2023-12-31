package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.LayingOff;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 下料表(ProduceLayingOff)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-23 14:25:09
 */
@Mapper
public interface LayingOffMapper extends BaseMapper<LayingOff> {

    @Select("select * from produce_laying_off_cache where item_id = '${itemId}' limit 1 ")
    LayingOff queryLayingOffCacheByItemId(@Param("itemId") String tiId);
}

