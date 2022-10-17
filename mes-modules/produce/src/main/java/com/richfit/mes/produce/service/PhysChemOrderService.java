package com.richfit.mes.produce.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.PhysChemOrder;
import com.richfit.mes.produce.entity.phyChemTestVo.PhyChemTaskVo;

/**
 * @author renzewen
 * @Description 理化检验委托单
 */
public interface PhysChemOrderService extends IService<PhysChemOrder> {

    IPage<PhysChemOrder> selectOrderList(PhyChemTaskVo phyChemTaskVo);
}
