package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.produce.dao.SjtjMapper;
import com.richfit.mes.produce.entity.SjtjDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;


@Component
public class SjtjServiceImpl extends ServiceImpl<SjtjMapper, SjtjDto> implements SjtjService {
    @Autowired
    private SjtjMapper sjtjMapper;

    public List<SjtjDto> query1(String branchCode, String createTime, String endTime) {
        List<SjtjDto> list = sjtjMapper.query1(branchCode, createTime, endTime);
        return list;
    }

}
