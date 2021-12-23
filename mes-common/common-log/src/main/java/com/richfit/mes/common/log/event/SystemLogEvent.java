package com.richfit.mes.common.log.event;

import com.richfit.mes.common.model.sys.SystemLog;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author sun
 * @Description 系统日志事件
 */
@Getter
@AllArgsConstructor
public class SystemLogEvent {
    private final SystemLog systemLog;
}
