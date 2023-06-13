package com.richfit.mes.base.provider;


import com.richfit.mes.base.provider.fallback.WmsServiceClientFallbackImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.ApplicationResult;
import com.richfit.mes.common.model.wms.InventoryQuery;
import com.richfit.mes.common.model.wms.InventoryReturn;
import com.richfit.mes.common.model.wms.MaterialBasis;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

/**
 * @author: llh
 * @date: 2023/3/30 11:30
 */
@FeignClient(name = "wms-service", decode404 = true, fallback = WmsServiceClientFallbackImpl.class)
public interface WmsServiceClient {

    /**
     * 同步物料数据物料到wms
     *
     * @param materialBasisList
     * @return
     */
    @PostMapping("/api/integration/wms/three/material_basis")
    public CommonResult materialBasis(@RequestBody List<MaterialBasis> materialBasisList);

    /**
     * MES实时查询WMS库存
     *
     * @param inventoryQuery
     * @return
     */
    @PostMapping("/api/integration/wms/three/inventory_query")
    public CommonResult<List<InventoryReturn>> inventoryQuery(@RequestBody InventoryQuery inventoryQuery);
}
