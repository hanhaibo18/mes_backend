package com.richfit.mes.produce.provider.fallback;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.dto.MessageDto;
import com.richfit.mes.produce.provider.WeChatServiceClient;

/**
 * @author HanHaiBo
 * @date 2023/2/28 15:56
 */
public class WeChatServiceClientFallbackImpl implements WeChatServiceClient {
    @Override
    public CommonResult<String> sendMessage(MessageDto messageInfo) {
        return null;
    }
}
