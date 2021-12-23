package com.richfit.mes.common.core.utils;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * @Author: GaoLiang
 * @Date: 2020/8/4 10:25
 */
public abstract class DateUtils {

    public static float decimalFormat(String pattern, double value) {
        return Float.parseFloat(new DecimalFormat(pattern).format(value));
    }

    /**
     * 通过时间秒毫秒数判断两个时间的间隔
     *
     * @param startDate
     * @param endData
     * @return
     */
    public static float differentDaysByMillisecond(Date startDate, Date endData) {
        long diffSeconds = endData.getTime() - startDate.getTime();
        long diffHour = diffSeconds / (1000 * 3600);
        //System.out.println("获得小时:" + diffHour);
        float diffDay = (float) diffHour / 24;
        float floatDay = decimalFormat("0.00", diffDay);
        //System.out.println(decimalFormat("0", diffDay));
        return floatDay;
    }

    public static int workDays(String strStartDate, String strEndDate) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        int i = 0;
        try {
            Date startDate = df.parse(strStartDate);
            Date endDate = df.parse(strEndDate);
            i =  workDays(startDate,endDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return i;
    }

    public static int workDays(Date startDate, Date endData) {

        Calendar cl1 = Calendar.getInstance();
        Calendar cl2 = Calendar.getInstance();

        cl1.setTime(startDate);
        cl2.setTime(endData);

        int count = -1;
        while (cl1.compareTo(cl2) <= 0) {
            if (cl1.get(Calendar.DAY_OF_WEEK) != 7 && cl1.get(Calendar.DAY_OF_WEEK) != 1)
                count++;
            cl1.add(Calendar.DAY_OF_MONTH, 1);
        }
        return count;


    }

    public static String getYearStart() {

        Calendar now = Calendar.getInstance();
        return now.get(Calendar.YEAR)+"_01_01";
    }

    public static String getYearEnd() {
        Calendar now = Calendar.getInstance();
        return now.get(Calendar.YEAR)+"_12_31";
    }

    public static String getMonthStart() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar now = Calendar.getInstance();
        //now.add(Calendar.MONTH, 1);
        now.set(Calendar.DAY_OF_MONTH,1);//设置为1号,当前日期既为本月第一天
        return format.format(now.getTime());
    }

    public static String getMonthEnd() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        calendar.set(Calendar.DAY_OF_MONTH, 0);
        return format.format(calendar.getTime());
    }
}
