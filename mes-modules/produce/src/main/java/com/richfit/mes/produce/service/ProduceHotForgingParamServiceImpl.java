package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.HotForgingParam;
import com.richfit.mes.produce.dao.ProduceHotForgingParamMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class ProduceHotForgingParamServiceImpl extends ServiceImpl<ProduceHotForgingParamMapper, HotForgingParam> implements ProduceHotForgingParamService{
    @Autowired
    private ProduceHotForgingParamMapper hotForgingParamMapper;
}
