package com.richfit.mes.produce.provider.fallback;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.PhyChemTaskVo;
import com.richfit.mes.common.model.produce.PhysChemOrder;
import com.richfit.mes.common.model.produce.PhysChemOrderInner;
import com.richfit.mes.produce.provider.MaterialInspectionServiceClient;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/31 16:51
 */
@Component
public class MaterialInspectionServiceClientFallbackImpl implements MaterialInspectionServiceClient {

    @Override
    public Page<PhysChemOrderInner> page(PhyChemTaskVo phyChemTaskVo) {
        return null;
    }

    @Override
    public boolean saveOrder(List<PhysChemOrderInner> physChemOrderInners) {
        return false;
    }

    @Override
    public List<PhysChemOrderInner> synResultInfos(List<String> reportNos) {
        return null;
    }

    @Override
    public boolean changeOrderSyncSatus(String reportNo, String syncStatus) {
        return false;
    }

    @Override
    public List<PhysChemOrderInner> getListByBatchNo(String batchNo) {
        return null;
    }

    @Override
    public List<PhysChemOrderInner> queryByOrderNo(String orderNo) {
        return null;
    }

    @Override
    public boolean deleteByOrderNo(String orderNo) {
        return false;
    }

    @Override
    public boolean changeOrderStatus(List<String> recordNos) {
        return false;
    }
}
