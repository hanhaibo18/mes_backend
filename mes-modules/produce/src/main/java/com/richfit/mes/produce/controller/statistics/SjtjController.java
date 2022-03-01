package com.richfit.mes.produce.controller.statistics;

import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.produce.service.SjtjServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
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
    @Autowired
    private SjtjServiceImpl sjtjService;

    @ApiOperation(value = "分页查询异常报告", notes = "根据分页查询异常报告")
    @GetMapping("/query")
    public List query1(String branchCode) throws IOException {
        List<Map> query  = sjtjService.query1(branchCode);

        return query;
    }


//    @ApiOperation(value = "分页查询异常报告", notes = "根据分页查询异常报告")
//    @GetMapping("/query")
//    public List getByTemplateCode(String branchCode) throws IOException {
//        String sql = "  ";
//        String sql2 = " ";
//        String sql3 = "";
//        String sql4 = "";
//        List<String> sqlList = new ArrayList<>();
//        sqlList.add(sql);
//        sqlList.add(sql2);
//        sqlList.add(sql3);
//        sqlList.add(sql4);
//        List list = new ArrayList();
//        for (String sqlData : sqlList) {
//            if (null != sqlData && sqlData.contains("call")) {
//                list = (List) jdbcTemplate.execute(new CallableStatementCreator() {
//                    @Override
//                    public CallableStatement createCallableStatement(Connection con) throws SQLException {
//                        // String storedProc = "{call sp_list_table(?,?)}";// 调用的sql
//                        String storedProc = String.format(sqlData, branchCode);// 调用的sql
//                        CallableStatement cs = con.prepareCall(storedProc);
//                        cs.setString(1, branchCode);// 设置输入参数的值
//                        return cs;
//                    }
//                }, new CallableStatementCallback() {
//                    @Override
//                    public Object doInCallableStatement(CallableStatement cs) throws SQLException, DataAccessException {
//                        List resultsMap = new ArrayList();
//                        cs.execute();
//                        ResultSet rs = (ResultSet) cs.getObject(2);// 获取游标一行的值
//                        ResultSetMetaData md = rs.getMetaData();
//                        int columnCount = md.getColumnCount();
//                        while (rs.next()) {// 转换每行的返回值到Map中
//                            Map rowData = new HashMap();
//                            for (int i = 1; i <= columnCount; i++) {
//                                rowData.put(md.getColumnName(i), rs.getObject(i));
//                            }
//                            resultsMap.add(rowData);
//                        }
//                        rs.close();
//                        return resultsMap;
//                    }
//                });
//            } else {
//                list.addAll(jdbcTemplate.queryForList(String.format(sqlData, branchCode))) ;
//            }
//        }
//        return list;
//    }
}
