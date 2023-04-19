package com.richfit.mes.base.provider.fallback;

import com.richfit.mes.base.provider.ProduceServiceClient;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.Order;
import com.richfit.mes.common.model.produce.TrackHead;
import com.richfit.mes.common.model.produce.TrackItem;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: zhiqiang.lu
 * @Date: 2022.9.22
 * @LastEditors: zhiqiang.lu
 * @LastEditTime: 2022.9.22
 * @Description: 添加produce实现
 * @LastEdit: 添加通过工艺id查询跟单列表
 */
@Component
public class ProduceServiceClientFallbackImpl implements ProduceServiceClient {

    @Override
    public CommonResult<List<TrackHead>> selectByRouterId(String routerId) {
        return null;
    }

    @Override
    public int queryCountByWorkNo(String projectBomId) {
        return 0;
    }

    @Override
    public CommonResult<List<Order>> queryByMaterialCode(List<String> materialCodes, String tenantId) {
        return null;
    }

    @Override
    public CommonResult<List<TrackHead>> getTrackHeadByMaterialCodeAndDrawingNo(List<String> materialCodes, List<String> drawingNos, String tenantId) {
        return null;
    }

    @Override
    public TrackItem qyeryTrackItemById(String id) {
        return null;
    }

    @Override
    public boolean updateBatch(List<TrackHead> trackHeadList) {
        return false;
    }
}
