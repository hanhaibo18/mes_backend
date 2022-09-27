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
        if (!StrUtil.isBlank(orderCol)) {
            if (!StrUtil.isBlank(order)) {
                if (ORDER_ASC.equals(order)) {
                    queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
                } else if (ORDER_DESC.equals(order)) {
                    queryWrapper.orderByAsc(StrUtil.toUnderlineCase(orderCol));
                }
            } else {
                queryWrapper.orderByDesc(StrUtil.toUnderlineCase(orderCol));
            }
        } else {
            queryWrapper.orderByDesc("modify_time");
        }
    }
}
