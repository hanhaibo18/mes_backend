package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.Tenant;
import com.richfit.mes.sys.dao.TenantMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * @author sun
 * @Description 租户服务
 */
@Service
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements TenantService{

    @Autowired
    private TenantMapper tenantMapper;

    @Override
    public JsonNode getAdditionalInfo(String tenantId) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String s = tenantMapper.getAdditionalInfo(tenantId);
        if(StringUtils.isEmpty(s)){
            return null;
        }else {
            return objectMapper.readTree(s);
        }
    }

    @Override
    public CommonResult saveAdditionalInfo(JsonNode addInfo, String tenantId) {
        return CommonResult.success(tenantMapper.saveAdditionalInfo(addInfo.toString(),tenantId));
    }
}
