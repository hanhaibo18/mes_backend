package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.base.OperationDevice;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 王瑞
 * @Description 工艺设备关联Mapper
 */
@Mapper
public interface OperationDeviceMapper extends BaseMapper<OperationDevice> {
}
