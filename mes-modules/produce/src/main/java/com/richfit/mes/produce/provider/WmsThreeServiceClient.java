package com.richfit.mes.produce.provider;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.WmsResult;
import com.richfit.mes.common.model.wms.ApplyListUpload;
import com.richfit.mes.common.model.wms.MaterialRequisitionUpload;
import com.richfit.mes.produce.provider.fallback.WmsServiceClientFallbackImpl;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

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

    /**
     * 功能描述: MES领料单上传WMS
     *
     * @param materialRequisitionUploads
     * @Author: xinYu.hou
     * @Date: 2023/6/20 17:40
     * @return: CommonResult
     **/
    @ApiOperation(value = "MES领料单上传WMS", notes = "将MES系统领料单上传WMS，若单据类型为自动出库时，WMS系统要检查各行项目物料的在该工厂下的库存数量能否满足领料数量，需全部行项目物料库存都满足时，WMS系统自动生成出库单，物资库存减少，调用WMS领料单出库信息上传MES接口")
    @PostMapping("/api/integration/wms/three/material_requisition_upload")
    public CommonResult materialRequisitionUpload(@RequestBody List<MaterialRequisitionUpload> materialRequisitionUploads);
}
