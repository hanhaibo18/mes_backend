package com.richfit.mes.produce.service.bsns;

import com.richfit.mes.common.model.produce.Certificate;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/1 16:27
 */
public abstract class AbstractCertAdditionalBsns implements CertAdditionalBsns {
    //是否生产交库
    public static boolean needScjk(Certificate certificate) {
        return Certificate.NEXT_OPT_WORK_BOMCO_SC.equals(certificate.getNextOptWork());
    }
}
