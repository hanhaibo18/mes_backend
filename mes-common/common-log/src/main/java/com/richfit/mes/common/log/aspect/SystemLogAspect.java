package com.richfit.mes.common.log.aspect;

import com.richfit.mes.common.log.annotation.SysLog;
import com.richfit.mes.common.log.event.SystemLogEvent;
import com.richfit.mes.common.log.util.SystemLogUtils;
import com.richfit.mes.common.model.sys.SystemLog;
import lombok.AllArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * @author sun
 * @Description 操作日志使用spring event异步入库
 */
@Slf4j
@Aspect
@AllArgsConstructor
public class SystemLogAspect {
    private final ApplicationEventPublisher publisher;

    @SneakyThrows
    @Around("@annotation(sysLog)")
    public Object around(ProceedingJoinPoint point, SysLog sysLog) {
        String strClassName = point.getTarget().getClass().getName();
        String strMethodName = point.getSignature().getName();
        log.debug("[类名]:{},[方法]:{}", strClassName, strMethodName);

        SystemLog logVo = SystemLogUtils.getSystemLog();
        logVo.setTitle(sysLog.title());
        logVo.setType(sysLog.type());
        Long startTime = System.currentTimeMillis();
        Object obj = point.proceed();
        Long endTime = System.currentTimeMillis();
        logVo.setTime(endTime - startTime);
        publisher.publishEvent(new SystemLogEvent(logVo));
        return obj;
    }
}
