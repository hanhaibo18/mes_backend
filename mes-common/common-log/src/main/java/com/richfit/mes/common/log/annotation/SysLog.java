package com.richfit.mes.common.log.annotation;

import java.lang.annotation.*;

/**
 * @author sun
 * @Description 操作日志注解
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLog {
    /**
     * 类型
     * @return {String}
     */
    String type();
    /**
     * 描述
     * @return {String}
     */
    String title() default "";
}
