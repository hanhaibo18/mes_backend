package com.richfit.mes.auth.authorization.service;

import com.richfit.mes.common.model.sys.Role;

import java.util.Set;

/**
 * @author sun
 * @Description 角色服务
 */
public interface RoleService {

    Set<Role> queryUserRolesByUserId(String userId);

}
