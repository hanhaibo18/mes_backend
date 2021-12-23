package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.sys.TenantUser;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.sys.entity.param.TenantUserQueryParam;

/**
 * <p>
 * 租户用户 服务类
 * </p>
 *
 * @author gaoliang
 * @since 2020-05-25
 */
public interface TenantUserService{
    /**
     * 根据用户唯一标识获取用户信息
     *
     * @param uniqueId
     * @return
     */
    TenantUser getByUniqueId(String uniqueId);
    /**
     * 获取用户
     *
     * @param id 用户id
     * @return UserVo
     */
    TenantUserVo get(String id);

    TenantUserVo findById(String id);

    /**
     * 新增用户
     *
     * @param tenantUser
     * @return
     */
    boolean add(TenantUser tenantUser);

    /**
     * 查询用户
     *
     * @return
     */
    IPage<TenantUserVo> query(Page<TenantUser> page, TenantUserQueryParam tenantUserQueryParam);

    /**
     * 更新用户信息
     *
     * @param tenantUser
     * @return
     */
    boolean update(TenantUser tenantUser);

    /**
     * 根据id删除用户
     *
     * @param id
     * @return
     */
    boolean delete(String id);
    /**
     * 修改密码
     *
     * @param id
     * @param oldPassword
     * @param newPassword
     * @return
     */
    boolean updatePassword(String id,String oldPassword,String newPassword);


    /**
     * 查询用户
     *
     * @return
     */
    IPage<TenantUserVo> queryByName(Page<TenantUser> page, String userAccount,String tenantId);
}
