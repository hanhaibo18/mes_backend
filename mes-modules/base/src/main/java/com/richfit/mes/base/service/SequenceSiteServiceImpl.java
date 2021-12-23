package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.base.SequenceSite;
import com.richfit.mes.base.dao.SequenceSiteMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author mafeng
 * @Description 工艺工位关联服务
 */
@Service
public class SequenceSiteServiceImpl extends ServiceImpl<SequenceSiteMapper, SequenceSite> implements SequenceSiteService{

    @Autowired
    private SequenceSiteMapper sequenceSiteMapper;

    public IPage<SequenceSite> selectPage(Page page, QueryWrapper<SequenceSite> qw)
    {
        return  sequenceSiteMapper.selectPage(page, qw);
    }
    
}
