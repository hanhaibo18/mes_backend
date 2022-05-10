package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.richfit.mes.common.model.base.Device;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * @author 马峰
 * @Description 工艺Mapper
 */
@Mapper
public interface DeviceMapper extends BaseMapper<Device> {
    @Select("delete from base_device ${ew.customSqlSegment}")
    Boolean delete(String id, @Param(Constants.WRAPPER) QueryWrapper<Device> queryWrapper);
}
