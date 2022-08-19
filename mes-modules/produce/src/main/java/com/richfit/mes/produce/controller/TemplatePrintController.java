package com.richfit.mes.produce.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.base.BaseController;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.ProduceTrackHeadTemplate;
import com.richfit.mes.common.model.produce.TrackFlow;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.CertificateService;
import com.richfit.mes.produce.service.ProduceTrackHeadTemplateService;
import com.richfit.mes.produce.service.TrackHeadFlowService;
import com.richfit.mes.produce.service.TrackHeadService;
import com.richfit.mes.produce.utils.FilesUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.CallableStatementCallback;
import org.springframework.jdbc.core.CallableStatementCreator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
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

    @Autowired
    private TrackHeadFlowService trackHeadFlowService;

    @Autowired
    private CertificateService certificateService;

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

            List<List<Map<String, Object>>> sheets = new ArrayList();

            // 根据配置SQL，获取SHEET1、2、3表数据
            sheets.add(getList(id, p.getSheet1()));
            sheets.add(getList(id, p.getSheet2()));
            sheets.add(getList(id, p.getSheet3()));

            // 生成EXCEL文件，并输出文件流
            try {
                // byte[] bytes = fastDfsService.downloadFile(attach.getGroupName(), attach.getFastFileId());
                //InputStream  inputStream = new java.io.ByteArrayInputStream(bytes);
                String templateFileId = p.getFileId();
                CommonResult<byte[]> result = systemServiceClient.getAttachmentInputStream(templateFileId);
                InputStream inputStream = new java.io.ByteArrayInputStream(result.getData());
                ExcelUtils.exportExcelOnSheetsData("跟单", inputStream, sheets, rsp);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param id
     * @param branchCode
     * @param rsp
     * @throws IOException
     */
    @ApiOperation(value = "根据flowId生成跟单模板EXCEL", notes = "按跟单模板编码生成跟单模板EXCEL")
    @GetMapping("/query/by_flow_id")
    public void getByFlowId(@ApiParam(value = "flowId", required = true) @RequestParam String id,
                                  @ApiParam(value = "工厂代码") @RequestParam(required = false) String branchCode,
                                  @ApiIgnore HttpServletResponse rsp) throws IOException {
        try {
            // 获取跟单
            TrackFlow byId = trackHeadFlowService.getById(id);
            TrackHead trackHead = trackHeadService.getById(byId.getTrackHeadId());
            QueryWrapper<ProduceTrackHeadTemplate> queryWrapper = new QueryWrapper<ProduceTrackHeadTemplate>();
            queryWrapper.like("template_code", trackHead.getTemplateCode());
            if (!StringUtils.isNullOrEmpty(branchCode)) {
                queryWrapper.like("branch_code", branchCode);
            }
            //获取跟单模板配置信息
            List<ProduceTrackHeadTemplate> trackHeadTemplates = produceTrackHeadTemplateService.list(queryWrapper);
            ProduceTrackHeadTemplate p = trackHeadTemplates.get(0);

            List<List<Map<String, Object>>> sheets = new ArrayList();

            // 根据配置SQL，获取SHEET1、2、3表数据
            sheets.add(getList(trackHead.getId(), p.getSheet1()));
            sheets.add(getList(id, p.getSheet2()));
            sheets.add(getList(id, p.getSheet3()));

            // 生成EXCEL文件，并输出文件流
            try {
                // byte[] bytes = fastDfsService.downloadFile(attach.getGroupName(), attach.getFastFileId());
                //InputStream  inputStream = new java.io.ByteArrayInputStream(bytes);
                String templateFileId = p.getFileId();
                CommonResult<byte[]> result = systemServiceClient.getAttachmentInputStream(templateFileId);
                InputStream inputStream = new java.io.ByteArrayInputStream(result.getData());
                ExcelUtils.exportExcelOnSheetsData("跟单", inputStream, sheets, rsp);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @ApiOperation(value = "批量导出bom跟单excel", notes = "按flowId集合生成bom跟单EXCEL,按压缩包下载")
    @PostMapping("/batch")
    public void printBatch(@ApiParam(value = "flowIds", required = true) @RequestBody List<String> ids,
                          @ApiParam(value = "工厂代码") @RequestParam(required = true) String branchCode,
                          @ApiIgnore HttpServletResponse rsp) throws Exception {

        QueryWrapper<ProduceTrackHeadTemplate> queryWrapper = new QueryWrapper<ProduceTrackHeadTemplate>();
        queryWrapper.eq("type", "0");
        queryWrapper.eq("branch_code", branchCode);

        //获取跟单模板配置信息
        List<ProduceTrackHeadTemplate> trackHeadTemplates = produceTrackHeadTemplateService.list(queryWrapper);
        if (trackHeadTemplates.isEmpty()) {
            throw new GlobalException("当前机构未配置跟单模板", ResultCode.FAILED);
        }
        ProduceTrackHeadTemplate p = trackHeadTemplates.get(0);

        String templateFileId = p.getFileId();
        CommonResult<byte[]> result = systemServiceClient.getAttachmentInputStream(templateFileId);

        File file = FilesUtil.createRandomTempDirectory();

        List<TrackHead> list = new ArrayList<>();
        for (String id : ids) {
            Map<String, String> map = new HashMap<>();
            map.put("id",id);
            map.put("branchCode", branchCode);
            map.put("tenantId", SecurityUtils.getCurrentUser().getTenantId());
            List<TrackHead> tractkHeads = trackHeadService.selectTrackFlowList(map);
            list.addAll(tractkHeads);
        }
        int i=1;
        for (TrackHead trackHead : list) {
            List<List<Map<String, Object>>> sheets = new ArrayList();

            // 根据配置SQL，获取SHEET1、2、3表数据
            sheets.add(getList(trackHead.getId(), p.getSheet1()));
            sheets.add(getList(trackHead.getFlowId(), p.getSheet2()));
            sheets.add(getList(trackHead.getFlowId(), p.getSheet3()));
            // 生成EXCEL文件，并输出文件流
            try {
                // byte[] bytes = fastDfsService.downloadFile(attach.getGroupName(), attach.getFastFileId());
                //InputStream  inputStream = new java.io.ByteArrayInputStream(bytes);

                InputStream inputStream = new java.io.ByteArrayInputStream(result.getData());
                ExcelUtils.exportExcelToFile(file.getAbsolutePath() + "/" + trackHead.getTrackNo() + "_跟单("+i+")", inputStream, sheets);
                i++;
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        //打包压缩包，下载输出
        try {
            FilesUtil.zip(file.getAbsolutePath());
            FilesUtil.downloads(rsp, file.getAbsolutePath() + ".zip");
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            file.delete();
            new File(file.getAbsolutePath() + ".zip").delete();
        }

    }


    @ApiOperation(value = "批量导出合格证excel", notes = "按合格证id集合生成合格证EXCEL,按压缩包下载")
    @PostMapping("/cert")
    public void printCert(@ApiParam(value = "合格证ids", required = true) @RequestBody List<String> ids,
                          @ApiParam(value = "工厂代码") @RequestParam(required = true) String branchCode,
                          @ApiIgnore HttpServletResponse rsp) throws IOException {

        QueryWrapper<ProduceTrackHeadTemplate> queryWrapper = new QueryWrapper<ProduceTrackHeadTemplate>();
        queryWrapper.eq("type", "1");
        queryWrapper.eq("branch_code", branchCode);

        //获取合格证模板配置信息
        List<ProduceTrackHeadTemplate> trackHeadTemplates = produceTrackHeadTemplateService.list(queryWrapper);
        if (trackHeadTemplates.isEmpty()) {
            throw new GlobalException("当前机构未配置合格证模板", ResultCode.FAILED);
        }
        ProduceTrackHeadTemplate p = trackHeadTemplates.get(0);

        String templateFileId = p.getFileId();
        CommonResult<byte[]> result = systemServiceClient.getAttachmentInputStream(templateFileId);

        File file = FilesUtil.createRandomTempDirectory();

        for (String id : ids) {

            Certificate certificate = certificateService.getById(id);

            List<List<Map<String, Object>>> sheets = new ArrayList();

            // 根据配置SQL，获取SHEET1、2、3表数据
            sheets.add(getList(id, p.getSheet1()));
            sheets.add(getList(id, p.getSheet2()));
            sheets.add(getList(id, p.getSheet3()));
            // 生成EXCEL文件，并输出文件流
            try {
                // byte[] bytes = fastDfsService.downloadFile(attach.getGroupName(), attach.getFastFileId());
                //InputStream  inputStream = new java.io.ByteArrayInputStream(bytes);

                InputStream inputStream = new java.io.ByteArrayInputStream(result.getData());
                ExcelUtils.exportExcelToFile(file.getAbsolutePath() + "/" + certificate.getCertificateNo() + "_合格证", inputStream, sheets);
            } catch (Exception e) {
                log.error(e.getMessage());
            }
        }

        //打包压缩包，下载输出
        try {
            FilesUtil.zip(file.getAbsolutePath());
            FilesUtil.downloads(rsp, file.getAbsolutePath() + ".zip");

        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            file.delete();
            new File(file.getAbsolutePath() + ".zip").delete();
        }

    }


    private List<Map<String, Object>> getList(String id, String sql) {
        List<Map<String, Object>> list = new ArrayList();

        if (null != sql && sql.contains("call")) {
            // 如果包含CALL，则只需执行存储过程
            list = getExecute(id, sql);
        } else if (null != sql) {
            // 如果不包含CALL，则执行SQL 查询
            list = jdbcTemplate.queryForList(String.format(sql, id));
        }
        return list;
    }

    /**
     * 执行存储过程
     *
     * @param id
     * @param sql1
     * @return
     */
    private List getExecute(String id, String sql1) {
        return (List) jdbcTemplate.execute(
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
    }
}
