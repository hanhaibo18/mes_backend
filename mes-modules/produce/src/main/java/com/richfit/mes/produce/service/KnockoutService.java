package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.Knockout;

/**
 * 扣箱工序报工记录表(Knockout)表服务接口
 *
 * @author makejava
 * @since 2023-05-08 10:18:48
 */
public interface KnockoutService extends IService<Knockout> {

    Knockout queryCacheByItemId(String tiId);
}

