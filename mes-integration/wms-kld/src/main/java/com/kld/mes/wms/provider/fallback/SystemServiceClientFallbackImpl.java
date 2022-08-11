package com.kld.mes.wms.provider.fallback;

import com.kld.mes.wms.provider.SystemServiceClient;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.MaterialReceive;
import com.richfit.mes.common.model.produce.MaterialReceiveDetail;
import com.richfit.mes.common.model.sys.ItemParam;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: GaoLiang
 * @Date: 2020/7/31 16:51
 */
@Component
public class SystemServiceClientFallbackImpl implements SystemServiceClient {

    @Override
    public CommonResult<ItemParam> findItemParamByCode(String code) {
        return CommonResult.success(null);
    }

    @Override
    public CommonResult<List<ItemParam>> selectItemClass(String name, String code, String header) {
        return CommonResult.success(null);
    }

    @Override
    public String getlastTime() {
        return null;
    }

    @Override
    public Boolean materialReceiveSaveBatch(List<MaterialReceive> materialReceiveList) {
        return null;
    }

    @Override
    public Boolean detailSaveBatch(List<MaterialReceiveDetail> detailList) {
        return null;
    }

}
