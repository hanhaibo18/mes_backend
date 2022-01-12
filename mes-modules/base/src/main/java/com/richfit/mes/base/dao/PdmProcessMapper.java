package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.base.PdmProcess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author renzewen
 * @Description 图纸审核管理
 */
@Mapper
public interface PdmProcessMapper extends BaseMapper<PdmProcess> {

    IPage<PdmProcess> queryPageList(Page<PdmProcess> page, @Param("pageVO") PdmProcess pdmProcess);

    List<PdmProcess> queryList(@Param("pageVO") PdmProcess pdmProcess);
}
