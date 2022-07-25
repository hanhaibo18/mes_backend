package com.richfit.mes.produce.service.bsns;

import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.TrackCertificate;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.ErpServiceClient;
import com.richfit.mes.produce.provider.WmsServiceClient;
import com.richfit.mes.produce.service.TrackHeadService;
import com.richfit.mes.produce.service.TrackItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/1 16:26
 */
@Service
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

    @Override
    public void doAdditionalBsns(Certificate certificate) {

        if (needScjk(certificate)) {

            wmsServiceClient.sendJcInfo(certificate);

            //erp工时推送
            String erpCode = SecurityUtils.getCurrentUser().getTenantErpCode();

            List<Product> list = baseServiceClient.selectProduct(certificate.getMaterialNo(), certificate.getDrawingNo(), "3").getData();
            String unit = "";
            if (list.size() > 0) {
                unit = list.get(0).getUnit();
            }

            for (TrackCertificate trackCertificate : certificate.getTrackCertificates()) {

                TrackHead trackHead = trackHeadService.getById(trackCertificate.getThId());

                List trackItems = trackItemService.queryTrackItemByTrackNo(trackCertificate.getThId());

                erpServiceClient.certWorkHourPush(trackItems, erpCode, trackHead.getProductionOrder(), certificate.getNumber(), unit);

            }
        }
    }


}
