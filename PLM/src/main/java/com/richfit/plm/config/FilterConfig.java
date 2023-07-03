package com.richfit.plm.config;

import com.richfit.plm.filter.UserFilter;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author HanHaiBo
 * @date 2023/7/3 11:03
 */
@Configuration
public class FilterConfig {
    @Bean
    public FilterRegistrationBean<UserFilter> userFilterRegistration() {
        FilterRegistrationBean<UserFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new UserFilter());
        /*
        1. `/*`：匹配所有URL路径。这意味着过滤器将应用于应用程序中的所有请求。

        2. `/path/*`：匹配以指定路径开头的所有URL。例如，`/api/*`将匹配`/api/users`、`/api/products`等路径。

        3. `*.extension`：匹配具有指定文件扩展名的URL路径。例如，`*.html`将匹配以`.html`结尾的路径，如`/page.html`。

        4. `/path`：匹配指定的精确URL路径。例如，`/users`将只匹配`/users`路径。

        这些URL模式可以在`addUrlPatterns()`方法中使用，以根据您的需求定义过滤器的范围。例如，如果您希望过滤器应用于所有路径，可以使用`addUrlPatterns("/*")`。
         */
        registrationBean.addUrlPatterns("/*"); // 设置要过滤的URL模式
        return registrationBean;
    }
}
