package com.richfit.mes.produce.controller;

import cn.hutool.core.util.StrUtil;
import cn.hutool.poi.excel.ExcelUtil;
import cn.hutool.poi.excel.ExcelWriter;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.PdmBom;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.TrackCertificate;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.CertQueryDto;
import com.richfit.mes.produce.service.CertificateService;
import com.richfit.mes.produce.service.TrackCertificateService;
import com.richfit.mes.produce.service.TrackHeadService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.Adler32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * @author 王瑞
 * @Description 合格证Controller
 */
@Slf4j
@Api("合格证管理")
@RestController
@RequestMapping("/api/produce/certificate")
public class CertificateController {

    public static String CERTIFICATE_NO_NULL_MESSAGE = "合格证编号不能为空!";
    public static String CERTIFICATE_NO_EXIST_MESSAGE = "合格证编号已存在,不能重复!";
    public static String TRACK_NO_NULL_MESSAGE = "请选择跟单!";
    public static String SUCCESS_MESSAGE = "操作成功！";
    public static String FAILED_MESSAGE = "操作失败！";

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private TrackCertificateService trackCertificateService;

    @Autowired
    private TrackHeadService trackHeadService;

    @ApiOperation(value = "生成合格证", notes = "生成合格证")
    @PostMapping("/certificate")
    public CommonResult<Certificate> addCertificate(@ApiParam(value = "合格证信息") @RequestBody Certificate certificate) throws Exception {
        if (StringUtils.isNullOrEmpty(certificate.getCertificateNo())) {
            return CommonResult.failed(CERTIFICATE_NO_NULL_MESSAGE);
        }
        if (certificate.getTrackCertificates() == null || certificate.getTrackCertificates().size() == 0) {
            return CommonResult.failed(TRACK_NO_NULL_MESSAGE);
        }
        if (certificateService.certNoExits(certificate.getCertificateNo(), certificate.getBranchCode())) {
            return CommonResult.failed(CERTIFICATE_NO_EXIST_MESSAGE);
        } else {

            certificateService.saveCertificate(certificate);

            return CommonResult.success(certificate);
        }
    }

    @ApiOperation(value = "修改合格证", notes = "修改合格证信息")
    @PutMapping("/certificate")
    public CommonResult<Certificate> updateCertificate(@ApiParam(value = "合格证信息") @RequestBody Certificate certificate,
                                                       @ApiParam(value = "是否变更关联跟单") @RequestParam(required = false) Boolean changeTrack) throws Exception {
        if (StringUtils.isNullOrEmpty(certificate.getCertificateNo())) {
            return CommonResult.failed(CERTIFICATE_NO_NULL_MESSAGE);
        } else {
            boolean bool = false;
            certificate.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            certificate.setModifyTime(new Date());

            certificateService.updateCertificate(certificate, changeTrack);

            return CommonResult.success(certificate, SUCCESS_MESSAGE);
        }
    }

    @PostMapping("/export")
    @ApiOperation(value = "导出合格证", notes = "根据模板导出合格证")
    public void exportBom(@RequestBody List<String> ids, HttpServletResponse rsp) throws IOException {
        //压缩输出流
        ZipOutputStream zos = null;
        try {
            File file = ResourceUtils.getFile("classpath:excel/合格证模板.xls");
            ExcelWriter writer = ExcelUtil.getReader(file).getWriter();
            rsp.reset();
            rsp.setCharacterEncoding("UTF-8");
            rsp.setContentType("application/zip");
            //默认Excel名称
            rsp.setHeader("Content-Disposition", String.format("attachment;filename=%s", URLEncoder.encode("合格证压缩包.zip", "UTF-8")));

            // 用于将数据压缩成Zip文件格式
            zos = new ZipOutputStream(rsp.getOutputStream());
            List<Certificate> result = certificateService.listByIds(ids);
            ZipEntry ze = null;
            for (Certificate c : result) {
                HSSFWorkbook wb = (HSSFWorkbook) writer.getWorkbook();
                HSSFSheet sheet = wb.getSheet("Sheet1");
                List<TrackHead> heads = trackHeadService.queryListByCertId(c.getId());

                int index = 2;
                for (TrackHead head: heads) {
                    HSSFRow row = sheet.getRow(index);
                    if (row == null) {
                        row = sheet.createRow(index);
                    }
                    // 设置行数据
                    setRow(row, head, c);
                    index++;
                }
                ZipEntry z = new ZipEntry("合格证" + c.getCertificateNo() + ".xls");
                zos.putNextEntry(z);
                //写入一个压缩文件
                wb.write(zos);
            }
            zos.flush();
        } catch (IOException e) {
            log.error(e.getMessage());
        } finally {
            if (zos != null) {
                zos.close();
            }
        }
    }

