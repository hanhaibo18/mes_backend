package com.richfit.mes.produce.service.wms;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.produce.dao.CertificateMapper;
import com.richfit.mes.produce.provider.WmsServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author 王瑞
 * @Description 操作信息服务
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class InventoryServiceImpl extends ServiceImpl<CertificateMapper, Certificate> implements InventoryService {

    @Autowired
    public WmsServiceClient wmsServiceClient;

    @Override
    public void handOver(Certificate certificate) throws Exception {
        if (!Certificate.NEXT_OPT_WORK_BOMCO_SC.equals(certificate.getNextOptWork())) {
            throw new Exception(certificate.getCertificateNo() + ":非生产入库合格证不进行工时推送;");
        }
        CommonResult commonResult = wmsServiceClient.sendJkInfo(certificate);
        if (commonResult.getStatus() != ResultCode.SUCCESS.getCode()) {
            throw new Exception(certificate.getCertificateNo() + ":" + commonResult.getMessage() + ";");
        }
    }
}
