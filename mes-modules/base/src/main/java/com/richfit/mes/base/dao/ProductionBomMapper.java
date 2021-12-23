package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.richfit.mes.common.model.base.ProductionBom;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author 王瑞
 * @Description 产品BOM Mapper
 */
@Mapper
public interface ProductionBomMapper extends BaseMapper<ProductionBom> {

    IPage<ProductionBom> getProductionBomByPage(IPage<ProductionBom> page, @Param(Constants.WRAPPER) Wrapper<ProductionBom> query);

    IPage<ProductionBom> getProductionBomHistory(IPage<ProductionBom> page, @Param(Constants.WRAPPER) Wrapper<ProductionBom> query);

    List<ProductionBom> getProductionBomList(@Param(Constants.WRAPPER) Wrapper<ProductionBom> query);

}







