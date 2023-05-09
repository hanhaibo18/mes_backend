package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.KnockoutCache;
import org.apache.ibatis.annotations.Mapper;

/**
 * 扣箱工序报工缓存记录表(KnockoutCache)表数据库访问层
 *
 * @author makejava
 * @since 2023-05-08 10:19:22
 */
@Mapper
public interface KnockoutCacheMapper extends BaseMapper<KnockoutCache> {

}

