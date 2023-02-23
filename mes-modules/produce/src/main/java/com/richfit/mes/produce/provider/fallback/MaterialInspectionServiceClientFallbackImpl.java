package com.richfit.mes.produce.provider.fallback;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.PhyChemTaskVo;
import com.richfit.mes.common.model.produce.PhysChemOrderInner;
import com.richfit.mes.produce.provider.MaterialInspectionServiceClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.GetMapping;

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
    public List<PhysChemOrderInner> queryByGroupId(String groupId) {
        return null;
    }

    @Override
    public List<PhysChemOrderInner> queryByOrderNo(String orderNo) {
        return null;
    }

    @Override
    public boolean deleteByGroupId(String groupId) {
        return false;
    }

    @Override
    public boolean changeOrderStatus(List<PhysChemOrderInner> physChemOrderInners) {
        return false;
    }

    @Override
    public List<PhysChemOrderInner> queryByReportNo(String reportNo) {
        return null;
    }

    @Override
    public CommonResult<Boolean> auditSnyPhysChemOrder(List<String> reportNos, String isAudit, String auditBy) {
        return null;
    }

    @Override
    public CommonResult<Boolean> isStandard(List<String> reportNos, String isStandard, String standardBy) {
        return null;
    }

    @Override
    public List<PhysChemOrderInner> getInnerListByGroupIds(List<String> groupIds) {
        return null;
    }

}
