package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.DeviceMapper;
import com.richfit.mes.common.model.base.Device;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author mafeng
 * @Description 工艺服务
 */
@Service
public class DeviceServiceImpl extends ServiceImpl<DeviceMapper, Device> implements DeviceService {

    @Autowired
    private DeviceMapper deviceMapper;

    public IPage<Device> selectPage(Page page, QueryWrapper<Device> qw) {
        return deviceMapper.selectPage(page, qw);
    }


    @Override
    public Boolean delete(String id, QueryWrapper<Device> queryWrapper) {
        return deviceMapper.delete(id, queryWrapper);
    }


}
