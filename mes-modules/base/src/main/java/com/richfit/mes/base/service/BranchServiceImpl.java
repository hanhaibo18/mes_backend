package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.dao.BranchMapper;
import com.richfit.mes.base.provider.SystemServiceClient;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.sys.Tenant;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;
import com.richfit.mes.common.security.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

/**
 * @author sun
 * @Description 组织机构服务
 */
@Service
public class BranchServiceImpl extends ServiceImpl<BranchMapper, Branch> implements BranchService {

    @Autowired
    private BranchMapper branchMapper;

    @Resource
    private SystemServiceClient systemServiceClient;

    @Override
    public Branch branchErpCode(Branch branch) {
        CommonResult<Tenant> tenant = systemServiceClient.getTenant(branch.getTenantId());
        branch.setRemark(tenant.getData().getTenantErpCode());
        return branch;
    }

    @Override
    public List<Branch> queryCode(String branchCode) {
        List<Branch> branchList = new ArrayList<Branch>();
        String tenantId = SecurityUtils.getCurrentUser().getTenantId();
        if (StringUtils.isNullOrEmpty(branchCode)) {
            QueryWrapper<Branch> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("branch_code", SecurityUtils.getCurrentUser().getBelongOrgId());
            queryWrapper.eq("tenant_id", tenantId);
            branchList = this.list(queryWrapper);
        } else {
            QueryWrapper<Branch> queryWrapperTop = new QueryWrapper<>();
            queryWrapperTop.eq("branch_code", branchCode);
            queryWrapperTop.eq("tenant_id", tenantId);
            branchList = this.list(queryWrapperTop);
        }
        if (branchList.isEmpty()) {
            QueryWrapper<Branch> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("branch_code", branchCode)
                    .eq("tenant_id", tenantId);
            return this.list(queryWrapper);
        }
        for (Branch branch : branchList) {
            queryByBranchCode(branch);
        }
        return branchList;
    }

    @Override
    public List<TenantUserVo> queryUserList(String branchCode) {
        List<Branch> branchList = new ArrayList<Branch>();
        List<TenantUserVo> tenantUserVo = new ArrayList<TenantUserVo>();
        QueryWrapper<Branch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("branch_code", branchCode);
        queryWrapper.eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        branchList = this.list(queryWrapper);
        if (!branchList.isEmpty()) {
            //不为空查询人员信 并进行递归查询
            for (Branch branch : branchList) {
                tenantUserVo.addAll(systemServiceClient.queryByBranchCode(branchCode));
            }
            queryByBranchCode(branchList, tenantUserVo);
        }
        return tenantUserVo;
    }

    @Override
    public List<Branch> queryAllCode() {
        List<Branch> branchList = new ArrayList<>();
        QueryWrapper<Branch> queryWrapper = new QueryWrapper<>();
        queryWrapper.isNull("main_branch_code");
        List<Branch> branches = this.list(queryWrapper);
        Branch branch = new Branch();
        branch.setBranchName("总公司");
        branch.setBranchList(branches);
        branchList.add(branch);
        for (Branch branch1 : branches) {
            queryByBranchCode(branch1);
        }
        return branchList;
    }

    private void queryByBranchCode(List<Branch> branchList, List<TenantUserVo> users) {
        for (Branch branch : branchList) {
            QueryWrapper<Branch> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("main_branch_code", branch.getBranchCode())
                    .eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
            List<Branch> branches = this.list(queryWrapper);
            if (branches.isEmpty()) {
                continue;
            }
            for (Branch branch1 : branches) {
                users.addAll(systemServiceClient.queryByBranchCode(branch1.getBranchCode()));
            }
            queryByBranchCode(branches, users);
            branch.setBranchList(branches);
        }
    }

    private void queryByBranchCode(Branch branch) {
        QueryWrapper<Branch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("main_branch_code", branch.getBranchCode())
                .eq("tenant_id", SecurityUtils.getCurrentUser().getTenantId());
        List<Branch> branches = this.list(queryWrapper);
        for (Branch branch1 : branches) {
            queryByBranchCode(branch1);
        }
        branch.setBranchList(branches);
    }
}
