package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.base.PdmLog;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author renzewen
 * @Description pdm同步日志
 */
@Mapper
public interface PdmLogMapper extends BaseMapper<PdmLog> {

    IPage<PdmLog> queryPageList(Page page, @Param("type") String type,
                                @Param("par") String par,
                                @Param("queryTimeStart") String queryTimeStart,
                                @Param("queryTimeEnd") String queryTimeEnd);
}