    private void setRow(HSSFRow row, TrackHead head, Certificate c){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        // 合格证号	生产单位	工作号	产品名称	零部件名称	材质	代用料	零部件图号	合格数量	试棒数量	本工序	下工序	检验员	检验日期	单重	预留1	产品编号	炉号	DOP标识号	车间订单号
        row.createCell(0).setCellValue(c.getCertificateNo());

        row.createCell(1).setCellValue(head != null && head.getBranchCode() != null ? head.getBranchCode() : "");
        row.createCell(2).setCellValue(head != null && head.getWorkNo() != null ? head.getWorkNo() : "");
        row.createCell(3).setCellValue(head != null && head.getProductName() != null ? head.getProductName() : "");
        row.createCell(4).setCellValue(head != null && head.getMaterialName() != null ? head.getMaterialName() : "");
        row.createCell(5).setCellValue(head != null && head.getTexture() != null ? head.getTexture() : "");
        row.createCell(6).setCellValue(head != null && head.getReplaceMaterial() != null ? head.getReplaceMaterial() : "");
        row.createCell(7).setCellValue(head != null && head.getMaterialNo() != null ? head.getMaterialNo() : "");
        row.createCell(8).setCellValue(head != null ? head.getNumberComplete() : 0);
        row.createCell(9).setCellValue(head != null && head.getTestBarNumber() != null? head.getTestBarNumber().toString() : "");
        row.createCell(14).setCellValue(head != null && head.getWeight() != null ? head.getWeight().toString() : "");
        row.createCell(16).setCellValue(head != null && head.getProductNo() != null ? head.getProductNo() : "");
        row.createCell(17).setCellValue(head != null && head.getBatchNo() != null ? head.getBatchNo() : "");
        row.createCell(19).setCellValue(head != null && head.getProductionOrder() != null ? head.getProductionOrder() : "");

        row.createCell(10).setCellValue(c.getOptName());
        row.createCell(11).setCellValue(c.getNextOpt());
        row.createCell(12).setCellValue(c.getCheckName());

        row.createCell(13).setCellValue(c.getCheckTime() != null ? sdf.format(c.getCheckTime()) : "");
        row.createCell(15).setCellValue("");
        row.createCell(18).setCellValue("");
    }

    @ApiOperation(value = "删除合格证信息", notes = "删除合格证信息")
    @DeleteMapping("/certificate")
    public CommonResult deleteCertificate(@ApiParam(value = "合格证Ids") @RequestBody List<String> ids) throws Exception {

        certificateService.delCertificate(ids);

        return CommonResult.success(true, SUCCESS_MESSAGE);

    }

