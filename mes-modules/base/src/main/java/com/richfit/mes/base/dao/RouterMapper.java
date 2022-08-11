package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.base.Router;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author 马峰
 * @Description 工艺Mapper
 */
@Mapper
public interface RouterMapper extends BaseMapper<Router> {

    List<Router> selectRouter(@Param(Constants.WRAPPER) Wrapper<Router> query);

}
