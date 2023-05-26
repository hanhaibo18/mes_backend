package com.richfit.mes.produce.service.erp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.ActionMapper;
import com.richfit.mes.produce.dao.CertificateMapper;
import com.richfit.mes.produce.entity.TrackHeadPublicDto;
import com.richfit.mes.produce.service.*;
import com.richfit.mes.produce.service.bsns.CertAdditionalBsns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 王瑞
 * @Description 操作信息服务
 */
@Service
public class WorkHoursServiceImpl extends ServiceImpl<CertificateMapper, Certificate> implements WorkHoursService {


    @Autowired
    private TrackHeadService trackHeadService;

    @Autowired
    public CertAdditionalBsns certAdditionalBsns;

    @Override
    public void push(Certificate certificate) throws Exception {
        if (!Certificate.NEXT_OPT_WORK_BOMCO_SC.equals(certificate.getNextOptWork())) {
            throw new Exception(certificate.getCertificateNo() + ":非生产入库合格证不进行工时推送;");
        }
        if (Certificate.IS_SENG_WORK_HOUR_1.equals(certificate.getIsSendWorkHour())) {
            throw new Exception(certificate.getCertificateNo() + ":已经推送过工时的不进行工时推送;");
        }
        QueryWrapper<TrackHead> queryWrapperTrackHead = new QueryWrapper<>();
        queryWrapperTrackHead.eq("certificate_no", certificate.getCertificateNo());
        queryWrapperTrackHead.eq("tenant_id", certificate.getTenantId());
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
        } else {
            throw new Exception(certificate.getCertificateNo() + ":没有找到该合格证的跟单信息;");
        }
    }
}
