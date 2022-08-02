package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.MaterialReceive;
import org.apache.ibatis.annotations.Mapper;
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

    @Select("SELECT CREATE_TIME FROM v_mes_out_headers ORDER BY CREATE_TIME DESC LIMIT 1")
    Date getlastTime();
}
