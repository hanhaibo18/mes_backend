package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.sys.dao.RoleMapper;
import com.richfit.mes.sys.entity.param.RoleQueryParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * @author sun
 * @Description 角色服务
 */
@Slf4j
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
    @Autowired
    private  UserRoleService userRoleService;

    @Override
    public boolean add(Role role) {
        boolean isSuccess = this.save(role);
        return isSuccess;
    }

    @Override
    public boolean delete(String id) {
        return this.removeById(id);
    }

    @Override
    public boolean update(Role role) {
        boolean isSuccess = this.updateById(role);
        return isSuccess;
    }

    @Override
    public Role get(String id) {
        Role role = this.getById(id);
        if (Objects.isNull(role)) {
            throw new GlobalException("role not found with id:" + id, ResultCode.ITEM_NOT_FOUND);
        }
        return role;
    }


    @Override
    public List<Role> query(String userId) {
        Set<String> roleIds = userRoleService.queryByUserId(userId);
        return this.listByIds(roleIds);
    }

    @Override
    public IPage<Role> query(Page page, RoleQueryParam roleQueryParam) {
        QueryWrapper<Role> queryWrapper = roleQueryParam.build();
        queryWrapper.like(StringUtils.isNotBlank(roleQueryParam.getRoleName()), "role_name", roleQueryParam.getRoleName());
        queryWrapper.like(StringUtils.isNotBlank(roleQueryParam.getRoleCode()), "role_code", roleQueryParam.getRoleCode());
        queryWrapper.eq(StringUtils.isNotBlank(roleQueryParam.getTenantId()), "tenant_id", roleQueryParam.getTenantId());
        return this.page(page, queryWrapper);
    }
}