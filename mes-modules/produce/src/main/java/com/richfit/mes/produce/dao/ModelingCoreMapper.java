package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.ModelingCore;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * 造型/制芯工序报工记录表(ModelingCore)表数据库访问层
 *
 * @author makejava
 * @since 2023-05-08 10:17:16
 */
@Mapper
public interface ModelingCoreMapper extends BaseMapper<ModelingCore> {

    @Select("select * from produce_modeling_core_cache where item_id = '${itemId}'")
    ModelingCore queryCacheByItemId(@Param("itemId") String tiId);
}

