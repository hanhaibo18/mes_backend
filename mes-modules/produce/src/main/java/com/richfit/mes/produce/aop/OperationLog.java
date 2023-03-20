package com.richfit.mes.produce.aop;

import io.swagger.annotations.ApiParam;

import java.lang.annotation.*;

/**
 * 自定义注解类
 *
 * @author Q
 */
@Target(ElementType.METHOD) //注解放置的目标位置,METHOD是可注解在方法级别上
@Retention(RetentionPolicy.RUNTIME) //注解在哪个阶段执行s
@Documented //生成文档

public @interface OperationLog {
    String value() default "saveAction";

    @ApiParam(value = "0-新增，1-修改，2-删除，3-撤回，4-其他")
    String actionType() default "";

    @ApiParam(value = "0-订单，1-计划，2-跟单，3-库存")
    String actionItem() default "";

    @ApiParam(value = "是否是计划号")
    boolean isPlanId() default false;

    @ApiParam(value = "是否是跟单")
    boolean isTrackHead() default false;

    @ApiParam(value = "是否是订单")
    boolean isOrder() default false;
}
