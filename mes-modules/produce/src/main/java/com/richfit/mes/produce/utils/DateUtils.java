package com.richfit.mes.produce.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.util.*;

public class DateUtils {

    public static final String DATE_FORMAT_IN = "yyyy-MM-dd'T'HH:mm:ss.SSS";
    public static final String DATE_FORMAT_OUT = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_OUT_STR = "yyyyMMddHHmmss";
    public static final String DATE_FORMAT_DAY = "yyyy-MM-dd";
    public static final String FORMATTEXT04 = "yyyyMMdd";
    private static final Map<Integer,String> weekMap = new HashMap<>();

    static {
        weekMap.put(0,"周日");
        weekMap.put(1,"周一");
        weekMap.put(2,"周二");
        weekMap.put(3,"周三");
        weekMap.put(4,"周四");
        weekMap.put(5,"周五");
        weekMap.put(6,"周六");

    }

    private DateUtils() {
    }

    public static Date now() {
        return new Date();
    }

    // 获得当天0点时间
    public static Date getTimesMorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();

    }

    // 获得当天0点时间
    public static Date getTimesMorning(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // 获得当天n点时间
    public static Date getTimes(int n) {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, n);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 获取当前日期是星期几 从下表0 开始 一周开始时间为周日
     *
     * @param date
     * @return
     * @author zhou.wb
     * @date 2019年12月6日
     */
    public static int getDayForWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        return c.get(Calendar.DAY_OF_WEEK) - 1;
    }
    /**
     * @Author Andre
     * @Description 根据时间获取星期，返回中文；格式：周X
     * @Date  2022/5/31
     * @Param [java.util.Date]
     * @return java.lang.String
     **/
    public static String getDayForWeekString(Date date) {
        return weekMap.get(getDayForWeek(date));
    }

    /**
     * 获取本周第一天
     *
     * @return
     * @author wb.zhou
     * @date 2019年12月23日
     */
    public static Date getFirstDateForWeek(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -(getDayForWeek(date)));
        return c.getTime();
    }

    /**
     * 获取本周第一天，周一为一周的第一天
     *
     * @return
     * @author wb.zhou
     * @date 2019年12月23日
     */
    public static Date getFirstDateForWeekByMonday(Date date) {
        Integer wd = getDayForWeek(date) - 1;
        if (wd < 0) {
            wd = 6;
        }
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, -(wd));
        return c.getTime();
    }

    /**
     * 字符串转时间
     *
     * @param date
     * @param pattern
     * @return
     * @author zhou.wb
     * @date 2019年12月6日
     */
    public static Date parseDate(String date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        try {
            return sdf.parse(date);
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 给日期加上 几年
     *
     * @param date
     * @param day
     * @return
     * @author zhou.wb
     * @date 2019年12月6日
     */
    public static Date addDateForYear(Date date, int year) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.YEAR, year);
        return c.getTime();
    }

    /**
     * 给日期加上 几月
     *
     * @param date
     * @param day
     * @return
     * @author zhou.wb
     * @date 2019年12月6日
     */
    public static Date addDateForMonth(Date date, int month) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, month);
        return c.getTime();
    }

    /**
     * 给日期加上 几天
     *
     * @param date
     * @param day
     * @return
     * @author zhou.wb
     * @date 2019年12月6日
     */
    public static Date addDateForDay(Date date, int day) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DAY_OF_MONTH, day);
        return c.getTime();
    }

    /**
     * 给日期加上 几小时
     *
     * @param date
     * @param day
     * @return
     * @author zhou.wb
     * @date 2019年12月6日
     */
    public static Date addDateForHour(Date date, int hour) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.HOUR_OF_DAY, hour);
        return c.getTime();
    }

    /**
     * 给日期加上 几分钟
     *
     * @param date
     * @param day
     * @return
     * @author zhou.wb
     * @date 2019年12月6日
     */
    public static Date addDateForMinute(Date date, int minute) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MINUTE, minute);
        return c.getTime();
    }

    /**
     * 给日期加上几秒
     *
     * @return
     * @author wb.zhou
     * @date 2020年3月6日
     */
    public static Date addDateForSecond(Date date, int second) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.SECOND, second);
        return c.getTime();
    }

    // 获得昨天0点时间
    public static Date getYesterdayMorning() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getTimesMorning().getTime() - 3600 * 24 * 1000);
        return cal.getTime();
    }

    // 获得当天近7天时间
    public static Date getWeekFromNow() {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(getTimesMorning().getTime() - 3600 * 24 * 1000 * 7);
        return cal.getTime();
    }

    // 获得当天23点59分59秒时间
    public static Date getTimesNight() {
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    // 获得当天23点59分59秒时间
    public static Date getTimesNight(Date date) {
        // return addDateForSecond(addDateForDay(getTimesMorning(date), 1), -1);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    /**
     * * 设置免打扰开始时间
     * @param date  此方法根据自己的需求使用  暂不删除 可能推送的时候有用到这个方法  发现的时候 使用下方的方法
     * @return
     */
    public static Date getNotDisturbStartTime() {
        // return addDateForSecond(addDateForDay(getTimesMorning(date), 1), -1);
        Date ntd = new Date();
        Date zerod = getTimesMorning();
        Calendar cal = Calendar.getInstance();
        if(ntd.before(zerod)){
            cal.setTime(ntd);
        }else{
            cal.setTime(addDateForDay(ntd,-1));
        }
        cal.set(Calendar.HOUR_OF_DAY, 21);
        cal.set(Calendar.MINUTE, 30);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     *获取免打扰时间
     * @param date
     * @return
     */
    public static Date getNotDisturbEndTime(Date date,int horuOfDay,int minuteOfDay) {
        // return addDateForSecond(addDateForDay(getTimesMorning(date), 1), -1);
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, horuOfDay);
        cal.set(Calendar.MINUTE,minuteOfDay);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // 获得本周一0点时间
    public static Date getTimesWeekMorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
        return cal.getTime();
    }

    // 获得本周日24点时间
    public static Date getTimesWeeknight() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getTimesWeekMorning());
        cal.add(Calendar.DAY_OF_WEEK, 7);
        return cal.getTime();
    }

    // 获得本月第一天0点时间
    public static Date getTimesMonthMorning() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
        return cal.getTime();
    }

    // 获得当月第一天0点时间
    public static Date getTimesMonthMorning(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    // 获得本月最后一天24点时间
    public static Date getTimesMonthNight() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    // 获得当月最后一天24点时间
    public static Date getTimesMonthNight(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH));
        cal.set(Calendar.HOUR_OF_DAY, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        return cal.getTime();
    }

    // 上月初0点时间
    public static Date getLastMonthStartMorning() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getTimesMonthMorning());
        cal.add(Calendar.MONTH, -1);
        return cal.getTime();
    }
    //获取按照当前月加或者减月份的0点时间
    public static Date getMonthStartMorning(int amount) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getTimesMonthMorning());
        cal.add(Calendar.MONTH, amount);
        return cal.getTime();
    }


    // 本季度开始点时间
    public static Date getCurrentQuarterStartTime() {
        Calendar c = Calendar.getInstance();
        int currentMonth = c.get(Calendar.MONTH) + 1;
        SimpleDateFormat longSdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat shortSdf = new SimpleDateFormat("yyyy-MM-dd");
        Date now = null;
        try {
            if (currentMonth <= 3) {
                c.set(Calendar.MONTH, 0);
            } else if (currentMonth <= 6) {
                c.set(Calendar.MONTH, 3);
            } else if (currentMonth <= 9) {
                c.set(Calendar.MONTH, 4);
            } else if (currentMonth <= 12) {
                c.set(Calendar.MONTH, 9);
            }
            c.set(Calendar.DATE, 1);
            now = longSdf.parse(shortSdf.format(c.getTime()) + " 00:00:00");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return now;
    }

    /**
     * 当前季度的结束时间，即2012-03-31 23:59:59
     *
     * @return
     */
    public static Date getCurrentQuarterEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentQuarterStartTime());
        cal.add(Calendar.MONTH, 3);
        return cal.getTime();
    }

    // 本年开始点时间
    public static Date getCurrentYearStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH), 0, 0, 0);
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.YEAR));
        return cal.getTime();
    }

    // 本年结束点时间
    public static Date getCurrentYearEndTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentYearStartTime());
        cal.add(Calendar.YEAR, 1);
        return cal.getTime();
    }

    // 上年开始点时间
    public static Date getLastYearStartTime() {
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentYearStartTime());
        cal.add(Calendar.YEAR, -1);
        return cal.getTime();
    }

    // N年前开始点时间
    public static Date getSomeYearAgoStartTime(int year) {
        if (year <= 0) {
            year = 1;
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(getCurrentYearStartTime());
        cal.add(Calendar.YEAR, year * (-1));
        return cal.getTime();
    }

    /**
     * 通过时间秒毫秒数判断两个时间间隔多少天
     *
     * @param date1
     * @param date2
     * @return
     * @author liyin
     */
    public static int differentDaysByMillisecond(Date date1, Date date2) {
        return daysBetween(date1, date2);
    }

    public static int differentYearByMillisecond(Date date1, Date date2) {
        return yearBetween(date1, date2);
    }

    /**
     * 通过时间秒毫秒数判断两个时间间隔相差毫秒数
     *
     * @param date1
     * @param date2
     * @return
     * @author zltdhr
     */
    public static long differentMillisecond(Date date1, Date date2) {
        return Math.abs(date2.getTime() - date1.getTime());
    }

    /**
     * 通过时间秒毫秒数判断两个时间间隔相差秒数
     *
     * @param date1
     * @param date2
     * @return
     * @author zltdhr
     */
    public static long differentSecond(Date date1, Date date2) {
        long millisecond = differentMillisecond(date1, date2);
        return millisecond / 1000L;
    }

    /**
     * 格式化
     *
     * @param date
     * @param format
     * @return
     * @author zhou.wb
     * @date 2019年7月22日
     */
    public static String formatDate(Date date, String format) {
        String result = "";
        try {
            if (date != null) {
                DateFormat df = new SimpleDateFormat(format);
                result = df.format(date);
            }
        } catch (Exception e) {
        }
        return result;
    }

    /**
     * formatType格式为yyyy-MM-dd HH:mm:ss//yyyy年MM月dd日 HH时mm分ss秒
     *
     * @param data
     * @param formatType
     * @return
     * @author liyin
     */
    public static String dateToString(Date data, String formatType) {
        return new SimpleDateFormat(formatType).format(data);
    }

    /**
     * string类型日期转date
     *
     * @param dateStr
     * @return
     */
    public static Date stringToDate(String dateStr) {
        Date date = null;
        SimpleDateFormat formater = new SimpleDateFormat();
        formater.applyPattern("yyyy-MM-dd");
        try {
            date = formater.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * string类型日期转date
     *
     * @param dateStr
     * @return
     */
    public static Date stringToDate(String dateStr, String format) {
        Date date = null;
        SimpleDateFormat formater = new SimpleDateFormat();
        formater.applyPattern(format);
        try {
            date = formater.parse(dateStr);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    /**
     * GMT格式本地化
     */
    public static String getDayTime(String isoDate) {
        try {
            DateFormat dfIn = new SimpleDateFormat(DATE_FORMAT_IN, Locale.CHINA);
            DateFormat dfOut = new SimpleDateFormat(DATE_FORMAT_OUT, Locale.CHINA);
            dfIn.setTimeZone(TimeZone.getTimeZone("GMT"));
            dfOut.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
            return dfOut.format(dfIn.parse(isoDate));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * 计算两个日期之间相差的天数
     *
     * @param smdate 较小的时间
     * @param bdate  较大的时间
     * @return 相差天数
     * @throws ParseException
     * @author liyin
     * @Date 2019-12-12 16:28:38
     */
    public static int daysBetween(Date smdate, Date bdate) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        try {
            smdate = sdf.parse(sdf.format(smdate));
            bdate = sdf.parse(sdf.format(bdate));
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        Calendar cal = Calendar.getInstance();
        cal.setTime(smdate);
        long time1 = cal.getTimeInMillis();
        cal.setTime(bdate);
        long time2 = cal.getTimeInMillis();
        long between_days = Math.abs((time2 - time1) / (1000 * 3600 * 24));
        return Integer.parseInt(String.valueOf(between_days));
    }

    /** 相差年数*/
    public static int yearBetween(Date smdate, Date bdate) {
        Period period = Period.between(LocalDate.parse(formatDate(smdate, "yyyy-MM-dd")),LocalDate.parse(formatDate(bdate, "yyyy-MM-dd")));
        return period.getYears();
    }

    /**
     * 比较日期大小
     *
     * @param smdate
     * @param bdate
     * @return
     */
    public static int compareDate(Date smdate, Date bdate) {
        // 获取当前日期
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");// 设置日期格式
        System.out.println(df.format(new Date()));
        Date date1 = null;
        Date date2 = null;
        int rows = 0;
        try {
            // 日期1
            date1 = df.parse(df.format(smdate));
            // 日期2smdate
            date2 = df.parse(df.format(bdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date1.getTime() < date2.getTime()) {
            rows = 1;
        } else if (date1.getTime() >= date2.getTime()) {
            rows = -1;
        }
        return rows;
    }

    /**
     * 比较日期大小
     *
     * @param smdate
     * @param bdate
     * @return
     */
    public static int compareDateTime(Date smdate, Date bdate) {
        // 获取当前日期
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");// 设置时间格式
        System.out.println(df.format(smdate));
        System.out.println(df.format(new Date()));
        Date date1 = null;
        Date date2 = null;
        int rows = 0;
        try {
            // 日期1
            date1 = df.parse(df.format(smdate));
            // 日期2smdate
            date2 = df.parse(df.format(bdate));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if (date1.getTime() <= date2.getTime()) {
            rows = 1;
        } else if (date1.getTime() > date2.getTime()) {
            rows = -1;
        }
        return rows;
    }


    public static boolean compareDateTime(String date1, String date2, String dateFormat) throws Exception {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
        Date time1 = simpleDateFormat.parse(date1);
        Date time2 = simpleDateFormat.parse(date2);
        // 如果后者不大于前者返回true，否则为false
        return time1.after(time2) || time1.equals(time2);
    }

    /**
     * 获取日期英文名
     *
     * @param pattern
     * @param date
     * @return
     * @author wb.zhou
     * @date 2020年2月27日
     */
    public static String getDateByEnglish(String pattern, Date date) {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat(pattern, Locale.US);
            return sdf.format(date);
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }

    /**
     * 获取年份
     *
     * @return
     */
    public static Integer getYearInteger() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.YEAR);
    }

    /**
     * 获取月份
     *
     * @return
     */
    public static Integer getMonthInteger() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取当前月的第几天
     *
     * @return
     */
    public static Integer getDayInteger() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前小时数
     *
     * @return
     */
    public static Integer getHourInteger() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前分钟数
     *
     * @return
     */
    public static Integer getMinuteInteger() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.MINUTE);
    }

    /**
     * 获取当前秒数
     *
     * @return
     */
    public static Integer getSecondInteger() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.SECOND);
    }

    /**
     * 获取年份
     *
     * @return
     */
    public static Integer getYearInteger(Date time) {
        Calendar now = Calendar.getInstance();
        now.setTime(time);
        return now.get(Calendar.YEAR);
    }

    /**
     * 获取月份
     *
     * @return
     */
    public static Integer getMonthInteger(Date time) {
        Calendar now = Calendar.getInstance();
        now.setTime(time);
        return now.get(Calendar.MONTH) + 1;
    }

    /**
     * 获取当前月的第几天
     *
     * @return
     */
    public static Integer getDayInteger(Date time) {
        Calendar now = Calendar.getInstance();
        now.setTime(time);
        return now.get(Calendar.DAY_OF_MONTH);
    }

    /**
     * 获取当前小时数
     *
     * @return
     */
    public static Integer getHourInteger(Date time) {
        Calendar now = Calendar.getInstance();
        now.setTime(time);
        return now.get(Calendar.HOUR_OF_DAY);
    }

    /**
     * 获取当前分钟数
     *
     * @return
     */
    public static Integer getMinuteInteger(Date time) {
        Calendar now = Calendar.getInstance();
        now.setTime(time);
        return now.get(Calendar.MINUTE);
    }

    /**
     * 获取当前秒数
     *
     * @return
     */
    public static Integer getSecondInteger(Date time) {
        Calendar now = Calendar.getInstance();
        now.setTime(time);
        return now.get(Calendar.SECOND);
    }

    /**
     * 获取当前时间的整点
     *
     * @param date
     * @return
     * @author wb.zhou
     * @date 2021年5月13日
     */
    public static Date getCurrentDateHourInteger(Date date) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(date);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTime();
    }

    /**
     * 根据生日获取年龄
     *
     * @param birthday
     * @return
     * @author guohaoran
     * @date 2021年12月7日
     */
    public static int getAgeByBirth(Date birthday) {
        int age = 0;
        try {
            Calendar now = Calendar.getInstance();
            now.setTime(new Date());// 当前时间

            Calendar birth = Calendar.getInstance();
            birth.setTime(birthday);

            if (birth.after(now)) {//如果传入的时间，在当前时间的后面，返回0岁
                age = 0;
            } else {
                age = now.get(Calendar.YEAR) - birth.get(Calendar.YEAR);
                if (now.get(Calendar.DAY_OF_YEAR) > birth.get(Calendar.DAY_OF_YEAR)) {
                    age += 1;
                }
            }
            return age;
        } catch (Exception e) {//兼容性更强,异常后返回数据
            return 0;
        }
    }

    /**
     * 时间加上00：:00:00
     *
     * @param date
     * @return
     */
    public static Date getStartOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()),
                ZoneId.systemDefault());
        LocalDateTime startOfDay = localDateTime.with(LocalTime.MIN);
        return Date.from(startOfDay.atZone(ZoneId.systemDefault()).toInstant());

    }

    /**
     * 时间加上23:59:59
     *
     * @param date
     * @return
     */
    public static Date getEndOfDay(Date date) {
        LocalDateTime localDateTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(date.getTime()),
                ZoneId.systemDefault());
        LocalDateTime endOfDay = localDateTime.with(LocalTime.MAX);
        return Date.from(endOfDay.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * @return java.util.Date
     * @Author Andre
     * @Description 根据当前时间和传入的小时差获取时间
     * @Date 2022/1/27
     * @Param [int]
     **/
    public static Date getCalculateDate(int hour) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.HOUR, hour);
        return calendar.getTime();
    }

    /**
     * @desc 转换特殊类型时间字符串
     * @user wb.zhou
     * @date 2022年03月23日
     * @param java.lang.String dateStr
     * @return java.util.Date
     */
    public static Date transTSDateStr(String dateStr){
        dateStr = dateStr.replace("T", " ");
        dateStr = dateStr.substring(0,dateStr.indexOf("."));
        return DateUtils.addDateForHour(DateUtils.parseDate(dateStr,"yyyy-MM-dd HH:mm:ss"), 8);
    }

    /**
     * @Author Andre
     * @Description 获取2099年12月31日时间
     * @Date  2022/3/30
     * @Param []
     * @return java.util.Date
     **/
    public static Date get2099Date() {
        try {
            return new SimpleDateFormat("yyyy-MM-dd").parse("2099-12-31");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * @Author Andre
     * @Description 获取当前时间的年月日，格式yyyyMMdd
     * @Date  2022/4/21
     * @Param []
     * @return java.lang.Integer
     **/
    public static Integer getNowDayInt() {
       return Integer.parseInt(dateToString(new Date(), "yyyyMMdd"));
    }

    /**
     * @return java.util.Date
     * @Author Andre
     * @Description 获取指定时间，指定月份跨度的时间
     * @Date 2022/4/28
     * @Param [java.util.Date, int]
     **/
    public static Date getDeductionMonthDate(Date date, int span) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.MONTH, span);
        return c.getTime();
    }

    public static void main(String[] args) {
//        Date date = stringToDate("2022-06-11", "yyyy-MM-dd");
//        String dayForWeek = getDayForWeekString(date);
//        System.out.println(dayForWeek);
//        List<Long> list = null;
//        Stream<Long> stream = list.stream();
//        BigDecimal price=BigDecimal.valueOf(0.01);
//        System.out.println( price.multiply(BigDecimal.valueOf(10)).multiply(BigDecimal.valueOf(0.7)));
//        Date date = new Date();
//        String s = dateToString(date, "yyyy-MM-dd HH:mm:ss");
//        System.out.println(s);
//        System.out.println(dateToString(addDateForDay(date,3), "yyyy-MM-dd HH:mm:ss"));


        Date dqDate = stringToDate("2022-08-31 11:00:00","yyyy-MM-dd HH:mm:ss");
        Date startDate = stringToDate("2022-08-31 12:00:00","yyyy-MM-dd HH:mm:ss");
        System.out.println(compareDateTime(dqDate,startDate));
        long l = differentSecond(dqDate, startDate) / 60 / 60;
        System.out.println(l);

        if(l>=12){
            System.out.println("-------------------12小时前");
        }else if(l>=6){
            System.out.println("-------------------6小时前");
        }else if(l>=2){
            System.out.println("-------------------2小时前");
        }else if(l>=0){
            System.out.println("-------------------2小时内");
        }

        Integer i=0;
        System.out.println(i==0);

        long time = DateUtils.parseDate("2022-08-31 12:00:00", "yyyy-MM-dd HH:mm:ss").getTime()-(1000*60*30);

        System.out.println(DateUtils.formatDate(new Date(time),"yyyy-MM-dd HH:mm:ss"));

        Integer a=10;
        System.out.println(a*0.90);





    }
}
