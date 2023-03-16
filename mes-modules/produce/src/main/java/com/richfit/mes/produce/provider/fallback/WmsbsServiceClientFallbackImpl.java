package com.richfit.mes.produce.provider.fallback;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.ApplicationResult;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.IngredientApplicationDto;
import com.richfit.mes.produce.provider.WmsServiceClient;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/25 11:29
 */
public class WmsbsServiceClientFallbackImpl implements WmsServiceClient {
    @Override
    public CommonResult<Boolean> sendJkInfo(Certificate certificate) {
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
}
