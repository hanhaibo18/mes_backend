package com.richfit.mes.common.log.util;

import cn.hutool.core.util.URLUtil;
import cn.hutool.extra.servlet.ServletUtil;
import cn.hutool.http.HttpUtil;
import com.richfit.mes.common.core.constant.CommonConstant;
import com.richfit.mes.common.model.sys.SystemLog;
import com.richfit.mes.common.security.util.SecurityUtils;
import lombok.experimental.UtilityClass;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Objects;

/**
 * @author sun
 * @Description 系统日志工具类
 */
@UtilityClass
public class SystemLogUtils {
    public SystemLog getSystemLog() {
        HttpServletRequest request = ((ServletRequestAttributes) Objects
                .requireNonNull(RequestContextHolder.getRequestAttributes())).getRequest();
        SystemLog systemLog = new SystemLog();
        systemLog.setTenantId(SecurityUtils.getCurrentUser().getTenantId());
        systemLog.setCreateBy(SecurityUtils.getCurrentUser().getUsername());
        systemLog.setResult(CommonConstant.SUCCESS);
        systemLog.setRemoteAddr(ServletUtil.getClientIP(request));
        systemLog.setRequestUri(URLUtil.getPath(request.getRequestURI()));
        systemLog.setMethod(request.getMethod());
        systemLog.setUserAgent(request.getHeader("user-agent"));
        systemLog.setParams(HttpUtil.toParams(request.getParameterMap()));
        systemLog.setServiceId(getClientId());
        return systemLog;
    }

    public SystemLog getSystemLog(HttpServletRequest request,String username) {
        SystemLog systemLog = new SystemLog();
        systemLog.setCreateBy(username);
        systemLog.setResult(CommonConstant.SUCCESS);
        systemLog.setRemoteAddr(ServletUtil.getClientIP(request));
        systemLog.setRequestUri(URLUtil.getPath(request.getRequestURI()));
        systemLog.setMethod(request.getMethod());
        systemLog.setUserAgent(request.getHeader("user-agent"));
        systemLog.setParams(HttpUtil.toParams(request.getParameterMap()));
        systemLog.setServiceId(getClientId());
        return systemLog;
    }

    /**
     * 获取客户端
     * @return clientId
     */
    private String getClientId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication instanceof OAuth2Authentication) {
            OAuth2Authentication auth2Authentication = (OAuth2Authentication) authentication;
            return auth2Authentication.getOAuth2Request().getClientId();
        }
        return null;
    }


}
