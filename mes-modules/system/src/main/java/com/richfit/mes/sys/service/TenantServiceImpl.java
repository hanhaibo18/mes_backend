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
public class TenantServiceImpl extends ServiceImpl<TenantMapper, Tenant> implements TenantService {

    @Autowired
    private TenantMapper tenantMapper;

    @Autowired
    private RoleService roleService;

    @Override
    public JsonNode getAdditionalInfo(String tenantId) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        String s = tenantMapper.getAdditionalInfo(tenantId);
        if (StringUtils.isEmpty(s)) {
            return null;
        } else {
            return objectMapper.readTree(s);
        }
    }

    @Override
    public CommonResult saveAdditionalInfo(JsonNode addInfo, String tenantId) {
        return CommonResult.success(tenantMapper.saveAdditionalInfo(addInfo.toString(), tenantId));
    }

    @Override
    public Boolean addTenant(Tenant tenant) {

        this.save(tenant);

        //默认创建一个该租户下的 租户管理员角色
        roleService.addTenantAdminRole(tenant.getId());

        return true;
    }
}
