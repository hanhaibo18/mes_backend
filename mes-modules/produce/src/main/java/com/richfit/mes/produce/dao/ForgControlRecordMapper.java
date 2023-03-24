package com.richfit.mes.produce.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.ForgControlRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 锻造工序控制记录表(ProduceForgControlRecord)表数据库访问层
 *
 * @author makejava
 * @since 2023-03-23 14:07:26
 */
@Mapper
public interface ForgControlRecordMapper extends BaseMapper<ForgControlRecord> {

}

