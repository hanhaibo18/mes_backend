package com.richfit.plm.filter;

import org.springframework.beans.factory.annotation.Value;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author HanHaiBo
 * @date 2023/7/3 10:50
 */
public class UserFilter implements Filter {

    private static final String REQUIRED_KEY = "14df8d45b8eb450b803ff8fe288d0ec4";

    @Override
    public void init(FilterConfig filterConfig) {
        // 过滤器初始化逻辑（可选）
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String key = httpRequest.getHeader("authorization");

        if (key != null && key.equals(REQUIRED_KEY)) {
            // key 符合要求，继续处理请求
            chain.doFilter(request, response);
        } else {
            // key 不符合要求，返回错误响应或拒绝访问
            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
        }
    }

    @Override
    public void destroy() {
        // 过滤器销毁逻辑（可选）
    }
}
