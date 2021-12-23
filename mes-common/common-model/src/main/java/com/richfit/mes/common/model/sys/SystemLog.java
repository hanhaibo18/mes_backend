package com.richfit.mes.common.model.sys;

import com.baomidou.mybatisplus.annotation.TableName;
import com.richfit.mes.common.core.base.BaseEntity;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotBlank;

/**
 * <p>
 * 系统日志
 * </p>
 *
 * @author gaoliang
 * @since 2020-05-25
 */
@Data
@EqualsAndHashCode(callSuper = true)
@Accessors(chain = true)
@NoArgsConstructor
@TableName(value = "log", keepGlobalPrefix = true)
public class SystemLog extends BaseEntity<SystemLog> {

    /**
     * 租户菜单Id
     */
    @ApiModelProperty(value = "租户ID")
    private String tenantId;
    /**
     * 服务ID
     */
    @ApiModelProperty(value = "应用标识")
    private String serviceId;

    /**
     * 日志类型
     */
    @NotBlank(message = "日志类型不能为空")
    @ApiModelProperty(value = "日志类型")
    private String type;

    /**
     * 日志标题
     */
    @NotBlank(message = "日志标题不能为空")
    @ApiModelProperty(value = "日志标题")
    private String title;


    /**
     * 操作IP地址
     */
    @ApiModelProperty(value = "操作ip地址")
    private String remoteAddr;

    /**
     * 用户代理
     */
    @ApiModelProperty(value = "用户代理")
    private String userAgent;

    /**
     * 请求URI
     */
    @ApiModelProperty(value = "请求uri")
    private String requestUri;

    /**
     * 操作方式
     */
    @ApiModelProperty(value = "操作方式")
    private String method;

    /**
     * 操作提交的数据
     */
    @ApiModelProperty(value = "提交数据")
    private String params;

    /**
     * 操作结果
     */
	@ApiModelProperty(value = "操作结果")
    private String result;
    /**
     * 执行时间
     */
    @ApiModelProperty(value = "方法执行时间")
    private Long time;

    /**
     * 异常信息
     */
    @ApiModelProperty(value = "异常信息")
    private String exception;

}
