package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.RouterCheck;

import java.util.List;

/**
 * @author 马峰
 * @Description 工序技术要求
 */
public interface RouterCheckService extends IService<RouterCheck> {

    public IPage<RouterCheck> selectPage(Page page, QueryWrapper<RouterCheck> qw);

    /**
     * 功能描述:根据optId查询工序质量资料
     *
     * @param optId
     * @param type
     * @param branchCode
     * @param tenantId
     * @Author: xinYu.hou
     * @Date: 2022/7/20 16:13
     * @return: List<RouterCheck>
     **/
    List<RouterCheck> queryRouterList(String optId, String type, String branchCode, String tenantId);
}
