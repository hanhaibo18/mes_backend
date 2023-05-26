package com.richfit.mes.produce.service.bsns;

import cn.hutool.core.date.DateUtil;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.TrackCertificate;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.common.model.sys.ItemParam;
import com.richfit.mes.common.model.sys.Tenant;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.ErpServiceClient;
import com.richfit.mes.produce.provider.SystemServiceClient;
import com.richfit.mes.produce.provider.WmsServiceClient;
import com.richfit.mes.produce.service.CertificateService;
import com.richfit.mes.produce.service.TrackHeadService;
import com.richfit.mes.produce.service.TrackItemService;
import com.richfit.mes.produce.service.erp.WorkHoursService;
import com.richfit.mes.produce.service.wms.InventoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
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

    @Autowired
    SystemServiceClient systemServiceClient;

    @Autowired
    WorkHoursService workHoursService;
    @Autowired
    InventoryService inventoryService;


    private final String pushSystemFlag = "systemFlag";

    @Override
    public void doAdditionalBsns(Certificate certificate) throws Exception {
        String companyCode = SecurityUtils.getCurrentUser().getCompanyCode();
        if (needScjk(certificate)) {
            inventoryService.handOver(certificate);
            //根据数据字段配置，判断推送哪个系统
            if (Tenant.COMPANYCODE_BEISHI.equals(companyCode)) {
                //推送北石
                pushWorkHourToBs(certificate);
            } else {
                //推送宝石
                workHoursService.push(certificate);
            }
        }
    }

    @Override
    public void pushWorkHourToBs(Certificate certificate) {
        if (certificate != null) {
            //erp工时推送
            String erpCode = SecurityUtils.getCurrentUser().getTenantErpCode();

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


    private boolean sendEnabled() {
        boolean enable = true;
        //增加逻辑  每月固定日期之间的日期才推送工时
        //日期范围通过字典保存：erpUrl中的  ErpStartDay：开始日期  ErpEndDay：结束日期
        CommonResult<ItemParam> startDay = systemServiceClient.findItemParamByCode("ErpStartDay");
        CommonResult<ItemParam> endDay = systemServiceClient.findItemParamByCode("ErpEndDay");

        int nowDay = DateUtil.dayOfMonth(new Date());
        if (startDay != null && startDay.getData() != null && endDay != null && endDay.getData() != null) {
            if (Integer.parseInt(startDay.getData().getLabel()) > nowDay || nowDay > Integer.parseInt(endDay.getData().getLabel())) {
                enable = false;
                log.debug("当前日期[{}]在同步工时接口允许日期范围外[{}]-[{}]，不进行工时推送", nowDay, startDay.getData().getLabel(), endDay.getData().getLabel());
            }
        } else {
            log.debug("本租户未配置工时推送有效日期，任何日期均可推送");
        }

        return enable;
    }

}
