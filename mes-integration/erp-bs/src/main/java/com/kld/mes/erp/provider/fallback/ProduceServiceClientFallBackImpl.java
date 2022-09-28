package com.kld.mes.erp.provider.fallback;

import com.kld.mes.erp.provider.ProduceServiceClient;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.MaterialReceive;
import com.richfit.mes.common.model.produce.MaterialReceiveDetail;
import com.richfit.mes.common.model.produce.TrackComplete;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @Description TODO
 * @Author ang
 * @Date 2022/8/19 11:20
 */
public class ProduceServiceClientFallBackImpl implements ProduceServiceClient {

    @GetMapping("/api/produce/material_receive/get_last_time")
    @Override
    public String getlastTime(String tenantId, String header) {
        return null;
    }

    @Override
    public Boolean materialReceiveSaveBatch(List<MaterialReceive> materialReceiveList, String header) {
        return null;
    }

    @Override
    public Boolean detailSaveBatch(List<MaterialReceiveDetail> detailList, String header) {
        return null;
    }

    @Override
    public CommonResult<List<TrackComplete>> trackCompleteFindByTiId(String tiId, String header) {
        return null;
    }

}
