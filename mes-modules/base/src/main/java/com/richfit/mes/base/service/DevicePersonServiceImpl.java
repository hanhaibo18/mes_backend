package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.base.DevicePerson;
import com.richfit.mes.base.dao.DevicePersonMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author mafeng
 * @Description 工艺服务
 */
@Service
public class DevicePersonServiceImpl extends ServiceImpl<DevicePersonMapper, DevicePerson> implements DevicePersonService{

    @Autowired
    private DevicePersonMapper devicePersonMapper;

    public IPage<DevicePerson> selectPage(Page page, QueryWrapper<DevicePerson> qw)
    {
        return  devicePersonMapper.selectPage(page, qw);
    }
    
}
