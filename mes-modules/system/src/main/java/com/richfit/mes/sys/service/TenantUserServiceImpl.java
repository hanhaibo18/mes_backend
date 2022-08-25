package com.richfit.mes.sys.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.sys.TenantUser;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.sys.dao.TenantUserMapper;
import com.richfit.mes.sys.entity.param.TenantUserQueryParam;
import com.richfit.mes.sys.provider.BaseServiceClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 租户用户 服务实现类
 * </p>
 *
 * @author gaoliang
 * @since 2020-05-25
 */
@Service
public class TenantUserServiceImpl extends ServiceImpl<TenantUserMapper, TenantUser> implements TenantUserService {

    @Autowired
    public PasswordEncoder passwordEncoder;

    @Autowired
    private UserRoleService userRoleService;

    @Autowired
    private TenantUserMapper tenantUserMapper;

    @Autowired
    private BaseServiceClient baseServiceClient;

    @Autowired
    private TenantService tenantService;

    @Override
    //@Cacheable(value = CacheConstant.SYS_USER_DETAILS, key = "#uniqueId")
    public TenantUser getByUniqueId(String uniqueId) {
        TenantUser user = this.getOne(new QueryWrapper<TenantUser>()
                .eq("user_account", uniqueId));

        //获取租户对应的ERP—code
        user.setTenantErpCode(tenantService.getById(user.getTenantId()).getTenantErpCode());

        if (Objects.isNull(user)) {
            throw new GlobalException("user not found with uniqueId:" + uniqueId, ResultCode.ITEM_NOT_FOUND);
        }
        user.setRoleIds(userRoleService.queryByUserId(user.getId()));
        return user;
    }

    @Override
    public TenantUserVo get(String id) {
        TenantUser tenantUser = this.getById(id);
        if (null == tenantUser) {
            tenantUser = this.getOne(new QueryWrapper<TenantUser>()
                    .eq("user_account", id));
        }
        if (Objects.isNull(tenantUser)) {
            throw new GlobalException("user not found with id:" + id, ResultCode.ITEM_NOT_FOUND);
        }
        TenantUserVo tenantUserVo = new TenantUserVo(tenantUser);
        tenantUserVo.setRoleList(userRoleService.queryRolesByUserId(id));
        return tenantUserVo;
    }

