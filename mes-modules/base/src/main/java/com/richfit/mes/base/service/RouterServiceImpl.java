package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.RouterMapper;
import com.richfit.mes.common.model.base.Router;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author mafeng
 * @Description 工艺服务
 */
@Service
public class RouterServiceImpl extends ServiceImpl<RouterMapper, Router> implements RouterService {

    @Autowired
    private RouterMapper routerMapper;

    public IPage<Router> selectPage(Page page, QueryWrapper<Router> qw) {
        return routerMapper.selectPage(page, qw);
    }

    /*
        查询列表后，直接查询每条工艺的历史工艺，一起返回
     */
    @Override
    public IPage<Router> selectPageAndChind(Page page, QueryWrapper<Router> qw) {
        IPage<Router> routers = this.page(page, qw);

        QueryWrapper<Router> queryWrapper = new QueryWrapper<Router>();

        for (Router u : routers.getRecords()) {
            queryWrapper.eq("router_no", u.getRouterNo());
            queryWrapper.eq("branch_code", u.getBranchCode());
            queryWrapper.eq("tenant_id", u.getTenantId());
            queryWrapper.eq("status", 2);    //历史版本
            u.setChildren(this.list(queryWrapper));
        }

        return routers;
    }

}
