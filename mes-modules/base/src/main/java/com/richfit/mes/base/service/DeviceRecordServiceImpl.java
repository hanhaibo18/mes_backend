package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.base.DeviceRecord;
import com.richfit.mes.base.dao.DeviceRecordMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author mafeng
 * @Description 工艺服务
 */
@Service
public class DeviceRecordServiceImpl extends ServiceImpl<DeviceRecordMapper, DeviceRecord> implements DeviceRecordService{

    @Autowired
    private DeviceRecordMapper deviceRecordMapper;

    public IPage<DeviceRecord> selectPage(Page page, QueryWrapper<DeviceRecord> qw)
    {
        return  deviceRecordMapper.selectPage(page, qw);
    }
    
}
