package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.Assign;
import com.richfit.mes.common.model.produce.TrackComplete;
import com.richfit.mes.common.model.produce.TrackItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author 马峰
 * @Description 报工Mapper
 */
@Mapper
public interface TrackCompleteMapper extends BaseMapper<TrackComplete> {

   @Select("select * from v_produce_complete a ${ew.customSqlSegment}")
   IPage<TrackComplete> queryPage(Page page, @Param(Constants.WRAPPER) Wrapper<TrackComplete> wrapper);

}
