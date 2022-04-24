package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.WorkingHoursMapper;
import com.richfit.mes.common.model.base.WorkingHours;

import com.richfit.mes.common.model.produce.OrderTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gwb
 */
@Slf4j
@Service
public class WorkingHoursServiceImpl extends ServiceImpl<WorkingHoursMapper, WorkingHours> implements WorkingHoursService {
    @Autowired
    private WorkingHoursMapper workingHoursMapper;


    @Override
    public List<WorkingHours> selectOrderTime(QueryWrapper<List> wrapper) {
        return workingHoursMapper.selectOrderTime(wrapper);
    }
}
