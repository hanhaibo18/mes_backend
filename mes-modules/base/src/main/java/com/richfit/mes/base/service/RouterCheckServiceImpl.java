package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.base.RouterCheck;
import com.richfit.mes.base.dao.RouterCheckMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author mafeng
 * @Description 工序技术要求
 */
@Service
public class RouterCheckServiceImpl extends ServiceImpl<RouterCheckMapper, RouterCheck> implements RouterCheckService{

    @Autowired
    private RouterCheckMapper routerCheckMapper;

    public IPage<RouterCheck> selectPage(Page page, QueryWrapper<RouterCheck> qw)
    {
        return  routerCheckMapper.selectPage(page, qw);
    }
    
}
