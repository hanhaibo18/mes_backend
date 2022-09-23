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

    /**
     * 功能描述: 通过合格证id生成合格证文件
     *
     * @param id   合格证id
     * @param path 生成文件路径
     * @return File 合格证文件
     * @Author: zhiqiang.lu
     * @Date: 2022/9/23
     */
    @Override
    public File certById(String id, String path) throws Exception {
        //通过id查询合格证信息
        Certificate certificate = certificateService.getById(id);
        return cert(certificate, path);
    }

    /**
     * 功能描述: 通过合格证序号，工厂代码，生成合格证文件
     *
     * @param certNo     合格证号
     * @param branchCode 工厂代码
     * @param path       生成文件路径
     * @return File 合格证文件
     * @Author: zhiqiang.lu
     * @Date: 2022/9/23
     */
    @Override
    public File certByNo(String certNo, String branchCode, String path) throws Exception {
        //通过合格证号，工厂代码查询合格证信息
        QueryWrapper<Certificate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("certificate_no", certNo);
        queryWrapper.eq("branch_code", branchCode);
        List<Certificate> certificateList = certificateService.list(queryWrapper);
        if (certificateList.isEmpty()) {
            return null;
        }
        return cert(certificateList.get(0), path);
    }

    /**
     * 功能描述: 通过合格证序号，工厂代码，生成合格证文件
     *
     * @param certificate 合格证信息
     * @param path        生成文件路径
     * @return File 合格证文件
     * @Author: zhiqiang.lu
     * @Date: 2022/9/23
     */
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

        //定义合格证的名称
        String fileName = path + "/" + certificate.getCertificateNo() + "_合格证";

        //创建需要导入sheet的内容
        List<List<Map<String, Object>>> sheets = new ArrayList();
        // 根据配置SQL，获取SHEET1、2、3表数据
        sheets.add(TemplateUtil.getDataList(certificate.getId(), p.getSheet1(), jdbcTemplate));
        sheets.add(TemplateUtil.getDataList(certificate.getId(), p.getSheet2(), jdbcTemplate));
        sheets.add(TemplateUtil.getDataList(certificate.getId(), p.getSheet3(), jdbcTemplate));

        // 使用工具类生成EXCEL文件，并输出文件流
        InputStream inputStream = new java.io.ByteArrayInputStream(result.getData());
        ExcelUtils.exportExcelToFile(fileName, inputStream, sheets);
        return new File(fileName);
    }
}
