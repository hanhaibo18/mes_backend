package com.richfit.mes.common.core.utils;

import cn.hutool.core.codec.Base64;
import com.richfit.mes.common.core.api.ResultCode;
import com.richfit.mes.common.core.exception.GlobalException;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * @author sun
 * @Description Miscellaneous utilities for web applications.
 */
@Slf4j
@UtilityClass
public class WebUtils extends org.springframework.web.util.WebUtils {

    private final String BASIC_ = "Basic ";

    private final String UNKNOWN = "unknown";

    /**
     * 解析 client id
     *
     * @param header
     * @param defVal
     * @return 如果解析失败返回默认值
     */
    public String extractClientId(String header, final String defVal) {

        if (header == null || !header.startsWith(BASIC_)) {
            log.debug("The client information in the request header is empty: {}", header);
            return defVal;
        }
        byte[] base64Token = header.substring(6).getBytes(StandardCharsets.UTF_8);
        byte[] decoded;
        try {
            decoded = Base64.decode(base64Token);
        } catch (IllegalArgumentException e) {
            log.debug("Failed to decode basic authentication token: {}", header);
            return defVal;
        }

        String token = new String(decoded, StandardCharsets.UTF_8);

        int delim = token.indexOf(":");

        if (delim == -1) {
            log.debug("Invalid basic authentication token: {}", header);
            return defVal;
        }
        return token.substring(0, delim);
    }

    /**
     * 从请求头中解析 client id
     *
     * @param header
     * @return
     */
    public Optional<String> extractClientId(String header) {
        return Optional.ofNullable(extractClientId(header, null));
    }

    /**
     * 从request 获取CLIENT_ID
     *
     * @return
     */
    public String getClientId(String header) {
        String clientId = extractClientId(header, null);
        if (clientId == null) {
            throw new GlobalException("Invalid basic authentication token", ResultCode.FAILED);
        }
        return clientId;
    }
}
