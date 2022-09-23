package com.richfit.mes.produce.utils;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用于模板打印流程方法进行工具类封装
 *
 * @author zhiqiang.lu
 * @date 2022.9.23
 */
public class TemplateUtil {

    /**
     * 用于判断sql语句是否存在call字符
     */
    static String call = "call";

    /**
     * 执行存储过程
     *
     * @param id           下载的文件id
     * @param sql          模板中的sql语句
     * @param jdbcTemplate jdbc模板查询
     * @return 返回查询获取数据集合
     */
    public static List<Map<String, Object>> getDataList(String id, String sql, JdbcTemplate jdbcTemplate) {
        List<Map<String, Object>> list = new ArrayList();
        if (null != sql && sql.contains(call)) {
            // 如果包含CALL，则只需执行存储过程
            list = getExecute(id, sql, jdbcTemplate);
        } else if (null != sql) {
            // 如果不包含CALL，则执行SQL 查询
            list = jdbcTemplate.queryForList(String.format(sql, id));
        }
        return list;
    }

    /**
     * 执行存储过程
     *
     * @param id  下载的文件id
     * @param sql 模板中的sql语句
     * @return 返回查询获取数据集合
     */
    public static List getExecute(String id, String sql, JdbcTemplate jdbcTemplate) {
        return (List) jdbcTemplate.execute(
                new CallableStatementCreator() {
                    @Override
                    public CallableStatement createCallableStatement(Connection con) throws SQLException {
                        // String storedProc = "{call sp_list_table(?,?)}";// 调用的sql
                        String storedProc = String.format(sql, id);// 调用的sql
                        CallableStatement cs = con.prepareCall(storedProc);
                        cs.setString(1, id);// 设置输入参数的值
                        return cs;
                    }
                }, new CallableStatementCallback() {
                    @Override
                    public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
                        List resultsMap = new ArrayList();
                        cs.execute();
                        ResultSet rs = (ResultSet) cs.getObject(2);// 获取游标一行的值
                        ResultSetMetaData md = rs.getMetaData();
                        int columnCount = md.getColumnCount();
                        while (rs.next()) {// 转换每行的返回值到Map中
                            Map rowData = new HashMap();
                            for (int i = 1; i <= columnCount; i++) {
                                rowData.put(md.getColumnName(i), rs.getObject(i));
                            }
                            resultsMap.add(rowData);
                        }
                        rs.close();
                        return resultsMap;
                    }
                });
    }
}
