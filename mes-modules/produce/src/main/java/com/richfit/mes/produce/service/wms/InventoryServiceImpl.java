package com.richfit.mes.produce.service.wms;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.WmsResult;
import com.richfit.mes.common.model.wms.ApplyListUpload;
import com.richfit.mes.produce.dao.CertificateMapper;
import com.richfit.mes.produce.provider.WmsServiceClient;
import com.richfit.mes.produce.provider.WmsThreeServiceClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述:库存管理
 *
 * @Author: zhiqiang.lu
 * @Date: 2023/05/26 16:27
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class InventoryServiceImpl extends ServiceImpl<CertificateMapper, Certificate> implements InventoryService {

    @Autowired
    public WmsThreeServiceClient wmsThreeServiceClient;

    @Override
    public CommonResult<Object> handOver(Certificate certificate) {
        if (!Certificate.IS_DELIVERY_TO_WAREHOUSE_1.equals(certificate.getIsDeliveryToWarehouse())) {
            if (!Certificate.NEXT_OPT_WORK_BOMCO_SC.equals(certificate.getNextOptWork())) {
                return CommonResult.failed(certificate.getCertificateNo() + ":非生产入库合格证不进行工时推送;");
            }
            List<ApplyListUpload> applyListUploads = new ArrayList<>();
            ApplyListUpload applyListUpload = new ApplyListUpload(certificate);
            applyListUploads.add(applyListUpload);
            CommonResult<WmsResult> commonResult = wmsThreeServiceClient.applyListUpload(applyListUploads);
            if (commonResult.getStatus() != ResultCode.SUCCESS.getCode()) {
                return CommonResult.failed(certificate.getCertificateNo() + ":" + commonResult.getMessage() + ";");
            }
        }
        return CommonResult.success("操作成功");
    }
}
