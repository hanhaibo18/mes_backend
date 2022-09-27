package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.PhysChemResult;

/**
 * @author renzewen
 * @Description 理化检验试验结果
 */
public interface PhysChemResultService extends IService<PhysChemResult> {

    /**
     * 修改试验结果数据
     * @param physChemResult
     * @return
     */
    public boolean updateResult(PhysChemResult physChemResult);
}
