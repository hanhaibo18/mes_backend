package com.richfit.mes.produce.utils;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * 工序号通用工具类
 *
 * @Author: xinYu.hou
 * @Date: 2023.1.12
 */
public class OptNameUtil {

    static String optName(String optName) {
        optName = optName.trim();
        optName = optName.replaceAll("-", "");
        optName = optName.replaceAll("[.]", "");
        optName = optName.replaceAll(" ", "");
        return optName;
    }


    /**
     * 功能描述: QueryWrapper Like查询条件
     *
     * @param queryWrapper 查询
     * @param colName      列名
     * @param optName      值
     * @Author: xinYu.hou
     * @Date: 2023.1.12
     */
    public static void queryLike(QueryWrapper queryWrapper, String colName, String optName) {
        queryWrapper.apply(queryLikeSql(colName, optName));
    }

    /**
     * 功能描述: QueryWrapper eq查询条件
     *
     * @param queryWrapper 查询
     * @param colName      列名
     * @param optName      值
     * @Author: xinYu.hou
     * @Date: 2023.1.12
     */
    public static void queryEq(QueryWrapper queryWrapper, String colName, String optName) {
        queryWrapper.apply(queryEqSql(colName, optName));
    }

    /**
     * 功能描述: sql like
     *
     * @param colName 列名
     * @param optName 值
     * @Author: xinYu.hou
     * @Date: 2023.1.12
     */
    public static String queryLikeSql(String colName, String optName) {
        optName = OptNameUtil.optName(optName);
        return "replace(replace(replace(" + colName + ", '-', ''), '.', ''),' ', '') like '%" + optName + "%'";
    }


    /**
     * 功能描述: sql =
     *
     * @param colName 列名
     * @param optName 值
     * @Author: xinYu.hou
     * @Date: 2023.1.12
     */
    public static String queryEqSql(String colName, String optName) {
        optName = OptNameUtil.optName(optName);
        return "replace(replace(replace(" + colName + ", '-', ''), '.', ''),' ', '') = '" + optName + "'";
    }
    
}
