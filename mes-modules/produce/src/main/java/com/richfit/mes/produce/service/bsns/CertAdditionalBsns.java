package com.richfit.mes.produce.service.bsns;

import com.richfit.mes.common.model.produce.Certificate;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/1 16:24
 */
public interface CertAdditionalBsns {

    void doAdditionalBsns(Certificate certificate) throws Exception;

    /**
     * 北石工时推送接口  fengxy
     * @param certificate
     */
    void pushWorkHourToBs(Certificate certificate);

}
