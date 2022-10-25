package com.kld.mes.erp.provider;

import com.kld.mes.erp.provider.fallback.ProduceServiceClientFallBackImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.MaterialReceive;
import com.richfit.mes.common.model.produce.MaterialReceiveDetail;
import com.richfit.mes.common.model.produce.TrackComplete;
import com.richfit.mes.common.security.constant.SecurityConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @className:ProduceServiceClient
 * @description: 类描述
 * @author:ang
 * @date:2022/8/19 11:19
 */
@FeignClient(name = "produce-service", decode404 = true, fallback = ProduceServiceClientFallBackImpl.class)
public interface ProduceServiceClient {

    @GetMapping(value = "/api/produce/material_receive/get_last_time")
    public String getlastTime(@RequestParam("tenantId") String tenantId,
                              @RequestHeader(value = SecurityConstants.FROM) String header);

    @PostMapping(value = "/api/produce/material_receive/material_receive/save_batch")
    public Boolean materialReceiveSaveBatch(@RequestBody List<MaterialReceive> materialReceiveList,
                                            @RequestHeader(value = SecurityConstants.FROM) String header);

    @PostMapping(value = "/api/produce/material_receive/detail/save_batch")
    public Boolean detailSaveBatch(@RequestBody List<MaterialReceiveDetail> detailList,
                                   @RequestHeader(value = SecurityConstants.FROM) String header);

    @GetMapping(value = "/api/produce/trackcomplete/find")
    public CommonResult<List<TrackComplete>> trackCompleteFindByTiId(@RequestParam("tiId") String tiId,
                                                                     @RequestHeader(value = SecurityConstants.FROM_INNER) String header);


}
