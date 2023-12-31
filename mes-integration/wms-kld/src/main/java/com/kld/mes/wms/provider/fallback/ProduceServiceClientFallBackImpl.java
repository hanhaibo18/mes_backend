package com.kld.mes.wms.provider.fallback;

import com.kld.mes.wms.provider.ProduceServiceClient;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.MaterialReceive;
import com.richfit.mes.common.model.produce.MaterialReceiveDetail;
import com.richfit.mes.common.model.produce.MaterialReceiveLog;
import com.richfit.mes.common.model.produce.RequestNoteDetail;
import com.richfit.mes.common.model.produce.dto.MaterialReceiveDto;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

/**
 * @Description TODO
 * @Author ang
 * @Date 2022/8/19 11:20
 */
public class ProduceServiceClientFallBackImpl implements ProduceServiceClient {

    @Override
    public CommonResult materialReceiveSaveBatchList(MaterialReceiveDto material, String header) {
        return null;
    }

    @Override
    public Boolean materialReceiveSaveLog(MaterialReceiveLog materialReceiveLog, String header) {
        return null;
    }

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
    public List<RequestNoteDetail> queryRequestNoteDetailDetails(String materialNo, String requestNoteNo, String header) {
        return null;
    }
}
