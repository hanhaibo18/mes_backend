package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.sys.Role;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.sys.dao.RoleMapper;
import com.richfit.mes.sys.entity.param.RoleQueryParam;
import com.richfit.mes.sys.provider.BaseServiceClient;
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
    private UserRoleService userRoleService;

    private final static String ADMIN_ROLE_CODE = "role_tenant_admin";

    @Autowired
    private BaseServiceClient baseServiceClient;

    @Override
    public boolean add(Role role) {
        return this.save(role);
    }

    @Override
    public boolean batchAdd(List<Role> roles) {
        return this.saveBatch(roles);
    }

    @Override
    public boolean delete(String id) {
        return this.removeById(id);
    }

    @Override
    public void addTenantAdminRole(String tenantId) {
        Role adminRole = new Role();
        adminRole.setTenantId(tenantId);
        adminRole.setRoleCode(ADMIN_ROLE_CODE);
        adminRole.setRoleType(ADMIN_ROLE_CODE);
        adminRole.setRoleName("租户管理员");
        adminRole.setRoleDesc("租户管理员");

        this.save(adminRole);
    }

    @Override
    public Role getAdminRole(String tenantId) {
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_code", ADMIN_ROLE_CODE);
        queryWrapper.eq("tenant_id", tenantId);

        List<Role> roleList = this.list(queryWrapper);
        //如果没有，则直接创建一个
        if (roleList.size() == 0) {
            this.addTenantAdminRole(tenantId);
            return getAdminRole(tenantId);
        }

        return roleList.get(0);
    }

    @Override
    public boolean isTenantAdminRole(String roleId) {
        if (StringUtils.isBlank(roleId)) {
            return false;
        }
        return this.get(roleId).getRoleCode().equals(ADMIN_ROLE_CODE);
    }

    @Override
    public boolean update(Role role) {
        return this.updateById(role);
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

        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());

        return fillBranchName(this.page(page, queryWrapper));
    }

    private IPage<Role> fillBranchName(IPage<Role> roleIPage) {

        List<Branch> branchList = baseServiceClient.selectBranchChildByCode("").getData();

        for (Role role : roleIPage.getRecords()) {
            for (Branch b : branchList) {
                if (b.getBranchCode().equals(role.getOrgId())) {
                    role.setOrgName(b.getBranchName());

                }
            }
        }
        return roleIPage;
    }
}
