package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.entity.ModelApplyItem;
import org.apache.ibatis.annotations.Mapper;

/**
 * 工序 模型请求表(ModelApplyItem)表数据库访问层
 *
 * @author makejava
 * @since 2023-06-14 16:04:24
 */
@Mapper
public interface ModelApplyItemMapper extends BaseMapper<ModelApplyItem> {

}

