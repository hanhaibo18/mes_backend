package com.richfit.mes.produce.service.bsns;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.TrackCertificate;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.ErpServiceClient;
import com.richfit.mes.produce.provider.WmsServiceClient;
import com.richfit.mes.produce.service.CertificateService;
import com.richfit.mes.produce.service.TrackHeadService;
import com.richfit.mes.produce.service.TrackItemService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/1 16:26
 */
@Service
@Slf4j
public class CertAdditionalBsnsImpl extends AbstractCertAdditionalBsns {

    @Autowired
    WmsServiceClient wmsServiceClient;

    @Autowired
    ErpServiceClient erpServiceClient;

    @Autowired
    TrackItemService trackItemService;

    @Autowired
    TrackHeadService trackHeadService;

    @Autowired
    BaseServiceClient baseServiceClient;

    @Autowired
    CertificateService certificateService;

    @Override
    public void doAdditionalBsns(Certificate certificate) {

        if (needScjk(certificate)) {

            wmsServiceClient.sendJkInfo(certificate);

            pushWorkHour(certificate);
        }
    }

    @Override
    public void pushWorkHour(Certificate certificate) {
        if(certificate != null) {
            //erp工时推送
            String erpCode = SecurityUtils.getCurrentUser().getTenantErpCode();

            List<Product> list = baseServiceClient.selectProduct(certificate.getMaterialNo(), certificate.getDrawingNo(), "3").getData();
            String unit = "";
            if (list.size() > 0) {
                unit = list.get(0).getUnit();
            }

            for (TrackCertificate trackCertificate : certificate.getTrackCertificates()) {

                TrackHead trackHead = trackHeadService.getById(trackCertificate.getThId());

                List<TrackItem> trackItems = trackItemService.queryTrackItemByTrackNo(trackCertificate.getThId());

                CommonResult<Boolean> b = erpServiceClient.certWorkHourPush(trackItems, erpCode, trackHead.getProductionOrder(), certificate.getNumber(), unit);

                log.debug("[{}] query erp push-hour finish , result is [{}]", trackHead.getTrackNo(), b.getData());

            }

            //标记已推送工时状态
            certificateService.setPushHourComplete(certificate);
        }
    }


}
