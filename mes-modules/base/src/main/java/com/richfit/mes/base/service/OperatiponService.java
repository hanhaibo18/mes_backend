package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.Operatipon;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * @author 马峰
 * @Description 工艺服务
 */
public interface OperatiponService extends IService<Operatipon> {
    
    public IPage<Operatipon> selectPage(Page page, QueryWrapper<Operatipon> qw);
}
