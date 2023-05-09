package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.TrackCertificate;

public interface TrackCertificateService extends IService<TrackCertificate> {
    /**
     * 功能描述: 报错合格证的关联关系
     *
     * @param certificate 合格证信息
     * @return
     * @Author zhiqiang.lu
     */
    public void save(Certificate certificate);
}
