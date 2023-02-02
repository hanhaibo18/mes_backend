package com.richfit.mes.produce.utils;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * 时间段查询工具，数据库中有create_time和modify_time方可使用
 *
 * @Author: zhiqiang.lu
 * @Date: 2023.2.1
 */
public class TimeUtil {
    /**
     * 功能描述: queryWrapper
     *
     * @param startTime
     * @Author: zhiqiang.lu
     * @Date: 2023.2.1
     */
    public static QueryWrapper<Object> queryStartTime(QueryWrapper queryWrapper, String startTime) {
        queryWrapper.apply(StrUtil.isNotBlank(startTime), queryStartTimeSql(startTime));
        return queryWrapper;
    }

    /**
     * 功能描述: queryWrapper
     *
     * @param endTime
     * @Author: zhiqiang.lu
     * @Date: 2023.2.1
     */
    public static QueryWrapper<Object> queryEndTime(QueryWrapper queryWrapper, String endTime) {
        queryWrapper.apply(StrUtil.isNotBlank(endTime), queryEndTimeSql(endTime));
        return queryWrapper;
    }

    /**
     * 功能描述: sql 语句拼接
     *
     * @param startTime
     * @Author: zhiqiang.lu
     * @Date: 2023.2.1
     */
    public static String queryStartTimeSql(String startTime) {
        if (StrUtil.isBlank(startTime)) {
            return "1=1";
        }
        startTime = startTime + " 00:00:00";
        return "create_time >= '" + startTime + "' ";
    }


    /**
     * 功能描述: sql 语句拼接
     *
     * @param endTime
     * @Author: zhiqiang.lu
     * @Date: 2023.2.1
     */
    public static String queryEndTimeSql(String endTime) {
        if (StrUtil.isBlank(endTime)) {
            return "1=1";
        }
        endTime = endTime + " 23:59:59";
        return " create_time <= '" + endTime + "' ";
    }
}