    @ApiOperation(value = "分页查询合格证信息", notes = "根据图号、合格证号、产品编号分页合格证信息")
    @GetMapping("/certificate")
    public CommonResult<IPage<Certificate>> selectCertificate(@ApiParam(value = "创建时间(起)") @RequestParam(required = false) String startDate,
                                                              @ApiParam(value = "创建时间(止)") @RequestParam(required = false) String endDate,
                                                              @ApiParam(value = "合格证Id") @RequestParam(required = false) String id,
                                                              @ApiParam(value = "图号") @RequestParam(required = false) String drawingNo,
                                                              @ApiParam(value = "合格证号") @RequestParam(required = false) String certificateNo,
                                                              @ApiParam(value = "产品编号") @RequestParam(required = false) String productNo,
                                                              @ApiParam(value = "类型") @RequestParam(required = false) String type,
                                                              @ApiParam(value = "来源") @RequestParam(required = false) String origin,
                                                              @ApiParam(value = "排序") @RequestParam(required = false) String order,
                                                              @ApiParam(value = "排序字段") @RequestParam(required = false) String orderCol,
                                                              @ApiParam(value = "分公司") String branchCode,
                                                              @ApiParam(value = "页码") int page,
                                                              @ApiParam(value = "每页条数") int limit) {
        QueryWrapper<Certificate> queryWrapper = new QueryWrapper<Certificate>();

        if (!StringUtils.isNullOrEmpty(startDate)) {
            queryWrapper.ge("check_time", startDate);
        }

        if (!StringUtils.isNullOrEmpty(endDate)) {
            queryWrapper.le("check_time", endDate);
        }

        if (!StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("pc.id", id);
        }
        if (!StringUtils.isNullOrEmpty(type)) {
            queryWrapper.eq("pc.type", type);
        }
        if (!StringUtils.isNullOrEmpty(origin)) {
            queryWrapper.eq("pc.cert_origin", origin);
        }
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            queryWrapper.like("drawing_no", drawingNo);
        }
        if (!StringUtils.isNullOrEmpty(certificateNo)) {
            queryWrapper.like("pc.certificate_no", certificateNo);
        }
        if (!StringUtils.isNullOrEmpty(productNo)) {
            queryWrapper.like("product_no", productNo);
        }
        if (!StringUtils.isNullOrEmpty(orderCol)) {
            if (!StringUtils.isNullOrEmpty(order)) {
                if (order.equals("desc")) {
                    queryWrapper.orderByDesc("pc." + StrUtil.toUnderlineCase(orderCol));
                } else if (order.equals("asc")) {
                    queryWrapper.orderByAsc("pc." + StrUtil.toUnderlineCase(orderCol));
                }
            } else {
                queryWrapper.orderByDesc("pc." + StrUtil.toUnderlineCase(orderCol));
            }
        } else {
            queryWrapper.orderByDesc("pc.modify_time");
        }
//        queryWrapper.apply("pc.id = track.certificate_id");
        queryWrapper.eq("pc.tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.eq("pc.branch_code", branchCode);
        return CommonResult.success(certificateService.selectCertificate(new Page<Certificate>(page, limit), queryWrapper), SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询需要本单位接收的合格证", notes = "根据图号、合格证号查询分页合格证信息")
    @GetMapping("/certificate/need_transfer")
    public CommonResult<IPage<Certificate>> selectCertificateForTransf(@ApiParam(value = "queryDto") CertQueryDto queryDto) {

        return CommonResult.success(certificateService.selectNeedTransferCert(queryDto), SUCCESS_MESSAGE);

    }

    /**
     * 弃用，返回的字段是关系表字段，数据项有限。
     * 推荐使用/api/produce/track_head/track_head/query_by_cert
     *
     * @param certificateId
     * @return
     */
    @ApiOperation(value = "查询合格证相关跟单ID", notes = "根据合格证ID查询合格证相关跟单")
    @GetMapping("/certificate/track")
    @Deprecated
    public CommonResult<List<TrackCertificate>> selectTrackCertificate(@ApiParam(value = "页码") String certificateId) {
        QueryWrapper<TrackCertificate> queryWrapper = new QueryWrapper<TrackCertificate>();
        if (!StringUtils.isNullOrEmpty(certificateId)) {
            queryWrapper.eq("certificate_id", certificateId);
        }
        return CommonResult.success(trackCertificateService.list(queryWrapper), SUCCESS_MESSAGE);
    }


}
