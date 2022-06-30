package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.richfit.mes.common.model.produce.LineStore;
import com.richfit.mes.common.model.produce.store.LineStoreSum;
import com.richfit.mes.common.model.produce.store.LineStoreSumZp;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @author 王瑞
 * @Description 线边库Mapper
 */
@Mapper
public interface LineStoreMapper extends BaseMapper<LineStore> {

    IPage<LineStoreSum> selectGroup(IPage<LineStore> page, @Param(Constants.WRAPPER) Wrapper<LineStore> query);

    IPage<LineStore> selectLineStoreByProduce(IPage<LineStore> page, @Param(Constants.WRAPPER) Wrapper<LineStore> query);

    List<LineStoreSumZp> selectStoreNumForAssembly(@Param("param") Map parMap);

    List<LineStoreSumZp> selectDeliveryNumber(@Param("param") Map parMap);

    List<LineStoreSumZp> selectRequireNum(@Param("param") Map parMap);

    List<LineStoreSumZp> selectAssemblyNum(@Param("param") Map parMap);
}
