package com.richfit.mes.produce.controller;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.TrackCertificate;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.service.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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
    public static String TRACK_NO_NULL_MESSAGE = "请选择跟单!";
    public static String SUCCESS_MESSAGE = "操作成功！";
    public static String FAILED_MESSAGE = "操作失败！";

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private TrackCertificateService trackCertificateService;

    @Autowired
    private TrackHeadService trackHeadService;

    @Autowired
    private TrackItemService trackItemService;

    @Autowired
    private StockRecordService stockRecordService;

    @Autowired
    private LineStoreService lineStoreService;

    @ApiOperation(value = "生成合格证", notes = "生成合格证")
    @PostMapping("/certificate")
    public CommonResult<Certificate> addCertificate(@ApiParam(value = "合格证信息") @RequestBody Certificate certificate,
                                                    @ApiParam(value = "分公司") @RequestParam String branchCode) throws Exception {
        if (StringUtils.isNullOrEmpty(certificate.getCertificateNo())) {
            return CommonResult.failed(CERTIFICATE_NO_NULL_MESSAGE);
        }
        if (certificate.getTrackCertificates() == null || certificate.getTrackCertificates().size() == 0) {
            return CommonResult.failed(TRACK_NO_NULL_MESSAGE);
        } else {

            certificate.setBranchCode(branchCode);

            boolean b = certificateService.saveCertificate(certificate);

            return CommonResult.success(certificate);
        }
    }

    @ApiOperation(value = "修改合格证", notes = "修改合格证信息")
    @PutMapping("/certificate")
    public CommonResult<Certificate> updateCertificate(@ApiParam(value = "合格证信息") @RequestBody Certificate certificate,
                                                       @ApiParam(value = "是否变更关联跟单") @RequestParam(required = false) Boolean changeTrack) {
        if (StringUtils.isNullOrEmpty(certificate.getCertificateNo())) {
            return CommonResult.failed(CERTIFICATE_NO_NULL_MESSAGE);
        } else {
            boolean bool = false;
            certificate.setModifyBy(SecurityUtils.getCurrentUser().getUsername());
            certificate.setModifyTime(new Date());
            bool = certificateService.updateById(certificate);

            if (bool) {
                if (changeTrack) {
                    QueryWrapper<TrackCertificate> queryWrapper = new QueryWrapper<TrackCertificate>();
                    queryWrapper.eq("certificate_id", certificate.getId());
                    List<TrackCertificate> result = trackCertificateService.list(queryWrapper);

                    //找出修改合格证时新选择的跟单
                    List<TrackCertificate> insert = certificate.getTrackCertificates().stream().filter(track -> {
                        track.setCertificateType(certificate.getType());
                        track.setCertificateId(certificate.getId());
                        boolean isNotHave = true;
                        for (TrackCertificate trackCertificate : result) {
                            if (trackCertificate.getTiId().equals(track.getTiId())
                                    && trackCertificate.getThId().equals(track.getThId())) {
                                isNotHave = false;
                                break;
                            }
                        }
                        if ("0".equals(certificate.getType())) { //工序合格证
                            if (isNotHave) {
                                TrackItem trackItem = new TrackItem();
                                trackItem.setId(track.getTiId());
                                trackItem.setCertificateNo(certificate.getCertificateNo());
                                trackItemService.updateById(trackItem);
                            }
                        } else if ("1".equals(certificate.getType())) { //完工合格证
                            if (isNotHave) {
                                TrackHead trackHead = new TrackHead();
                                trackHead.setId(track.getThId());
                                trackHead.setCertificateNo(certificate.getCertificateNo());
                                trackHeadService.updateById(trackHead);
                            }
                        }
                        return isNotHave;
                    }).collect(Collectors.toList());
                    trackCertificateService.saveBatch(insert);

                    //找出修改合格证时取消选择的跟单
                    List<String> delete = result.stream().filter(track -> {
                        boolean isHave = false;
                        for (TrackCertificate trackCertificate : certificate.getTrackCertificates()) {
                            if (trackCertificate.getTiId().equals(track.getTiId())
                                    && trackCertificate.getThId().equals(track.getThId())) {
                                isHave = true;
                                break;
                            }
                        }
                        if (!isHave) {
                            if ("0".equals(certificate.getType())) { //工序合格证
                                TrackItem trackItem = new TrackItem();
                                trackItem.setId(track.getTiId());
                                trackItem.setCertificateNo("");
                                trackItemService.updateById(trackItem);
                            } else if ("1".equals(certificate.getType())) { //完工合格证
                                TrackHead trackHead = new TrackHead();
                                trackHead.setId(track.getThId());
                                trackHead.setCertificateNo("");
                                trackHeadService.updateById(trackHead);
                            }
                        }
                        return !isHave;
                    }).map(track -> track.getId()).collect(Collectors.toList());
                    trackCertificateService.removeByIds(delete);
                }

                return CommonResult.success(certificate, SUCCESS_MESSAGE);
            } else {
                return CommonResult.failed(FAILED_MESSAGE);
            }
        }
    }

    @ApiOperation(value = "删除入库信息", notes = "删除入库信息")
    @DeleteMapping("/certificate")
    public CommonResult deleteCertificate(@ApiParam(value = "合格证Ids") @RequestBody List<String> ids) {
        QueryWrapper<TrackCertificate> queryWrapper = new QueryWrapper<TrackCertificate>();
        queryWrapper.in("certificate_id", ids);
        List<TrackCertificate> list = trackCertificateService.list(queryWrapper);
        list.stream().forEach(track -> {
            if ("0".equals(track.getCertificateType())) { //工序合格证
                UpdateWrapper<TrackItem> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("certificate_no", "");
                updateWrapper.eq("id", track.getTiId());
                trackItemService.update(updateWrapper);
            } else if ("1".equals(track.getCertificateType())) { //工序合格证
                UpdateWrapper<TrackHead> updateWrapper = new UpdateWrapper<>();
                updateWrapper.set("certificate_no", "");
                updateWrapper.eq("id", track.getThId());
                trackHeadService.update(updateWrapper);
            }
        });

        boolean bool = certificateService.removeByIds(ids);
        if (bool) {
            return CommonResult.success(true, SUCCESS_MESSAGE);
        } else {
            return CommonResult.failed(FAILED_MESSAGE);
        }
    }

    @ApiOperation(value = "分页查询合格证信息", notes = "根据图号、合格证号、产品编号分页合格证信息")
    @GetMapping("/certificate")
    public CommonResult<IPage<Certificate>> selectCertificate(@ApiParam(value = "创建时间(起)") @RequestParam(required = false) String startDate,
                                                              @ApiParam(value = "创建时间(止)") @RequestParam(required = false) String endDate,
                                                              @ApiParam(value = "合格证Id") @RequestParam(required = false) String id,
                                                              @ApiParam(value = "图号") @RequestParam(required = false) String drawingNo,
                                                              @ApiParam(value = "合格证号") @RequestParam(required = false) String certificateNo,
                                                              @ApiParam(value = "产品编号") @RequestParam(required = false) String productNo,
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

        if (!StringUtils.isNullOrEmpty(id)) {
            queryWrapper.eq("pc.id", id);
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
        queryWrapper.apply("pc.id = track.certificate_id");
        queryWrapper.eq("pc.tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        queryWrapper.eq("pc.branch_code", branchCode);
        return CommonResult.success(certificateService.selectCertificate(new Page<Certificate>(page, limit), queryWrapper), SUCCESS_MESSAGE);
    }

    @ApiOperation(value = "查询合格证相关跟单", notes = "根据合格证ID查询合格证相关跟单")
    @GetMapping("/certificate/track")
    public CommonResult<List<TrackCertificate>> selectTrackCertificate(@ApiParam(value = "页码") String certificateId) {
        QueryWrapper<TrackCertificate> queryWrapper = new QueryWrapper<TrackCertificate>();
        if (!StringUtils.isNullOrEmpty(certificateId)) {
            queryWrapper.eq("certificate_id", certificateId);
        }
        return CommonResult.success(trackCertificateService.list(queryWrapper), SUCCESS_MESSAGE);
    }
}
