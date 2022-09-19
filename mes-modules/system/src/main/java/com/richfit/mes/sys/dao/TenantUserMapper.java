package com.richfit.mes.sys.dao;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Constants;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.sys.TenantUser;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.sys.entity.param.TenantUserQueryParam;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * 租户用户 Mapper 接口
 * </p>
 *
 * @author gaoliang
 * @since 2020-05-25
 */
@Mapper
public interface TenantUserMapper extends BaseMapper<TenantUser> {

    IPage<TenantUserVo> queryTenantUser(Page page, @Param("param") TenantUserQueryParam tenantUserQueryParam, @Param("isAdmin") boolean isAdmin);

    IPage<TenantUserVo> queryTenantAdmin(Page page, @Param("param") TenantUserQueryParam tenantUserQueryParam);

    /**
     * 功能描述: 根据条件查询人员(不返回密码)
     *
     * @param queryWrapper
     * @Author: xinYu.hou
     * @Date: 2022/6/20 15:57
     * @return: List<TenantUserVo>
     **/
    @Select("Select * From sys_tenant_user ${ew.customSqlSegment}")
    List<TenantUserVo> queryUserList(@Param(Constants.WRAPPER) QueryWrapper<TenantUserVo> queryWrapper);

    /**
     * 功能描述: 查询用户
     *
     * @param queryWrapper
     * @Author: xinYu.hou
     * @Date: 2022/6/27 16:47
     * @return: TenantUserVo
     **/
    @Select("Select * From sys_tenant_user ${ew.customSqlSegment}")
    TenantUserVo queryUser(@Param(Constants.WRAPPER) QueryWrapper<TenantUserVo> queryWrapper);

    /**
     * 功能描述: 根据车间code查询人员
     *
     * @param queryWrapper
     * @Author: xinYu.hou
     * @Date: 2022/7/8 15:49
     * @return: List<TenantUserVo>
     **/
    @Select("SELECT DISTINCT(role.user_id),users.*,role.user_type user_role_type FROM sys_tenant_user users LEFT JOIN sys_user_role role ON role.user_id = users.id ${ew.customSqlSegment}")
    List<TenantUserVo> queryByBranchCode(@Param(Constants.WRAPPER) QueryWrapper<TenantUserVo> queryWrapper);
}
