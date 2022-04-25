package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.hourSum.WorkingHours;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface WorkingHoursMapper extends BaseMapper<WorkingHours> {

    @Select("select material_no,product_name from base_product ${ew.customSqlSegment}")
    List<Product> selectOrderTime(@Param(Constants.WRAPPER) QueryWrapper<List> wrapper);

}
