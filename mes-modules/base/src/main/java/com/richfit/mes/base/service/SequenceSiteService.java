package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.SequenceSite;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author 马峰
 * @Description 工艺工位关联服务
 */
public interface SequenceSiteService extends IService<SequenceSite> {
    
    public IPage<SequenceSite> selectPage(Page page, QueryWrapper<SequenceSite> qw);
}
