package com.richfit.mes.auth.authorization.service;

import com.richfit.mes.auth.authorization.provider.SystemServiceClient;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.TenantUser;
import com.richfit.mes.common.security.constant.SecurityConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author sun
 * @Description 用户服务
 */
@Service
public class UserServiceImpl implements UserService{
    @Autowired
    private SystemServiceClient systemServiceClient;

    @Override
    public TenantUser getByUniqueId(String uniqueId) {
        CommonResult<TenantUser> result =  systemServiceClient.getUserByUniqueId(uniqueId, SecurityConstants.FROM_INNER);
        if (result == null || result.getData() == null) {
            throw new UsernameNotFoundException("User " + uniqueId + " not found");
        }
        return result.getData();
    }
}
