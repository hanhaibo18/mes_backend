package com.richfit.mes.produce.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.ForgControlRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 锻造工序控制记录表(ProduceForgControlRecord)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-23 14:07:26
 */
@Mapper
public interface ForgControlRecordMapper extends BaseMapper<ForgControlRecord> {

    @Select("select * from produce_forg_control_record_cache where item_id = '${itemId}'")
    List<ForgControlRecord> queryForgControlRecordCacheByItemId(@Param("itemId") String tiId);
}

