package com.richfit.mes.sys.entity.param;

import com.richfit.mes.common.core.base.BasePageParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * @author sun
 * @Description 角色查询参数
 */
@Data
@ApiModel
@EqualsAndHashCode(callSuper = true)
public class SystemLogPageParam extends BasePageParam<SystemLogParam> {
    @ApiModelProperty(value = "日志类型")
    private String type;
    @ApiModelProperty(value = "日志结果")
    private String result;
    @ApiModelProperty(value = "开始时间")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createTimeStart;
    @ApiModelProperty(value = "截止时间")
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createTimeEnd;
}