    @Override
    public TenantUserVo findById(String id) {
        TenantUser tenantUser = this.getById(id);
        if (Objects.isNull(tenantUser)) {
            throw new GlobalException("user not found with id:" + id, ResultCode.ITEM_NOT_FOUND);
        }
        return new TenantUserVo(tenantUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean add(TenantUser tenantUser) {
        if (StringUtils.isNotBlank(tenantUser.getPasswd())) {
            tenantUser.setPasswd(passwordEncoder.encode(tenantUser.getPasswd()));
        }
        //设备默认的belongOrgId为租户的tenantCode
        if (StringUtils.isEmpty(tenantUser.getBelongOrgId())) {
            tenantUser.setBelongOrgId(tenantService.getById(tenantUser.getTenantId()).getTenantCode());
        }

        boolean inserts = this.save(tenantUser);
        userRoleService.saveBatch(tenantUser.getId(), tenantUser.getRoleIds());
        return inserts;
    }

    @Override
    public IPage<TenantUserVo> query(Page<TenantUser> page, TenantUserQueryParam tenantUserQueryParam) {
        if (tenantUserQueryParam.getOrgId() != null) {
            List<Branch> branchList = baseServiceClient.selectBranchChildByCode(tenantUserQueryParam.getOrgId()).getData();
            StringBuilder strBuilder = new StringBuilder();
            for (Branch b : branchList) {
                strBuilder.append(b.getBranchCode()).append(",");
            }
            if (strBuilder.length() > 0) {
                tenantUserQueryParam.setOrgId(strBuilder.substring(0, strBuilder.length() - 1));
            }
        }
        List<GrantedAuthority> authorities = new ArrayList<>(SecurityUtils.getCurrentUser().getAuthorities());
        boolean isAdmin = false;
        for (GrantedAuthority authority : authorities) {
            //超级管理员 ROLE_12345678901234567890000000000000
            if ("ROLE_12345678901234567890000000000000".equals(authority.getAuthority())) {
                isAdmin = true;
                break;
            }
        }
        return fillBranchName(tenantUserMapper.queryTenantUser(page, tenantUserQueryParam, isAdmin));
    }

    @Override
    public IPage<TenantUserVo> queryAdmin(Page<TenantUser> page, TenantUserQueryParam tenantUserQueryParam) {
        return tenantUserMapper.queryTenantAdmin(page, tenantUserQueryParam);
    }

    /**
     * 拼接所属、所在组织机构
     */
    private IPage<TenantUserVo> fillBranchName(IPage<TenantUserVo> tenantUserVoIPage) {

        List<Branch> branchList = baseServiceClient.selectBranchChildByCode("").getData();
        for (TenantUserVo user : tenantUserVoIPage.getRecords()) {
            for (Branch b : branchList) {
                if (b.getBranchCode().equals(user.getBelongOrgId())) {
                    user.setBelongOrgName(b.getBranchName());
                }
                if (b.getBranchCode().equals(user.getOrgId())) {
                    user.setOrgName(b.getBranchName());
                }
            }
        }
        return tenantUserVoIPage;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(TenantUser tenantUser) {
        if (StringUtils.isNotBlank(tenantUser.getPasswd())) {
            tenantUser.setPasswd(passwordEncoder.encode(tenantUser.getPasswd()));
        }
        boolean isSuccess = this.updateById(tenantUser);
        if (tenantUser.getRoleIds() != null && !tenantUser.getRoleIds().isEmpty()) {
            isSuccess = userRoleService.saveBatch(tenantUser.getId(), tenantUser.getRoleIds());
        }

        return isSuccess;
    }

    @Override
    public boolean delete(String id) {
        this.removeById(id);
        return userRoleService.removeByUserId(id);
    }

    @Override
    public boolean updatePassword(String id, String oldPassword, String newPassword) {
        TenantUser tenantUser = this.getById(id);
        if (Objects.isNull(tenantUser)) {
            throw new GlobalException("user not found with id:" + id, ResultCode.ITEM_NOT_FOUND);
        }

        if (!passwordEncoder.matches(oldPassword, tenantUser.getPasswd())) {
            throw new GlobalException("old password not match", ResultCode.FAILED);
        } else {
            tenantUser.setPasswd(newPassword);
            return this.update(tenantUser);
        }
    }

    @Override
    public IPage<TenantUserVo> queryByName(Page<TenantUser> page, String userAccount, String tenantId) {

        return tenantUserMapper.selectPage(page,
                new QueryWrapper()
                        .eq(StringUtils.isNotEmpty(userAccount), "user_account", userAccount)
//                用户名全局唯一，不应加租户限制
//                        .eq(StringUtils.isNotEmpty(tenantId), "tenant_id", tenantId)
        );

    }


    @Override
    public List<TenantUserVo> queryUserByBranchCode(String branchCode) {
        CommonResult<List<Branch>> queryCode = baseServiceClient.queryCode(branchCode);
        List<TenantUserVo> tenantUserList = new ArrayList<>();
        for (Branch branch : queryCode.getData()) {
            QueryWrapper<TenantUserVo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("belong_org_id", branch.getBranchCode());
            tenantUserList.addAll(tenantUserMapper.queryUserList(queryWrapper));
        }
        return tenantUserList;
    }

    @Override
    public TenantUserVo queryByUserAccount(String userAccount) {
        QueryWrapper<TenantUserVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        return tenantUserMapper.queryUser(queryWrapper);
    }

    @Override
    public List<TenantUserVo> queryByBranchCode(String branchCode) {
        QueryWrapper<TenantUserVo> queryWrapper = new QueryWrapper<>();
        //Todo:后期放到配置文件中 方便配置
        queryWrapper.eq("role.role_id", "0697-63FB-476E-BD86-9E157B336ED7");
        queryWrapper.eq("users.belong_org_id", branchCode);
        queryWrapper.eq("users.tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        return tenantUserMapper.queryByBranchCode(queryWrapper);
    }

}
