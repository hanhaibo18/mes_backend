package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.Branch;

import java.util.List;

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

    /**
     * 功能描述: 根据传入上级branchCode 查询子Code列表
     *
     * @param branchCode
     * @Author: xinYu.hou
     * @return: List<Branch>
     **/
    List<Branch> queryCode(String branchCode);

}
