package com.richfit.mes.produce.service.print;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.core.utils.ExcelUtils;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.ProduceInspectionRecordCard;
import com.richfit.mes.common.model.produce.ProduceTrackHeadTemplate;
import com.richfit.mes.produce.dao.quality.ProduceInspectionRecordCardMapper;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.service.CertificateService;
import com.richfit.mes.produce.service.ProduceTrackHeadTemplateService;
import com.richfit.mes.produce.utils.TemplateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhiqiang.lu
 * @date 2022.8.25
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class TemplateServiceImpl extends ServiceImpl<ProduceInspectionRecordCardMapper, ProduceInspectionRecordCard> implements TemplateService {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Resource
    private SystemServiceClient systemServiceClient;

    @Resource
    private CertificateService certificateService;

    @Resource
    private ProduceTrackHeadTemplateService produceTrackHeadTemplateService;

    @Override
    public File certById(String id, String path) throws Exception {
        Certificate certificate = certificateService.getById(id);
        return cert(certificate, path);
    }

    @Override
    public File certByNo(String certNo, String branchCode, String path) throws Exception {
        QueryWrapper<Certificate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("certificate_no", "certNo");
        queryWrapper.eq("branch_code", branchCode);
        List<Certificate> certificateList = certificateService.list(queryWrapper);
        if (certificateList.isEmpty()) {
            return null;
        }
        return cert(certificateList.get(0), path);
    }

    private File cert(Certificate certificate, String path) throws Exception {
        //当前机构配置合格证模板判断
        QueryWrapper<ProduceTrackHeadTemplate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("type", "1");
        queryWrapper.eq("branch_code", certificate.getBranchCode());
        List<ProduceTrackHeadTemplate> trackHeadTemplates = produceTrackHeadTemplateService.list(queryWrapper);
        if (trackHeadTemplates.isEmpty()) {
            throw new GlobalException("当前机构未配置合格证模板", ResultCode.FAILED);
        }

        //获取合格证模板文件流
        ProduceTrackHeadTemplate p = trackHeadTemplates.get(0);
        CommonResult<byte[]> result = systemServiceClient.getAttachmentInputStream(p.getFileId());

        String fileName = path + "/" + certificate.getCertificateNo() + "_合格证";
        List<List<Map<String, Object>>> sheets = new ArrayList();
        // 根据配置SQL，获取SHEET1、2、3表数据
        sheets.add(TemplateUtil.getDataList(certificate.getId(), p.getSheet1(), jdbcTemplate));
        sheets.add(TemplateUtil.getDataList(certificate.getId(), p.getSheet2(), jdbcTemplate));
        sheets.add(TemplateUtil.getDataList(certificate.getId(), p.getSheet3(), jdbcTemplate));
        // 生成EXCEL文件，并输出文件流
        try {
            InputStream inputStream = new java.io.ByteArrayInputStream(result.getData());
            ExcelUtils.exportExcelToFile(fileName, inputStream, sheets);
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }
        return new File(fileName);
    }
}
