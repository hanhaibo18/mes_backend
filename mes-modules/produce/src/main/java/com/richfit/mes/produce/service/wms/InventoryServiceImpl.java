package com.richfit.mes.produce.service.wms;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.TrackFlow;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.wms.ApplyListUpload;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.produce.dao.CertificateMapper;
import com.richfit.mes.produce.provider.BaseServiceClient;
import com.richfit.mes.produce.provider.WmsThreeServiceClient;
import com.richfit.mes.produce.service.CertificateService;
import com.richfit.mes.produce.service.TrackHeadFlowService;
import com.richfit.mes.produce.service.TrackHeadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 功能描述:库存管理
 *
 * @Author: zhiqiang.lu
 * @Date: 2023/05/26 16:27
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class InventoryServiceImpl implements InventoryService {

    @Autowired
    public CertificateService certificateService;

    @Autowired
    public TrackHeadFlowService trackHeadFlowService;

    @Autowired
    public BaseServiceClient baseServiceClient;

    @Autowired
    public WmsThreeServiceClient wmsThreeServiceClient;

    @Override
    public void handOver(List<Certificate> certificateList) {
        //批量获取物料信息
        List<String> materialNoList = certificateList.stream().map(Certificate::getMaterialNo).collect(Collectors.toList());
        List<Product> products = baseServiceClient.listByMaterialNoList(materialNoList);
        Map<String, Product> productMap = products.stream().collect(Collectors.toMap(Product::getMaterialNo, x -> x));
        List<ApplyListUpload> applyListUploads = new ArrayList<>();
        for (Certificate certificate : certificateList) {
            if (!Certificate.IS_DELIVERY_TO_WAREHOUSE_1.equals(certificate.getIsDeliveryToWarehouse())) {
                if (!Certificate.NEXT_OPT_WORK_BOMCO_SC.equals(certificate.getNextOptWork())) {
                    certificate.setIsDeliveryToWarehouse("2");
                    certificate.setDeliveryToWarehouseMessage("非生产入库合格证不进行生产入库");
                } else {
                    certificate.setIsDeliveryToWarehouse("1");
                    certificate.setDeliveryToWarehouseMessage("操作成功");
                    Map<String, String> map = new HashMap<>(1);
                    map.put("certificateNo", certificate.getCertificateNo());
                    List<TrackFlow> trackFlows = new ArrayList<>();
                    try {
                        trackFlows = trackHeadFlowService.selectFlowList(map);
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    ApplyListUpload applyListUpload = new ApplyListUpload(certificate, productMap.get(certificate.getMaterialNo()), trackFlows, SecurityUtils.getCurrentUser().getTenantErpCode());
                    applyListUploads.add(applyListUpload);
                }
            } else {
                certificate.setIsDeliveryToWarehouse("2");
                certificate.setDeliveryToWarehouseMessage("已生产入库合格证不进行生产入库");
            }
        }
        if (CollectionUtils.isEmpty(applyListUploads)) {
            return;
        }
        CommonResult commonResult = wmsThreeServiceClient.applyListUpload(applyListUploads);
        if (commonResult.getStatus() == ResultCode.SUCCESS.getCode()) {
            certificateService.updateBatchById(certificateList);
        } else {
            for (Certificate certificate : certificateList) {
                certificate.setIsDeliveryToWarehouse("2");
                certificate.setDeliveryToWarehouseMessage(commonResult.getMessage());
            }
            certificateService.updateBatchById(certificateList);
        }
    }
}
