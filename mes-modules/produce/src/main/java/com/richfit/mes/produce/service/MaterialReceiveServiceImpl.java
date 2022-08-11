package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.MaterialReceive;
import com.richfit.mes.produce.dao.MaterialReceiveMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * @Description TODO
 * @Author ang
 * @Date 2022/7/29 17:55
 */
@Slf4j
@Service
public class MaterialReceiveServiceImpl extends ServiceImpl<MaterialReceiveMapper, MaterialReceive> implements MaterialReceiveService{

    @Autowired
    MaterialReceiveMapper materialReceiveMapper;

    @Override
    public String getlastTime() {
       return materialReceiveMapper.getlastTime();
    }

    @Override
    public Page<MaterialReceive> getPage(Page<MaterialReceive> materialReceivePage, QueryWrapper<MaterialReceive> queryWrapper) {
        return materialReceiveMapper.getPage(materialReceivePage,queryWrapper);
    }
}
