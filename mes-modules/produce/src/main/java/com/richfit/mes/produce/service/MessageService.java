package com.richfit.mes.produce.service;

import com.richfit.mes.produce.entity.MessageDto;

/**
 * @author HanHaiBo
 * @date 2023/2/27 9:55
 */
public interface MessageService {
    /**
     * 发送公众号消息
     * @param messageInfo 消息内容
     * @return ok/error
     */
    String sentGzhMessage(MessageDto messageInfo);
}
