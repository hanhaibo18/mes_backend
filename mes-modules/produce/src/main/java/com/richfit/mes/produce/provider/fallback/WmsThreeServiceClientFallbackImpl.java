package com.richfit.mes.produce.provider.fallback;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.WmsResult;
import com.richfit.mes.common.model.wms.ApplyListUpload;
import com.richfit.mes.common.model.wms.MaterialRequisitionUpload;
import com.richfit.mes.produce.provider.WmsThreeServiceClient;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2022/7/25 11:29
 */
public class WmsThreeServiceClientFallbackImpl implements WmsThreeServiceClient {

    @Override
    public CommonResult<WmsResult> applyListUpload(List<ApplyListUpload> applyListUploads) {
        return null;
    }

    @Override
    public CommonResult materialRequisitionUpload(List<MaterialRequisitionUpload> materialRequisitionUploads) {
        return null;
    }
}
