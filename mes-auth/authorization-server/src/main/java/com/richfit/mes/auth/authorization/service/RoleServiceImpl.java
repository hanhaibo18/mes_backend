package com.richfit.mes.auth.authorization.service;

import com.richfit.mes.auth.authorization.provider.SystemServiceClient;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.security.constant.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author sun
 * @Description 角色服务
 */
@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private SystemServiceClient systemServiceClient;

    @Override
    public Set<Role> queryUserRolesByUserId(String userId) {
        return systemServiceClient.queryRolesByUserId(userId,SecurityConstants.FROM_INNER).getData();
    }
}
