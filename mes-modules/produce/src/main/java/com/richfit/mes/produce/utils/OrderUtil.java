package com.richfit.mes.produce.utils;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * 排序通用工具方法
 *
 * @author zhiqiang.lu
 * @date 2022.9.20
 */
public class OrderUtil {

    /**
     * 排序  升序
     */
    public static final String ORDER_ASC = "asc";

    /**
     * 排序  降序
     */
    public static final String ORDER_DESC = "desc";

    /**
     * 功能描述: 排序通用方法
     *
     * @param queryWrapper 查询
     * @param orderCol     排序列
     * @param order        排序方式
     * @Author: zhiqiang.lu
     * @Date: 2022.9.27
     */
    public static void query(QueryWrapper queryWrapper, String orderCol, String order) {
        query(queryWrapper, null, orderCol, order);
    }

    /**
     * 功能描述: 排序通用方法(用于带有代理表名的)
     *
     * @param queryWrapper 查询
     * @param table        代理表名
     * @param orderCol     排序列
     * @param order        排序方式
     * @Author: zhiqiang.lu
     * @Date: 2022.9.28
     */
    public static void query(QueryWrapper queryWrapper, String table, String orderCol, String order) {
        table = tableName(table);
        if (!StrUtil.isBlank(orderCol)) {
            if (!StrUtil.isBlank(order)) {
                if (ORDER_ASC.equals(order)) {
                    queryWrapper.orderByAsc(table + StrUtil.toUnderlineCase(orderCol));
                } else if (ORDER_DESC.equals(order)) {
                    queryWrapper.orderByDesc(table + StrUtil.toUnderlineCase(orderCol));
                }
            } else {
                queryWrapper.orderByDesc(table + StrUtil.toUnderlineCase(orderCol));
            }
        } else {
            queryWrapper.orderByDesc(table + "modify_time");
        }
    }

    /**
     * 功能描述: 排序sql字符串方法
     *
     * @param orderCol 排序列
     * @param order    排序方式
     * @Author: zhiqiang.lu
     * @Date: 2022.9.28
     */
    public static String querySql(String orderCol, String order) {
        return querySql(null, orderCol, order);
    }

    /**
     * 功能描述: 排序sql字符串方法(用于带有代理表名的)
     *
     * @param table    代理表名
     * @param orderCol 排序列
     * @param order    排序方式
     * @Author: zhiqiang.lu
     * @Date: 2022.9.28
     */
    public static String querySql(String table, String orderCol, String order) {
        table = tableName(table);
        if (!StrUtil.isBlank(orderCol)) {
            if (!StrUtil.isBlank(order)) {
                return "order by " + table + orderCol + " " + order;
            } else {
                return "order by " + table + orderCol + " desc";
            }
        } else {
            return "order by " + table + "modify_time desc";
        }
    }

    /**
     * 功能描述: 代理表面处理(例如：t.modify_time查询)
     *
     * @param table 代理表名
     * @Author: zhiqiang.lu
     * @Date: 2022.9.28
     */
    public static String tableName(String table) {
        if (StrUtil.isBlank(table)) {
            return "";
        } else {
            return table + ".";
        }
    }
}
