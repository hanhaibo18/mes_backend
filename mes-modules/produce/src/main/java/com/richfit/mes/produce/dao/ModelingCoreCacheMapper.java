package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.ModelingCoreCache;
import org.apache.ibatis.annotations.Mapper;

/**
 * 造型/制芯工序报工缓存记录表(ModelingCoreCache)表数据库访问层
 *
 * @author makejava
 * @since 2023-05-08 10:18:07
 */
@Mapper
public interface ModelingCoreCacheMapper extends BaseMapper<ModelingCoreCache> {

}

