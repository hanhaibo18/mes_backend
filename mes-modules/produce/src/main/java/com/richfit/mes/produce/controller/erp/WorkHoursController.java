package com.richfit.mes.produce.controller.erp;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.TrackCertificate;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.util.DrawingNoUtil;
import com.richfit.mes.common.model.util.OrderUtil;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.entity.CertQueryDto;
import com.richfit.mes.produce.service.*;
import com.richfit.mes.produce.service.bsns.CertAdditionalBsns;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import static com.richfit.mes.produce.service.bsns.AbstractCertAdditionalBsns.needScjk;
import static java.util.stream.Collectors.collectingAndThen;
import static java.util.stream.Collectors.toCollection;

/**
 * @author 王瑞
 * @Description 合格证Controller
 */
@Slf4j
@Api(value = "合格证管理", tags = {"合格证管理"})
@RestController
@RequestMapping("/api/produce/work/hours")
public class WorkHoursController {

    @Autowired
    private CertificateService certificateService;

    @Autowired
    private TrackHeadService trackHeadService;

    @Autowired
    public CertAdditionalBsns certAdditionalBsns;

    @ApiOperation(value = "自动推送工时", notes = "自动推送工时")
    @PostMapping("/push")
    public CommonResult push(@ApiParam(value = "合格证", required = true) @RequestBody List<Certificate> certificateList) throws Exception {
        int i = 0;
        String message = "";
        for (Certificate certificate : certificateList) {
            System.out.println("---------------------------------" + i++);
            if (!Certificate.NEXT_OPT_WORK_BOMCO_SC.equals(certificate.getNextOptWork())) {
                message += certificate.getCertificateNo() + ":非生产入库合格证不进行工时推送;";
                //非生产入库合格证不进行工时推送
                break;
            }
            if (Certificate.IS_SENG_WORK_HOUR_1.equals(certificate.getIsSendWorkHour())) {
                //已经推送过工时的不进行工时推送
                break;
            }
            try {
                QueryWrapper<TrackHead> queryWrapperTrackHead = new QueryWrapper<>();
                queryWrapperTrackHead.eq("certificate_no", certificate.getCertificateNo());
                queryWrapperTrackHead.eq("tenant_id", certificate.getCertificateNo());
                List<TrackHead> trackHeadList = trackHeadService.list(queryWrapperTrackHead);
                if (CollectionUtils.isNotEmpty(trackHeadList)) {
                    List<TrackCertificate> trackCertificates = new ArrayList<>();
                    for (TrackHead trackHead : trackHeadList) {
                        TrackCertificate trackCertificate = new TrackCertificate();
                        trackCertificate.setThId(trackHead.getId());
                        trackCertificates.add(trackCertificate);
                    }
                    certificate.setTrackCertificates(trackCertificates);
                    certAdditionalBsns.pushWorkHour(certificate);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return CommonResult.success("");
    }

    @ApiOperation(value = "自动推送工时", notes = "自动推送工时")
    @GetMapping("/auto/push")
    public void autoPush() throws Exception {
        QueryWrapper<Certificate> queryWrapper = new QueryWrapper<>();
        queryWrapper.ne("is_send_work_hour", "1");
        queryWrapper.eq("next_opt_work", "BOMCO_SC");
        queryWrapper.eq("type", "1");
        List<Certificate> certificateList = certificateService.list(queryWrapper);
        int i = 0;
        for (Certificate certificate : certificateList) {
            System.out.println("---------------------------------" + i++);
            try {
                QueryWrapper<TrackHead> queryWrapperTrackHead = new QueryWrapper<>();
                queryWrapperTrackHead.eq("certificate_no", certificate.getCertificateNo());
                queryWrapperTrackHead.eq("tenant_id", certificate.getCertificateNo());
                List<TrackHead> trackHeadList = trackHeadService.list(queryWrapperTrackHead);
                if (CollectionUtils.isNotEmpty(trackHeadList)) {
                    List<TrackCertificate> trackCertificates = new ArrayList<>();
                    for (TrackHead trackHead : trackHeadList) {
                        TrackCertificate trackCertificate = new TrackCertificate();
                        trackCertificate.setThId(trackHead.getId());
                        trackCertificates.add(trackCertificate);
                    }
                    certificate.setTrackCertificates(trackCertificates);
                    certAdditionalBsns.pushWorkHour(certificate);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
