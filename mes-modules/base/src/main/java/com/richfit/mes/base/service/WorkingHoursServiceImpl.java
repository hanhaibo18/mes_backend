package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.WorkingHoursMapper;
import com.richfit.mes.common.model.base.WorkingHours;

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
    public List<WorkingHours> selectOrderTime() {
       return workingHoursMapper.selectOrderTime();
    }
}
