package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.Certificate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author 王瑞
 * @Description 物料 Mapper
 */
@Mapper
public interface ProductMapper extends BaseMapper<Product> {
    IPage<Product> selectProduct(IPage<Product> page, @Param(Constants.WRAPPER) Wrapper<Product> query);
}
