package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.ProductionRouteMapper;
import com.richfit.mes.common.model.base.ProductionRoute;
import org.springframework.stereotype.Service;

/**
 * @author HanHaiBo
 * @date 2023/2/20 15:42
 */
@Service
public class ProductionRouteServiceImpl extends ServiceImpl<ProductionRouteMapper, ProductionRoute> implements ProductionRouteService {
}
