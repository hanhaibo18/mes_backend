package com.richfit.mes.produce.service.erp;

import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.*;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.CertificateMapper;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.ErpServiceClient;
import com.richfit.mes.produce.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * 功能描述:工时管理
 *
 * @Author: zhiqiang.lu
 * @Date: 2023/05/26 16:27
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class WorkHoursServiceImpl extends ServiceImpl<CertificateMapper, Certificate> implements WorkHoursService {

    @Autowired
    TrackHeadService trackHeadService;


    @Autowired
    BaseServiceClient baseServiceClient;


    @Autowired
    TrackItemService trackItemService;

    @Autowired
    ErpServiceClient erpServiceClient;

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
            String erpCode = Objects.requireNonNull(SecurityUtils.getCurrentUser()).getTenantErpCode();
            if (StrUtil.isBlank(erpCode)) {
                throw new Exception(certificate.getCertificateNo() + ":没有找到单位的erpCode;");
            }
            List<Product> list = baseServiceClient.selectProduct(certificate.getTenantId(), certificate.getMaterialNo(), certificate.getDrawingNo(), "3").getData();
            String unit;
            if (CollectionUtils.isNotEmpty(list)) {
                unit = list.get(0).getUnit();
            } else {
                throw new Exception(certificate.getCertificateNo() + ":物料中没有找到成品信息;");
            }
            if (StrUtil.isBlank(unit)) {
                throw new Exception(certificate.getCertificateNo() + ":物料中没有找到成品的单位信息;");
            }
            for (TrackCertificate trackCertificate : certificate.getTrackCertificates()) {
                TrackHead trackHead = trackHeadService.getById(trackCertificate.getThId());
                List<TrackItem> trackItems = trackItemService.queryTrackItemByTrackNo(trackCertificate.getThId());
                CommonResult<Object> commonResult = erpServiceClient.certWorkHourPush(trackItems, erpCode, trackHead.getProductionOrder(), trackHead.getNumber(), unit);
                if (commonResult.getStatus() != ResultCode.SUCCESS.getCode()) {
                    throw new Exception(certificate.getCertificateNo() + ":【" + "跟单号：" + trackHead.getTrackNo() + ":" + commonResult.getMessage() + "】;");
                }
            }
        }
    }
}
