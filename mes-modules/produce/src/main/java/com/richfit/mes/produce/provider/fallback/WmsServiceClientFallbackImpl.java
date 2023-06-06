package com.richfit.mes.produce.provider.fallback;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.ApplicationResult;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.IngredientApplicationDto;
import com.richfit.mes.common.model.wms.ApplyListUpload;
import com.richfit.mes.common.model.wms.InventoryQuery;
import com.richfit.mes.common.model.wms.InventoryReturn;
import com.richfit.mes.produce.provider.WmsServiceClient;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/25 11:29
 */
public class WmsServiceClientFallbackImpl implements WmsServiceClient {
    @Override
    public CommonResult<Object> sendJkInfo(Certificate certificate) {
        return null;
    }

    @Override
    public CommonResult<Integer> queryMaterialCount(String materialNo) {
        return null;
    }

    @Override
    public CommonResult<ApplicationResult> anApplicationForm(IngredientApplicationDto ingredientApplicationDto) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<ApplicationResult> applyListUpload(List<ApplyListUpload> applyListUpload) {
        return null;
    }

    @Override
    public CommonResult<List<InventoryReturn>> inventoryQuery(InventoryQuery inventoryQuery) {
        return null;
    }
}
