package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.model.sys.UserRole;
import com.richfit.mes.sys.dao.UserRoleMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author sun
 * @Description 用户角色服务
 */
@Service
public class UserRoleServiceImpl extends ServiceImpl<UserRoleMapper, UserRole> implements UserRoleService {

    @Override
    public boolean saveBatch(String userId, Set<String> roleIds, String userType) {
        baseMapper.deleteByUserId(userId);
        List<UserRole> userRoleList = roleIds.stream().map(roleId -> {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);
            userRole.setUserType(userType);
            return userRole;
        }).collect(Collectors.toList());
        return this.saveBatch(userRoleList);
    }

    @Override
    public boolean removeByUserId(String userId) {
        return baseMapper.deleteByUserId(userId);
    }

    @Override
    public Set<String> queryByUserId(String userId) {
        QueryWrapper<UserRole> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<UserRole> userRoleList = list(queryWrapper);
        return userRoleList.stream().map(UserRole::getRoleId).collect(Collectors.toSet());
    }

    @Override
    public List<Role> queryRolesByUserId(String userId) {
        return baseMapper.queryRolesByUserId(userId);
    }
}
