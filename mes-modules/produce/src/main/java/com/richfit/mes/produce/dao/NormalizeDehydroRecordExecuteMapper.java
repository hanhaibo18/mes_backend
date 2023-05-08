package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.NormalizeDehydroExecuteRecord;
import com.richfit.mes.common.model.produce.NormalizeDehydroRecord;
import org.apache.ibatis.annotations.Mapper;

/**
 * 正火去氢工艺执行记录表(NormalizeDehydroRecordExecuteMapper)表数据库访问层
 *
 * @author hujia
 * @since 2023-05-08 14:18:24
 */
@Mapper
public interface NormalizeDehydroRecordExecuteMapper extends BaseMapper<NormalizeDehydroExecuteRecord> {
}

