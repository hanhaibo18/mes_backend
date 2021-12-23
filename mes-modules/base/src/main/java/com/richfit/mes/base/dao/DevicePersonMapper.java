package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.base.DevicePerson;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 马峰
 * @Description 设备人员Mapper
 */
@Mapper
public interface DevicePersonMapper extends BaseMapper<DevicePerson> {
}
