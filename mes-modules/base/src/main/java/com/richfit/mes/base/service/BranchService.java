package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.Branch;
import com.richfit.mes.common.model.sys.vo.TenantUserVo;

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

    /**
     * 功能描述: 获取车间下质检人员
     *
     * @param branchCode
     * @Author: xinYu.hou
     * @Date: 2022/7/8 14:55
     * @return: List<TenantUserVo>
     **/
    List<TenantUserVo> queryUserList(String branchCode);
}
