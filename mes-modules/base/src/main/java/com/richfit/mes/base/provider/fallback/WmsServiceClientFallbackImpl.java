package com.richfit.mes.base.provider.fallback;

import com.richfit.mes.base.provider.WmsServiceClient;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.ApplicationResult;
import com.richfit.mes.common.model.wms.MaterialBasis;
import org.springframework.stereotype.Component;

import java.util.List;


/**
 * @author: llh
 * @date: 2023/3/30 11:30
 */
@Component
public class WmsServiceClientFallbackImpl implements WmsServiceClient {


    @Override
    public CommonResult<ApplicationResult> materialBasis(List<MaterialBasis> materialBasisList) {
        return null;
    }
}
