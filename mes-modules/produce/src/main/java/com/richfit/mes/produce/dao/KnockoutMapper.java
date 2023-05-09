package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.Knockout;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 扣箱工序报工记录表(Knockout)表数据库访问层
 *
 * @author makejava
 * @since 2023-05-08 10:18:48
 */
@Mapper
public interface KnockoutMapper extends BaseMapper<Knockout> {

    @Select("select * from produce_knockout_cache where item_id = '${itemId}'")
    Knockout queryCacheByItemId(@Param("itemId") String tiId);
}

