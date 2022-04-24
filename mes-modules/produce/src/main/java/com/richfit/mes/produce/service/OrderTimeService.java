package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.OrderTime;

import java.util.List;

/**
 * @author gwb
 */
public interface OrderTimeService extends IService<OrderTime> {

    List<OrderTime> select(Page page, QueryWrapper<List> wrapper );


}
