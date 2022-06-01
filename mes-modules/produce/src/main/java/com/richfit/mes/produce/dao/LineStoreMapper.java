package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.richfit.mes.common.model.produce.LineStore;
import com.richfit.mes.common.model.produce.store.LineStoreSum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * @author 王瑞
 * @Description 线边库Mapper
 */
@Mapper
public interface LineStoreMapper extends BaseMapper<LineStore> {

    IPage<LineStoreSum> selectGroup(IPage<LineStore> page, @Param(Constants.WRAPPER) Wrapper<LineStore> query);

    IPage<LineStore> selectLineStoreByProduce(IPage<LineStore> page, @Param(Constants.WRAPPER) Wrapper<LineStore> query);
}
