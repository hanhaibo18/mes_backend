package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.model.produce.ProduceTrackHeadTemplate;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.ProduceTrackHeadTemplateService;
import com.richfit.mes.produce.service.TrackHeadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 跟单模板打印
 * </p>
 *
 * @author 马峰
 * @since 2022-2-14
 */
@Slf4j
@Api("跟单模板打印")
@RestController
@RequestMapping("/api/produce/templateprint")
public class TemplatePrintController extends BaseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private ProduceTrackHeadTemplateService produceTrackHeadTemplateService;


    @Autowired
    private SystemServiceClient systemServiceClient;

    @Autowired
    private TrackHeadService trackHeadService;

    /**
     * 功能描述: 按跟单模板编码生成跟单模板EXCEL
     *
     * @Author: mafeng
     * @Date: 2022-2-14
     **/
    @ApiOperation(value = "按跟单模板编码生成跟单模板EXCEL", notes = "按跟单模板编码生成跟单模板EXCEL")
    @GetMapping("/query")
    public void getByTemplateCode(@ApiParam(value = "跟单id", required = true) @RequestParam String id,
                                  @ApiParam(value = "工厂代码") @RequestParam(required = false) String branchCode,
                                  @ApiIgnore HttpServletResponse rsp) throws IOException {
        try {
            // 获取跟单
            TrackHead trackHead = trackHeadService.getById(id);
            QueryWrapper<ProduceTrackHeadTemplate> queryWrapper = new QueryWrapper<ProduceTrackHeadTemplate>();
            queryWrapper.like("template_code", trackHead.getTemplateCode());
            if (!StringUtils.isNullOrEmpty(branchCode)) {
                queryWrapper.like("branch_code", branchCode);
            }
            //获取跟单模板配置信息
            List<ProduceTrackHeadTemplate> trackHeadTemplates = produceTrackHeadTemplateService.list(queryWrapper);
            ProduceTrackHeadTemplate p = trackHeadTemplates.get(0);
            String sql1 = p.getSheet1();
            String sql2 = p.getSheet2();
            String sql3 = p.getSheet1();
            String sql4 = p.getSheet1();
            String templateFileId = p.getFileId();
//        Attachment attach = systemServiceClient.attachment(templateFileId).getData();
            //InputStream inputStream = fastDfsService.downloadFile(attach.getGroupName(),attach.getFastFileId());
            List<List<Map<String, Object>>> sheets = new ArrayList();
            List<Map<String, Object>> list = new ArrayList();
            List<Map<String, Object>> list2 = new ArrayList();
            // 根据配置SQL，获取SHEET1表数据
            if (null != sql1 && sql1.contains("call")) {
                // 如果包含CALL，则只需存储过程
                list = (List) jdbcTemplate.execute(
                        new CallableStatementCreator() {
                            @Override
                            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                                // String storedProc = "{call sp_list_table(?,?)}";// 调用的sql
                                String storedProc = String.format(sql1, id);// 调用的sql
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
            } else if (null != sql1) {
                // 如果不包含CALL，则执行SQL 查询
                list = jdbcTemplate.queryForList(String.format(sql1, id));
            } else {

            }
            // 根据配置SQL，获取SHEET2表数据
            if (null != sql2 && sql1.contains("call")) {
                list = (List) jdbcTemplate.execute(
                        new CallableStatementCreator() {
                            @Override
                            public CallableStatement createCallableStatement(Connection con) throws SQLException {
                                // String storedProc = "{call sp_list_table(?,?)}";// 调用的sql
                                String storedProc = String.format(sql2, id);// 调用的sql
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
            } else if (null != sql2) {
                list2 = jdbcTemplate.queryForList(String.format(sql2, id));
            } else {

            }
            sheets.add(list);
            sheets.add(list2);
            // 生成EXCEL文件，并输出文件流
            try {
                // byte[] bytes = fastDfsService.downloadFile(attach.getGroupName(), attach.getFastFileId());
                //InputStream  inputStream = new java.io.ByteArrayInputStream(bytes);
                CommonResult<byte[]> result = systemServiceClient.getAttachmentInputStream(templateFileId);
                InputStream inputStream = new java.io.ByteArrayInputStream(result.getData());
                ExcelUtils.exportExcelOnSheetsData("跟单", inputStream, sheets, rsp);
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
