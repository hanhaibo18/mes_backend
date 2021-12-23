package com.richfit.mes.common.log.event;

import com.richfit.mes.common.log.provider.LogServiceClient;
import com.richfit.mes.common.model.sys.SystemLog;
import com.richfit.mes.common.security.constant.SecurityConstants;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.core.annotation.Order;
import org.springframework.scheduling.annotation.Async;

/**
 * @author sun
 * @Description 异步监听日志事件
 */
@Slf4j
@AllArgsConstructor
public class SystemLogListener {

    private final LogServiceClient logServiceClient;

    @Async
    @Order
    @EventListener(SystemLogEvent.class)
    public void saveSystemLog(SystemLogEvent event) {
        SystemLog systemLog = event.getSystemLog();
        logServiceClient.saveLog(systemLog, SecurityConstants.FROM_INNER);
    }

}

