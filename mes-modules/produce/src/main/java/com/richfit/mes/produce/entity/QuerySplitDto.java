package com.richfit.mes.produce.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.Date;

/**
 * @ClassName: QuerySplitVo.java
 * @Author: Hou XinYu
 * @Description: 跟单查分列表
 * @CreateTime: 2022年05月13日 07:01:00
 */
@Data
public class QuerySplitDto {
    @ApiModelProperty(value = "开始时间", required = true)
    private Date startTime;
    @ApiModelProperty(value = "结束时间", required = true)
    private Date endTime;
    @ApiModelProperty(value = "跟单状态", required = true)
    private Integer status;
    @ApiModelProperty(value = "跟单编号(模糊)", required = true)
    private String trackNo;
    @ApiModelProperty(value = "产品编号(模糊)", required = true)
    private String productNo;
    @ApiModelProperty(value = "图号(模糊)", required = true)
    private String drawingNo;
    @ApiModelProperty(value = "跟单模板类型", required = true)
    private String templateCode;
    @ApiModelProperty(value = "工作号", required = true)
    private String workPlanId;
    @ApiModelProperty(value = "炉批号", required = true)
    private String batchNo;
    @ApiModelProperty(value = "订单编号", required = true)
    private String productionOrder;
    @ApiModelProperty(value = "生成跟单员(无)", required = true)
    private String trackBy;
    @ApiModelProperty(value = "填发日期(无)", required = true)
    private Date fillDate;
    @ApiModelProperty(value = "跟单子类(无)", required = true)
    private Integer trackSubclass;
}
