package com.richfit.mes.base.provider.fallback;

import com.richfit.mes.base.provider.ProduceServiceClient;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.TrackHead;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @Author: zhiqiang.lu
 * @Date: 2022.9.22
 * @LastEditors: zhiqiang.lu
 * @LastEditTime: 2022.9.22
 * @Description: 添加produce实现
 * @LastEdit: 添加通过工艺id查询跟单列表
 */
@Component
public class ProduceServiceClientFallbackImpl implements ProduceServiceClient {

    @Override
    public CommonResult<List<TrackHead>> selectByRouterId(String routerId) {
        return null;
    }
}
