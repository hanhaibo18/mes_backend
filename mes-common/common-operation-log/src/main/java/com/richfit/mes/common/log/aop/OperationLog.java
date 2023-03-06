package com.richfit.mes.common.log.aop;

import java.lang.annotation.*;

/**
 * 自定义注解类
 */
@Target(ElementType.METHOD) //注解放置的目标位置,METHOD是可注解在方法级别上
@Retention(RetentionPolicy.RUNTIME) //注解在哪个阶段执行s
@Documented //生成文档

public @interface OperationLog {
    String value() default "saveAction";

    String actionType() default "";

    String actionItem() default "";
}
