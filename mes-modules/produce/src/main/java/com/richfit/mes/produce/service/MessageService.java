package com.richfit.mes.produce.service;

import com.richfit.mes.produce.entity.MessageDto;

/**
 * @author HanHaiBo
 * @date 2023/2/27 9:55
 */
public interface MessageService {
    String sentGzhMessage(MessageDto messageInfo);
}
