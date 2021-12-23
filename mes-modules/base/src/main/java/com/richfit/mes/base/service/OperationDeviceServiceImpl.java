package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.DeviceMapper;
import com.richfit.mes.base.dao.OperationDeviceMapper;
import com.richfit.mes.common.model.base.Device;
import com.richfit.mes.common.model.base.OperationDevice;
import org.springframework.stereotype.Service;

/**
 * @author 王瑞
 * @Description 工艺设备关联服务
 */
@Service
public class OperationDeviceServiceImpl extends ServiceImpl<OperationDeviceMapper, OperationDevice> implements OperationDeviceService {

}
