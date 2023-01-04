package com.richfit.mes.produce.entity;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @ClassName: ForDispatchingDto.java
 * @Author: Hou XinYu
 * @Description: 未派工查询参数
 * @CreateTime: 2022年09月28日 10:23:00
 */
@Data
public class ForDispatchingDto extends QueryPageDto {
    @ApiModelProperty(value = "siteId", dataType = "String")
    private String siteId;
    @ApiModelProperty(value = "跟单编号", dataType = "String")
    private String trackNo;
    @ApiModelProperty(value = "图号", dataType = "String")
    private String routerNo;
    @ApiModelProperty(value = "开始时间", dataType = "String")
    private String startTime;
    @ApiModelProperty(value = "结束时间", dataType = "String")
    private String endTime;
    @ApiModelProperty(value = "状态", dataType = "String")
    private String state;
    @ApiModelProperty(value = "派工用户ID", dataType = "String")
    private String userId;
    @ApiModelProperty(value = "产品编号", dataType = "String")
    private String productNo;
    @ApiModelProperty(value = "车间类型", dataType = "String")
    private String classes;
    @ApiModelProperty(value = "实施温度℃（热工）", dataType = "String")
    private String tempWork;
    @ApiModelProperty(value = "实施温度℃（热工）扩展", dataType = "String")
    private String tempWork1;
}
