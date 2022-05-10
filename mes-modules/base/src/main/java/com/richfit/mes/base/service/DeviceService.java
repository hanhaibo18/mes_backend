package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.Device;

/**
 * @author 马峰
 * @Description 工艺服务
 */
public interface DeviceService extends IService<Device> {

    public IPage<Device> selectPage(Page page, QueryWrapper<Device> qw);


    Boolean delete(String id, QueryWrapper<Device> queryWrapper);
}
