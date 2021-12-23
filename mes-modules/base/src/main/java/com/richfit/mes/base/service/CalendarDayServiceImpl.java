package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.CalendarDayMapper;
import com.richfit.mes.common.model.base.CalendarDay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author sun
 * @Description 组织机构服务
 */
@Service
public class CalendarDayServiceImpl extends ServiceImpl<CalendarDayMapper, CalendarDay> implements CalendarDayService{

    @Autowired
    private CalendarDayMapper calendarDayMapper;

}
