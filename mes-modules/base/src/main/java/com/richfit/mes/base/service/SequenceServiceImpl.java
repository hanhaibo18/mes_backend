package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.base.Sequence;
import com.richfit.mes.base.dao.SequenceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author mafeng
 * @Description 工艺服务
 */
@Service
public class SequenceServiceImpl extends ServiceImpl<SequenceMapper, Sequence> implements SequenceService{

    @Autowired
    private SequenceMapper sequenceMapper;

    public IPage<Sequence> selectPage(Page page, QueryWrapper<Sequence> qw)
    {
        return  sequenceMapper.selectPage(page, qw);
    }
    
}
