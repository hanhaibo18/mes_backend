package com.richfit.mes.produce.controller;

import java.io.IOException;
import java.sql.CallableStatement;
import java.sql.ResultSetMetaData;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.model.produce.ProduceTrackHeadTemplate;
import com.richfit.mes.common.model.sys.Attachment;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.ProduceTrackHeadTemplateService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.web.bind.annotation.*;
import org.springframework.jdbc.core.JdbcTemplate;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.core.utils.FileUtils;

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

    @ApiOperation(value = "分页查询异常报告", notes = "根据分页查询异常报告")
    @GetMapping("/query")
    public void getByTemplateCode(String templateCode, String branchCode, String id, HttpServletResponse rsp) throws IOException {

        QueryWrapper<ProduceTrackHeadTemplate> queryWrapper = new QueryWrapper<ProduceTrackHeadTemplate>();
        if (!StringUtils.isNullOrEmpty(templateCode)) {
            queryWrapper.like("template_code", "%" + templateCode + "%");
        }
        if (!StringUtils.isNullOrEmpty(branchCode)) {
            queryWrapper.like("branch_code", "%" + branchCode + "%");
        }
        List<ProduceTrackHeadTemplate> trackHeadTemplates = produceTrackHeadTemplateService.list(queryWrapper);
        ProduceTrackHeadTemplate p = trackHeadTemplates.get(0);
        String sql1 = p.getSheet1();
        String sql2 = p.getSheet1();
        String sql3 = p.getSheet1();
        String sql4 = p.getSheet1();
        String templateFileId = p.getFileId();
        Attachment attach = systemServiceClient.attachment(templateFileId).getData();
        //InputStream inputStream = fastDfsService.downloadFile(attach.getGroupName(),attach.getFastFileId());




        List<List<Map<String, Object>>> sheets = new ArrayList();
        List<Map<String, Object>> list = new ArrayList();
        List<Map<String, Object>> list2 = new ArrayList();
        if(null!=sql1&&sql1.contains("call")) {
             list = (List) jdbcTemplate.execute(
                    new CallableStatementCreator() {
                        public CallableStatement createCallableStatement(Connection con) throws SQLException {
                            // String storedProc = "{call sp_list_table(?,?)}";// 调用的sql
                            String storedProc = String.format(sql1,id);// 调用的sql
                            CallableStatement cs = con.prepareCall(storedProc);
                            cs.setString(1, id);// 设置输入参数的值
                            return cs;
                        }
                    }, new CallableStatementCallback() {
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
        } else if(null!=sql1) {
            list =jdbcTemplate.queryForList(String.format(sql1,id));
        }else if(null!=sql2) {
            list2 =jdbcTemplate.queryForList(String.format(sql2,id));
        }
        else {

        }
        sheets.add(list);
        sheets.add(list2);
        try {
           // byte[] bytes = fastDfsService.downloadFile(attach.getGroupName(), attach.getFastFileId());
           //InputStream  inputStream = new java.io.ByteArrayInputStream(bytes);
            CommonResult<byte[]> result = systemServiceClient.getAttachmentInputStream(templateFileId);
            InputStream  inputStream = new java.io.ByteArrayInputStream(result.getData());
            ExcelUtils.exportExcelOnSheetsData("跟单",inputStream, sheets , rsp);
        }catch (Exception e) {
            System.out.println(e.getMessage());
        }


    }
}
