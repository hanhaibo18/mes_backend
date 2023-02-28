package com.kld.mes.wechat.controller;

import com.kld.mes.wechat.entity.MessageDto;
import com.kld.mes.wechat.service.MessageService;
import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.core.base.BaseController;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author HanHaiBo
 * @date 2023/2/27 10:31
 */
@Slf4j
@Api(tags = "消息管理")
@RestController
@RequestMapping("/api/wechat/message")
public class MessageController extends BaseController{
    @Autowired
    private MessageService messageService;
    @ApiOperation(value = "公众号发送模板消息", notes = "公众号发送模板消息")
    @PostMapping("/sendMessage")
    public CommonResult<String> sendMessage(@ApiParam(value = "消息参数") @RequestBody MessageDto messageInfo){
        return CommonResult.success(messageService.sentGzhMessage(messageInfo));
    }
}
