package com.richfit.mes.produce.data;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import java.util.List;
import java.util.Map;

public class BaseProjectBom {
    private static JdbcTemplate jdbcTemplate;

    static {
        String driver = "com.mysql.cj.jdbc.Driver";//mysql驱动
        String url ="jdbc:mysql://11.54.93.106:3306/mes_produce";//连接地址
        String user ="mes_produce";//用户
        String password ="Mes_produce@mes";//密码

        DriverManagerDataSource dataSource=new DriverManagerDataSource();
        dataSource.setUrl(url);
        dataSource.setDriverClassName(driver);
        dataSource.setUsername(user);
        dataSource.setPassword(password);

        jdbcTemplate=new JdbcTemplate(dataSource);
    }

    public static void main(String[] args) {
        String sql ="select draw_no,work_no from produce_plan where tenant_id = '12345678901234567890123456789002' GROUP BY draw_no,work_no ORDER BY draw_no;";//student 数据库表明
        List<Map<String, Object>> maps = jdbcTemplate.queryForList(sql);
        System.out.println(maps.size());
    }
}
