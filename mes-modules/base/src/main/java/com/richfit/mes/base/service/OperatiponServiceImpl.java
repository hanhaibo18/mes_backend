package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.base.Operatipon;
import com.richfit.mes.base.dao.OperatiponMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author mafeng
 * @Description 工艺服务
 */
@Service
public class OperatiponServiceImpl extends ServiceImpl<OperatiponMapper, Operatipon> implements OperatiponService{

    @Autowired
    private OperatiponMapper operatiponMapper;

    public IPage<Operatipon> selectPage(Page page, QueryWrapper<Operatipon> qw)
    {
        return  operatiponMapper.selectPage(page, qw);
    }
    
}
