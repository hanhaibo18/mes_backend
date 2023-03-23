package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.common.model.produce.LayingOff;
import com.richfit.mes.produce.dao.LayingOffMapper;
import org.springframework.stereotype.Service;

/**
 * 下料表(ProduceLayingOff)表服务实现类
 *
 * @author makejava
 * @since 2023-03-23 14:25:09
 */
@Service("produceLayingOffService")
public class LayingOffServiceImpl extends ServiceImpl<LayingOffMapper, LayingOff> implements LayingOffService {

}

