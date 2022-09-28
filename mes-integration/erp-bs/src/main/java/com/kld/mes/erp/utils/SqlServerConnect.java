package com.kld.mes.erp.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Description 北石ERP物料、工单接口数据库连接
 * @Author fengxy
 * @Date 2022年9月14日15:08:04
 */
@Component
@Slf4j
public class SqlServerConnect {


    @Value("${interface.erp.username}")
    private String userName;

    @Value("${interface.erp.password}")
    private String password;

    @Value("${interface.erp.sql-server-url}")
    private String getErpUrl;

    /**
     * 获取ERP数据库连接
     * @return
     */
    public Connection getConnection() {
        try {
            //1.加载驱动
            Class.forName("net.sourceforge.jtds.jdbc.Driver");
            //2.用户信息和url
            String name=userName;
            String pwd=password;
            String url=getErpUrl;
            //3.连接成功
            Connection conn = DriverManager.getConnection(url, name, pwd);
            //log.info("获取ERP数据库SQL_SERVER连接成功:{}"+conn.getSchema());
            return conn;
        } catch (Exception e) {
            log.error("获取ERP数据库SQL_SERVER连接失败:{}"+e.getMessage());
            throw new IllegalArgumentException(e);
        }
    }

    public List<Map<String, Object>> executeQuery(String sql, String columns) {

        try {
            List<Map<String, Object>> queryResult = new ArrayList<Map<String, Object>>();
            Connection conn = getConnection();
            Statement st = conn.createStatement();
            ResultSet set = st.executeQuery(sql);
            while (set.next()) {
                Map<String,Object> result = new HashMap<>();
                String[] columnList = columns.split(",");
                for(String str:columnList){
                    result.put(str,set.getString(str));
                }
                queryResult.add(result);
            }
            set.close();
            st.close();
            conn.close();
            return queryResult;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }
}
