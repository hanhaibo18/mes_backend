package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.RawMaterial;
import org.apache.ibatis.annotations.Mapper;

/**
 * 原材料表(RawMaterial)表数据库访问层
 *
 * @author makejava
 * @since 2023-04-27 14:18:09
 */
@Mapper
public interface RawMaterialMapper extends BaseMapper<RawMaterial> {

}

