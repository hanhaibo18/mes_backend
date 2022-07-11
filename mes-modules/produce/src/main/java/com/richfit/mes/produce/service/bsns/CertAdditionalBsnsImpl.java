package com.richfit.mes.produce.service.bsns;

import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.produce.thirdApi.wms.ProductToWmsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/1 16:26
 */
@Service
public class CertAdditionalBsnsImpl extends AbstractCertAdditionalBsns {

    @Autowired
    ProductToWmsService productToWmsService;

    @Override
    public void doAdditionalBsns(Certificate certificate) {

        if (needScjk(certificate)) {

            productToWmsService.sendRequest(certificate);

            //TODO  erp工时推送
        }
    }


}
