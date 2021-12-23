package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.DeviceRecord;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author 马峰
 * @Description 工艺服务
 */
public interface DeviceRecordService extends IService<DeviceRecord> {
    
    public IPage<DeviceRecord> selectPage(Page page, QueryWrapper<DeviceRecord> qw);
}
