package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.CalendarClassMapper;
import com.richfit.mes.common.model.base.CalendarClass;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author sun
 * @Description 组织机构服务
 */
@Service
public class CalendarClassServiceImpl extends ServiceImpl<CalendarClassMapper, CalendarClass> implements CalendarClassService{

    @Autowired
    private CalendarClassMapper calendarClassMapper;

}
