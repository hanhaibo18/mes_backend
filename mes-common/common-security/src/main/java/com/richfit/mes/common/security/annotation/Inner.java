package com.richfit.mes.common.security.annotation;

import java.lang.annotation.*;

/**
 * @author sun
 * @Description 服务调用鉴权注解
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Inner {

    /**
     * 是否AOP统一处理
     * @return false, true
     */
    boolean value() default true;
}
