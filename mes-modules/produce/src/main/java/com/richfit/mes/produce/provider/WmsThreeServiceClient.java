package com.richfit.mes.produce.provider;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.ApplicationResult;
import com.richfit.mes.common.model.produce.Certificate;
import com.richfit.mes.common.model.produce.IngredientApplicationDto;
import com.richfit.mes.common.model.produce.WmsResult;
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
 * @Author: zhiqiang.lu
 * @Date: 2023/6/12 09:41
 */
@FeignClient(name = "wms-service", decode404 = true, fallback = WmsServiceClientFallbackImpl.class)
public interface WmsThreeServiceClient {

    /**
     * 功能描述: 五、	MES申请单上传WMS（完工合格证生产入库）
     *
     * @param applyListUploads 合格证信息
     * @return CommonResult<WmsResult>
     **/
    @ApiOperation(value = "MES申请单上传WMS（已上线）", notes = "将MES系统满足条件申请单上传WMS。提供上传WMS按钮，用户点击按钮可手动将申请单上传WMS系统")
    @PostMapping("/api/integration/wms/three/apply_list_upload")
    public CommonResult<WmsResult> applyListUpload(@RequestBody List<ApplyListUpload> applyListUploads);
}
