package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.SequenceMapper;
import com.richfit.mes.common.model.base.Sequence;
import com.richfit.mes.common.model.util.OptNameUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author mafeng
 * @Description 工艺服务
 */
@Service
public class SequenceServiceImpl extends ServiceImpl<SequenceMapper, Sequence> implements SequenceService {

    @Autowired
    private SequenceMapper sequenceMapper;

    @Override
    public IPage<Sequence> selectPage(Page page, QueryWrapper<Sequence> qw) {
        return sequenceMapper.selectPage(page, qw);
    }

    @Override
    public String queryCraft(String optName, String branchCode) {
        return sequenceMapper.queryVersion(OptNameUtil.queryEqSql("sequence.opt_name ", optName), branchCode);
    }

}
