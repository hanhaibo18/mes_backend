package com.richfit.mes.produce.provider;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.ApplicationResult;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.IngredientApplicationDto;
import com.richfit.mes.produce.provider.fallback.WmsServiceClientFallbackImpl;
import com.richfit.mes.produce.provider.fallback.WmsbsServiceClientFallbackImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/25 11:27
 */
@FeignClient(name = "wmsbs-service", decode404 = true, fallback = WmsbsServiceClientFallbackImpl.class)
public interface WmsbsServiceClient {

    @PostMapping("/api/integration/wmsbs/send_scjk")
    public CommonResult<Boolean> sendJkInfo(@RequestBody Certificate certificate);

    @GetMapping("/api/integration/wmsbs/queryMaterialCount")
    public CommonResult<Integer> queryMaterialCount(@RequestParam("materialNo") String materialNo);

    @PostMapping("/api/integration/wmsbs/anApplicationForm")
    public CommonResult<ApplicationResult> anApplicationForm(@RequestBody IngredientApplicationDto ingredientApplicationDto);
}
