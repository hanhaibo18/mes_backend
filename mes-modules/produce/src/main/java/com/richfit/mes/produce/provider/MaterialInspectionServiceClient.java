package com.richfit.mes.produce.provider;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.richfit.mes.common.model.produce.PhyChemTaskVo;
import com.richfit.mes.common.model.produce.PhysChemOrder;
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
    public boolean saveOrder(@RequestBody PhysChemOrderInner physChemOrderInner);
    @PostMapping("/api/material/synResultInfos")
    public List<PhysChemOrderInner> synResultInfos(@RequestBody List<String> reportNos);
    @GetMapping("/api/material/changeOrderSatus")
    public boolean changeOrderSatus(@RequestParam("reportNo") String reportNo, @RequestParam("reportStatus")  String reportStatus);
    @GetMapping("/api/material/changeOrderSyncStatus")
    public boolean changeOrderSyncSatus(@RequestParam("reportNo") String reportNo,@RequestParam("syncStatus")  String syncStatus);
    @GetMapping("/api/material/getListByBatchNo")
    public List<PhysChemOrderInner> getListByBatchNo(@RequestParam("batchNo") String batchNo);

}
