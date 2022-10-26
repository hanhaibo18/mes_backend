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
import com.richfit.mes.produce.utils.TemplateUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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
@Api(value = "跟单模板打印", tags = {"跟单模板打印"})
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
            sheets.add(TemplateUtil.getDataList(id, p.getSheet1(), jdbcTemplate));
            sheets.add(TemplateUtil.getDataList(id, p.getSheet2(), jdbcTemplate));
            sheets.add(TemplateUtil.getDataList(id, p.getSheet3(), jdbcTemplate));
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
            sheets.add(TemplateUtil.getDataList(trackHead.getId(), p.getSheet1(), jdbcTemplate));
            sheets.add(TemplateUtil.getDataList(id, p.getSheet2(), jdbcTemplate));
            sheets.add(TemplateUtil.getDataList(id, p.getSheet3(), jdbcTemplate));

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
            map.put("id", id);
            map.put("branchCode", branchCode);
            map.put("tenantId", SecurityUtils.getCurrentUser().getTenantId());
            List<TrackHead> tractkHeads = trackHeadService.selectTrackFlowList(map);
            list.addAll(tractkHeads);
        }
        int i = 1;
        for (TrackHead trackHead : list) {
            List<List<Map<String, Object>>> sheets = new ArrayList();

            // 根据配置SQL，获取SHEET1、2、3表数据
            sheets.add(TemplateUtil.getDataList(trackHead.getId(), p.getSheet1(), jdbcTemplate));
            sheets.add(TemplateUtil.getDataList(trackHead.getFlowId(), p.getSheet2(), jdbcTemplate));
            sheets.add(TemplateUtil.getDataList(trackHead.getFlowId(), p.getSheet3(), jdbcTemplate));
            // 生成EXCEL文件，并输出文件流
            try {
                // byte[] bytes = fastDfsService.downloadFile(attach.getGroupName(), attach.getFastFileId());
                //InputStream  inputStream = new java.io.ByteArrayInputStream(bytes);

                InputStream inputStream = new java.io.ByteArrayInputStream(result.getData());
                ExcelUtils.exportExcelToFile(file.getAbsolutePath() + "/" + trackHead.getTrackNo() + "_跟单(" + i + ")", inputStream, sheets);
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
            sheets.add(TemplateUtil.getDataList(id, p.getSheet1(), jdbcTemplate));
            sheets.add(TemplateUtil.getDataList(id, p.getSheet2(), jdbcTemplate));
            sheets.add(TemplateUtil.getDataList(id, p.getSheet3(), jdbcTemplate));
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
}
