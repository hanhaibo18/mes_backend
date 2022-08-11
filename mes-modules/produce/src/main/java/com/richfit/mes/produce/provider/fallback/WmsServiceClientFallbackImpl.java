package com.richfit.mes.produce.provider.fallback;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.IngredientApplicationDto;
import com.richfit.mes.produce.provider.WmsServiceClient;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/25 11:29
 */
public class WmsServiceClientFallbackImpl implements WmsServiceClient {
    @Override
    public CommonResult<Boolean> sendJkInfo(Certificate certificate) {
        return null;
    }

    @Override
    public CommonResult<Integer> queryMaterialCount(String materialNo) {
        return null;
    }

    @Override
    public CommonResult<Boolean> anApplicationForm(IngredientApplicationDto ingredientApplicationDto) {
        return null;
    }
}
