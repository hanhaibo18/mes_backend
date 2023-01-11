package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.Device;
import com.richfit.mes.common.model.base.DevicePerson;

import java.util.List;

/**
 * @author 马峰
 * @Description 工艺服务
 */
public interface DevicePersonService extends IService<DevicePerson> {

    public IPage<DevicePerson> selectPage(Page page, QueryWrapper<DevicePerson> qw);

    /**
     * 功能描述: 根据用户ID查询设备
     *
     * @param userId
     * @Author: xinYu.hou
     * @Date: 2022/6/20 16:51
     * @return: List<Device>
     **/
    List<Device> queryDeviceByUserId(String userId,String branchCode);
}
