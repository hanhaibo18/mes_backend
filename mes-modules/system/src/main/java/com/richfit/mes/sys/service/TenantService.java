package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.Tenant;

/**
 * @author sun
 * @Description 租户服务
 */
public interface TenantService extends IService<Tenant> {
    JsonNode getAdditionalInfo(String tenantId) throws JsonProcessingException;

    CommonResult saveAdditionalInfo(JsonNode addInfo, String tenantId);
}
