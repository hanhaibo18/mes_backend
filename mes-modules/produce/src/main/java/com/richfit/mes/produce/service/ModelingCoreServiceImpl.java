package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.ModelingCore;
import com.richfit.mes.produce.dao.ModelingCoreMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 造型/制芯工序报工记录表(ModelingCore)表服务实现类
 *
 * @author makejava
 * @since 2023-05-08 10:17:16
 */
@Service
public class ModelingCoreServiceImpl extends ServiceImpl<ModelingCoreMapper, ModelingCore> implements ModelingCoreService {

    @Autowired
    private ModelingCoreMapper modelingCoreMapper;

    @Override
    public ModelingCore queryCacheByItemId(String tiId) {
        return modelingCoreMapper.queryCacheByItemId(tiId);
    }
}

