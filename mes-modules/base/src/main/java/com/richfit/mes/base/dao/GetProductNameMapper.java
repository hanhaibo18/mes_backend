package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.richfit.mes.common.model.base.Product;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface GetProductNameMapper extends BaseMapper<Product> {


    /**
     * 获取产品名称
     **/

    @Select("select material_no,product_name from base_product ${ew.customSqlSegment}")
    List<Product> queryProductName(@Param(Constants.WRAPPER) QueryWrapper<List> wrapper);

}
