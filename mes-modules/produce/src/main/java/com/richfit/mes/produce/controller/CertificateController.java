package com.richfit.mes.produce.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.TrackCertificate;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.CertQueryDto;
import com.richfit.mes.produce.service.CertificateService;
import com.richfit.mes.produce.service.TrackCertificateService;
import com.richfit.mes.produce.service.TrackHeadService;
import com.richfit.mes.produce.service.TrackItemService;
import com.richfit.mes.produce.utils.Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * @author 王瑞
 * @Description 合格证Controller
 */
@Slf4j
@Api(value = "合格证管理", tags = {"合格证管理"})
@RestController
@RequestMapping("/api/produce/certificate")
public class CertificateController {

    public static String CERTIFICATE_NO_NULL_MESSAGE = "合格证编号不能为空!";
    public static String CERTIFICATE_NO_EXIST_MESSAGE = "合格证编号已存在,不能重复!";
    public static String TRACK_NO_NULL_MESSAGE = "请选择跟单!";
    public static String SUCCESS_MESSAGE = "操作成功！";
    public static String FAILED_MESSAGE = "操作失败！";
    public static String FAILED_ON_COMPLETE = "工序未完成，不允许开具合格证";

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private TrackCertificateService trackCertificateService;

    @Autowired
    private TrackHeadService trackHeadService;
    @Autowired
    public TrackItemService trackItemService;

    @ApiOperation(value = "生成合格证", notes = "生成合格证")
    @PostMapping("/certificate")
    public CommonResult<Certificate> autoCertificate(@ApiParam(value = "合格证信息") @RequestBody Certificate certificate) throws Exception {
        if (StringUtils.isNullOrEmpty(certificate.getCertificateNo())) {
            return CommonResult.failed(CERTIFICATE_NO_NULL_MESSAGE);
        }
        if (certificate.getTrackCertificates() == null || certificate.getTrackCertificates().size() == 0) {
            return CommonResult.failed(TRACK_NO_NULL_MESSAGE);
        }
        if (certificateService.certNoExits(certificate.getCertificateNo(), certificate.getBranchCode())) {
            return CommonResult.failed(CERTIFICATE_NO_EXIST_MESSAGE);
        } else {
            //检查当前工序之前有没有未完成的工序
            this.checkBefore(certificate);
            //重写拼接产品编号
            certificate.setProductNoContinuous(Utils.productNoContinuous(certificate.getProductNo()));
            certificateService.saveCertificate(certificate);
            return CommonResult.success(certificate);
        }
    }

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
            //检查当前工序之前有没有未完成的工序
            this.checkBefore(certificate);
            certificateService.saveCertificate(certificate);
            return CommonResult.success(certificate);
        }
    }

    private CommonResult<Certificate> checkBefore(Certificate certificate) {
        //判断当前工序之前有没有未完成的工序
        List<String> ids = certificate.getTrackCertificates().stream().map(TrackCertificate::getThId).collect(Collectors.toList());
        QueryWrapper<TrackItem> queryWrapper = new QueryWrapper<TrackItem>();
        queryWrapper.in("track_head_id", ids);
        List<TrackItem> list = trackItemService.list(queryWrapper);
        //去重操作(工序号+工序名 都一样认为重复)
        ArrayList<TrackItem> collect = list.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(trackItem -> trackItem.getOptName() + "-" + trackItem.getOptNo()))), ArrayList::new));
        List<TrackItem> current = collect.stream().filter(x -> x.getIsCurrent() == 1).collect(Collectors.toList());
        //当前工序的前工序 并且最终完成状态 is_final_complete 不为1 的
        List<TrackItem> before = collect.stream().filter(x -> x.getOptSequence() < current.get(0).getOptSequence() & !"1".equals(x.getIsFinalComplete())).collect(Collectors.toList());
        if (CollectionUtils.isNotEmpty(before)) {
            return CommonResult.failed(before.get(0).getOptName() + " " + FAILED_ON_COMPLETE);
        }
        return null;
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
            DrawingNoUtil.queryLike(queryWrapper, "drawing_no", drawingNo);
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
        IPage<Certificate> certificateIPage = certificateService.selectCertificate(new Page<Certificate>(page, limit), queryWrapper);
        List<Certificate> collect = certificateIPage.getRecords().stream().collect(collectingAndThen(
                toCollection(() -> new TreeSet<>(Comparator.comparing(Certificate::getId))), ArrayList::new)
        );
        certificateIPage.setRecords(collect);
        return CommonResult.success(certificateIPage, SUCCESS_MESSAGE);
    }


    @ApiOperation(value = "分页查询合格证信息", notes = "根据图号、合格证号、产品编号分页合格证信息")
    @GetMapping("/page")
    public CommonResult<PageInfo<Certificate>> page(@ApiParam(value = "创建时间(起)") @RequestParam(required = false) String startDate,
                                                    @ApiParam(value = "创建时间(止)") @RequestParam(required = false) String endDate,
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
            queryWrapper.ge("create_time", startDate);
        }
        if (!StringUtils.isNullOrEmpty(endDate)) {
            queryWrapper.le("create_time", endDate);
        }
        if (!StringUtils.isNullOrEmpty(type)) {
            queryWrapper.eq("type", type);
        }
        if (!StringUtils.isNullOrEmpty(origin)) {
            queryWrapper.eq("cert_origin", origin);
        }
        if (!StringUtils.isNullOrEmpty(drawingNo)) {
            DrawingNoUtil.queryLike(queryWrapper, "drawing_no", drawingNo);
        }
        if (!StringUtils.isNullOrEmpty(certificateNo)) {
            queryWrapper.like("certificate_no", certificateNo);
        }
        if (!StringUtils.isNullOrEmpty(productNo)) {
            queryWrapper.like("product_no", productNo);
        }
        OrderUtil.query(queryWrapper, orderCol, order);
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.eq("branch_code", branchCode);
        PageHelper.startPage(page, limit);
        List<Certificate> certificateList = certificateService.list(queryWrapper);
        PageInfo<Certificate> trackFlowPage = new PageInfo(certificateList);
        return CommonResult.success(trackFlowPage, SUCCESS_MESSAGE);
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

    @ApiOperation(value = "合格证号查询合格证信息", notes = "通过合格证号查询合格证信息")
    @GetMapping("/select/certificate_no")
    public CommonResult<List<Certificate>> selectByCertificateNo(@ApiParam(value = "合格证号码", required = true) @RequestParam String certificateNo) {
        QueryWrapper<Certificate> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(!StrUtil.isBlank(certificateNo), "certificate_no", certificateNo);
        return CommonResult.success(certificateService.list(queryWrapper), SUCCESS_MESSAGE);

    }
}
