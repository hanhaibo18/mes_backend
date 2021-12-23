package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.base.dao.BranchMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author sun
 * @Description 组织机构服务
 */
@Service
public class BranchServiceImpl extends ServiceImpl<BranchMapper, Branch> implements BranchService{

    @Autowired
    private BranchMapper branchMapper;

}
