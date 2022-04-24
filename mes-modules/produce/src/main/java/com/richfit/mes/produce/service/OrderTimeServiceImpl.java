package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.produce.dao.OrderTimeMapper;
import com.richfit.mes.common.model.produce.OrderTime;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gwb
 */
@Slf4j
@Service
public class OrderTimeServiceImpl extends ServiceImpl<OrderTimeMapper, OrderTime> implements OrderTimeService {


    @Autowired
    private OrderTimeMapper orderTimeMapper;

    @Override
    public  List<OrderTime> select(Page page, QueryWrapper<List> wrapper){
        return orderTimeMapper.query(page,wrapper);
    }

}
