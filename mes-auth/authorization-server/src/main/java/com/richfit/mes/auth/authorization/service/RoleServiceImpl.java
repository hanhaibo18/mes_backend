package com.richfit.mes.auth.authorization.service;

import com.richfit.mes.auth.authorization.provider.SystemServiceClient;
import com.richfit.mes.common.model.sys.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * @author sun
 * @Description TODO
 */
@Service
public class RoleServiceImpl implements RoleService {
    @Autowired
    private SystemServiceClient systemServiceClient;

    @Override
    public Set<Role> queryUserRolesByUserId(String userId) {
        return systemServiceClient.queryRolesByUserId(userId).getData();
    }
}
