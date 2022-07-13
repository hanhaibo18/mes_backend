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
        //获取第二级别参数
        for (Branch branch1 : branchList) {
            QueryWrapper<Branch> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("main_branch_code", branch1.getBranchCode())
                    .eq("tenant_id", tenantId);
            List<Branch> list2 = this.list(queryWrapper);
            //获取第三级参数
            if (list2 != null && !list2.isEmpty()) {
                for (Branch branch2 : list2) {
                    QueryWrapper<Branch> query = new QueryWrapper<>();
                    query.eq("main_branch_code", branch2.getBranchCode())
                            .eq("tenant_id", tenantId);
                    List<Branch> list3 = this.list(query);
                    //添加第三级数据到第二级别
                    if (list3 != null && !list3.isEmpty()) {
                        branch2.setBranchList(list3);
                    }
                }
                //第二级别数据添加到第三级
                branch1.setBranchList(list2);
            }
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
}
