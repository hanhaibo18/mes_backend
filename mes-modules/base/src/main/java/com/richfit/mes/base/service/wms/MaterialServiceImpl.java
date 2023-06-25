package com.richfit.mes.base.service.wms;

import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.richfit.mes.base.provider.WmsServiceClient;
import com.richfit.mes.base.service.ProductService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.wms.MaterialBasis;
import com.richfit.mes.common.security.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能描述:物料管理
 *
 * @Author: zhiqiang.lu
 * @Date: 2023/05/26 16:27
 **/
@Service
@Transactional(rollbackFor = Exception.class)
public class MaterialServiceImpl implements MaterialService {

    @Autowired
    private ProductService productService;

    @Autowired
    public WmsServiceClient wmsServiceClient;

    @Override
    public void sync(List<Product> products) {
        this.sync(products, SecurityUtils.getCurrentUser().getTenantErpCode());
    }

    @Override
    public void sync(List<Product> products, String erpCode) {
        if (CollectionUtils.isEmpty(products)) {
            return;
        }
        List<MaterialBasis> materialBasisList = new ArrayList<>(products.size());
        for (Product product : products) {
            MaterialBasis materialBasis = new MaterialBasis(product, erpCode);
            materialBasisList.add(materialBasis);
        }
        try {
            // 同步到wms中
            CommonResult commonResult = wmsServiceClient.materialBasis(materialBasisList);
            if (commonResult.getStatus() == ResultCode.SUCCESS.getCode()) {
                for (Product product : products) {
                    product.setSynchronousRegime(1);
                    product.setSynchronousMessage("同步成功");
                }
            } else {
                for (Product product : products) {
                    product.setSynchronousRegime(2);
                    product.setSynchronousMessage(commonResult.getMessage());
                }
            }
            productService.updateBatchById(products);
        } catch (Exception e) {
            for (Product product : products) {
                product.setSynchronousRegime(2);
                product.setSynchronousMessage(e.getMessage());
            }
            productService.updateBatchById(products);
        }
    }
}
