package com.richfit.mes.sys.service;

import com.richfit.mes.common.model.sys.Role;

import java.util.List;
import java.util.Set;

/**
 * @author sun
 * @Description 用户角色服务
 */
public interface UserRoleService {
    /**
     * 给用户添加角色
     *
     * @param userId
     * @param roleIds
     * @return
     */
    boolean saveBatch(String userId, Set<String> roleIds);

    /**
     * 删除用户拥有的角色
     *
     * @param userId
     * @return
     */
    boolean removeByUserId(String userId);

    /**
     * 根据userId查询用户拥有角色id集合
     *
     * @param userId
     * @return
     */
    Set<String> queryByUserId(String userId);

    /**
     * 根据userId查询用户拥有角色列表
     *
     * @param userId
     * @return
     */
    List<Role> queryRolesByUserId(String userId);
}
