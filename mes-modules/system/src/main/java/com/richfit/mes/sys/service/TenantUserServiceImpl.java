package com.richfit.mes.sys.service;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.base.BaseEntity;
import com.richfit.mes.common.core.exception.GlobalException;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.sys.*;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.security.userdetails.TenantUserDetails;
import com.richfit.mes.common.security.util.SecurityUtils;
import com.richfit.mes.sys.dao.TenantUserMapper;
import com.richfit.mes.sys.entity.param.TenantUserQueryParam;
import com.richfit.mes.sys.provider.BaseServiceClient;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

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

    @Resource
    private RoleService roleService;
    @Resource
    private ItemParamService itemParamService;
    @Autowired
    private RoleMenuService roleMenuService;
    @Autowired
    private MenuService menuService;


    @Value("${password.default:mes@123456}")
    private String defaultPassword;

    public static final String RECORD_AUDIT_PEOPLE = "_JLSH";

    @Override
    //@Cacheable(value = CacheConstant.SYS_USER_DETAILS, key = "#uniqueId")
    public TenantUser getByUniqueId(String uniqueId) {
        TenantUser user = this.getOne(new QueryWrapper<TenantUser>()
                .eq("user_account", uniqueId));

        //获取租户对应的ERP—code
        Tenant tenant = tenantService.getById(user.getTenantId());
        user.setTenantErpCode(tenant.getTenantErpCode());
        user.setCompanyCode(tenant.getCompanyCode());
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
        QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", tenantUserVo.getId());
        List<UserRole> roleList = userRoleService.list(wrapper);
        if (roleList != null && roleList.size() > 0) {
            tenantUserVo.setUserRoleType(roleList.get(0).getUserType());
        }
        TenantUserDetails tenantUserDetails = SecurityUtils.getCurrentUser();
        tenantUserVo.setTenantErpCode(tenantUserDetails.getTenantErpCode());
        tenantUserVo.setCompanyCode(tenantUserDetails.getCompanyCode());
        //菜单权限标识 menu_type = 2 的menu_code
        if(!CollectionUtil.isEmpty(tenantUserVo.getRoleList())){
            List<String> roleIds = tenantUserVo.getRoleList().stream().map(item -> item.getId()).collect(Collectors.toList());
            QueryWrapper<RoleMenu> roleMenuQueryWrapper = new QueryWrapper<>();
            roleMenuQueryWrapper.in("role_id",roleIds);
            List<RoleMenu> list = roleMenuService.list(roleMenuQueryWrapper);
            Set<String> menuIds = list.stream().map(item -> item.getMenuId()).collect(Collectors.toSet());
            if(!CollectionUtil.isEmpty(menuIds)){
                QueryWrapper<Menu> menuQueryWrapper = new QueryWrapper<>();
                menuQueryWrapper.in("id",menuIds)
                        .eq("menu_type",2);
                List<Menu> menus = menuService.list(menuQueryWrapper);
                if(!CollectionUtil.isEmpty(menus)){
                    tenantUserVo.setPermissions(menus.stream().map(item->item.getMenuCode()).collect(Collectors.toSet()));
                }
            }
        }
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
    public List<TenantUser> findByIds(List<String> ids) {
        return this.listByIds(ids);
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
        userRoleService.saveBatch(tenantUser.getId(), tenantUser.getRoleIds(), tenantUser.getUserRoleType());
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
            isSuccess = userRoleService.saveBatch(tenantUser.getId(), tenantUser.getRoleIds(), tenantUser.getUserRoleType());
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
            queryWrapper.eq("belong_org_id", branch.getBranchCode())
                    .eq("status", 1);
            tenantUserList.addAll(tenantUserMapper.queryUserList(queryWrapper));
        }
        return tenantUserList;
    }

    @Override
    public List<TenantUserVo> queryUserByBranchCodeList(List<String> branchCodes) {
        List<TenantUserVo> tenantUserList = new ArrayList<>();
        for (String branchCode : branchCodes) {
            CommonResult<List<Branch>> queryCode = baseServiceClient.queryCode(branchCode);
            for (Branch branch : queryCode.getData()) {
                QueryWrapper<TenantUserVo> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("belong_org_id", branch.getBranchCode());
                tenantUserList.addAll(tenantUserMapper.queryUserList(queryWrapper));
            }
        }
        //去重
        tenantUserList = tenantUserList.stream().collect(Collectors
                .collectingAndThen(
                        Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(TenantUserVo::getUserAccount))),
                        ArrayList::new));
        return tenantUserList;
    }

    @Override
    public List<TenantUserVo> queryUserByBranchCodePage(String branchCode) {
        CommonResult<List<Branch>> queryCode = baseServiceClient.queryCode(branchCode);
        List<TenantUserVo> tenantUserList = new ArrayList<>();
        for (Branch branch : queryCode.getData()) {
            QueryWrapper<TenantUserVo> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("belong_org_id", branch.getBranchCode());
            tenantUserList.addAll(tenantUserMapper.queryUserList(queryWrapper));
            queryUserListByBranchCode(branch, tenantUserList);
        }
        return tenantUserList;
    }

    //递归向下查询机构人员
    public void queryUserListByBranchCode(Branch branch, List<TenantUserVo> tenantUserList) {
        if (!ObjectUtil.isEmpty(branch.getBranchList()) && branch.getBranchList().size() > 0) {
            for (Branch child : branch.getBranchList()) {
                QueryWrapper<TenantUserVo> queryWrapper = new QueryWrapper<>();
                queryWrapper.eq("belong_org_id", child.getBranchCode());
                tenantUserList.addAll(tenantUserMapper.queryUserList(queryWrapper));
                queryUserListByBranchCode(child, tenantUserList);
            }
        }
    }


    @Override
    public TenantUserVo queryByUserAccount(String userAccount) {
        QueryWrapper<TenantUserVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account", userAccount);
        TenantUserVo tenantUserVo = tenantUserMapper.queryUser(queryWrapper);
        if (ObjectUtil.isEmpty(tenantUserVo)) {
            throw new GlobalException("用户不存在！", ResultCode.FAILED);
        }
        return tenantUserMapper.queryUser(queryWrapper);
    }

    @Override
    public Map<String, TenantUserVo> queryByUserAccountList(List<String> userAccountList) {
        QueryWrapper<TenantUserVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("user_account", userAccountList);
        List<TenantUserVo> tenantUserVos = tenantUserMapper.queryUserList(queryWrapper);
        Map<String, TenantUserVo> tenantUserVoMap = new HashMap<>();
        if (CollectionUtils.isNotEmpty(tenantUserVos)) {
            tenantUserVoMap = tenantUserVos.stream().collect(Collectors.toMap(x -> x.getUserAccount(), x -> x));
        }
        return tenantUserVoMap;
    }

    @Override
    public List<TenantUserVo> queryByBranchCode(String branchCode) {
        QueryWrapper<TenantUserVo> queryWrapper = new QueryWrapper<>();
        //0 = 普通用户 1 = 本公司质检用户 2 = 租户质检用户
        queryWrapper.notIn("role.user_type", "0");
        queryWrapper.eq("users.belong_org_id", branchCode);
        return tenantUserMapper.queryByBranchCode(queryWrapper);
    }

    @Override
    public List<TenantUserVo> queryByTendId() {
        QueryWrapper<TenantUserVo> queryWrapper = new QueryWrapper<>();
        //0 = 普通用户 1 = 本公司质检用户 2 = 租户质检用户
        queryWrapper.notIn("role.user_type", "0");
        queryWrapper.eq("users.tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        return tenantUserMapper.queryZjUserByTendId(queryWrapper);
    }


    @Override
    public TenantUserVo queryByUserId(String userId) {
        TenantUser tenantUser = this.getById(userId);
        if (null == tenantUser) {
            tenantUser = this.getOne(new QueryWrapper<TenantUser>()
                    .eq("user_account", userId));
        }
        if (Objects.isNull(tenantUser)) {
            throw new GlobalException("user not found with id:" + userId, ResultCode.ITEM_NOT_FOUND);
        }
        TenantUserVo tenantUserVo = new TenantUserVo(tenantUser);
        tenantUserVo.setRoleList(userRoleService.queryRolesByUserId(userId));
        QueryWrapper<UserRole> wrapper = new QueryWrapper<>();
        wrapper.eq("user_id", tenantUserVo.getId());
        List<UserRole> roleList = userRoleService.list(wrapper);
        tenantUserVo.setUserRoleType(roleList.get(0).getUserType());
        return tenantUserVo;
    }

    @Override
    public List<TenantUserVo> queryUserByTenantId(String tenantId) {
        QueryWrapper<TenantUserVo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("tenant_id", tenantId);
        return tenantUserMapper.queryUserList(queryWrapper);
    }

    @Override
    public boolean defaultPassword(List<String> userIds) {
        if (userIds.size() > 0) {
            QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("id", userIds);
            List<TenantUser> tenantUsers = this.list(queryWrapper);
            for (TenantUser tenantUser : tenantUsers) {
                tenantUser.setPasswd(passwordEncoder.encode(defaultPassword));
            }
            this.updateBatchById(tenantUsers);
        }
        return true;
    }

    @Override
    public List<TenantUserVo> queryUserByTenantIdAndRole(String tenantId) {
        //BOMCO_ZJ_JMAQ_QC
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("role_code", "BOMCO_ZJ_JMAQ_QC");
        queryWrapper.eq("tenant_id", tenantId);
        List<Role> roleList = roleService.list(queryWrapper);
        List<String> roleIdList = roleList.stream().map(BaseEntity::getId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(roleIdList)) {
            QueryWrapper<TenantUserVo> queryUser = new QueryWrapper<>();
            queryUser.in("role_id", roleIdList);
            return tenantUserMapper.queryByCondition(queryUser);
        }
        return new ArrayList<>();
    }

    /**
     * 这个方法前端没用 角色配置以改变（慎用）
     *
     * @param classes
     * @param branchCode
     * @param tenantId
     * @return
     */
    @Override
    public List<TenantUserVo> queryQualityInspectionDepartment(String classes, String branchCode, String tenantId) {
        //组装角色标识 1机加  2装配 3热处理 4钢结构
        List<String> codeList = new ArrayList<>();
        if ("1".equals(classes) || StrUtil.isBlank(classes)) {
            String machiningRole = branchCode + "_JMAQ_JJZJ";
            codeList.add(machiningRole);
        }
        if ("2".equals(classes) || StrUtil.isBlank(classes)) {
            String assembleRole = branchCode + "_JMAQ_ZPZJ";
            codeList.add(assembleRole);
        }
        if ("3".equals(classes) || StrUtil.isBlank(classes)) {
            String heatTreatmentRole = branchCode + "_JMAQ_JJZJ";
        }
        if ("4".equals(classes) || StrUtil.isBlank(classes)) {
            String steelworkRole = branchCode + "_JMAQ_JJZJ";
        }
        if ("5".equals(classes) || StrUtil.isBlank(classes)) {
            String machiningRole = branchCode + "_JMAQ_RGZJ";
            codeList.add(machiningRole);
        }
        if (codeList.size() > 0) {
            QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
            //判断如果codeList 为空会报错
            queryWrapper.in("role_code", codeList);
            queryWrapper.eq("tenant_id", tenantId);
            List<Role> roleList = roleService.list(queryWrapper);
            List<String> roleIdList = roleList.stream().map(BaseEntity::getId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(roleIdList)) {
                QueryWrapper<TenantUserVo> queryUser = new QueryWrapper<>();
                queryUser.in("role_id", roleIdList)
                        .eq("status", 1);
                return tenantUserMapper.queryByCondition(queryUser);
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<TenantUserVo> queryQualityInspectionDepartmentByBranchCode(String branchCode) {
        //组装角色标识 根据组织机构区分
        String role = branchCode + "_JMAQ_ZJ";
        if (!StringUtils.isEmpty(role)) {
            QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
            //判断如果codeList 为空会报错
            queryWrapper.eq("role_code", role);
            queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            List<Role> roleList = roleService.list(queryWrapper);
            List<String> roleIdList = roleList.stream().map(BaseEntity::getId).collect(Collectors.toList());
            if (!CollectionUtils.isEmpty(roleIdList)) {
                QueryWrapper<TenantUserVo> queryUser = new QueryWrapper<>();
                queryUser.in("role_id", roleIdList)
                        .eq("status", 1);
                return tenantUserMapper.queryByCondition(queryUser);
            }
        }
        return new ArrayList<>();
    }

    @Override
    public List<TenantUserVo> queryAllQualityUser(String branchCode) {
        //获取当前登录人车间的所有质检人员
        List<TenantUserVo> userList = this.queryQualityInspectionDepartmentByBranchCode(branchCode);
//        List<TenantUserVo> userList = new ArrayList<>();
        //获取质检公司
       /* List<ItemParam> item = null;
        try {
            item = itemParamService.queryItemByCode("qualityManagement");
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException("质检检测部门查询失败", ResultCode.FAILED);
        }
        if (CollectionUtils.isEmpty(item)) {
            throw new GlobalException("未查询到质量检测部门", ResultCode.FAILED);
        }
        //根据租户code 查询tenantId
        List<String> collect = item.stream().map(ItemParam::getCode).collect(Collectors.toList());
        QueryWrapper<Tenant> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("tenant_code", collect);
        List<Tenant> tenantList = tenantService.list(queryWrapper);
        //获取质检租户下所有质检人员
        if (CollectionUtils.isEmpty(userList)) {
            return userList;
        }
        for (Tenant tenantEntity : tenantList) {
            List<TenantUserVo> userVoList = this.queryQualityInspectionDepartment(classes, tenantEntity.getTenantCode(), tenantEntity.getId());
            if (!CollectionUtils.isEmpty(userVoList)) {
                userList.addAll(userVoList);
            }
        }*/
        return userList;
    }

    @Override
    public List<TenantUserVo> queryQualityInspectionUser(String classes) {
        //获取需要查询那个公司下质检人员列表
        List<ItemParam> item = null;
        try {
            item = itemParamService.queryItemByCode("qualityManagement");
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException("质检检测部门查询失败", ResultCode.FAILED);
        }
        if (CollectionUtils.isEmpty(item)) {
            throw new GlobalException("未查询到质量检测部门", ResultCode.FAILED);
        }
        //根据租户code 查询tenantId
        List<String> collect = item.stream().map(ItemParam::getCode).collect(Collectors.toList());
        QueryWrapper<Tenant> queryWrapper = new QueryWrapper<>();
        queryWrapper.in("tenant_code", collect);
        List<Tenant> tenantList = tenantService.list(queryWrapper);
        //获取质检租户下所有质检人员
        List<TenantUserVo> tenantUserList = new ArrayList<>();
        for (Tenant tenant : tenantList) {
            tenantUserList.addAll(this.queryQualityInspectionDepartment(classes, tenant.getTenantCode(), tenant.getId()));
        }
        return tenantUserList;
    }

    @Override
    public List<TenantUserVo> queryAllQualityUserByTenantId(String branchCode, String tenantId) {
        if (!StringUtils.isEmpty(tenantId)) {
            //组装角色标识 根据组织机构区分
            String role = branchCode + "_JMAQ_ZJ";
            if (!StringUtils.isEmpty(role)) {
                QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
                //判断如果codeList 为空会报错
                queryWrapper.eq("role_code", role);
                queryWrapper.eq("tenant_id", tenantId);
                List<Role> roleList = roleService.list(queryWrapper);
                List<String> roleIdList = roleList.stream().map(BaseEntity::getId).collect(Collectors.toList());
                if (!CollectionUtils.isEmpty(roleIdList)) {
                    QueryWrapper<TenantUserVo> queryUser = new QueryWrapper<>();
                    queryUser.in("role_id", roleIdList)
                            .eq("status", 1);
                    return tenantUserMapper.queryByCondition(queryUser);
                }
            }
            /*//获取质检公司
            List<ItemParam> item = null;
            try {
                item = itemParamService.queryItemByCodeAndTenantId("qualityManagement", tenantId);
            } catch (Exception e) {
                e.printStackTrace();
                throw new GlobalException("质检检测部门查询失败", ResultCode.FAILED);
            }
            if (CollectionUtils.isEmpty(item)) {
                throw new GlobalException("未查询到质量检测部门", ResultCode.FAILED);
            }
            //根据租户code 查询tenantId
            List<String> collect = item.stream().map(ItemParam::getCode).collect(Collectors.toList());
            QueryWrapper<Tenant> queryWrapper = new QueryWrapper<>();
            queryWrapper.in("tenant_code", collect);
            List<Tenant> tenantList = tenantService.list(queryWrapper);
            //获取质检租户下所有质检人员
            for (Tenant tenantEntity : tenantList) {
                userList.addAll(this.queryQualityInspectionDepartment(classes, tenantEntity.getTenantCode(), tenantEntity.getId()));
            }*/
            ;
        }
        return null;
    }

    @Override
    public Map usersAccount() {
        Map<String, String> map = new HashMap();
        QueryWrapper<TenantUserVo> queryWrapper = new QueryWrapper<>();
        List<TenantUserVo> users = tenantUserMapper.queryUserList(queryWrapper);
        for (TenantUserVo user : users) {
            map.put(user.getUserAccount(), user.getEmplName());
        }
        return map;
    }

    @Override
    public List<TenantUserVo> queryAuditsList(String branchCode) {
        List<TenantUserVo> tenantUserVos = new ArrayList<>();
        QueryWrapper<Role> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_code", branchCode + RECORD_AUDIT_PEOPLE);
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId())
                .eq("org_id", branchCode);
        List<Role> roleList = roleService.list(queryWrapper);
        List<String> roleIdList = roleList.stream().map(BaseEntity::getId).collect(Collectors.toList());
        if (!CollectionUtils.isEmpty(roleIdList)) {
            QueryWrapper<TenantUserVo> queryUser = new QueryWrapper<>();
            queryUser.in("role_id", roleIdList);
            tenantUserVos = tenantUserMapper.queryByCondition(queryUser);
        }
        return tenantUserVos;
    }

    @Override
    public List<TenantUser> queryClass(String userAccount) {
        QueryWrapper<TenantUser> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_account",userAccount);
        TenantUser tenantUser = this.getOne(queryWrapper);
        QueryWrapper<TenantUser> tenantUserQueryWrapper = new QueryWrapper<>();
        tenantUserQueryWrapper.eq("belong_org_id",tenantUser.getBelongOrgId());
        return this.list(tenantUserQueryWrapper);
    }

}
