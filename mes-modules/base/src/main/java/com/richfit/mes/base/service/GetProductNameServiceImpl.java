package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.GetProductNameMapper;
import com.richfit.mes.common.model.base.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author gwb
 */
@Slf4j
@Service
public class GetProductNameServiceImpl extends ServiceImpl<GetProductNameMapper, Product> implements GetProductNameService {
    @Autowired
    private GetProductNameMapper workingHoursMapper;


    @Override
    public List<Product> queryProductName(QueryWrapper<List> wrapper) {
        return workingHoursMapper.queryProductName(wrapper);
    }
}
