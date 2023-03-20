package com.richfit.mes.common.model.util;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import java.util.List;

/**
 * 图号通用工具
 *
 * @Author: zhiqiang.lu
 * @Date: 2023.1.9
 */
public class DrawingNoUtil {

    public static String drawingNo(String drawingNo) {
        drawingNo = drawingNo.trim();
        drawingNo = drawingNo.replaceAll("-", "");
        drawingNo = drawingNo.replaceAll("[.]", "");
        drawingNo = drawingNo.replaceAll(" ", "");
        drawingNo = drawingNo.replaceAll("[(]", "");
        drawingNo = drawingNo.replaceAll("[)]", "");
        drawingNo = drawingNo.replaceAll("（", "");
        drawingNo = drawingNo.replaceAll("）", "");
        return drawingNo;}

    public static String replaceStr(String colName) {
        return "replace(replace(replace(replace(replace(replace(replace(" + colName + ", '-', ''), '.', ''),' ', ''),'(', ''),')', ''),'（', ''),'）', '')";
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
     * 功能描述: QueryWrapper eq查询条件
     *
     * @param queryWrapper 查询
     * @param colName      列名
     * @param drawingNos   值
     * @Author: renzewen
     * @Date: 2023.1.12
     */
    public static void queryIn(QueryWrapper queryWrapper, String colName, List<String> drawingNos) {
        queryWrapper.apply(queryInSql(colName, drawingNos));
    }

    /**
     * 功能描述: QueryWrapper eq查询条件 带返回值
     *
     * @param queryWrapper 查询
     * @param colName      列名
     * @param drawingNo    值
     * @Author: renzewen
     * @Date: 2023.1.9
     */
    public static QueryWrapper<Object> queryReturn(QueryWrapper queryWrapper, String colName, String drawingNo) {
        queryWrapper.apply(queryEqSql(colName, drawingNo));
        return queryWrapper;
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
        return replaceStr(colName) + " like '%" + drawingNo + "%'";
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
        return replaceStr(colName) + " = '" + drawingNo + "'";
    }

    /**
     * 功能描述: sql in
     *
     * @param colName    列名
     * @param drawingNos 值
     * @Author: renzewen
     * @Date: 2023.1.12
     */
    public static String queryInSql(String colName, List<String> drawingNos) {
        StringBuilder drawingNoStr = new StringBuilder();
        for (String drawingNo : drawingNos) {
            if (!StrUtil.isBlank(String.valueOf(drawingNoStr))) {
                drawingNoStr.append(",");
                drawingNoStr.append("'" + DrawingNoUtil.drawingNo(drawingNo) + "'");
            } else {
                drawingNoStr.append("'" + DrawingNoUtil.drawingNo(drawingNo) + "'");
            }
        }
        return replaceStr(colName) + " in (" + drawingNoStr + ")";
    }
}
