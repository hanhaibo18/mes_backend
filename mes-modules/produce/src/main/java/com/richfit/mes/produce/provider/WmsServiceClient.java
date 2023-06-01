package com.richfit.mes.produce.provider;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.ApplicationResult;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.IngredientApplicationDto;
import com.richfit.mes.common.model.wms.ApplyListUpload;
import com.richfit.mes.common.model.wms.InventoryQuery;
import com.richfit.mes.common.model.wms.InventoryReturn;
import com.richfit.mes.produce.provider.fallback.WmsServiceClientFallbackImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/25 11:27
 */
@FeignClient(name = "wms-service", decode404 = true, fallback = WmsServiceClientFallbackImpl.class)
public interface WmsServiceClient {

    /**
     * 功能描述: wms生产交库
     *
     * @param certificate 合格证信息
     * @return Object
     **/
    @PostMapping("/api/integration/wms/send_scjk")
    CommonResult<Object> sendJkInfo(@RequestBody Certificate certificate);

    /**
     * 功能描述: wms物料号查询数量
     *
     * @param materialNo 物料号码
     * @return Integer
     **/
    @GetMapping("/api/integration/wms/queryMaterialCount")
    CommonResult<Integer> queryMaterialCount(@RequestParam("materialNo") String materialNo);

    /**
     * 功能描述: wms配料申请单上传
     *
     * @param ingredientApplicationDto 配料申请单
     * @return ApplicationResult
     **/
    @PostMapping("/api/integration/wms/anApplicationForm")
    CommonResult<ApplicationResult> anApplicationForm(@RequestBody IngredientApplicationDto ingredientApplicationDto);

    /**
     * 功能描述: wms3配料申请单上传
     *
     * @param applyListUpload List<ApplyListUpload> 配料申请单
     * @return ApplicationResult
     **/
    @ApiOperation(value = "MES申请单上传WMS（已上线）", notes = "将MES系统满足条件申请单上传WMS。提供上传WMS按钮，用户点击按钮可手动将申请单上传WMS系统")
    @PostMapping("/api/integration/wms/three/apply_list_upload")
    CommonResult<ApplicationResult> applyListUpload(@RequestBody List<ApplyListUpload> applyListUpload);

    /**
     * MES实时查询WMS库存
     *
     * @param inventoryQuery
     * @return
     */
    @PostMapping("/api/integration/wms/three/inventory_query")
    public CommonResult<List<InventoryReturn>> inventoryQuery(@RequestBody InventoryQuery inventoryQuery);

}
