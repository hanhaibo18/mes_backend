package com.richfit.mes.common.log.provider;

import com.richfit.mes.common.core.api.CommonResult;
import com.richfit.mes.common.model.sys.SystemLog;
import com.richfit.mes.common.security.constant.SecurityConstants;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

/**
 * @author sun
 * @Description 日志服务
 */
@FeignClient(name = "system-service",contextId = "logServiceClient")
public interface LogServiceClient {
    /**
     * 保存日志
     * @param sysLog 日志实体
     * @param from 是否内部调用
     * @return succes、false
     */
    @PostMapping("/api/sys/log/save")
    CommonResult<Boolean> saveLog(@RequestBody SystemLog sysLog, @RequestHeader(SecurityConstants.FROM) String from);
}
