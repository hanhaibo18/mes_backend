package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.base.PdmMesProcess;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author zhiqiang.lu
 * @date 2022-06-29 13:27
 */
@Mapper
public interface PdmMesProcessMapper extends BaseMapper<PdmMesProcess> {

    IPage<PdmMesProcess> queryPageList(Page<PdmMesProcess> page, @Param("pageVO") PdmMesProcess pdmProcess);

    List<PdmMesProcess> queryList(@Param("pageVO") PdmMesProcess pdmProcess);
}
