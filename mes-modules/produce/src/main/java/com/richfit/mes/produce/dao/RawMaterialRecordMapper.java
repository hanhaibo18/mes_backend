package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.RawMaterialRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 原材料消耗记录表(RawMaterialRecord)表数据库访问层
 *
 * @author makejava
 * @since 2023-04-27 14:16:36
 */
@Mapper
public interface RawMaterialRecordMapper extends BaseMapper<RawMaterialRecord> {

    @Select("select * from produce_raw_material_record_cache where item_id = '${itemId}'")
    List<RawMaterialRecord> queryrawMaterialRecordCacheByItemId(@Param("itemId") String tiId);
}

