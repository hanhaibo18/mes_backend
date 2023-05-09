package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.ModelingCore;

/**
 * 造型/制芯工序报工记录表(ModelingCore)表服务接口
 *
 * @author makejava
 * @since 2023-05-08 10:17:16
 */
public interface ModelingCoreService extends IService<ModelingCore> {

    ModelingCore queryCacheByItemId(String tiId);
}

