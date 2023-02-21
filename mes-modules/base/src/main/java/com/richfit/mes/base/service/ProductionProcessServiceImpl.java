package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.richfit.mes.base.dao.ProductionProcessMapper;
import com.richfit.mes.common.model.base.ProductionProcess;
import org.springframework.stereotype.Service;

/**
 * @author HanHaiBo
 * @date 2023/2/20 15:36
 */
@Service
public class ProductionProcessServiceImpl extends ServiceImpl<ProductionProcessMapper, ProductionProcess> implements ProductionProcessService {
}
