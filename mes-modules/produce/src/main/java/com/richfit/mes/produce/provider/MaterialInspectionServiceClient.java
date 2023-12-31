package com.richfit.mes.produce.provider;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.PhyChemTaskVo;
import com.richfit.mes.common.model.produce.PhysChemOrderInner;
import com.richfit.mes.produce.provider.fallback.MaterialInspectionServiceClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/25 11:32
 */

@FeignClient(name = "material-inspection-service", decode404 = true, fallback = MaterialInspectionServiceClientFallbackImpl.class)
public interface MaterialInspectionServiceClient {

    @PostMapping("/api/material/query/page")
    public Page<PhysChemOrderInner> page(@RequestBody PhyChemTaskVo phyChemTaskVo);
    @PostMapping("/api/material/saveOrder")
    public boolean saveOrder(@RequestBody List<PhysChemOrderInner> physChemOrderInners);
    @PostMapping("/api/material/synResultInfos")
    public List<PhysChemOrderInner> synResultInfos(@RequestBody List<String> reportNos);
    @GetMapping("/api/material/changeOrderSyncStatus")
    public boolean changeOrderSyncSatus(@RequestParam("reportNo") String reportNo,@RequestParam("syncStatus")  String syncStatus);
    @GetMapping("/api/material/getListByBatchNo")
    public List<PhysChemOrderInner> getListByBatchNo(@RequestParam("batchNo") String batchNo);
    @GetMapping("/api/material/queryByGroupId")
    public List<PhysChemOrderInner> queryByGroupId(@RequestParam("groupId") String groupId);
    @GetMapping("/api/material/queryByOrderNo")
    public List<PhysChemOrderInner> queryByOrderNo(@RequestParam("orderNo") String orderNo);
    @GetMapping("/api/material/deleteByGroupId")
    public boolean deleteByGroupId(@RequestParam("groupId") String groupId);
    @PostMapping("/api/material/changeOrderStatus")
    public boolean changeOrderStatus(@RequestBody List<PhysChemOrderInner> physChemOrderInners);
    @GetMapping("/api/material/queryByReportNo")
    public List<PhysChemOrderInner> queryByReportNo(@RequestParam("reportNo") String reportNo);
    @PostMapping("/api/material/auditSnyPhysChemOrder")
    public CommonResult<Boolean> auditSnyPhysChemOrder(@RequestBody List<String> reportNos,@RequestParam("isAudit") String isAudit,@RequestParam("auditBy") String auditBy);
    @PostMapping("/api/material/isStandard")
    public CommonResult<Boolean> isStandard(@RequestBody List<String> reportNos,@RequestParam("isStandard") String isStandard,@RequestParam("standardBy") String standardBy);
    @PostMapping("/api/material/getInnerListByGroupIds")
    public List<PhysChemOrderInner> getInnerListByGroupIds(@RequestBody List<String> groupIds);
    @GetMapping("/api/material/delete")
    public CommonResult<Boolean> delete(@RequestParam("groupId") String groupId);

}
