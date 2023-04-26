package com.richfit.mes.produce.provider;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.ApplicationResult;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.IngredientApplicationDto;
import com.richfit.mes.common.model.wms.ApplyListUpload;
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

    @PostMapping("/api/integration/wms/send_scjk")
    public CommonResult<Boolean> sendJkInfo(@RequestBody Certificate certificate);

    @GetMapping("/api/integration/wms/queryMaterialCount")
    public CommonResult<Integer> queryMaterialCount(@RequestParam("materialNo") String materialNo);

    @PostMapping("/api/integration/wms/anApplicationForm")
    public CommonResult<ApplicationResult> anApplicationForm(@RequestBody IngredientApplicationDto ingredientApplicationDto);

    @ApiOperation(value = "MES申请单上传WMS（已上线）", notes = "将MES系统满足条件申请单上传WMS。提供上传WMS按钮，用户点击按钮可手动将申请单上传WMS系统")
    @PostMapping("/api/integration/wms/three/apply_list_upload")
    public CommonResult<ApplicationResult> applyListUpload(@RequestBody List<ApplyListUpload> applyListUpload);
}
