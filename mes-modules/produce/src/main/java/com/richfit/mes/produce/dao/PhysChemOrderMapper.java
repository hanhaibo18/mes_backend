package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.PhysChemOrder;
import com.richfit.mes.produce.entity.phyChemTestVo.PhyChemTaskVo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author renzewen
 */
@Mapper
public interface PhysChemOrderMapper extends BaseMapper<PhysChemOrder> {

    /**
     * 分页查询
     * @param page
     * @param param
     * @return
     */
    IPage<PhysChemOrder> queryTestPageList(@Param("page") Page page, PhyChemTaskVo param,@Param("orderTableName") String orderTableName);
}
