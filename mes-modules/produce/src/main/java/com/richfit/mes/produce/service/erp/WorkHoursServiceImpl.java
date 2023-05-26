package com.richfit.mes.produce.service.erp;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.ActionMapper;
import com.richfit.mes.produce.dao.CertificateMapper;
import com.richfit.mes.produce.entity.TrackHeadPublicDto;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.ErpServiceClient;
import com.richfit.mes.produce.service.*;
import com.richfit.mes.produce.service.bsns.CertAdditionalBsns;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author 王瑞
 * @Description 操作信息服务
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class WorkHoursServiceImpl extends ServiceImpl<CertificateMapper, Certificate> implements WorkHoursService {


    @Autowired
    private TrackHeadService trackHeadService;


    @Autowired
    public BaseServiceClient baseServiceClient;


    @Autowired
    public TrackItemService trackItemService;

    @Autowired
    public ErpServiceClient erpServiceClient;

    @Override
    public void push(Certificate certificate) throws Exception {
        if (!Certificate.NEXT_OPT_WORK_BOMCO_SC.equals(certificate.getNextOptWork())) {
            throw new Exception(certificate.getCertificateNo() + ":非生产入库合格证不进行工时推送;");
        }
//        if (Certificate.IS_SENG_WORK_HOUR_1.equals(certificate.getIsSendWorkHour())) {
//            throw new Exception(certificate.getCertificateNo() + ":已经推送过工时的不进行工时推送;");
//        }
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
            this.toErp(certificate);
            //更新合格证的状态
            certificate.setIsSendWorkHour("1");
            this.updateById(certificate);
        } else {
            throw new Exception(certificate.getCertificateNo() + ":没有找到该合格证的跟单信息;");
        }
    }

    public void toErp(Certificate certificate) throws Exception {
        //erp工时推送
        if (certificate != null) {
            String erpCode = SecurityUtils.getCurrentUser().getTenantErpCode();
            List<Product> list = baseServiceClient.selectProduct(certificate.getMaterialNo(), certificate.getDrawingNo(), "3").getData();
            String unit = "";
            if (CollectionUtils.isNotEmpty(list)) {
                unit = list.get(0).getUnit();
            } else {
                throw new Exception(certificate.getCertificateNo() + ":物料中没有找到成品信息;");
            }
            for (TrackCertificate trackCertificate : certificate.getTrackCertificates()) {
                TrackHead trackHead = trackHeadService.getById(trackCertificate.getThId());
                List<TrackItem> trackItems = trackItemService.queryTrackItemByTrackNo(trackCertificate.getThId());
                CommonResult b = erpServiceClient.certWorkHourPush(trackItems, erpCode, trackHead.getProductionOrder(), trackHead.getNumber(), unit);
                if (b.getStatus() != 200) {
                    throw new Exception(certificate.getCertificateNo() + ":" + "跟单号：" + trackHead.getTrackNo() + ":" + b.getMessage() + ";");
                }
            }
        }
    }
}
