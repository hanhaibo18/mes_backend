package com.richfit.mes.produce.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.produce.PrechargeFurnaceAssignPerson;
import org.apache.ibatis.annotations.Mapper;

/**
 * 预装炉派工表(PrechargeFurnaceAssign)表数据库访问层
 *
 * @author makejava
 * @since 2023-05-16 10:01:16
 */
@Mapper
public interface PrechargeFurnaceAssignPersonMapper extends BaseMapper<PrechargeFurnaceAssignPerson> {

}

