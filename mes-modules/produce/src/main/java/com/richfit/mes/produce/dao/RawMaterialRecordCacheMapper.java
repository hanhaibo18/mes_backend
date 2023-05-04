package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.RawMaterialRecordCache;
import org.apache.ibatis.annotations.Mapper;

/**
 * 原材料消耗记录缓存表(RawMaterialRecordCache)表数据库访问层
 *
 * @author makejava
 * @since 2023-04-27 14:17:21
 */
@Mapper
public interface RawMaterialRecordCacheMapper extends BaseMapper<RawMaterialRecordCache> {

}

