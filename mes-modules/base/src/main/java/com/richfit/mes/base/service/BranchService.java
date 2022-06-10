package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.Branch;

/**
 * @author 王瑞
 * @Description 组织结构服务
 */
public interface BranchService extends IService<Branch> {

    /**
     * @author 鲁志强
     * @Description 重新封装Branch加入erpcode
     */
    Branch branchErpCode(Branch branch);
}
