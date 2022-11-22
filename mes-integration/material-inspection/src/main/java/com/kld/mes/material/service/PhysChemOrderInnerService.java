package com.kld.mes.material.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.richfit.mes.common.model.produce.PhysChemOrderInner;

import java.util.List;

/**
 * @author renzewen
 * @Description 理化检验委托单
 */
public interface PhysChemOrderInnerService extends IService<PhysChemOrderInner> {

    public Boolean changeOrderSatus(String reportNo,String reportStatus);

    public List<PhysChemOrderInner> synResultInfos(List<String> reportNos);

    public Boolean changeOrderSyncSatus(String reportNo, String reportStatus);

    public List<PhysChemOrderInner>  getListByBatchNo(String batchNo);
}
