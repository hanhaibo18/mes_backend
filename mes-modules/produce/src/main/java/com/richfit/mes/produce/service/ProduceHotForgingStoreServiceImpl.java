package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.HotForgingStore;
import com.richfit.mes.produce.dao.ProduceHotForgingStoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProduceHotForgingStoreServiceImpl extends ServiceImpl<ProduceHotForgingStoreMapper, HotForgingStore> implements ProduceHotForgingStoreService{
    @Autowired
    private ProduceHotForgingStoreMapper hotForgingStoreMapper;

}
