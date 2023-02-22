package com.richfit.mes.base.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.base.ProductionProcess;

/**
 * @author HanHaiBo
 * @date 2023/2/20 15:35
 */
public interface ProductionProcessService extends IService<ProductionProcess> {
    boolean updateBatch(ProductionProcess[] productionProcesses);
}
