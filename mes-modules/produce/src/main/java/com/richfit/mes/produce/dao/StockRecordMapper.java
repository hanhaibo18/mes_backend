package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.StockRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 王瑞
 * @Description 出入库表Mapper
 */
@Mapper
public interface StockRecordMapper extends BaseMapper<StockRecord> {

}
