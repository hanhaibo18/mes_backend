package com.richfit.mes.base.provider.fallback;

import com.richfit.mes.base.provider.ErpServiceClient;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.common.model.produce.TrackItem;

import java.util.List;

/**
 * @author: 王瑞
 * @date: 2022/8/3 11:32
 */
public class ErpServiceClientFallbackImpl implements ErpServiceClient {
    @Override
    public CommonResult<Boolean> pushRouter(List<Router> routers, String header) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<List<Product>> getMaterial(String date, String erpCode) {
        return null;
    }
}
