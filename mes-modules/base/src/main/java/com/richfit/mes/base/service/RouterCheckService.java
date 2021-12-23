package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.RouterCheck;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author 马峰
 * @Description 工序技术要求
 */
public interface RouterCheckService extends IService<RouterCheck> {
    
    public IPage<RouterCheck> selectPage(Page page, QueryWrapper<RouterCheck> qw);
}
