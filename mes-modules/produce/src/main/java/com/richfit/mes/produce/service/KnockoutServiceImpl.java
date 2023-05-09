package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.Knockout;
import com.richfit.mes.produce.dao.KnockoutMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 扣箱工序报工记录表(Knockout)表服务实现类
 *
 * @author makejava
 * @since 2023-05-08 10:18:48
 */
@Service
public class KnockoutServiceImpl extends ServiceImpl<KnockoutMapper, Knockout> implements KnockoutService {
    @Autowired
    private KnockoutMapper knockoutMapper;

    @Override
    public Knockout queryCacheByItemId(String tiId) {
        return knockoutMapper.queryCacheByItemId(tiId);
    }
}

