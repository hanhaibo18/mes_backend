package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.base.CalendarClass;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 王瑞
 * @Description 日历班次Mapper
 */
@Mapper
public interface CalendarClassMapper extends BaseMapper<CalendarClass> {
}
