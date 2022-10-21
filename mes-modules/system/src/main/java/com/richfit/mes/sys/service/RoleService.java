package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.sys.entity.param.RoleQueryParam;

import java.util.List;

/**
 * @author sun
 * @Description 角色服务
 */
public interface RoleService extends IService<Role> {
    /**
     * 获取角色
     *
     * @param id
     * @return
     */
    Role get(String id);


    /**
     * 新增角色
     *
     * @param role
     * @return
     */
    boolean add(Role role);

    /**
     * 批量新增角色
     *
     * @param roles
     * @return
     */
    boolean batchAdd(List<Role> roles);

    /**
     * 查询角色
     *
     * @param page
     * @param roleQueryParam
     * @return
     */
    IPage<Role> query(Page page, RoleQueryParam roleQueryParam);

    /**
     * 根据用户id查询用户拥有的角色
     *
     * @param userId
     * @return
     */
    List<Role> query(String userId);

    /**
     * 更新角色信息
     *
     * @param role
     * @param role
     * @return
     */
    boolean update(Role role);

    /**
     * 根据id删除角色
     *
     * @param
     * @param id
     * @return
     */
    boolean delete(String id);

    /**
     * 根据租户id 创建租户管理员角色
     *
     * @param
     * @param tenantId
     * @return
     */
    void addTenantAdminRole(String tenantId);

    Role getAdminRole(String tenantId);

    boolean isTenantAdminRole(String roleId);

    List<Role> queryRolesByUserId(String userId);
}
