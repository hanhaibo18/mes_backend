package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.mysql.cj.util.StringUtils;
import com.richfit.mes.base.dao.BranchMapper;
import com.richfit.mes.base.entity.TreeVo;
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
import java.util.stream.Collectors;

/**
 * @author sun
 * @Description 组织机构服务
 */
@Service
public class BranchServiceImpl extends ServiceImpl<BranchMapper, Branch> implements BranchService {

    private final static String BOMCO_ZJ = "BOMCO_ZJ";

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
        if (StringUtils.isNullOrEmpty(branchCode)) {
            QueryWrapper<Branch> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("branch_code", SecurityUtils.getCurrentUser().getBelongOrgId());
            branchList = this.list(queryWrapper);
        } else {
            QueryWrapper<Branch> queryWrapperTop = new QueryWrapper<>();
            queryWrapperTop.eq("branch_code", branchCode);
            branchList = this.list(queryWrapperTop);
        }
        if (branchList.isEmpty()) {
            QueryWrapper<Branch> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("branch_code", branchCode);
            return this.list(queryWrapper);
        }
        for (Branch branch : branchList) {
            queryByBranchCode(branch);
        }
        return branchList;
    }

    @Override
    public List<TenantUserVo> queryUserList(String branchCode) {
        //先获取所有车间
        List<Branch> branchList = new ArrayList<>();
        List<TenantUserVo> tenantUserVo = new ArrayList<>();
        QueryWrapper<Branch> queryWrapper = new QueryWrapper<>();
        branchList = this.list(queryWrapper);
        //在获取质检人员
        if (!branchList.isEmpty()) {
            //不为空查询人员
            for (Branch branch : branchList) {
                tenantUserVo.addAll(systemServiceClient.queryByBranchCode(branch.getBranchCode()));
            }
        }
        return tenantUserVo;
    }

    @Override
    public List<TenantUserVo> queryUsers(String auditBy) {
        List<TenantUserVo> tenantUserVo = new ArrayList<>();
        if(!StringUtils.isNullOrEmpty(auditBy)){
            TenantUserVo user = systemServiceClient.getUserById(auditBy).getData();
            tenantUserVo.add(user);
        }else{
            //先获取所有车间
            tenantUserVo.addAll(systemServiceClient.queryUserByBranchCode(BOMCO_ZJ).getData());
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
        branch.setBranchCode("ranch_code");
        branch.setBranchList(branches);
        branchList.add(branch);
        for (Branch branch1 : branches) {
            queryByBranchCode(branch1);
        }
        return branchList;
    }

    @Override
    public List<TreeVo> queryUserTreeList(List<String> branchCodeList) {
        //返回列表
        List<TreeVo> treeList = new ArrayList<>();
        TreeVo treeVo = new TreeVo();
        treeVo.setName("总公司");
        treeList.add(treeVo);
        //暂存 两个车间参数list
        List<TreeVo> treeVoList = new ArrayList<>();
        for (String code : branchCodeList) {
            //查询车间
            QueryWrapper<Branch> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("branch_code", code);
            Branch branch = this.getOne(queryWrapper);
            TreeVo tree = new TreeVo();
            tree.setName(branch.getBranchName());
            tree.setCode(branch.getBranchCode());
            //查询人员 查询挂在本车间下的人员 并拼接到 当前车间下面
            List<TenantUserVo> userList = systemServiceClient.queryByBranchCode(branch.getBranchCode());
            List<TreeVo> list = userList.stream().map(user -> {
                TreeVo treeEntity = new TreeVo();
                treeEntity.setCode(user.getUserAccount());
                treeEntity.setName(user.getEmplName());
                treeEntity.setUserType(user.getUserRoleType());
                return treeEntity;
            }).collect(Collectors.toList());
            tree.setTree(list);
            treeVoList.add(tree);
            //递归查询
            queryUserByBranchCode(tree);
        }
        //总公司下两个车间数据
        treeVo.setTree(treeVoList);
        return treeList;
    }

    @Override
    public List<Branch> queryBranchCodeList(List<String> branchCodeList) {
        //返回列表
        List<Branch> treeList = new ArrayList<>();
        Branch branch = new Branch();
        branch.setBranchName("总公司");
        treeList.add(branch);
        //对总公司下存储车间结点
        List<Branch> list = new ArrayList<>();
        for (String code : branchCodeList) {
            //查询车间
            QueryWrapper<Branch> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("branch_code", code);
            Branch branchEntity = this.getOne(queryWrapper);
            queryByBranchCode(branchEntity);
            list.add(branchEntity);
        }
        branch.setBranchList(list);
        return treeList;
    }

    private void queryByBranchCode(Branch branch) {
        QueryWrapper<Branch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("main_branch_code", branch.getBranchCode());
        List<Branch> branches = this.list(queryWrapper);
        for (Branch branch1 : branches) {
            queryByBranchCode(branch1);
        }
        branch.setBranchList(branches);
    }

    private void queryUserByBranchCode(TreeVo tree) {
        QueryWrapper<Branch> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("main_branch_code", tree.getCode());
        List<Branch> branches = this.list(queryWrapper);
        //暂存车间数据
        List<TreeVo> treeVoList = new ArrayList<>();
        for (Branch branch1 : branches) {
            //查询人员 查询挂在本车间下的人员 并拼接到 当前车间里
            List<TenantUserVo> userList = systemServiceClient.queryByBranchCode(branch1.getBranchCode());
            List<TreeVo> list = userList.stream().map(user -> {
                TreeVo treeEntity = new TreeVo();
                treeEntity.setCode(user.getUserAccount());
                treeEntity.setName(user.getEmplName());
                treeEntity.setUserType(user.getUserRoleType());
                return treeEntity;
            }).collect(Collectors.toList());
            TreeVo treeVo = new TreeVo();
            treeVo.setCode(branch1.getBranchCode());
            treeVo.setName(branch1.getBranchName());
            treeVo.setTree(list);
            treeVoList.add(treeVo);
            //递归
            queryUserByBranchCode(treeVo);
        }
        tree.getTree().addAll(treeVoList);
    }
}
