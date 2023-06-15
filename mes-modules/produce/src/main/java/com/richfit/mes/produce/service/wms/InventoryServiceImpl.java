package com.richfit.mes.produce.service.wms;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.TrackFlow;
import com.richfit.mes.common.model.wms.ApplyListUpload;
import com.richfit.mes.produce.dao.CertificateMapper;
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
    public void handOver(List<Certificate> certificateList, List<Product> products, List<TrackFlow> trackFlows) {
        List<ApplyListUpload> applyListUploads = new ArrayList<>();
        for (Certificate certificate : certificateList) {
            if (!Certificate.IS_DELIVERY_TO_WAREHOUSE_1.equals(certificate.getIsDeliveryToWarehouse())) {
                if (!Certificate.NEXT_OPT_WORK_BOMCO_SC.equals(certificate.getNextOptWork())) {
                    certificate.setIsDeliveryToWarehouse("2");
                    certificate.setDeliveryToWarehouseMessage("非生产入库合格证不进行生产入库");
                } else {
                    certificate.setIsDeliveryToWarehouse("1");
                    certificate.setDeliveryToWarehouseMessage("操作成功");
                    ApplyListUpload applyListUpload = new ApplyListUpload(certificate,products,trackFlows);
                    applyListUploads.add(applyListUpload);
                }
            } else {
                certificate.setIsDeliveryToWarehouse("2");
                certificate.setDeliveryToWarehouseMessage("已生产入库合格证不进行生产入库");
            }
        }
        CommonResult commonResult = wmsThreeServiceClient.applyListUpload(applyListUploads);
        if (commonResult.getStatus() == ResultCode.SUCCESS.getCode()) {
            this.updateBatchById(certificateList);
        } else {
            for (Certificate certificate : certificateList) {
                certificate.setIsDeliveryToWarehouse("2");
                certificate.setDeliveryToWarehouseMessage(commonResult.getMessage());
            }
            this.updateBatchById(certificateList);
        }
    }
}
