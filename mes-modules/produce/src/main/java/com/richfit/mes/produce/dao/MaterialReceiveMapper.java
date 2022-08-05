package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.MaterialReceive;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.sql.Date;

/**
 * @className:MaterialReceiveMapper
 * @description: 类描述
 * @author:ang
 * @date:2022/7/29 17:56
 */
@Mapper
public interface MaterialReceiveMapper extends BaseMapper<MaterialReceive> {

    @Select("SELECT CREATE_TIME FROM produce_material_receive ORDER BY CREATE_TIME DESC LIMIT 1")
    Date getlastTime();

    @Select("SELECT mr.*,prn.track_head_id FROM produce_material_receive mr LEFT JOIN produce_request_note prn ON mr.aply_num = prn.request_note_number  ${ew.customSqlSegment}")
    Page<MaterialReceive> getPage(@Param(Constants.WRAPPER) Page<MaterialReceive> materialReceivePage, QueryWrapper<MaterialReceive> queryWrapper);
}
