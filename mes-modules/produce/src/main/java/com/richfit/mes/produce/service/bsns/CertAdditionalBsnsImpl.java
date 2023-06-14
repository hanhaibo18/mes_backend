package com.richfit.mes.produce.service.bsns;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.TrackCertificate;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.model.sys.Tenant;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.ErpServiceClient;

import com.richfit.mes.produce.service.CertificateService;
import com.richfit.mes.produce.service.TrackHeadService;
import com.richfit.mes.produce.service.TrackItemService;
import com.richfit.mes.produce.service.erp.WorkHoursService;
import com.richfit.mes.produce.service.wms.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/1 16:26
 */
@Service
@Slf4j
public class CertAdditionalBsnsImpl extends AbstractCertAdditionalBsns {

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

    @Autowired
    WorkHoursService workHoursService;
    @Autowired
    InventoryService inventoryService;

    @Override
    public void doAdditionalBsns(Certificate certificate) {
        if (needScjk(certificate)) {
            List<Certificate> certificateList = new ArrayList<>();
            certificateList.add(certificate);
            inventoryService.handOver(certificateList);
            String companyCode = Objects.requireNonNull(SecurityUtils.getCurrentUser()).getCompanyCode();
            //根据数据字段配置，判断推送哪个系统
            if (Tenant.COMPANYCODE_BEISHI.equals(companyCode)) {
                //推送北石
                pushWorkHourToBs(certificate);
            } else {
                CommonResult<Object> commonResultGS = workHoursService.push(certificate);
                //推送宝石
                if (commonResultGS.getStatus() == ResultCode.SUCCESS.getCode()) {
                    certificate.setIsSendWorkHour("1");
                    certificate.setSendWorkHourMessage("操作成功");
                } else {
                    certificate.setIsSendWorkHour("2");
                    certificate.setSendWorkHourMessage(commonResultGS.getMessage());
                }
            }
        }
    }

    @Override
    public void pushWorkHourToBs(Certificate certificate) {
        if (certificate != null) {
            //erp工时推送
            String erpCode = Objects.requireNonNull(SecurityUtils.getCurrentUser()).getTenantErpCode();

            List<Product> list = baseServiceClient.selectProduct(certificate.getTenantId(), certificate.getMaterialNo(), certificate.getDrawingNo(), "3").getData();
            String unit = "";
            if (list.size() > 0) {
                unit = list.get(0).getUnit();
            }

            for (TrackCertificate trackCertificate : certificate.getTrackCertificates()) {

                TrackHead trackHead = trackHeadService.getById(trackCertificate.getThId());

                List<TrackItem> trackItems = trackItemService.queryTrackItemByTrackNo(trackCertificate.getThId());

                CommonResult<Boolean> b = erpServiceClient.certWorkHourPushToBs(trackItems, erpCode, trackHead.getProductionOrder(), trackHead.getMaterialNo(),
                        certificate.getNumber(), unit);

                log.debug("[{}] query erp push-hour finish , result is [{}]", trackHead.getTrackNo(), b.getData());

            }
            //标记已推送工时状态
            certificateService.setPushHourComplete(certificate);
        }
    }
}
