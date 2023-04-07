package com.kld.mes.pdm.provider;

import com.kld.mes.pdm.provider.fallback.ProduceServiceClientFallBackImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.MaterialReceive;
import com.richfit.mes.common.model.produce.MaterialReceiveDetail;
import com.richfit.mes.common.model.produce.MaterialReceiveLog;
import com.richfit.mes.common.model.produce.RequestNoteDetail;
import com.richfit.mes.common.model.produce.dto.MaterialReceiveDto;
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

    @PostMapping(value = "/api/produce/material_receive/material_receive/save_batch_list")
    public CommonResult materialReceiveSaveBatchList(@RequestBody MaterialReceiveDto material, @RequestHeader(value = SecurityConstants.FROM) String header);

    @PostMapping(value = "/api/produce/material_receive/material_receive/save_log")
    public Boolean materialReceiveSaveLog(@RequestBody MaterialReceiveLog materialReceiveLog, @RequestHeader(value = SecurityConstants.FROM) String header);

    @GetMapping(value = "/api/produce/material_receive/get_last_time")
    public String getlastTime(@RequestParam("tenantId") String tenantId, @RequestHeader(value = SecurityConstants.FROM) String header);

    @PostMapping(value = "/api/produce/material_receive/material_receive/save_batch")
    public Boolean materialReceiveSaveBatch(@RequestBody List<MaterialReceive> materialReceiveList, @RequestHeader(value = SecurityConstants.FROM) String header);

    @PostMapping(value = "/api/produce/material_receive/detail/save_batch")
    public Boolean detailSaveBatch(@RequestBody List<MaterialReceiveDetail> detailList, @RequestHeader(value = SecurityConstants.FROM) String header);

    /**
     * 功能描述: 根据申请单号 和 物料号 查询物料
     *
     * @param materialNo
     * @param requestNoteNo
     * @param header
     * @Author: xinYu.hou
     * @Date: 2022/11/10 13:43
     * @return: List<RequestNoteDetail>
     **/
    @GetMapping(value = "/api/produce/request_note/queryRequestNoteDetailDetails/inner")
    public List<RequestNoteDetail> queryRequestNoteDetailDetails(@RequestParam("materialNo") String materialNo, @RequestParam("requestNoteNo") String requestNoteNo, @RequestHeader(value = SecurityConstants.FROM) String header);
}
