package com.richfit.mes.produce.controller.statistics;

import com.richfit.mes.common.core.base.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author 马峰
 */
@Slf4j
@Api("数据统计")
@RestController
@RequestMapping("/api/produce/sjtj")
public class SjtjController extends BaseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;


    @ApiOperation(value = "分页查询异常报告", notes = "根据分页查询异常报告")
    @GetMapping("/query")
    public List getByTemplateCode(String branchCode) throws IOException {
        String sql = "select template_code,Sum(number) from produce_track_head where branch_code like '%%BOMCO_ZS%%'  GROUP BY template_code";
        String sql2 = "select Sum(a.qty) FROM\n" +
                " produce_assign a \n" +
                "LEFT\n" +
                "\tJOIN produce_track_complete b ON a.tenant_id = b.tenant_id \n" +
                "LEFT\n" +
                "\tJOIN produce_track_head c ON b.tenant_id = c.tenant_id \n" +
                "LEFT\n" +
                "\tJOIN produce_track_check d ON c.tenant_id = d.tenant_id\n" +
                "WHERE a.branch_code LIKE '%%BOMCO_ZS%%' GROUP BY c.template_code";
        String sql3 = "select\n" +
                "\tSum(a.completed_qty) \n" +
                "FROM\n" +
                "\t produce_track_complete a\n" +
                "\tLEFT JOIN produce_track_head b ON a.tenant_id = b.tenant_id \n" +
                "\tLEFT JOIN produce_assign c ON b.tenant_id = c.tenant_id\n" +
                "\tLEFT JOIN produce_track_check d ON c.tenant_id = d.tenant_id \n" +
                "WHERE\n" +
                "\ta.branch_code LIKE '%%BOMCO_ZS%%' GROUP BY b.template_code";
        String sql4 = "select \n" +
                "  Sum(a.qualify+a.unqualify) \n" +
                "  FROM\n" +
                "  produce_track_check a\n" +
                "  LEFT JOIN produce_track_complete b ON a.tenant_id = b.tenant_id\n" +
                "  LEFT JOIN produce_track_head c ON b.tenant_id = c.tenant_id\n" +
                "  LEFT JOIN produce_assign d ON c.tenant_id = d.tenant_id\n" +
                "  WHERE\n" +
                "  a.branch_code LIKE '%%BOMCO_ZS%%'\n" +
                "  GROUP BY\n" +
                "  c.template_code";
        List<String> sqlList = new ArrayList<>(4);
        sqlList.add(sql);
        sqlList.add(sql2);
        sqlList.add(sql3);
        sqlList.add(sql4);
        List list = new ArrayList();
        for (String sqlData : sqlList) {
            if (null != sqlData && sqlData.contains("call")) {
                list = (List) jdbcTemplate.execute(new CallableStatementCreator() {
                    @Override
                    public CallableStatement createCallableStatement(Connection con) throws SQLException {
                        // String storedProc = "{call sp_list_table(?,?)}";// 调用的sql
                        String storedProc = String.format(sqlData, branchCode);// 调用的sql
                        CallableStatement cs = con.prepareCall(storedProc);
                        cs.setString(1, branchCode);// 设置输入参数的值
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
            } else {
                list.addAll(jdbcTemplate.queryForList(String.format(sqlData, branchCode))) ;
            }
        }
        return list;
    }
}
