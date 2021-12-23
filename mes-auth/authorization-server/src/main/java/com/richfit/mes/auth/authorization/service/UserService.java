package com.richfit.mes.auth.authorization.service;

import com.richfit.mes.common.model.sys.TenantUser;
/**
 * @author sun
 * @Description 用户服务
 */
public interface UserService {

    /**
     * 根据用户唯一标识获取用户信息
     *
     * @param uniqueId
     * @return
     */
    TenantUser getByUniqueId(String uniqueId);
}
