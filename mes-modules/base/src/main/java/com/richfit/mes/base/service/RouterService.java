package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.Router;

/**
 * @author 马峰
 * @Description 工艺服务
 */
public interface RouterService extends IService<Router> {

    public IPage<Router> selectPage(Page page, QueryWrapper<Router> qw);

    public IPage<Router> selectPageAndChind(Page page, QueryWrapper<Router> qw);
}
