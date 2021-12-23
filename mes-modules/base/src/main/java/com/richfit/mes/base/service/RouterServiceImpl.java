package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.base.Router;
import com.richfit.mes.base.dao.RouterMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author mafeng
 * @Description 工艺服务
 */
@Service
public class RouterServiceImpl extends ServiceImpl<RouterMapper, Router> implements RouterService{

    @Autowired
    private RouterMapper routerMapper;

    public IPage<Router> selectPage(Page page, QueryWrapper<Router> qw)
    {
        return  routerMapper.selectPage(page, qw);
    }
    
}
