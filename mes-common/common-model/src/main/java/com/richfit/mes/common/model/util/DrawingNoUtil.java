package com.richfit.mes.common.model.util;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

/**
 * 图号通用工具
 *
 * @Author: zhiqiang.lu
 * @Date: 2023.1.9
 */
public class DrawingNoUtil {

    static String drawingNo(String drawingNo) {
        drawingNo = drawingNo.trim();
        drawingNo = drawingNo.replaceAll("-", "");
        drawingNo = drawingNo.replaceAll("[.]", "");
        drawingNo = drawingNo.replaceAll(" ", "");
        return drawingNo;
    }


    /**
     * 功能描述: QueryWrapper Like查询条件
     *
     * @param queryWrapper 查询
     * @param colName      列名
     * @param drawingNo    值
     * @Author: zhiqiang.lu
     * @Date: 2023.1.9
     */
    public static void queryLike(QueryWrapper queryWrapper, String colName, String drawingNo) {
        queryWrapper.apply(queryLikeSql(colName, drawingNo));
    }

    /**
     * 功能描述: QueryWrapper eq查询条件
     *
     * @param queryWrapper 查询
     * @param colName      列名
     * @param drawingNo    值
     * @Author: zhiqiang.lu
     * @Date: 2023.1.9
     */
    public static void queryEq(QueryWrapper queryWrapper, String colName, String drawingNo) {
        queryWrapper.apply(queryEqSql(colName, drawingNo));
    }

    /**
     * 功能描述: sql like
     *
     * @param colName   列名
     * @param drawingNo 值
     * @Author: zhiqiang.lu
     * @Date: 2023.1.9
     */
    public static String queryLikeSql(String colName, String drawingNo) {
        drawingNo = DrawingNoUtil.drawingNo(drawingNo);
        return "replace(replace(replace(" + colName + ", '-', ''), '.', ''),' ', '') like '%" + drawingNo + "%'";
    }

    /**
     * 功能描述: sql =
     *
     * @param colName   列名
     * @param drawingNo 值
     * @Author: zhiqiang.lu
     * @Date: 2023.1.9
     */
    public static String queryEqSql(String colName, String drawingNo) {
        drawingNo = DrawingNoUtil.drawingNo(drawingNo);
        return "replace(replace(replace(" + colName + ", '-', ''), '.', ''),' ', '') = '" + drawingNo + "'";
    }
}
