package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.DevicePersonMapper;
import com.richfit.mes.common.model.base.Device;
import com.richfit.mes.common.model.base.DevicePerson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * @author mafeng
 * @Description 工艺服务
 */
@Service
public class DevicePersonServiceImpl extends ServiceImpl<DevicePersonMapper, DevicePerson> implements DevicePersonService {

    @Autowired
    private DevicePersonMapper devicePersonMapper;
    @Resource
    private DeviceService deviceService;

    public IPage<DevicePerson> selectPage(Page page, QueryWrapper<DevicePerson> qw) {
        return devicePersonMapper.selectPage(page, qw);
    }

    @Override
    public List<Device> queryDeviceByUserId(String userId) {
        QueryWrapper<DevicePerson> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<DevicePerson> devicePeople = this.list(queryWrapper);
        if (devicePeople != null) {
            List<Device> deviceList = new ArrayList<>();
            for (DevicePerson devicePerson : devicePeople) {
                deviceList.add(deviceService.getById(devicePerson.getDeviceId()));
            }
            return deviceList;
        }
        return Collections.emptyList();
    }

}
