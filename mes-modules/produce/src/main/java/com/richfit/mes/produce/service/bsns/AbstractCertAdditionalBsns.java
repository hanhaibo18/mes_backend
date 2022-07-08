package com.richfit.mes.produce.service.bsns;

import com.richfit.mes.common.model.produce.Certificate;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/1 16:27
 */
public abstract class AbstractCertAdditionalBsns implements CertAdditionalBsns {


    //转出工厂存在
    public static boolean needTransfer(Certificate certificate) {

        return !certificate.getNextOptWork().equals(certificate.getBranchCode());

    }


    //是否生产交库
    public static boolean needScjk(Certificate certificate) {

        return "BOMCO_SC".equals(certificate.getNextOptWork());

    }


}
