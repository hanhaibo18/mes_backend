package com.richfit.mes.produce.provider;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.produce.dto.MessageDto;
import com.richfit.mes.produce.provider.fallback.WeChatServiceClientFallbackImpl;
import io.swagger.annotations.ApiParam;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author HanHaiBo
 * @date 2023/2/28 15:54
 */
@FeignClient(name = "wechat-service", decode404 = true, fallback = WeChatServiceClientFallbackImpl.class)
public interface WeChatServiceClient {
    @PostMapping("/api/wechat/message/sendMessage")
    public CommonResult<String> sendMessage(@ApiParam(value = "消息参数") @RequestBody MessageDto messageInfo);
}
