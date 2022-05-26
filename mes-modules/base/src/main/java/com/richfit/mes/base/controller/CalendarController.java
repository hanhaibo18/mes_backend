package com.richfit.mes.base.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.service.CalendarClassService;
import com.richfit.mes.base.service.CalendarDayService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.base.CalendarClass;
import com.richfit.mes.common.model.base.CalendarDay;
import com.richfit.mes.common.security.util.SecurityUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * @author 王瑞
 * @Description 日历Controller
 */
@Slf4j
@Api("日历管理")
@RestController
@RequestMapping("/api/base/calendar")
public class CalendarController extends BaseController {

    @Autowired
    private CalendarDayService calendarDayService;

    @Autowired
    private CalendarClassService calendarClassService;

    public static String SUCCESS_MESSAGE = "操作成功！";
    public static String FAILED_MESSAGE = "操作失败！";
    public static String ID_NULL_MESSAGE = "ID不能为空！";
    public static String DAY_NAME_NULL_MESSAGE = "日历日期名不能为空！";
    public static String DAY_TYPE_NULL_MESSAGE = "请选择日期类型！";

    public static String CLASS_NAME_NULL_MESSAGE = "班次名不能为空！";

    @ApiOperation(value = "新增日历日期", notes = "新增日历日期")
    @PostMapping("/day")
    public CommonResult<CalendarDay> addCalendarDay(@RequestBody CalendarDay calendarDay) {
        if (StringUtils.isNullOrEmpty(calendarDay.getName())) {
            return CommonResult.failed(DAY_NAME_NULL_MESSAGE);
        } else if (StringUtils.isNullOrEmpty(calendarDay.getType())) {
            return CommonResult.failed(DAY_TYPE_NULL_MESSAGE);
        } else {
            calendarDay.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            calendarDay.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
            calendarDay.setCreateTime(new Date());
            boolean bool = calendarDayService.save(calendarDay);
            if (bool) {
                return CommonResult.success(calendarDay, SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "修改日历日期", notes = "修改日历日期")
    @PutMapping("/day")
    public CommonResult<CalendarDay> updateCalendarDay(@RequestBody CalendarDay calendarDay) {
        if (StringUtils.isNullOrEmpty(calendarDay.getName())) {
            return CommonResult.failed(DAY_NAME_NULL_MESSAGE);
        } else if (StringUtils.isNullOrEmpty(calendarDay.getType())) {
            return CommonResult.failed(DAY_TYPE_NULL_MESSAGE);
        } else {
            calendarDay.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            calendarDay.setModifyTime(new Date());
            boolean bool = calendarDayService.updateById(calendarDay);
            if (bool) {
                return CommonResult.success(calendarDay, SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "删除日历日期", notes = "根据日期ID删除日历日期")
    @DeleteMapping("/day")
    public CommonResult deleteCalendarDayById(@RequestBody List<String> ids) {
        if (ids == null || ids.size() == 0) {
            return CommonResult.failed(ID_NULL_MESSAGE);
        } else {
            boolean bool = calendarDayService.removeByIds(ids);
            if (bool) {
                return CommonResult.success(null, SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "分页查询日历日期", notes = "根据日期类型、日期名分页查询日历日期")
    @GetMapping("/day")
    public CommonResult<IPage<CalendarDay>> selectCalendarDay(String type, String name, int page, int limit) {
        QueryWrapper<CalendarDay> queryWrapper = new QueryWrapper<CalendarDay>();
        if (!StringUtils.isNullOrEmpty(type)) {
            queryWrapper.eq("type", type);
        }
        if (!StringUtils.isNullOrEmpty(name)) {
            queryWrapper.like("name", name);
        }
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        return CommonResult.success(calendarDayService.page(new Page<CalendarDay>(page, limit), queryWrapper), SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询日历日期", notes = "根据日期类型、日期名查询日历日期")
    @GetMapping("/day/list")
    public CommonResult<List<CalendarDay>> selectCalendarDay(String type, String name, String startDate, String endDate) {
        QueryWrapper<CalendarDay> queryWrapper = new QueryWrapper<CalendarDay>();
        if (!StringUtils.isNullOrEmpty(type)) {
            queryWrapper.eq("type", type);
        }
        if (!StringUtils.isNullOrEmpty(name)) {
            queryWrapper.like("name", name);
        }
        if (!StringUtils.isNullOrEmpty(startDate) && !StringUtils.isNullOrEmpty(endDate)) {
            queryWrapper.apply("date_type = '1' or (date_type = '0' and (start_date >= '" + startDate + "' or end_date >= '" + startDate + "') and (start_date <= '" + endDate + "' or end_date <= '" + endDate + "'))");
        }

        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        return CommonResult.success(calendarDayService.list(queryWrapper), SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "新增班次时间", notes = "新增班次时间")
    @PostMapping("/class")
    public CommonResult<CalendarClass> addCalendarClass(@RequestBody CalendarClass calendarClass) {
        if (StringUtils.isNullOrEmpty(calendarClass.getName())) {
            return CommonResult.failed(DAY_NAME_NULL_MESSAGE);
        } else {
            calendarClass.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
            calendarClass.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
            calendarClass.setCreateTime(new Date());
            boolean bool = calendarClassService.save(calendarClass);
            if (bool) {
                return CommonResult.success(calendarClass, SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "修改班次时间", notes = "修改班次时间")
    @PutMapping("/class")
    public CommonResult<CalendarClass> updateCalendarClass(@RequestBody CalendarClass calendarClass) {
        if (StringUtils.isNullOrEmpty(calendarClass.getName())) {
            return CommonResult.failed(DAY_NAME_NULL_MESSAGE);
        } else {
            calendarClass.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            calendarClass.setModifyTime(new Date());
            boolean bool = calendarClassService.updateById(calendarClass);
            if (bool) {
                return CommonResult.success(calendarClass, SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "删除班次时间", notes = "根据ID删除班次时间")
    @DeleteMapping("/class")
    public CommonResult deleteCalendarClassById(@RequestBody List<String> ids) {
        if (ids == null || ids.size() == 0) {
            return CommonResult.failed(ID_NULL_MESSAGE);
        } else {
            boolean bool = calendarClassService.removeByIds(ids);
            if (bool) {
                return CommonResult.success(null, SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "查询班次", notes = "根据班次名查询班次时间")
    @GetMapping("/class")
    public CommonResult<List<CalendarClass>> selectCalendarClass(String name) {
        QueryWrapper<CalendarClass> queryWrapper = new QueryWrapper<CalendarClass>();
        if (!StringUtils.isNullOrEmpty(name)) {
            queryWrapper.like("name", name);
        }
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.orderByAsc("start_time");
        return CommonResult.success(calendarClassService.list(queryWrapper), SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询工作日", notes = "根据时间范围查询有多少工作日")
    @GetMapping("/day/work")
    public CommonResult<Integer> selectWorkDay(String startDate, String endDate) {
        QueryWrapper<CalendarDay> queryWrapper = new QueryWrapper<CalendarDay>();
        queryWrapper.apply("date_type = '1' or (date_type = '0' and (start_date >= '" + startDate + "' or end_date >= '" + startDate + "') and (start_date <= '" + endDate + "' or end_date <= '" + endDate + "'))");
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        List<CalendarDay> days = calendarDayService.list(queryWrapper);

        List<Integer> restDays = new ArrayList<>();

        days.stream().filter(day -> {
            if (day.getDateType().equals("1")) {
                return true;
            }
            return false;
        }).forEach(day -> {
            if (day.getType().equals("0")) {
                Integer week = day.getWeek() == 7 ? 1 : day.getWeek() + 1;
                restDays.add(week);
            }
        });

        Integer workDay = 0;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date start = sdf.parse(startDate);
            Date end = sdf.parse(endDate);
            List<Date> workDays = getDateArrays(start, end, restDays);

            workDay = workDays.size();

            for (CalendarDay day : days) {
                if (day.getDateType().equals("0")) { //按时间范围
                    if (day.getType().equals("0")) { //休息
                        Date date1 = day.getStartDate();
                        Date date2 = day.getEndDate();

                        int startGap = day.getStartDate().compareTo(start);
                        //compareTo()方法的返回值，date1小于date2返回-1，date1大于date2返回1，相等返回0
                        if (startGap == -1) {
                            date1 = start;
                        }

                        int endGap = end.compareTo(day.getEndDate());
                        if (endGap == -1) {
                            date2 = end;
                        }
                        List<Date> result = getDateArrays(date1, date2, restDays);
                        long count = result.size();

                        workDay -= (int) count;
                    } else if (day.getType().equals("1")) { //加班
                        long count = getDiff(day.getEndDate(), day.getStartDate()) + 1;
                        int startGap = day.getStartDate().compareTo(start);
                        //compareTo()方法的返回值，date1小于date2返回-1，date1大于date2返回1，相等返回0
                        if (startGap == -1) {
                            long diff = getDiff(start, day.getStartDate()) + 1;
                            count -= diff;
                        }

                        int endGap = end.compareTo(day.getEndDate());
                        if (endGap == -1) {
                            long diff = getDiff(day.getEndDate(), end) + 1;
                            count -= diff;
                        }
                        workDay += (int) count;
                    }
                }
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return CommonResult.success(workDay, SUCCESS_MESSAGE);
    }

    public long getDiff(Date start, Date end) {
        long diffInMillis = Math.abs(start.getTime() - end.getTime());
        long diff = TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
        return diff;
    }

    public static List<Date> getDateArrays(Date start, Date end, List<Integer> restDays) {
        ArrayList ret = new ArrayList();
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(start);
        Date tmpDate = calendar.getTime();
        long endTime = end.getTime();
        while (tmpDate.before(end) || tmpDate.getTime() == endTime) {
            boolean isHave = false;
            for (Integer d : restDays) {
                if (calendar.get(Calendar.DAY_OF_WEEK) == d) {
                    isHave = true;
                }
            }
            if (!isHave) {
                ret.add(calendar.getTime());
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
            tmpDate = calendar.getTime();
        }
        return ret;
    }


}
