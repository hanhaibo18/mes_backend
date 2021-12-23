package com.richfit.mes.base.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.richfit.mes.common.model.base.CalendarDay;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author 王瑞
 * @Description 日历日期Mapper
 */
@Mapper
public interface CalendarDayMapper extends BaseMapper<CalendarDay> {
}
