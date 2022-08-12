package com.richfit.mes.produce.provider.fallback;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Product;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.common.model.produce.TrackItem;
import com.richfit.mes.produce.provider.ErpServiceClient;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/25 14:00
 */
public class ErpServiceClientFallbackImpl implements ErpServiceClient {
    @Override
    public CommonResult<Boolean> certWorkHourPush(List<TrackItem> trackItemList, String erpCode, String orderNo, int qty, String unit) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<List<Order>> getErpOrder(String erpCode, String selectDate, String orderNo, String controller) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<List<Product>> getStorage(String materialNos, String erpCode) {
        return CommonResult.success(null);
    }
}
