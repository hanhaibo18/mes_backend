package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.sys.TenantUser;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.sys.entity.param.TenantUserQueryParam;

import java.util.List;

/**
 * <p>
 * 租户用户 服务类
 * </p>
 *
 * @author gaoliang
 * @since 2020-05-25
 */
public interface TenantUserService {
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
     * 查询租户管理员
     *
     * @return
     */
    IPage<TenantUserVo> queryAdmin(Page<TenantUser> page, TenantUserQueryParam tenantUserQueryParam);

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
    boolean updatePassword(String id, String oldPassword, String newPassword);


    /**
     * 查询用户
     *
     * @return
     */
    IPage<TenantUserVo> queryByName(Page<TenantUser> page, String userAccount, String tenantId);

    /**
     * 功能描述:根据部门查询用户
     *
     * @param branchCode
     * @Author: xinYu.hou
     * @return: List<Map < String, String>>
     **/
    List<TenantUserVo> queryUserByBranchCode(String branchCode);


    /**
     * 功能描述: 根据用户编号查询用户信息
     *
     * @param userAccount
     * @Author: xinYu.hou
     * @Date: 2022/6/27 16:43
     * @return: TenantUserVo
     **/
    TenantUserVo queryByUserAccount(String userAccount);

    /**
     * 功能描述: 根据车间code查询人员列表
     *
     * @param BranchCode
     * @Author: xinYu.hou
     * @Date: 2022/7/8 15:57
     * @return: List<TenantUserVo>
     **/
    List<TenantUserVo> queryByBranchCode(String BranchCode);

    
}
